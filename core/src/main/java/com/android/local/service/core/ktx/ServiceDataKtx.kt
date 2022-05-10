package com.android.local.service.core.ktx

import com.android.local.service.core.data.ServiceConfig
import com.android.local.service.core.data.ServiceInfo

fun ServiceConfig.toServiceInfo(): ServiceInfo {
    val serviceClass = this.serviceClass
    val serviceName = serviceClass.simpleName
    val createServiceName = "ALS_$serviceName"
    val fullClassName = serviceClass.name
    val packageName = fullClassName.toString().substring(0, fullClassName.toString().lastIndexOf("."))
    return ServiceInfo(serviceName, this.port, createServiceName, packageName)
}