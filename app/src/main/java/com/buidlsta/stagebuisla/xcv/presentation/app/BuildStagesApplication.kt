package com.buidlsta.stagebuisla.xcv.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.buidlsta.stagebuisla.di.databaseModule
import com.buidlsta.stagebuisla.di.preferencesModule
import com.buidlsta.stagebuisla.di.repositoryModule
import com.buidlsta.stagebuisla.di.viewModelModule
import com.buidlsta.stagebuisla.xcv.presentation.di.buildStagesModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface BuildStagesAppsFlyerState {
    data object BuildStagesDefault : BuildStagesAppsFlyerState
    data class BuildStagesSuccess(val buildStagesData: MutableMap<String, Any>?) :
        BuildStagesAppsFlyerState

    data object BuildStagesError : BuildStagesAppsFlyerState
}

interface BuildStagesAppsApi {
    @Headers("Content-Type: application/json")
    @GET(BUILD_STAGES_LIN)
    fun buildStagesGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val BUILD_STAGES_APP_DEV = "KzSKQgPu9Ggkst3G4ZVADP"
private const val BUILD_STAGES_LIN = "com.buidlsta.stagebuisla"

class BuildStagesApplication : Application() {

    private var buildStagesIsResumed = false
    ///////
    private var buildStagesConversionTimeoutJob: Job? = null
    private var buildStagesDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        buildStagesSetDebufLogger(appsflyer)
        buildStagesMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        buildStagesExtractDeepMap(p0.deepLink)
                        Log.d(BUILD_STAGES_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(BUILD_STAGES_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(BUILD_STAGES_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            BUILD_STAGES_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    //////////
                    buildStagesConversionTimeoutJob?.cancel()
                    Log.d(BUILD_STAGES_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = buildStagesGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.buildStagesGetClient(
                                    devkey = BUILD_STAGES_APP_DEV,
                                    deviceId = buildStagesGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(BUILD_STAGES_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    buildStagesResume(
                                        BuildStagesAppsFlyerState.BuildStagesError
                                    )
                                } else {
                                    buildStagesResume(
                                        BuildStagesAppsFlyerState.BuildStagesSuccess(
                                            resp
                                        )
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(BUILD_STAGES_MAIN_TAG, "Error: ${d.message}")
                                buildStagesResume(BuildStagesAppsFlyerState.BuildStagesError)
                            }
                        }
                    } else {
                        buildStagesResume(
                            BuildStagesAppsFlyerState.BuildStagesSuccess(
                                p0
                            )
                        )
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    /////////
                    buildStagesConversionTimeoutJob?.cancel()
                    Log.d(BUILD_STAGES_MAIN_TAG, "onConversionDataFail: $p0")
                    buildStagesResume(BuildStagesAppsFlyerState.BuildStagesError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(BUILD_STAGES_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(BUILD_STAGES_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, BUILD_STAGES_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(BUILD_STAGES_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(BUILD_STAGES_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        ///////////
        buildStagesStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@BuildStagesApplication)
            modules(
                listOf(
                    buildStagesModule, databaseModule, preferencesModule, repositoryModule, viewModelModule
                )
            )
        }
    }

    private fun buildStagesExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(BUILD_STAGES_MAIN_TAG, "Extracted DeepLink data: $map")
        buildStagesDeepLinkData = map
    }
    /////////////////

    private fun buildStagesStartConversionTimeout() {
        buildStagesConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!buildStagesIsResumed) {
                Log.d(BUILD_STAGES_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                buildStagesResume(BuildStagesAppsFlyerState.BuildStagesError)
            }
        }
    }

    private fun buildStagesResume(state: BuildStagesAppsFlyerState) {
        ////////////
        buildStagesConversionTimeoutJob?.cancel()
        if (state is BuildStagesAppsFlyerState.BuildStagesSuccess) {
            val convData = state.buildStagesData ?: mutableMapOf()
            val deepData = buildStagesDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!buildStagesIsResumed) {
                buildStagesIsResumed = true
                buildStagesConversionFlow.value =
                    BuildStagesAppsFlyerState.BuildStagesSuccess(merged)
            }
        } else {
            if (!buildStagesIsResumed) {
                buildStagesIsResumed = true
                buildStagesConversionFlow.value = state
            }
        }
    }

    private fun buildStagesGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(BUILD_STAGES_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun buildStagesSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun buildStagesMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun buildStagesGetApi(url: String, client: OkHttpClient?): BuildStagesAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var buildStagesInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val buildStagesConversionFlow: MutableStateFlow<BuildStagesAppsFlyerState> = MutableStateFlow(
            BuildStagesAppsFlyerState.BuildStagesDefault
        )
        var BUILD_STAGES_FB_LI: String? = null
        const val BUILD_STAGES_MAIN_TAG = "BuildStagesMainTag"
    }
}