package org.team2471.bunnybots.robot.subsystems

import com.ctre.MotorControl.CANTalon
import com.ctre.MotorControl.SmartMotorController
import edu.wpi.first.wpilibj.PIDController
import org.team2471.bunnybots.robot.RobotMap
import org.team2471.frc.lib.sensors.Magnepot


object Arm {
    private val shoulderMotorMaster = CANTalon(RobotMap.Talons.SHOULDER_MOTOR_1)
    private val shoulderMotorSlave = CANTalon(RobotMap.Talons.SHOULDER_MOTOR_2)
    private val wristMotor = CANTalon(RobotMap.Talons.WRIST_MOTOR)
    private val intakeMotor = CANTalon(RobotMap.Talons.INTAKE_MOTOR)
    private val wristEncoder = Magnepot(0)
    private val shoulderEncoder = Magnepot(0)
    val shoulderController = PIDController(0.0, 0.0,0.0,shoulderEncoder, shoulderMotorMaster)
    val wristController = PIDController(0.0,0.0,0.0, wristEncoder, wristMotor)
    init {
        shoulderMotorSlave.changeControlMode(SmartMotorController.TalonControlMode.Follower)
        shoulderController.setpoint = 0.0
        wristController.setpoint = 0.0
        shoulderController.enable()
        wristController.enable()
    }
    var intake: Double
        get() = intakeMotor.get()
        set(speed) = intakeMotor.set(speed)

    var shoulderAngle: Double
        get() = shoulderEncoder.pidGet()
        set(angle) {
            shoulderController.setpoint = angle
        }
    var wristAngle: Double
        get() = wristEncoder.pidGet()
        set(angle) {
            shoulderController.setpoint = angle
        }
    val hasBucket: Boolean
        get() = RobotMap.pdp.getCurrent(0) > 20
}