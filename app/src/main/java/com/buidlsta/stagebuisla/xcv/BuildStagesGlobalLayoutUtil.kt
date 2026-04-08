package com.buidlsta.stagebuisla.xcv

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.buidlsta.stagebuisla.xcv.presentation.app.BuildStagesApplication

class BuildStagesGlobalLayoutUtil {

    private var buildStagesMChildOfContent: View? = null
    private var buildStagesUsableHeightPrevious = 0

    fun buildStagesAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        buildStagesMChildOfContent = content.getChildAt(0)

        buildStagesMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val buildStagesUsableHeightNow = buildStagesComputeUsableHeight()
        if (buildStagesUsableHeightNow != buildStagesUsableHeightPrevious) {
            val buildStagesUsableHeightSansKeyboard = buildStagesMChildOfContent?.rootView?.height ?: 0
            val buildStagesHeightDifference = buildStagesUsableHeightSansKeyboard - buildStagesUsableHeightNow

            if (buildStagesHeightDifference > (buildStagesUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(BuildStagesApplication.buildStagesInputMode)
            } else {
                activity.window.setSoftInputMode(BuildStagesApplication.buildStagesInputMode)
            }
//            mChildOfContent?.requestLayout()
            buildStagesUsableHeightPrevious = buildStagesUsableHeightNow
        }
    }

    private fun buildStagesComputeUsableHeight(): Int {
        val r = Rect()
        buildStagesMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}