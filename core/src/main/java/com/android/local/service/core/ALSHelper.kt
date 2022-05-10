package com.android.local.service.core

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.android.local.service.core.data.ServiceConfig
import com.android.local.service.core.data.ServiceInfo
import com.android.local.service.core.i.IService
import com.android.local.service.core.ktx.toServiceInfo

@SuppressLint("StaticFieldLeak")
object ALSHelper {

    private val TAG = this::class.java.simpleName

    private val serviceInstanceMaps = hashMapOf<String, IService>()

    var context: Context? = null

    var serviceList: MutableList<ServiceInfo> = mutableListOf()

    fun init(context: Context) {
        this.context = context
    }

    fun startService(serviceConfig: ServiceConfig) {
        instanceService(serviceConfig.toServiceInfo())
    }

    fun startServices(list: List<ServiceConfig>) {
        list.forEach { startService(it) }
    }

    fun stopService(serviceConfig: ServiceConfig) {
        val info = serviceConfig.toServiceInfo()
        try {
            serviceInstanceMaps.forEach {
                val fullClassName = info.originFullClassName()
                if (it.key == fullClassName) {
                    it.value.getService().stop()
                    Log.d(TAG, "stopService: 《${info.serviceName}》服务已停止")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "stopService：《${info.serviceName}》：${e.message}")
        }
    }

    fun stopServices(list: List<ServiceConfig>) {
        list.forEach { stopService(it) }
    }

    fun stopAllServices() {
        serviceInstanceMaps.forEach {
            try {
                it.value.getService().stop()
                Log.d(TAG, "stopService: 《${it.key}》服务已停止")
            } catch (e: Exception) {
                Log.d(TAG, "stopService：《${it.key}》 ${e.message}")
            }
        }
    }

    /**
     * 通过反射区实例化service去启动本地服务，免去手动实例化启动服务的繁琐步骤
     */
    private fun instanceService(serviceInfo: ServiceInfo) {
        val key = serviceInfo.originFullClassName()
        val serviceWrapper = if (serviceInstanceMaps.containsKey(key)) {
            serviceInstanceMaps[key]
        } else {
            try {
                val fullClassName = serviceInfo.createFullClassName()
                val cls = Class.forName(fullClassName)
                val serviceWrapper = cls.newInstance() as IService
                serviceInstanceMaps[key] = serviceWrapper
                serviceWrapper
            } catch (e: Exception) {
                Log.d(TAG, "instanceService实例化服务类失败")
                null
            }
        }
        start(serviceWrapper, serviceInfo)
    }

    /**
     * 启动服务
     */
    private fun start(serviceWrapper: IService?, serviceInfo: ServiceInfo) {
        if (serviceWrapper == null) return
        val port = serviceInfo.port
        val service = if (port > 0) {
            serviceWrapper.getServiceByPort(port)
        } else {
            serviceWrapper.getService()
        }
        val servicePort = serviceWrapper.getServicePort()
        serviceList.add(serviceInfo.apply { this.port = servicePort })
        try {
            if (service.wasStarted()) {
                Log.d(TAG, "initService《${serviceInfo.serviceName}》发现已开启过服务，要先关闭")
                service.stop()
                Log.d(TAG, "initService《${serviceInfo.serviceName}》服务已关闭")
            }
            service.start()
            Log.d(TAG, "initPCService《${serviceInfo.serviceName}》服务已《《启动》》，端口号：$servicePort")
        } catch (e: Exception) {
            Log.d(
                TAG,
                "initPCService《${serviceInfo.serviceName}》服务启动《《失败》》，端口号：$servicePort  失败原因是：" + e.message
            )
        }
    }
}