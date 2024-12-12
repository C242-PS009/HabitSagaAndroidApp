package com.c242_ps009.habitsaga.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object Connection {
    fun checkConnection(context: Context): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val conn = connManager.activeNetwork ?: return false
        val activeConn = connManager.getNetworkCapabilities(conn) ?: return false

        return when {
            activeConn.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeConn.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            activeConn.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeConn.hasTransport(NetworkCapabilities.TRANSPORT_VPN) && activeConn.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeConn.hasTransport(NetworkCapabilities.TRANSPORT_VPN) && activeConn.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            activeConn.hasTransport(NetworkCapabilities.TRANSPORT_VPN) && activeConn.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}