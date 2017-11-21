package org.team2471.bunnybots.robot.subsystems

import com.ctre.MotorControl.CANTalon
import com.ctre.MotorControl.SmartMotorController
import edu.wpi.first.wpilibj.networktables.NetworkTable
import kotlinx.coroutines.experimental.CommonPool
import org.team2471.bunnybots.robot.Driver
import org.team2471.frc.lib.control.experimental.Command
import org.team2471.frc.lib.control.experimental.registerDefaultCommand
import org.team2471.frc.lib.control.plus
import org.team2471.frc.lib.util.measureTimeFPGA
import org.team2471.bunnybots.robot.RobotMap.Talons as Talons

private const val DRIVE_CURRENT_LIMIT = 25
private const val DRIVE_RAMP_RATE = 72.0

object Drive {
    private val EDGES_PER_100_MS = 216 * 4.0 / 10.0
    private val table = NetworkTable.getTable("Drive")

    private val leftMotors = CANTalon(Talons.DRIVE_LEFT_MOTOR_1).apply {
        changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)
        setVoltageRampRate(DRIVE_RAMP_RATE)
        setCurrentLimit(DRIVE_CURRENT_LIMIT)
        EnableCurrentLimit(true)
    } + CANTalon(Talons.DRIVE_LEFT_MOTOR_2).apply {
        setVoltageRampRate(DRIVE_RAMP_RATE)
        setCurrentLimit(DRIVE_CURRENT_LIMIT)
        EnableCurrentLimit(true)
    } + CANTalon(Talons.DRIVE_LEFT_MOTOR_3).apply {
        setVoltageRampRate(DRIVE_RAMP_RATE)
        setCurrentLimit(DRIVE_CURRENT_LIMIT)
        EnableCurrentLimit(true)
    } + CANTalon(Talons.DRIVE_LEFT_MOTOR_4).apply {
        setVoltageRampRate(DRIVE_RAMP_RATE)
        setCurrentLimit(DRIVE_CURRENT_LIMIT)
        EnableCurrentLimit(true)
    }

    private val rightMotors = CANTalon(Talons.DRIVE_RIGHT_MOTOR_1).apply {
        changeControlMode(SmartMotorController.TalonControlMode.PercentVbus)
        inverted = true
        setVoltageRampRate(DRIVE_RAMP_RATE)
        setCurrentLimit(DRIVE_CURRENT_LIMIT)
        EnableCurrentLimit(true)
    } + CANTalon(Talons.DRIVE_RIGHT_MOTOR_2).apply {
        setVoltageRampRate(DRIVE_RAMP_RATE)
        setCurrentLimit(DRIVE_CURRENT_LIMIT)
        EnableCurrentLimit(true)
    } + CANTalon(Talons.DRIVE_RIGHT_MOTOR_3).apply {
        setVoltageRampRate(DRIVE_RAMP_RATE)
        setCurrentLimit(DRIVE_CURRENT_LIMIT)
        EnableCurrentLimit(true)
    } + CANTalon(Talons.DRIVE_RIGHT_MOTOR_4).apply {
        setVoltageRampRate(DRIVE_RAMP_RATE)
        setCurrentLimit(DRIVE_CURRENT_LIMIT)
        EnableCurrentLimit(true)
    }

    val speed: Double get() = Math.abs(-leftMotors.encVelocity / EDGES_PER_100_MS + rightMotors.encVelocity / EDGES_PER_100_MS) / 2.0

    val leftDistance: Double get() = leftMotors.position
    val rightDistance: Double get() = rightMotors.position

    init {
        registerDefaultCommand(CommonPool, Command(this) {
            periodic(15) {
                val time = measureTimeFPGA {
                    drive(Driver.throttle, Driver.softTurn, Driver.hardTurn)
                }
                table.putNumber("Loop Timing", time)
            }
        })
    }

    fun drive(throttle: Double, softTurn: Double, hardTurn: Double) {
        var leftPower = throttle + (softTurn * Math.abs(throttle)) + hardTurn
        var rightPower = throttle - (softTurn * Math.abs(throttle)) - hardTurn

        val maxPower = Math.max(Math.abs(leftPower), Math.abs(rightPower))
        if (maxPower > 1) {
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
}
