package com.android.local.service.demo.service

import com.android.local.service.BuildConfig
import com.android.local.service.annotation.Get
import com.android.local.service.annotation.Page
import com.android.local.service.annotation.Service
import com.android.local.service.demo.livedata.LiveDataHelper
import java.util.*
import kotlin.collections.HashMap

@Service(port = 2222)
abstract class PCService {

    @Page("index")
    fun getIndexFileName() = "test_page.html"

    @Get("saveData")
    fun saveData(content: String) {
        LiveDataHelper.saveDataLiveData.postValue(content + UUID.randomUUID());
    }

    @Get("queryAppInfo")
    fun getAppInfo(): HashMap<String, Any> {
        return hashMapOf(
            "applicationId" to BuildConfig.APPLICATION_ID,
            "versionName" to BuildConfig.VERSION_NAME,
            "versionCode" to BuildConfig.VERSION_CODE,
            "uuid" to UUID.randomUUID(),
        )
    }
}