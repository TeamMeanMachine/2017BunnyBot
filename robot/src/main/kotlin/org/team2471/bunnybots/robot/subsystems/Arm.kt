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

    private const val SHOULDER_SCALE_TO_DEGREES = 90.0 / 250.0
    private const val SHOULDER_OFFSET = 170 // in SRX units
    private const val WRIST_GEAR_RATIO = 48.0/18.0

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
        setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        setPID(0.75, 0.0, 0.0)
        position = 0.0
        enable()
    }

    private val intakeMotor = CANTalon(RobotMap.Talons.ARM_INTAKE_MOTOR).apply {
        setVoltageRampRate(72.0)
    }

    var intake: Double
        get() = intakeMotor.get()
        set(speed) = intakeMotor.set(speed)

    var shoulderAngle: Double
        get() = shoulderUnitsToDegrees(shoulderMotors.position)
        set(value) {
            shoulderMotors.setpoint = degreesToShoulderUnits(value)
            table.putNumber("Shoulder Setpoint", value)
            table.putNumber("Shoulder Error", shoulderError)
            table.putNumber("Shoulder Position", shoulderAngle)
            table.putNumber("Shoulder Output", shoulderMotors.outputVoltage)
        }

    val shoulderError get() = shoulderAngle - shoulderUnitsToDegrees(shoulderMotors.setpoint)

    private var wristAngle: Double
        get() = wristUnitsToDegrees(wristMotor.position)
        set(value) {
            val srxAngle = degreesToWristUnits(value)
            wristMotor.setpoint = srxAngle

            table.putNumber("Wrist Setpoint", value)
            table.putNumber("Wrist Error", wristError)
            table.putNumber("Wrist Position", wristAngle)
            table.putNumber("Wrist Output", wristMotor.outputVoltage)
        }

    val wristError get() = wristAngle - wristUnitsToDegrees(wristMotor.setpoint)

    val intakeCurrent get() = RobotMap.pdp.getCurrent(RobotMap.Talons.ARM_INTAKE_MOTOR)
    val hasBucket: Boolean
        get() = intakeCurrent > 12.5


    init {
        registerDefaultCommand(CommonPool, Command(this) {
            try {
                table.putBoolean("Default Command running", true)
                periodic {
                    intake = CoDriver.intake
                    shoulderAngle = CoDriver.shoulder * 60 + 90
                    wristAngle = CoDriver.wrist * 180
                    table.putNumber("Intake Current", intakeCurrent)
                }
            } finally {
                table.putBoolean("Default Command running", false)
            }
        })
    }

    // conversion functions
    private fun degreesToWristUnits(angle: Double) = angle / 360 * WRIST_GEAR_RATIO

    private fun wristUnitsToDegrees(angle: Double) = angle / WRIST_GEAR_RATIO * 360

    private fun degreesToShoulderUnits(angle: Double) = angle / SHOULDER_SCALE_TO_DEGREES + SHOULDER_OFFSET

    private fun shoulderUnitsToDegrees(angle: Double) = (angle - SHOULDER_OFFSET) * SHOULDER_SCALE_TO_DEGREES

    fun animateToPose(pose: Pose, time: Double) = Command(this) {
        val shoulderCurve = MotionCurve()
        val wristCurve = MotionCurve()
        shoulderCurve.storeValue(0.0, shoulderAngle)
        wristCurve.storeValue(0.0, wristAngle)
        shoulderCurve.storeValue(time, pose.shoulderAngle)
        wristCurve.storeValue(time, pose.wristAngle)

        periodic(condition = { elapsedTimeSeconds < time }) {
            val currentTime = elapsedTimeSeconds
            shoulderAngle = shoulderCurve.getValue(currentTime)
            wristAngle = wristCurve.getValue(currentTime)
        }

        shoulderAngle = pose.shoulderAngle
        wristAngle = pose.wristAngle

        suspendUntil { Math.abs(shoulderError) < 3.0 && Math.abs(wristError) < 3.0 }
    }

    fun moveToPose(pose: Pose) = Command(this) {
        table.putBoolean("moveToPose running", true)
        shoulderAngle = pose.shoulderAngle
        wristAngle = pose.wristAngle

        println("Blocking to $pose")
        suspendUntil { Math.abs(shoulderError) < 3.0 && Math.abs(wristError) < 3.0 }
        println("Completed")

        table.putBoolean("moveToPose running", false)
    }

    // poses
    enum class Pose(val shoulderAngle: Double, val wristAngle: Double) {

        IDLE(0.0, 0.0),
        DUMP(5.0, 40.0),
        SPIT(90.0, 90.0),
        GRAB_UPRIGHT_BUCKET(-5.0, 175.0),
        PRE_GRAB_FALLEN_BUCKET(60.0, -30.0),
        GRAB_FALLEN_BUCKET(40.0, -55.0);

        override fun toString(): String = "${super.toString()}(shoulderAngle=$shoulderAngle, wristAngle=$wristAngle)"
    }

}
