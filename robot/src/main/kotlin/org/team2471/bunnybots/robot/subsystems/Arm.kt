package org.team2471.bunnybots.robot.subsystems

import com.ctre.CANTalon
import com.sun.org.apache.xpath.internal.operations.Bool
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.networktables.NetworkTable
import kotlinx.coroutines.experimental.delay
import org.team2471.bunnybots.plus
import org.team2471.bunnybots.robot.CoDriver
import org.team2471.bunnybots.robot.RobotMap
import org.team2471.frc.lib.control.experimental.Command
import org.team2471.frc.lib.control.experimental.periodic
import org.team2471.frc.lib.control.experimental.registerDefaultCommand
import org.team2471.frc.lib.control.experimental.suspendUntil
import org.team2471.frc.lib.motion_profiling.MotionCurve


object Arm {
    private val table = NetworkTable.getTable("Arm")

    private const val SHOULDER_SCALE_TO_DEGREES = 90.0 / 250.0
    private const val SHOULDER_OFFSET = 170 // in SRX units
    private const val WRIST_GEAR_RATIO = 48.0 / 18.0
    private const val WRIST_OFFSET = 0

    private val shoulderMotors = CANTalon(RobotMap.Talons.ARM_SHOULDER_MOTOR_1).apply {
        changeControlMode(CANTalon.TalonControlMode.Position)
        enableBrakeMode(true)
        setFeedbackDevice(CANTalon.FeedbackDevice.AnalogPot)
        reverseOutput(true)
        setPID(20.0, 0.0, 5.0)
        enable()
    } + (CANTalon(RobotMap.Talons.ARM_SHOULDER_MOTOR_2).apply {
        reverseOutput(true)
        enableBrakeMode(true)
    })

    private val wristMotor = CANTalon(RobotMap.Talons.ARM_WRIST_MOTOR).apply {
        changeControlMode(CANTalon.TalonControlMode.Position)
        enableBrakeMode(true)
        setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        setPID(1.5, 0.0, 0.1)
        println("Position Before: $position EncPosition: $pulseWidthPosition")
        position = 0.0

        println("Position After: $position EncPosition: $pulseWidthPosition")
        //position = (pulseWidthPosition + WRIST_OFFSET) / 4096.0
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
            var delta = value - wristAngle
            while (delta > 180) {
                delta -= 360
            }
            while (delta < -180) {
                delta += 360
            }
            val newWristAngle = wristAngle + delta
            //println("Wrist Setpoint: $(newWristAngle)")
            val srxAngle = degreesToWristUnits(newWristAngle)
            wristMotor.setpoint = srxAngle

            table.putNumber("Wrist Setpoint", newWristAngle)
            table.putNumber("Wrist Error", wristError)
            table.putNumber("Wrist Position", wristAngle)
            table.putNumber("Wrist Output", wristMotor.outputVoltage)
        }

    val wristError get() = wristAngle - wristUnitsToDegrees(wristMotor.setpoint)

    val intakeCurrent get() = RobotMap.pdp.getCurrent(RobotMap.Talons.ARM_INTAKE_MOTOR)
    val hasBucket: Boolean
        get() = intakeCurrent > 12.5


    init {
        registerDefaultCommand(Command("Arm Default" , this) {
            var wristAngle = wristAngle
            while(wristAngle > 180)
                wristAngle -= 360
            while (wristAngle < -180)
                wristAngle +=360
            val animation = if (wristAngle < -60 && wristAngle > -180){
                Animation(0.0 to currentPose, 0.5 to Pose(45.0, 90.0), 1.0 to Pose.IDLE)
            }
            else {
                Animation(0.0 to currentPose, 0.5 to Pose.IDLE)
            }
            Arm.playAnimation(animation)

            delay(Long.MAX_VALUE)
        })
    }

    // conversion functions
    private fun degreesToWristUnits(angle: Double) = angle / 360 * WRIST_GEAR_RATIO

    private fun wristUnitsToDegrees(angle: Double) = angle / WRIST_GEAR_RATIO * 360

    private fun degreesToShoulderUnits(angle: Double) = angle / SHOULDER_SCALE_TO_DEGREES + SHOULDER_OFFSET

    private fun shoulderUnitsToDegrees(angle: Double) = (angle - SHOULDER_OFFSET) * SHOULDER_SCALE_TO_DEGREES

