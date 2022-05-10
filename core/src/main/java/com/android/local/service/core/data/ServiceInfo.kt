package com.android.local.service.core.data

class ServiceInfo(
    val serviceName: String,//自己创建的类名：XXXService
    var port: Int,//端口号
    val createServiceName: String,//根据自己创建的类自动生成的类名: ALS_XXXService
    val packageName: String//类所在的包名
) {
    fun originFullClassName() = "${packageName}.$serviceName"
    fun createFullClassName() = "${packageName}.$createServiceName"
}