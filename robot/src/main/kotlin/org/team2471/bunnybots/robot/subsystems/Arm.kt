package org.team2471.bunnybots.robot.subsystems

import com.ctre.CANTalon
import edu.wpi.first.wpilibj.networktables.NetworkTable
import kotlinx.coroutines.experimental.CommonPool
import org.team2471.bunnybots.plus
import org.team2471.bunnybots.robot.CoDriver
import org.team2471.bunnybots.robot.RobotMap
import org.team2471.frc.lib.control.experimental.Command
import org.team2471.frc.lib.control.experimental.registerDefaultCommand
import org.team2471.frc.lib.motion_profiling.MotionCurve


object Arm {
    private val table = NetworkTable.getTable("Arm")

    private const val SHOULDER_SCALE_TO_DEGREES = 90.0/250.0
    private const val SHOULDER_OFFSET = 170 // in SRX units
    private const val WRIST_SCALE_TO_DEGREES = 90.0/0.658
    private var wristOffset = 0.108

    private val shoulderMotors = CANTalon(RobotMap.Talons.ARM_SHOULDER_MOTOR_1).apply {
        changeControlMode(CANTalon.TalonControlMode.Position)
        enableBrakeMode(true)
        setFeedbackDevice(CANTalon.FeedbackDevice.AnalogPot)
        reverseOutput(true)
        setPID(10.0, 0.0, 0.0)
        enable()
    } + (CANTalon(RobotMap.Talons.ARM_SHOULDER_MOTOR_2).apply {
        reverseOutput(true)
        enableBrakeMode(true)
    })
    private val wristMotor = CANTalon(RobotMap.Talons.ARM_WRIST_MOTOR).apply {
        changeControlMode(CANTalon.TalonControlMode.Position)
        enableBrakeMode(true)
        setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute)
        setPID(0.75, 0.0, 0.0)
        wristOffset = position
        enable()
    }
    private val intakeMotor = CANTalon(RobotMap.Talons.ARM_INTAKE_MOTOR)

    var intake: Double
        get() = intakeMotor.get()
        set(speed) = intakeMotor.set(speed)

    var shoulderAngle: Double
        get() = shoulderUnitsToDegrees(shoulderMotors.position)
        set(value) {
            shoulderMotors.setpoint = degreesToShoulderUnits(value)
        }

    var wristAngle: Double
        get() = wristUnitsToDegrees(wristMotor.position)
        set(value) {
            val srxAngle = degreesToWristUnits(value)
            wristMotor.setpoint = srxAngle
            table.putNumber("Wrist Setpoint", value)
            table.putNumber("Wrist Error", wristMotor.error)
            table.putNumber("Wrist Position", wristAngle)
            table.putNumber("Wrist Output", wristMotor.outputVoltage)
        }

    val hasBucket: Boolean
        get() = RobotMap.pdp.getCurrent(0) > 20

    fun playAnimation(shoulderCurve: MotionCurve, wristCurve: MotionCurve) = Command(this) {
        val length = Math.max(shoulderCurve.length, wristCurve.length)
        periodic(condition = { elapsedTimeSeconds < length }) {
            shoulderAngle = shoulderCurve.getValue(elapsedTimeSeconds)
            wristAngle = wristCurve.getValue(elapsedTimeSeconds)
        }
    }

    init {
        registerDefaultCommand(CommonPool, Command(this) {
           periodic {
               intake = CoDriver.intake
               shoulderAngle = CoDriver.shoulder * 60 + 90
               wristAngle = CoDriver.wrist * 180
               table.putNumber("Intake", CoDriver.intake)
               table.putNumber("Shoulder", CoDriver.shoulder)
               table.putNumber("Wrist", CoDriver.wrist)
           }
        })
    }

    // conversion functions
    private fun degreesToWristUnits(angle: Double) = angle / WRIST_SCALE_TO_DEGREES + wristOffset

    private fun degreesToShoulderUnits(angle: Double) = angle / SHOULDER_SCALE_TO_DEGREES + SHOULDER_OFFSET

    private fun wristUnitsToDegrees(angle: Double) = (angle - wristOffset) * WRIST_SCALE_TO_DEGREES

    private fun shoulderUnitsToDegrees(angle: Double) = (angle - SHOULDER_OFFSET) * SHOULDER_SCALE_TO_DEGREES
}

