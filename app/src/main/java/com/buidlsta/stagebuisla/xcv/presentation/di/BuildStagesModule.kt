package com.buidlsta.stagebuisla.xcv.presentation.di

import com.buidlsta.stagebuisla.xcv.presentation.pushhandler.BuildStagesPushHandler
import com.buidlsta.stagebuisla.xcv.presentation.ui.load.BuildStagesLoadViewModel
import com.buidlsta.stagebuisla.xcv.presentation.ui.view.BuildStagesViFun
import com.buidlsta.stagebuisla.xcv.data.repo.BuildStagesRepository
import com.buidlsta.stagebuisla.xcv.data.shar.BuildStagesSharedPreference
import com.buidlsta.stagebuisla.xcv.data.utils.BuildStagesPushToken
import com.buidlsta.stagebuisla.xcv.data.utils.BuildStagesSystemService
import com.buidlsta.stagebuisla.xcv.domain.usecases.BuildStagesGetAllUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val buildStagesModule = module {
    factory {
        BuildStagesPushHandler()
    }
    single {
        BuildStagesRepository()
    }
    single {
        BuildStagesSharedPreference(get())
    }
    factory {
        BuildStagesPushToken()
    }
    factory {
        BuildStagesSystemService(get())
    }
    factory {
        BuildStagesGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        BuildStagesViFun(get())
    }
    viewModel {
        BuildStagesLoadViewModel(get(), get(), get())
    }
}