package org.team2471.bunnybots.robot.subsystems

import com.ctre.MotorControl.CANTalon


object Arm {
    private val shoulderMotor1 = CANTalon(0)
    private val shoulderMotor2 = CANTalon(0)
    private val wristMotor = CANTalon(0)
    private val intakeMotor = CANTalon(0)

    var intake: Double = 0.0
        set(speed) = intakeMotor.set(speed)
        //hi
    var wrist: Double = 0.0
        set(speed) = wristMotor.set(speed)
    //shdfiashiofhasdf



}