package com.android.local.service.demo.service

import com.android.local.service.annotation.Get
import com.android.local.service.annotation.Service
import com.android.local.service.demo.livedata.LiveDataHelper

@Service(port = 3333)
abstract class OtherService {

    @Get("query")
    fun query(
        aaa: Boolean,
        bbb: Double,
        ccc: Float,
        ddd: String,
        eee: Int,
    ): List<String> {
        return listOf("$aaa", "$bbb", "$ccc", "$ddd", "$eee")
    }

    @Get("delete")
    fun delete(id: Int, name: String) {
        LiveDataHelper.saveDataLiveData.postValue("id=${id},name=${name}");
    }
}