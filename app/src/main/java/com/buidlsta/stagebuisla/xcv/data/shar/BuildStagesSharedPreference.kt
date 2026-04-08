package com.buidlsta.stagebuisla.xcv.data.shar

import android.content.Context
import androidx.core.content.edit

class BuildStagesSharedPreference(context: Context) {
    private val buildStagesPrefs = context.getSharedPreferences("buildStagesSharedPrefsAb", Context.MODE_PRIVATE)

    var buildStagesSavedUrl: String
        get() = buildStagesPrefs.getString(BUILD_STAGES_SAVED_URL, "") ?: ""
        set(value) = buildStagesPrefs.edit { putString(BUILD_STAGES_SAVED_URL, value) }

    var buildStagesExpired : Long
        get() = buildStagesPrefs.getLong(BUILD_STAGES_EXPIRED, 0L)
        set(value) = buildStagesPrefs.edit { putLong(BUILD_STAGES_EXPIRED, value) }

    var buildStagesAppState: Int
        get() = buildStagesPrefs.getInt(BUILD_STAGES_APPLICATION_STATE, 0)
        set(value) = buildStagesPrefs.edit { putInt(BUILD_STAGES_APPLICATION_STATE, value) }

    var buildStagesNotificationRequest: Long
        get() = buildStagesPrefs.getLong(BUILD_STAGES_NOTIFICAITON_REQUEST, 0L)
        set(value) = buildStagesPrefs.edit { putLong(BUILD_STAGES_NOTIFICAITON_REQUEST, value) }


    var buildStagesNotificationState:Int
        get() = buildStagesPrefs.getInt(BUILD_STAGES_NOTIFICATION_STATE, 0)
        set(value) = buildStagesPrefs.edit { putInt(BUILD_STAGES_NOTIFICATION_STATE, value) }

    companion object {
        private const val BUILD_STAGES_NOTIFICATION_STATE = "buildStagesNotificationState"
        private const val BUILD_STAGES_SAVED_URL = "buildStagesSavedUrl"
        private const val BUILD_STAGES_EXPIRED = "buildStagesExpired"
        private const val BUILD_STAGES_APPLICATION_STATE = "buildStagesApplicationState"
        private const val BUILD_STAGES_NOTIFICAITON_REQUEST = "buildStagesNotificationRequest"
    }
}