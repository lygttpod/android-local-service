package com.android.local.service.demo.livedata

import androidx.lifecycle.MutableLiveData

object LiveDataHelper {
    val saveDataLiveData = MutableLiveData<String>()
    val appInfoLiveData = MutableLiveData<String>()
    val changeDataLiveData = MutableLiveData<String>()
}