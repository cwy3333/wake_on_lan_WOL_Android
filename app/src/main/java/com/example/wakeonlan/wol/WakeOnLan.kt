package com.example.wakeonlan.wol

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object WakeOnLan {

    fun send(macAddress: String, ipAddress: String, port: Int): Result<Unit> {
        return try {
            val macBytes = parseMacAddress(macAddress)
            val magicPacket = buildMagicPacket(macBytes)

            DatagramSocket().use { socket ->
                socket.broadcast = true
                val address = InetAddress.getByName(ipAddress)
                val packet = DatagramPacket(magicPacket, magicPacket.size, address, port)
                socket.send(packet)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseMacAddress(mac: String): ByteArray {
        val hexOnly = mac.replace(Regex("[:\\-\\s]"), "")
        require(hexOnly.length == 12) { "MAC 地址必须为 12 位十六进制字符" }
        return hexOnly.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }

    private fun buildMagicPacket(macBytes: ByteArray): ByteArray {
        val packet = ByteArray(6 + 16 * macBytes.size)
        // 前 6 字节为 0xFF
        for (i in 0 until 6) {
            packet[i] = 0xFF.toByte()
        }
        // 重复 16 次 MAC 地址
        for (i in 0 until 16) {
            val offset = 6 + i * macBytes.size
            System.arraycopy(macBytes, 0, packet, offset, macBytes.size)
        }
        return packet
    }
}
