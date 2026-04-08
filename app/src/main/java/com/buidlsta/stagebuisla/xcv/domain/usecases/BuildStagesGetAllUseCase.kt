package com.buidlsta.stagebuisla.xcv.domain.usecases

import android.util.Log
import com.buidlsta.stagebuisla.xcv.data.repo.BuildStagesRepository
import com.buidlsta.stagebuisla.xcv.data.utils.BuildStagesPushToken
import com.buidlsta.stagebuisla.xcv.data.utils.BuildStagesSystemService
import com.buidlsta.stagebuisla.xcv.domain.model.BuildStagesEntity
import com.buidlsta.stagebuisla.xcv.domain.model.BuildStagesParam
import com.buidlsta.stagebuisla.xcv.presentation.app.BuildStagesApplication

class BuildStagesGetAllUseCase(
    private val buildStagesRepository: BuildStagesRepository,
    private val buildStagesSystemService: BuildStagesSystemService,
    private val buildStagesPushToken: BuildStagesPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : BuildStagesEntity?{
        val params = BuildStagesParam(
            buildStagesLocale = buildStagesSystemService.buildStagesGetLocale(),
            buildStagesPushToken = buildStagesPushToken.buildStagesGetToken(),
            buildStagesAfId = buildStagesSystemService.buildStagesGetAppsflyerId()
        )
        Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "Params for request: $params")
        return buildStagesRepository.buildStagesGetClient(params, conversion)
    }



}