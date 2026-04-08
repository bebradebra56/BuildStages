package com.buidlsta.stagebuisla.xcv.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.buidlsta.stagebuisla.xcv.presentation.app.BuildStagesApplication

class BuildStagesPushHandler {
    fun buildStagesHandlePush(extras: Bundle?) {
        Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = buildStagesBundleToMap(extras)
            Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    BuildStagesApplication.BUILD_STAGES_FB_LI = map["url"]
                    Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "Push data no!")
        }
    }

    private fun buildStagesBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}