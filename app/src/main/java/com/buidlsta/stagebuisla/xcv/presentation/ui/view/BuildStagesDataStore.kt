package com.buidlsta.stagebuisla.xcv.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class BuildStagesDataStore : ViewModel(){
    val buildStagesViList: MutableList<BuildStagesVi> = mutableListOf()
    var buildStagesIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var buildStagesContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var buildStagesView: BuildStagesVi

}