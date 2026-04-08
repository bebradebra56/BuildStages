package com.buidlsta.stagebuisla

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.buidlsta.stagebuisla.xcv.BuildStagesGlobalLayoutUtil
import com.buidlsta.stagebuisla.xcv.buildStagesSetupSystemBars
import com.buidlsta.stagebuisla.xcv.presentation.app.BuildStagesApplication
import com.buidlsta.stagebuisla.xcv.presentation.pushhandler.BuildStagesPushHandler
import org.koin.android.ext.android.inject

class BuildStagesActivity : AppCompatActivity() {

    private val buildStagesPushHandler by inject<BuildStagesPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buildStagesSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_build_stages)

        val buildStagesRootView = findViewById<View>(android.R.id.content)
        BuildStagesGlobalLayoutUtil().buildStagesAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(buildStagesRootView) { buildStagesView, buildStagesInsets ->
            val buildStagesSystemBars = buildStagesInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val buildStagesDisplayCutout = buildStagesInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val buildStagesIme = buildStagesInsets.getInsets(WindowInsetsCompat.Type.ime())


            val buildStagesTopPadding = maxOf(buildStagesSystemBars.top, buildStagesDisplayCutout.top)
            val buildStagesLeftPadding = maxOf(buildStagesSystemBars.left, buildStagesDisplayCutout.left)
            val buildStagesRightPadding = maxOf(buildStagesSystemBars.right, buildStagesDisplayCutout.right)
            window.setSoftInputMode(BuildStagesApplication.buildStagesInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "ADJUST PUN")
                val buildStagesBottomInset = maxOf(buildStagesSystemBars.bottom, buildStagesDisplayCutout.bottom)

                buildStagesView.setPadding(buildStagesLeftPadding, buildStagesTopPadding, buildStagesRightPadding, 0)

                buildStagesView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = buildStagesBottomInset
                }
            } else {
                Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "ADJUST RESIZE")

                val buildStagesBottomInset = maxOf(buildStagesSystemBars.bottom, buildStagesDisplayCutout.bottom, buildStagesIme.bottom)

                buildStagesView.setPadding(buildStagesLeftPadding, buildStagesTopPadding, buildStagesRightPadding, 0)

                buildStagesView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = buildStagesBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "Activity onCreate()")
        buildStagesPushHandler.buildStagesHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            buildStagesSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        buildStagesSetupSystemBars()
    }
}