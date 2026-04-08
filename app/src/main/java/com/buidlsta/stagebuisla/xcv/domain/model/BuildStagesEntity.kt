package com.buidlsta.stagebuisla.xcv.domain.model

import com.google.gson.annotations.SerializedName


data class BuildStagesEntity (
    @SerializedName("ok")
    val buildStagesOk: String,
    @SerializedName("url")
    val buildStagesUrl: String,
    @SerializedName("expires")
    val buildStagesExpires: Long,
)