package com.android.local.service.core.i

import fi.iki.elonen.NanoHTTPD

interface RequestListener {
    fun onRequest(action: String, params: Map<String, String>): NanoHTTPD.Response
}