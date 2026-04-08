package com.buidlsta.stagebuisla.xcv.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.buidlsta.stagebuisla.xcv.presentation.app.BuildStagesApplication
import com.buidlsta.stagebuisla.xcv.presentation.ui.load.BuildStagesLoadFragment
import org.koin.android.ext.android.inject

class BuildStagesV : Fragment(){

    private lateinit var buildStagesPhoto: Uri
    private var buildStagesFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val buildStagesTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        buildStagesFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        buildStagesFilePathFromChrome = null
    }

    private val buildStagesTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            buildStagesFilePathFromChrome?.onReceiveValue(arrayOf(buildStagesPhoto))
            buildStagesFilePathFromChrome = null
        } else {
            buildStagesFilePathFromChrome?.onReceiveValue(null)
            buildStagesFilePathFromChrome = null
        }
    }

    private val buildStagesDataStore by activityViewModels<BuildStagesDataStore>()


    private val buildStagesViFun by inject<BuildStagesViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (buildStagesDataStore.buildStagesView.canGoBack()) {
                        buildStagesDataStore.buildStagesView.goBack()
                        Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "WebView can go back")
                    } else if (buildStagesDataStore.buildStagesViList.size > 1) {
                        Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "WebView can`t go back")
                        buildStagesDataStore.buildStagesViList.removeAt(buildStagesDataStore.buildStagesViList.lastIndex)
                        Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "WebView list size ${buildStagesDataStore.buildStagesViList.size}")
                        buildStagesDataStore.buildStagesView.destroy()
                        val previousWebView = buildStagesDataStore.buildStagesViList.last()
                        buildStagesAttachWebViewToContainer(previousWebView)
                        buildStagesDataStore.buildStagesView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (buildStagesDataStore.buildStagesIsFirstCreate) {
            buildStagesDataStore.buildStagesIsFirstCreate = false
            buildStagesDataStore.buildStagesContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return buildStagesDataStore.buildStagesContainerView
        } else {
            return buildStagesDataStore.buildStagesContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "onViewCreated")
        if (buildStagesDataStore.buildStagesViList.isEmpty()) {
            buildStagesDataStore.buildStagesView = BuildStagesVi(requireContext(), object :
                BuildStagesCallBack {
                override fun buildStagesHandleCreateWebWindowRequest(buildStagesVi: BuildStagesVi) {
                    buildStagesDataStore.buildStagesViList.add(buildStagesVi)
                    Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "WebView list size = ${buildStagesDataStore.buildStagesViList.size}")
                    Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "CreateWebWindowRequest")
                    buildStagesDataStore.buildStagesView = buildStagesVi
                    buildStagesVi.buildStagesSetFileChooserHandler { callback ->
                        buildStagesHandleFileChooser(callback)
                    }
                    buildStagesAttachWebViewToContainer(buildStagesVi)
                }

            }, buildStagesWindow = requireActivity().window).apply {
                buildStagesSetFileChooserHandler { callback ->
                    buildStagesHandleFileChooser(callback)
                }
            }
            buildStagesDataStore.buildStagesView.buildStagesFLoad(arguments?.getString(
                BuildStagesLoadFragment.BUILD_STAGES_D) ?: "")
//            ejvview.fLoad("www.google.com")
            buildStagesDataStore.buildStagesViList.add(buildStagesDataStore.buildStagesView)
            buildStagesAttachWebViewToContainer(buildStagesDataStore.buildStagesView)
        } else {
            buildStagesDataStore.buildStagesViList.forEach { webView ->
                webView.buildStagesSetFileChooserHandler { callback ->
                    buildStagesHandleFileChooser(callback)
                }
            }
            buildStagesDataStore.buildStagesView = buildStagesDataStore.buildStagesViList.last()

            buildStagesAttachWebViewToContainer(buildStagesDataStore.buildStagesView)
        }
        Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "WebView list size = ${buildStagesDataStore.buildStagesViList.size}")
    }

    private fun buildStagesHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        buildStagesFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "Launching file picker")
                    buildStagesTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "Launching camera")
                    buildStagesPhoto = buildStagesViFun.buildStagesSavePhoto()
                    buildStagesTakePhoto.launch(buildStagesPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                buildStagesFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun buildStagesAttachWebViewToContainer(w: BuildStagesVi) {
        buildStagesDataStore.buildStagesContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            buildStagesDataStore.buildStagesContainerView.removeAllViews()
            buildStagesDataStore.buildStagesContainerView.addView(w)
        }
    }


}