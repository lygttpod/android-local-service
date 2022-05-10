package com.android.local.service.core.i

import com.android.local.service.core.service.ALSService

interface IService {
    fun getService(): ALSService
    fun getServiceByPort(port: Int): ALSService
    fun getServicePort(): Int
}