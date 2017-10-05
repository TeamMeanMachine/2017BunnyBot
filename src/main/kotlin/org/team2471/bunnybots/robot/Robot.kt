package org.team2471.bunnybots.robot


import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.IterativeRobot
import edu.wpi.first.wpilibj.XboxController
import org.team2471.bunnybots.robot.subsystems.Drive

class Robot : IterativeRobot() {
    private val controller: XboxController = XboxController(0)
    override fun robotInit() {
        Drive
    }
    override fun teleopPeriodic() {
        Drive.drive(controller.getY(GenericHID.Hand.kLeft),controller.getX(GenericHID.Hand.kRight),
                controller.getTriggerAxis(GenericHID.Hand.kRight)-controller.getTriggerAxis(GenericHID.Hand.kLeft))
    }
}