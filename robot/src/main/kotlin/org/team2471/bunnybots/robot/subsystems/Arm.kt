package org.team2471.bunnybots.robot.subsystems

import com.ctre.MotorControl.CANTalon
import com.ctre.MotorControl.SmartMotorController
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.team2471.bunnybots.robot.RobotMap
import org.team2471.frc.lib.control.experimental.Command
import org.team2471.frc.lib.control.plus
import org.team2471.frc.lib.motion_profiling.MotionCurve
import org.team2471.frc.lib.motion_profiling.MotionProfileAnimation
import javax.swing.undo.CannotRedoException


object Arm {
    private val shoulderMotors = CANTalon(RobotMap.Talons.ARM_SHOULDER_MOTOR_1).apply {
        changeControlMode(SmartMotorController.TalonControlMode.Position)
        setFeedbackDevice(SmartMotorController.FeedbackDevice.AnalogPot)
    } + (CANTalon(RobotMap.Talons.ARM_SHOULDER_MOTOR_2).apply { inverted = true })
    private val wristMotor = CANTalon(RobotMap.Talons.ARM_WRIST_MOTOR).apply {
        changeControlMode(SmartMotorController.TalonControlMode.Position)
        setFeedbackDevice(SmartMotorController.FeedbackDevice.CtreMagEncoder_Absolute)
    }
    private val intakeMotor = CANTalon(RobotMap.Talons.ARM_INTAKE_MOTOR)

    var intake: Double
        get() = intakeMotor.get()
        set(speed) = intakeMotor.set(speed)

    var shoulderAngle: Double
        get() = shoulderMotors.pidGet()
        set(angle) {
            shoulderMotors.set(angle)
        }
    var wristAngle: Double
        get() = wristMotor.pidGet()
        set(angle) {
            wristMotor.set(angle)
        }
    val hasBucket: Boolean
        get() = RobotMap.pdp.getCurrent(0) > 20

    fun playAnimation(shoulderCurve:MotionCurve, wristCurve:MotionCurve) =  Command(this) {
        val length = Math.max(shoulderCurve.length, wristCurve.length)
        periodic(condition = { elapsedTimeSeconds < length }) {
            shoulderAngle = shoulderCurve.getValue(elapsedTimeSeconds)
            wristAngle = wristCurve.getValue(elapsedTimeSeconds)
        }
    }



}