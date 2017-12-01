package org.team2471.bunnybots.robot.subsystems

import com.ctre.CANTalon
import edu.wpi.first.wpilibj.networktables.NetworkTable
import kotlinx.coroutines.experimental.delay
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
        println( "Position Before: " + position + " EncPosition: " + pulseWidthPosition)
        position = 0.0

        println( "Position After: " + position + " EncPosition: " + pulseWidthPosition)
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
        registerDefaultCommand(Command(this) {
            try {
                table.putBoolean("Default Command running", true)
                periodic {
                    intake = CoDriver.intake
 //                   shoulderAngle = CoDriver.shoulder * 60 + 90
//                    wristAngle = CoDriver.wrist * 180
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

    fun playAnimation(animation: Animation) = Command(Arm) {
        periodic(condition = { elapsedTimeSeconds < animation.length }) {
            val time = elapsedTimeSeconds
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
    enum class Pose(val shoulderAngle: Double, val wristAngle: Double) {

        IDLE(0.0, 0.0),
        DUMP(5.0, 50.0),
        SPIT(70.0, 90.0),
        GRAB_UPRIGHT_BUCKET(0.0, 170.0),
        PRE_GRAB_FALLEN_BUCKET(75.0, -50.0),
        GRAB_FALLEN_BUCKET(35.0, -55.0),
        FALLEN_BUCKET_MID(50.0, 170.0)
//        override fun toString(): String = "${super.toString()}(shoulderAngle=$shoulderAngle, wristAngle=$wristAngle)"
    }

    enum class Animation(vararg keyframes: Pair<Double, Pose>) {
        IDLE_TO_GRAB_UPRIGHT_BUCKET(0.0 to Pose.IDLE, .75 to Pose.GRAB_FALLEN_BUCKET, 1.5 to Pose.GRAB_UPRIGHT_BUCKET),
        GRAB_UPRIGHT_BUCKET_TO_DUMP(0.0 to Pose.GRAB_UPRIGHT_BUCKET, 1.0 to Pose.DUMP),
        DUMP_TO_SPIT(0.0 to Pose.DUMP, 0.5 to Pose.SPIT),
        SPIT_TO_IDLE(0.0 to Pose.SPIT, 1.0 to Pose.IDLE),
        IDLE_TO_PRE_GRAB_FALLEN_BUCKET(0.0 to Pose.IDLE, .75 to Pose.PRE_GRAB_FALLEN_BUCKET),
        PRE_GRAB_TO_GRAB_FALLEN_BUCKET(0.0 to Pose.PRE_GRAB_FALLEN_BUCKET, 0.375 to Pose.GRAB_FALLEN_BUCKET),
        GRAB_FALLEN_BUCKET_TO_DUMP(0.0 to Pose.GRAB_FALLEN_BUCKET, 0.5 to Pose.FALLEN_BUCKET_MID, 1.0 to Pose.DUMP);

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
}

// commands
val intakeBucketCommand = Command(Arm) {
    try {
        fork(Arm.playAnimation(Arm.Animation.IDLE_TO_GRAB_UPRIGHT_BUCKET))
        Arm.intake = 1.0
        suspendUntil {
            val current = Arm.intakeCurrent
            //println("Current: $current")
            current > 30.0
        }
        println("Has bucket")
        Arm.intake = 0.0

        fork(Arm.playAnimation(Arm.Animation.GRAB_UPRIGHT_BUCKET_TO_DUMP))
//        delay(750)

        fork(Arm.playAnimation(Arm.Animation.DUMP_TO_SPIT))
        Arm.intake = -0.75
        delay(600)

        fork(Arm.playAnimation(Arm.Animation.SPIT_TO_IDLE))
    } finally {
        Arm.intake = 0.0
    }
}

val preIntakeFallenBucketCommand = Command(Arm) {
    try {
        fork(Arm.playAnimation(Arm.Animation.IDLE_TO_PRE_GRAB_FALLEN_BUCKET))
    } finally {
        Arm.intake = 0.0
    }
}

val intakeFallenBucketCommand = Command(Arm) {
    try {
        Arm.intake = 1.0
        fork(Arm.playAnimation(Arm.Animation.PRE_GRAB_TO_GRAB_FALLEN_BUCKET))

        suspendUntil {
            val current = Arm.intakeCurrent
            //println("Current: $current")
            current > 30.0
        }
        println("Has bucket")
        Arm.intake = 0.0

        fork(Arm.playAnimation(Arm.Animation.GRAB_FALLEN_BUCKET_TO_DUMP))
//        delay(750)

        fork(Arm.playAnimation(Arm.Animation.DUMP_TO_SPIT))
        Arm.intake = -0.75
        delay(600)

        fork(Arm.playAnimation(Arm.Animation.SPIT_TO_IDLE))
    } finally {
        Arm.intake = 0.0
    }
}