package org.team2471.bunnybots.robot

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.SerialPort

object LEDController {
    private val port: SerialPort? = try {
        println("Serial port found.")
        SerialPort(9600, SerialPort.Port.kUSB1)
    } catch (_: Exception) {
        DriverStation.reportError("LEDController serial port not found!", false)
        null
    }

    fun write(message: String) {
        port?.writeString(message + '\n')
    }


}



