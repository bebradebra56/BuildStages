package com.buidlsta.stagebuisla.xcv.domain.model

import com.google.gson.annotations.SerializedName


private const val BUILD_STAGES_A = "com.buidlsta.stagebuisla"
private const val BUILD_STAGES_B = "buildstages-d890c"
data class BuildStagesParam (
    @SerializedName("af_id")
    val buildStagesAfId: String,
    @SerializedName("bundle_id")
    val buildStagesBundleId: String = BUILD_STAGES_A,
    @SerializedName("os")
    val buildStagesOs: String = "Android",
    @SerializedName("store_id")
    val buildStagesStoreId: String = BUILD_STAGES_A,
    @SerializedName("locale")
    val buildStagesLocale: String,
    @SerializedName("push_token")
    val buildStagesPushToken: String,
    @SerializedName("firebase_project_id")
    val buildStagesFirebaseProjectId: String = BUILD_STAGES_B,

    )