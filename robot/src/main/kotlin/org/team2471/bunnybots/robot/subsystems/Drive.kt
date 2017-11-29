package org.team2471.bunnybots.robot.subsystems

import com.ctre.CANTalon
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.networktables.NetworkTable
import kotlinx.coroutines.experimental.CommonPool
import org.team2471.bunnybots.plus
import org.team2471.bunnybots.robot.Driver
import org.team2471.bunnybots.robot.RobotMap
import org.team2471.frc.lib.control.experimental.Command
import org.team2471.frc.lib.control.experimental.registerDefaultCommand
import org.team2471.bunnybots.robot.RobotMap.Talons as Talons


object Drive {
    private const val EDGES_PER_100_MS = 216 * 4.0 / 10.0
    private const val HIGH_SHIFTPOINT = 5.0
    private const val LOW_SHIFTPOINT = 4.0
    private const val CURRENT_LIMIT = 25
    private const val RAMP_RATE = 72.0

    private val table = NetworkTable.getTable("Drive")

    private val shifter = Solenoid(0)

    private val leftMotors = CANTalon(Talons.DRIVE_LEFT_MOTOR_1).apply {
        changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        setVoltageRampRate(RAMP_RATE)
        setCurrentLimit(CURRENT_LIMIT)
        EnableCurrentLimit(true)
    } + CANTalon(Talons.DRIVE_LEFT_MOTOR_2).apply {
        setVoltageRampRate(RAMP_RATE)
        setCurrentLimit(CURRENT_LIMIT)
        EnableCurrentLimit(true)
    } + CANTalon(Talons.DRIVE_LEFT_MOTOR_3).apply {
        setVoltageRampRate(RAMP_RATE)
        setCurrentLimit(CURRENT_LIMIT)
        EnableCurrentLimit(true)
    } + CANTalon(Talons.DRIVE_LEFT_MOTOR_4).apply {
        setVoltageRampRate(RAMP_RATE)
        setCurrentLimit(CURRENT_LIMIT)
        EnableCurrentLimit(true)
    }

    private val rightMotors = CANTalon(Talons.DRIVE_RIGHT_MOTOR_3).apply {
        changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        inverted = true
        setCurrentLimit(CURRENT_LIMIT)
        EnableCurrentLimit(true)
    } + CANTalon(Talons.DRIVE_RIGHT_MOTOR_2).apply {
        setCurrentLimit(CURRENT_LIMIT)
        EnableCurrentLimit(true)
    } + CANTalon(Talons.DRIVE_RIGHT_MOTOR_1).apply {
        setCurrentLimit(CURRENT_LIMIT)
        EnableCurrentLimit(true)
    } + CANTalon(Talons.DRIVE_RIGHT_MOTOR_4).apply {
        setCurrentLimit(CURRENT_LIMIT)
        EnableCurrentLimit(true)
    }

    val speed: Double get() = Math.abs(-leftMotors.encVelocity / EDGES_PER_100_MS + rightMotors.encVelocity / EDGES_PER_100_MS) / 2.0

    val leftDistance: Double get() = leftMotors.position
    val rightDistance: Double get() = rightMotors.position

    init {
        registerDefaultCommand(Command(this) {
            periodic(15) {
                drive(Driver.throttle, Driver.softTurn, Driver.hardTurn)

                val leftAmperage = listOf(Talons.DRIVE_LEFT_MOTOR_1, Talons.DRIVE_LEFT_MOTOR_2,
                        Talons.DRIVE_LEFT_MOTOR_3, Talons.DRIVE_LEFT_MOTOR_4).map { RobotMap.pdp.getCurrent(it) }.average()
                val rightAmperage = listOf(Talons.DRIVE_LEFT_MOTOR_1, Talons.DRIVE_LEFT_MOTOR_2,
                        Talons.DRIVE_LEFT_MOTOR_3, Talons.DRIVE_LEFT_MOTOR_4).map { RobotMap.pdp.getCurrent(it) }.average()

                table.putNumber("Left Side Average Amperage", leftAmperage)
                table.putNumber("Right Side Average Amperage", rightAmperage)
                table.putNumber("Left Side Raw Speed", leftMotors.encVelocity.toDouble())
                table.putNumber("Right Side Raw Speed", rightMotors.encVelocity.toDouble())
                table.putNumber("Left Side Output Voltage", leftMotors.outputVoltage)
                table.putNumber("Right Side Output Voltage", rightMotors.outputVoltage)
                table.putNumber("Speed", speed)
            }
        })
    }

    fun drive(throttle: Double, softTurn: Double, hardTurn: Double, shiftSetting: ShiftSetting = ShiftSetting.AUTOMATIC) {
        var leftPower = throttle + (softTurn * Math.abs(throttle)) + hardTurn
        var rightPower = throttle - (softTurn * Math.abs(throttle)) - hardTurn

        val maxPower = Math.max(Math.abs(leftPower), Math.abs(rightPower))
        if (maxPower > 1) {
            leftPower /= maxPower
            rightPower /= maxPower
        }

        // when the solenoid is extended the shifter is in low gear
        when(shiftSetting) {
            ShiftSetting.AUTOMATIC -> {
                val currentSpeed = speed
                if(currentSpeed > HIGH_SHIFTPOINT) shifter.set(false)
                else if(currentSpeed < LOW_SHIFTPOINT) shifter.set(true)
            }

            ShiftSetting.FORCE_HIGH -> shifter.set(false)
            ShiftSetting.FORCE_LOW -> shifter.set(true)
        }

        table.putBoolean("High Gear Engaged", !shifter.get())

        leftMotors.set(leftPower)
        rightMotors.set(rightPower)
    }

    fun driveStraight(throttle: Double, shiftSetting: ShiftSetting = ShiftSetting.AUTOMATIC) =
            drive(throttle, 0.0, 0.0, shiftSetting)

    enum class ShiftSetting {
        AUTOMATIC,
        FORCE_HIGH,
        FORCE_LOW
    }
}
