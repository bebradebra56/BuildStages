package com.buidlsta.stagebuisla.xcv.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buidlsta.stagebuisla.xcv.data.shar.BuildStagesSharedPreference
import com.buidlsta.stagebuisla.xcv.data.utils.BuildStagesSystemService
import com.buidlsta.stagebuisla.xcv.domain.usecases.BuildStagesGetAllUseCase
import com.buidlsta.stagebuisla.xcv.presentation.app.BuildStagesAppsFlyerState
import com.buidlsta.stagebuisla.xcv.presentation.app.BuildStagesApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BuildStagesLoadViewModel(
    private val buildStagesGetAllUseCase: BuildStagesGetAllUseCase,
    private val buildStagesSharedPreference: BuildStagesSharedPreference,
    private val buildStagesSystemService: BuildStagesSystemService
) : ViewModel() {

    private val _buildStagesHomeScreenState: MutableStateFlow<BuildStagesHomeScreenState> =
        MutableStateFlow(BuildStagesHomeScreenState.BuildStagesLoading)
    val buildStagesHomeScreenState = _buildStagesHomeScreenState.asStateFlow()

    private var buildStagesGetApps = false


    init {
        viewModelScope.launch {
            when (buildStagesSharedPreference.buildStagesAppState) {
                0 -> {
                    if (buildStagesSystemService.buildStagesIsOnline()) {
                        BuildStagesApplication.buildStagesConversionFlow.collect {
                            when(it) {
                                BuildStagesAppsFlyerState.BuildStagesDefault -> {}
                                BuildStagesAppsFlyerState.BuildStagesError -> {
                                    buildStagesSharedPreference.buildStagesAppState = 2
                                    _buildStagesHomeScreenState.value =
                                        BuildStagesHomeScreenState.BuildStagesError
                                    buildStagesGetApps = true
                                }
                                is BuildStagesAppsFlyerState.BuildStagesSuccess -> {
                                    if (!buildStagesGetApps) {
                                        buildStagesGetData(it.buildStagesData)
                                        buildStagesGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _buildStagesHomeScreenState.value =
                            BuildStagesHomeScreenState.BuildStagesNotInternet
                    }
                }
                1 -> {
                    if (buildStagesSystemService.buildStagesIsOnline()) {
                        if (BuildStagesApplication.BUILD_STAGES_FB_LI != null) {
                            _buildStagesHomeScreenState.value =
                                BuildStagesHomeScreenState.BuildStagesSuccess(
                                    BuildStagesApplication.BUILD_STAGES_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > buildStagesSharedPreference.buildStagesExpired) {
                            Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "Current time more then expired, repeat request")
                            BuildStagesApplication.buildStagesConversionFlow.collect {
                                when(it) {
                                    BuildStagesAppsFlyerState.BuildStagesDefault -> {}
                                    BuildStagesAppsFlyerState.BuildStagesError -> {
                                        _buildStagesHomeScreenState.value =
                                            BuildStagesHomeScreenState.BuildStagesSuccess(
                                                buildStagesSharedPreference.buildStagesSavedUrl
                                            )
                                        buildStagesGetApps = true
                                    }
                                    is BuildStagesAppsFlyerState.BuildStagesSuccess -> {
                                        if (!buildStagesGetApps) {
                                            buildStagesGetData(it.buildStagesData)
                                            buildStagesGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "Current time less then expired, use saved url")
                            _buildStagesHomeScreenState.value =
                                BuildStagesHomeScreenState.BuildStagesSuccess(
                                    buildStagesSharedPreference.buildStagesSavedUrl
                                )
                        }
                    } else {
                        _buildStagesHomeScreenState.value =
                            BuildStagesHomeScreenState.BuildStagesNotInternet
                    }
                }
                2 -> {
                    _buildStagesHomeScreenState.value =
                        BuildStagesHomeScreenState.BuildStagesError
                }
            }
        }
    }


    private suspend fun buildStagesGetData(conversation: MutableMap<String, Any>?) {
        val buildStagesData = buildStagesGetAllUseCase.invoke(conversation)
        if (buildStagesSharedPreference.buildStagesAppState == 0) {
            if (buildStagesData == null) {
                buildStagesSharedPreference.buildStagesAppState = 2
                _buildStagesHomeScreenState.value =
                    BuildStagesHomeScreenState.BuildStagesError
            } else {
                buildStagesSharedPreference.buildStagesAppState = 1
                buildStagesSharedPreference.apply {
                    buildStagesExpired = buildStagesData.buildStagesExpires
                    buildStagesSavedUrl = buildStagesData.buildStagesUrl
                }
                _buildStagesHomeScreenState.value =
                    BuildStagesHomeScreenState.BuildStagesSuccess(buildStagesData.buildStagesUrl)
            }
        } else  {
            if (buildStagesData == null) {
                _buildStagesHomeScreenState.value =
                    BuildStagesHomeScreenState.BuildStagesSuccess(
                        buildStagesSharedPreference.buildStagesSavedUrl
                    )
            } else {
                buildStagesSharedPreference.apply {
                    buildStagesExpired = buildStagesData.buildStagesExpires
                    buildStagesSavedUrl = buildStagesData.buildStagesUrl
                }
                _buildStagesHomeScreenState.value =
                    BuildStagesHomeScreenState.BuildStagesSuccess(buildStagesData.buildStagesUrl)
            }
        }
    }


    sealed class BuildStagesHomeScreenState {
        data object BuildStagesLoading : BuildStagesHomeScreenState()
        data object BuildStagesError : BuildStagesHomeScreenState()
        data class BuildStagesSuccess(val data: String) : BuildStagesHomeScreenState()
        data object BuildStagesNotInternet: BuildStagesHomeScreenState()
    }
}