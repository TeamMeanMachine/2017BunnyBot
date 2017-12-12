package org.team2471.bunnybots.robot

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.SerialPort

object LEDController {
    private val port: SerialPort? = try {
        SerialPort(9600, SerialPort.Port.kUSB1)
    } catch (_: Exception) {
        DriverStation.reportError("LEDController serial port not found!", false)
        null
    }

    private var lastMessage = ""

    fun send(message: String) {
        if (lastMessage == message) return
        port?.writeString(message + '\n')
        lastMessage = message
    }
}
