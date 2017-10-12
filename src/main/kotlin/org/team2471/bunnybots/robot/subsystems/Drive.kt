package org.team2471.bunnybots.robot.subsystems

import com.ctre.MotorControl.CANTalon

object Drive {
    private val leftMotor1 = CANTalon(12)
    private val leftMotor2 = CANTalon(13)
    private val rightMotor1 = CANTalon(3)
    private val rightMotor2 = CANTalon(2)

    fun drive(throttle: Double, softTurn: Double, hardTurn: Double) {
        var leftPower = throttle - (softTurn * Math.abs(throttle))
        var rightPower = throttle + (softTurn * Math.abs(throttle))
        leftPower -= hardTurn
        rightPower += hardTurn

        val maxPower = Math.max(Math.abs(leftPower), Math.abs(rightPower))
        if (maxPower > 1){
            leftPower /= maxPower
            rightPower /= maxPower
        }

        leftMotor1.set(leftPower)
        rightMotor1.set(-rightPower)
        // TODO: make motors 2 and 3 followers
        leftMotor2.set(leftPower)
        rightMotor2.set(-rightPower)
    }
}