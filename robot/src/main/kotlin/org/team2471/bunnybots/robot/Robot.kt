package org.team2471.bunnybots.robot


import edu.wpi.first.wpilibj.IterativeRobot
import org.team2471.bunnybots.robot.subsystems.Arm
import org.team2471.frc.lib.control.command.Scheduler

class Robot : IterativeRobot() {
    override fun robotInit() {
        Arm
    }

    override fun robotPeriodic() {
        Scheduler.tick()
    }

    override fun autonomousInit() {
    }

    override fun autonomousPeriodic() {
    }

    override fun teleopInit() {
    }

    override fun teleopPeriodic() {
    }
}