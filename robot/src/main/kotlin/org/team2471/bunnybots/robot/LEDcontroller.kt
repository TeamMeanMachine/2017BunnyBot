package org.team2471.bunnybots.robot

import edu.wpi.first.wpilibj.SerialPort

object LEDController {
    private val port = SerialPort(9600, SerialPort.Port.kUSB1)

    fun write(message: String)
    {
        port.writeString(message + '\n')

    }


}



