package com.android.local.service.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.local.service.core.ALSHelper
import com.android.local.service.core.data.ServiceConfig
import com.android.local.service.databinding.ActivityMainBinding
import com.android.local.service.demo.livedata.LiveDataHelper
import com.android.local.service.demo.service.AndroidService
import com.android.local.service.demo.service.OtherService
import com.android.local.service.demo.service.PCService
import com.android.local.service.demo.utils.getPhoneWifiIpAddress
import okhttp3.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val client = OkHttpClient()

    private var ipAddress: String? = null

    private var androidServicePort = 1111
    private var pcServicePort = 2222

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //只用之前先初始化
        ALSHelper.init(this)
        //启动单个服务用法
        ALSHelper.startService(ServiceConfig(AndroidService::class.java))
        //启动多个服务用法
        ALSHelper.startServices(
            listOf(
                ServiceConfig(PCService::class.java),
                ServiceConfig(OtherService::class.java, 4444)
            )
        )
        initIpAddress()
        initView()
        pcServiceDemoTip()
        initObserver()
    }

    private fun pcServiceDemoTip() {
        val pcBaseUrl = "http://${ipAddress}:${pcServicePort}"
        val showH5Page = "$pcBaseUrl/index"
        val queryAppInfo = "$pcBaseUrl/queryAppInfo"
        val saveData = "$pcBaseUrl/saveData?content=我是来自PC端的数据"
        binding.tvPcServiceTip.text =
            "局域网内电脑端可输入一下地址看demo效果：\n$showH5Page\n$queryAppInfo\n$saveData"
    }

    private fun initIpAddress() {
        if (ipAddress == null) {
            ipAddress = getPhoneWifiIpAddress()
        }
        pcServicePort =
            ALSHelper.serviceList.find { it.serviceName == PCService::class.java.simpleName }?.port
                ?: 0
        androidServicePort =
            ALSHelper.serviceList.find { it.serviceName == AndroidService::class.java.simpleName }?.port
                ?: 0
    }

    private fun initObserver() {
        LiveDataHelper.saveDataLiveData.observe(this) { showUIData(it) }
        LiveDataHelper.appInfoLiveData.observe(this) { showUIData(it) }
        LiveDataHelper.changeDataLiveData.observe(this) { showUIData(it) }
    }

    private fun showUIData(it: String?) {
        binding.tvContent.text = it
    }

    private fun showResultUI(it: String?) {
        binding.tvRequestResult.text = "请求本地服务器响应的结果：\n$it"
    }

    private fun initView() {
        binding.tvServiceAddress.text = "本机服务器地址：$ipAddress"

        var serviceListData = ""
        ALSHelper.serviceList.forEach { serviceListData += "服务名：${it.serviceName}   端口号：${it.port}\n" }
        binding.tvServiceList.text = serviceListData


        binding.btnQueryAppInfo.setOnClickListener {
            binding.tvContent.text = ""
            sendRequest("http://${ipAddress}:${androidServicePort}/appInfo")
        }
        binding.btnChangeData.setOnClickListener {
            sendRequest("http://${ipAddress}:${androidServicePort}/changeData?data=这是一条通过本地服务修改本地数据的请求${UUID.randomUUID()}")
        }
    }

    private fun sendRequest(url: String) {
        val request = Request.Builder().url(url).build();
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    showResultUI(e.message)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                runOnUiThread {
                    showResultUI(result)
                }
            }
        })
    }
}