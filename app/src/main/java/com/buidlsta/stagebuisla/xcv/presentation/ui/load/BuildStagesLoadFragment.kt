package com.buidlsta.stagebuisla.xcv.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.buidlsta.stagebuisla.MainActivity
import com.buidlsta.stagebuisla.R
import com.buidlsta.stagebuisla.databinding.FragmentLoadBuildStagesBinding
import com.buidlsta.stagebuisla.xcv.data.shar.BuildStagesSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class BuildStagesLoadFragment : Fragment(R.layout.fragment_load_build_stages) {
    private lateinit var buildStagesLoadBinding: FragmentLoadBuildStagesBinding

    private val buildStagesLoadViewModel by viewModel<BuildStagesLoadViewModel>()

    private val buildStagesSharedPreference by inject<BuildStagesSharedPreference>()

    private var buildStagesUrl = ""

    private val buildStagesRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        buildStagesSharedPreference.buildStagesNotificationState = 2
        buildStagesNavigateToSuccess(buildStagesUrl)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildStagesLoadBinding = FragmentLoadBuildStagesBinding.bind(view)

        buildStagesLoadBinding.buildStagesGrandButton.setOnClickListener {
            val buildStagesPermission = Manifest.permission.POST_NOTIFICATIONS
            buildStagesRequestNotificationPermission.launch(buildStagesPermission)
        }

        buildStagesLoadBinding.buildStagesSkipButton.setOnClickListener {
            buildStagesSharedPreference.buildStagesNotificationState = 1
            buildStagesSharedPreference.buildStagesNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            buildStagesNavigateToSuccess(buildStagesUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                buildStagesLoadViewModel.buildStagesHomeScreenState.collect {
                    when (it) {
                        is BuildStagesLoadViewModel.BuildStagesHomeScreenState.BuildStagesLoading -> {

                        }

                        is BuildStagesLoadViewModel.BuildStagesHomeScreenState.BuildStagesError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is BuildStagesLoadViewModel.BuildStagesHomeScreenState.BuildStagesSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val buildStagesNotificationState = buildStagesSharedPreference.buildStagesNotificationState
                                when (buildStagesNotificationState) {
                                    0 -> {
                                        buildStagesLoadBinding.buildStagesNotiGroup.visibility = View.VISIBLE
                                        buildStagesLoadBinding.buildStagesLoadingGroup.visibility = View.GONE
                                        buildStagesUrl = it.data
                                    }
                                    1 -> {
                                        if (System.currentTimeMillis() / 1000 > buildStagesSharedPreference.buildStagesNotificationRequest) {
                                            buildStagesLoadBinding.buildStagesNotiGroup.visibility = View.VISIBLE
                                            buildStagesLoadBinding.buildStagesLoadingGroup.visibility = View.GONE
                                            buildStagesUrl = it.data
                                        } else {
                                            buildStagesNavigateToSuccess(it.data)
                                        }
                                    }
                                    2 -> {
                                        buildStagesNavigateToSuccess(it.data)
                                    }
                                }
                            } else {
                                buildStagesNavigateToSuccess(it.data)
                            }
                        }

                        BuildStagesLoadViewModel.BuildStagesHomeScreenState.BuildStagesNotInternet -> {
                            buildStagesLoadBinding.buildStagesStateGroup.visibility = View.VISIBLE
                            buildStagesLoadBinding.buildStagesLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun buildStagesNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_buildStagesLoadFragment_to_buildStagesV,
            bundleOf(BUILD_STAGES_D to data)
        )
    }

    companion object {
        const val BUILD_STAGES_D = "buildStagesData"
    }
}