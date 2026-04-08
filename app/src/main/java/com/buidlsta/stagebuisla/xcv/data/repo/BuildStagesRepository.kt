package com.buidlsta.stagebuisla.xcv.data.repo

import android.util.Log
import com.buidlsta.stagebuisla.xcv.domain.model.BuildStagesEntity
import com.buidlsta.stagebuisla.xcv.domain.model.BuildStagesParam
import com.buidlsta.stagebuisla.xcv.presentation.app.BuildStagesApplication.Companion.BUILD_STAGES_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface BuildStagesApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun buildStagesGetClient(
        @Body jsonString: JsonObject,
    ): Call<BuildStagesEntity>
}


private const val BUILD_STAGES_MAIN = "https://builldstages.com/"
class BuildStagesRepository {

    suspend fun buildStagesGetClient(
        buildStagesParam: BuildStagesParam,
        buildStagesConversion: MutableMap<String, Any>?
    ): BuildStagesEntity? {
        val gson = Gson()
        val api = buildStagesGetApi(BUILD_STAGES_MAIN, null)

        val buildStagesJsonObject = gson.toJsonTree(buildStagesParam).asJsonObject
        buildStagesConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            buildStagesJsonObject.add(key, element)
        }
        return try {
            val buildStagesRequest: Call<BuildStagesEntity> = api.buildStagesGetClient(
                jsonString = buildStagesJsonObject,
            )
            val buildStagesResult = buildStagesRequest.awaitResponse()
            Log.d(BUILD_STAGES_MAIN_TAG, "Retrofit: Result code: ${buildStagesResult.code()}")
            if (buildStagesResult.code() == 200) {
                Log.d(BUILD_STAGES_MAIN_TAG, "Retrofit: Get request success")
                Log.d(BUILD_STAGES_MAIN_TAG, "Retrofit: Code = ${buildStagesResult.code()}")
                Log.d(BUILD_STAGES_MAIN_TAG, "Retrofit: ${buildStagesResult.body()}")
                buildStagesResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(BUILD_STAGES_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(BUILD_STAGES_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun buildStagesGetApi(url: String, client: OkHttpClient?) : BuildStagesApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