    suspend fun playAnimation(animation: Animation) {
        val startTime = Timer.getFPGATimestamp()

        periodic(condition = { Timer.getFPGATimestamp() - startTime < animation.length }) {
            val time = Timer.getFPGATimestamp() - startTime
            shoulderAngle = animation.shoulderCurve.getValue(time)
            wristAngle = animation.wristCurve.getValue(time)
        }
        suspendUntil {
            val shoulderError = shoulderError
            val wristError = wristError
            //println("Errors: $shoulderError, $wristError")
            Math.abs(shoulderError) < 3 && Math.abs(wristError) < 3
        }
    }

    // poses
    class Pose(val shoulderAngle: Double, val wristAngle: Double) {
        companion object {

            val IDLE = Pose(0.0, 0.0)
            val DUMP = Pose(5.0, 50.0)
            val SPIT = Pose(70.0, 90.0)
            val GRAB_UPRIGHT_BUCKET = Pose(0.0, 180.0)
            val GRAB_UPRIGHT_MID = Pose(35.0, -75.0)
            val PRE_GRAB_FALLEN_BUCKET = Pose(75.0, -50.0)
            val GRAB_FALLEN_BUCKET = Pose(35.0, -55.0)
            val FALLEN_BUCKET_MID = Pose(50.0, 170.0)
        }

//        operator fun equals(pose: Pose): Boolean {
//            return Math.abs(shoulderAngle - pose.shoulderAngle)< 3.0 && Math.abs(wristAngle - pose.wristAngle)< 3.0
//        }
    }
    val currentPose get() = Pose(shoulderAngle, wristAngle)

    class Animation(vararg keyframes: Pair<Double, Pose>) {
        companion object {
            val IDLE_TO_GRAB_UPRIGHT_BUCKET = Animation(0.0 to Pose.IDLE, 0.5 to Pose.GRAB_UPRIGHT_MID, 1.0 to Pose.GRAB_UPRIGHT_BUCKET)
            val GRAB_UPRIGHT_BUCKET_TO_DUMP = Animation(0.0 to Pose.GRAB_UPRIGHT_BUCKET, 0.5 to Pose.DUMP)
            val DUMP_TO_SPIT = Animation(0.0 to Pose.DUMP, 0.25 to Pose.SPIT)
            val SPIT_TO_IDLE = Animation(0.0 to Pose.SPIT, 0.75 to Pose.IDLE)
            val IDLE_TO_PRE_GRAB_FALLEN_BUCKET = Animation(0.0 to Pose.IDLE, 0.5 to Pose.PRE_GRAB_FALLEN_BUCKET)
            val PRE_GRAB_TO_GRAB_FALLEN_BUCKET = Animation(0.0 to Pose.PRE_GRAB_FALLEN_BUCKET, 0.25 to Pose.GRAB_FALLEN_BUCKET)
            val GRAB_FALLEN_BUCKET_TO_DUMP = Animation(0.0 to Pose.GRAB_FALLEN_BUCKET, 0.375 to Pose.FALLEN_BUCKET_MID, 0.75 to Pose.DUMP)
            val BACK_TO_PRE_GRAB_FALLEN_BUCKET = Animation(0.0 to Pose.GRAB_FALLEN_BUCKET, 0.5 to Pose.PRE_GRAB_FALLEN_BUCKET)

        }

        val shoulderCurve: MotionCurve = MotionCurve().apply {
            keyframes.forEach { (time, pose) ->
                storeValue(time, pose.shoulderAngle)
            }
        }
        val wristCurve: MotionCurve = MotionCurve().apply {
            keyframes.forEach { (time, pose) ->
                var delta = pose.wristAngle - getValue(time)
                while (delta > 180) {
                    delta -= 360
                }
                while (delta < -180) {
                    delta += 360
                }
                storeValue(time, getValue(time) + delta)
            }
        }
        val length = shoulderCurve.length
    }
    val emergencyMode = Command("Arm Emergency Mode", Arm) {
        try {
            shoulderMotors.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
            wristMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
            periodic {
                shoulderMotors.set(CoDriver.shoulder)
                wristMotor.set(CoDriver.wrist)
            }
        } finally {
            shoulderMotors.changeControlMode(CANTalon.TalonControlMode.Position)
            wristMotor.changeControlMode(CANTalon.TalonControlMode.Position)
        }

    }
}
