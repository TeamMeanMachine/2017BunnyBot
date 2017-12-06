package org.team2471.bunnybots.robot


import edu.wpi.first.wpilibj.IterativeRobot
import edu.wpi.first.wpilibj.networktables.NetworkTable
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.runBlocking
import org.team2471.bunnybots.robot.subsystems.Arm
import org.team2471.bunnybots.robot.subsystems.Drive
import org.team2471.frc.lib.control.experimental.Command
import org.team2471.frc.lib.control.experimental.CommandSystem
import java.util.concurrent.TimeUnit

class Robot : IterativeRobot() {
    override fun robotInit() {
        CommandSystem.initCoroutineContext(newFixedThreadPoolContext(2, "Command Pool"))

        Drive
        Arm
        CoDriver
    }

    override fun robotPeriodic() {
        NetworkTable.getTable("LEDController").putNumber("Amperage", RobotMap.pdp.getCurrent(11))
    }

    override fun autonomousInit() {
        CommandSystem.isEnabled = true
        SimpleAuto()
    }

    override fun autonomousPeriodic() {
    }

    override fun teleopInit() {
        CommandSystem.isEnabled = true
    }

    override fun teleopPeriodic() {
    }

    override fun testInit() {
        CommandSystem.isEnabled = true
    }

    override fun testPeriodic() {
    }

    override fun disabledInit() {
        CommandSystem.isEnabled = false
    }
}