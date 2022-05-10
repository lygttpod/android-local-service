package com.android.local.service.core.service

import android.content.res.AssetManager
import android.util.Log
import com.android.local.service.core.ALSHelper
import com.android.local.service.core.i.RequestListener
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import java.io.IOException

class ALSService(port: Int) : NanoHTTPD(port) {
    private val TAG = this::class.java.simpleName

    private var requestListener: RequestListener? = null

    fun setRequestListener(listener: RequestListener) {
        this.requestListener = listener
    }

    override fun serve(session: IHTTPSession): Response {

        val method = session.method
        val uri = session.uri
        var params = session.parms

        if (Method.POST == method) {
            try {
                session.parseBody(mapOf())
                params = session.parms
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // 默认传入的url是以“/”开头的，需要删除掉，否则就变成了绝对路径
        val action = uri?.substring(1) ?: return wrapResponse(session, fileNotFoundResponse())

        Log.d(TAG, "uri = $uri   method = $method   params = $params")
        val response = requestListener?.onRequest(action, params ?: mapOf())
            ?: error("setRequestListener方法没有设置")
        return wrapResponse(session, response)
    }

    private fun wrapResponse(session: IHTTPSession, response: Response): Response {
        //下面是跨域的参数
        var allowHeaders = ""
        session.headers?.let {
            val requestHeaders = it["access-control-request-headers"]
            allowHeaders = requestHeaders ?: "Content-Type"
        }
        response.addHeader("Access-Control-Allow-Headers", allowHeaders);
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Max-Age", "" + 42 * 60 * 60);
        return response
    }

    private fun getFileExtensionName(fileName: String): String {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase()
    }

    private fun jsonResponse(code: Int, message: String, data: Any): Response {
        val result = hashMapOf<String, Any>()
        result["code"] = code
        result["message"] = message
        result["data"] = data
        return newFixedLengthResponse(
            Response.Status.OK,
            mimeTypes()["json"],
            Gson().toJson(result)
        )
    }

    private fun fileResponse(fileName: String): Response {
        val context = ALSHelper.context ?: return fileNotFoundResponse()
        val assetManager: AssetManager = context.assets
        return try {
            val stream = assetManager.open(fileName)
            val extension: String = getFileExtensionName(fileName)
            newChunkedResponse(Response.Status.OK, mimeTypes()[extension], stream)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "File not exist. $fileName")
            fileError()
        }
    }

    private fun fileNotFoundResponse(error: String = "Error 404, file not found."): Response {
        return newFixedLengthResponse(
            Response.Status.NOT_FOUND,
            MIME_PLAINTEXT,
            error
        )
    }

    fun success(data: Any) = jsonResponse(code = 200, message = "success", data = data)
    fun successEmpty() = jsonResponse(code = 200, message = "success", data = "")
    fun error(message: String) = jsonResponse(code = 500, message = message, data = "")
    fun errorPath() = jsonResponse(code = 500, message = "unknow request path, please check it", data = "")

    fun fileSuccess(fileName: String) = fileResponse(fileName)
    fun fileError() = fileNotFoundResponse()

}