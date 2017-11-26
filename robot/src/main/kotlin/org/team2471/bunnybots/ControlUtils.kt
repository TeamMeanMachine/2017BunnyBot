package org.team2471.bunnybots

import com.ctre.CANTalon

operator fun CANTalon.plus(slave: CANTalon): CANTalon = apply {
    slave.changeControlMode(CANTalon.TalonControlMode.Follower)
    slave.set(this.deviceID.toDouble())
}
