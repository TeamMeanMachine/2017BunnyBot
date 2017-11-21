package org.team2471.bunnybots.test

import com.ctre.MotorControl.CANTalon
import edu.wpi.first.wpilibj.IterativeRobot
import edu.wpi.first.wpilibj.SampleRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.buttons.JoystickButton
import org.team2471.bunnybots.robot.RobotMap.Talons

class MotorTest : IterativeRobot() {
    lateinit var controller: XboxController
    lateinit var leftMotor1: CANTalon
    lateinit var leftMotor2: CANTalon
    lateinit var leftMotor3: CANTalon
    lateinit var leftMotor4: CANTalon
    lateinit var rightMotor1: CANTalon
    lateinit var rightMotor2: CANTalon
    lateinit var rightMotor3: CANTalon
    lateinit var rightMotor4: CANTalon

    override fun robotInit() {
        controller = XboxController(0)

        leftMotor1 = CANTalon(Talons.DRIVE_LEFT_MOTOR_1)
        leftMotor2 = CANTalon(Talons.DRIVE_LEFT_MOTOR_2)
        leftMotor3 = CANTalon(Talons.DRIVE_LEFT_MOTOR_3)
        leftMotor4 = CANTalon(Talons.DRIVE_LEFT_MOTOR_4)

        rightMotor1 = CANTalon(Talons.DRIVE_RIGHT_MOTOR_1)
        rightMotor2 = CANTalon(Talons.DRIVE_RIGHT_MOTOR_2)
        rightMotor3 = CANTalon(Talons.DRIVE_RIGHT_MOTOR_3)
        rightMotor4 = CANTalon(Talons.DRIVE_RIGHT_MOTOR_4)
    }

    override fun teleopPeriodic() {
        leftMotor1.set(if(controller.aButton) 0.8 else 0.0)
        leftMotor2.set(if(controller.bButton) 0.8 else 0.0)
        leftMotor3.set(if(controller.xButton) 0.8 else 0.0)
        leftMotor4.set(if(controller.yButton) 0.8 else 0.0)
    }

    override fun testPeriodic() {
        rightMotor1.set(if(controller.aButton) 0.8 else 0.0)
        rightMotor2.set(if(controller.bButton) 0.8 else 0.0)
        rightMotor3.set(if(controller.xButton) 0.8 else 0.0)
        rightMotor4.set(if(controller.yButton) 0.8 else 0.0)
    }
}

