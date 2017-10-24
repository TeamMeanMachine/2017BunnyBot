package org.team2471.bunnybots.robot.subsystems

import com.ctre.MotorControl.CANTalon
import com.ctre.MotorControl.SmartMotorController
import edu.wpi.first.wpilibj.command.Command
import edu.wpi.first.wpilibj.command.Subsystem
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team2471.bunnybots.robot.Driver

object Drive : Subsystem() {

    private val EDGES_PER_100_MS = 216 * 4.0 / 10.0

    private val leftMotors = {
        val master = CANTalon(12)
        val slave1 = CANTalon(13)
        val slave2 = CANTalon(14)
        val slave3 = CANTalon(15)

        master.changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)
        slave1.changeControlMode(SmartMotorController.TalonControlMode.Follower)
        slave1.set(master.deviceID.toDouble())
        slave2.changeControlMode(SmartMotorController.TalonControlMode.Follower)
        slave2.set(master.deviceID.toDouble())
        slave3.changeControlMode(SmartMotorController.TalonControlMode.Follower)
        slave3.set(master.deviceID.toDouble())

        master.setVoltageRampRate(72.0)
        slave1.setVoltageRampRate(72.0)
        slave2.setVoltageRampRate(72.0)
        slave3.setVoltageRampRate(72.0)

        master
    }()

    private val rightMotors = {
        val master = CANTalon(1)
        val slave1 = CANTalon(2)
        val slave2 = CANTalon(3)
        val slave3 = CANTalon(4)

        master.changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)
        slave1.changeControlMode(SmartMotorController.TalonControlMode.Follower)
        slave1.set(master.deviceID.toDouble())
        slave2.changeControlMode(SmartMotorController.TalonControlMode.Follower)
        slave2.set(master.deviceID.toDouble())
        slave3.changeControlMode(SmartMotorController.TalonControlMode.Follower)
        slave3.set(master.deviceID.toDouble())

        master.inverted = true

        master.setVoltageRampRate(72.0)
        slave1.setVoltageRampRate(72.0)
        slave2.setVoltageRampRate(72.0)
        slave3.setVoltageRampRate(72.0)

        master
    }()

    val getSpeed: Double get() = Math.abs(-leftMotors.getEncVelocity() / EDGES_PER_100_MS + rightMotors.getEncVelocity() / EDGES_PER_100_MS) / 2.0

    val leftDistance: Double get() = leftMotors.position
    val rightDistance: Double get() = rightMotors.position


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

        leftMotors.set(leftPower)
        rightMotors.set(rightPower)

    }

    fun driveStraight(throttle: Double) {
        leftMotors.set(throttle)
        rightMotors.set(throttle)
    }

    override fun initDefaultCommand() {
        defaultCommand = DriveDefaultCommand
    }
}

object DriveDefaultCommand : Command() {

    init {
        requires(Drive)
    }

    override fun execute() {
        Drive.drive(Driver.throttle, Driver.softTurn, Driver.hardTurn)
        SmartDashboard.putNumber("Drive Speed", Drive.getSpeed)
        SmartDashboard.putString("Drive Distance", "${Drive.leftDistance}:${Drive.rightDistance}")

    }

    override fun isFinished(): Boolean = false

}