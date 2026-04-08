package com.buidlsta.stagebuisla.xcv.data.utils

import android.util.Log
import com.buidlsta.stagebuisla.xcv.presentation.app.BuildStagesApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class BuildStagesPushToken {

    suspend fun buildStagesGetToken(
        buildStagesMaxAttempts: Int = 3,
        buildStagesDelayMs: Long = 1500
    ): String {

        repeat(buildStagesMaxAttempts - 1) {
            try {
                val buildStagesToken = FirebaseMessaging.getInstance().token.await()
                return buildStagesToken
            } catch (e: Exception) {
                Log.e(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(buildStagesDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}