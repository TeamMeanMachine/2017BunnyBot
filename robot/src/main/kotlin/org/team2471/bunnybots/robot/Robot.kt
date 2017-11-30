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
import java.util.concurrent.TimeUnit

class Robot : IterativeRobot() {
    override fun robotInit() {
        Command.initCoroutineContext(newFixedThreadPoolContext(2, "Command Pool"))

        Drive
        Arm
        CoDriver
    }

    override fun robotPeriodic() {
        NetworkTable.getTable("LEDController").putNumber("Amperage", RobotMap.pdp.getCurrent(11))
    }

    override fun autonomousInit() {
    }

    override fun autonomousPeriodic() {
    }

    override fun teleopInit() {
    }

    override fun teleopPeriodic() {
    }

    override fun testInit() {
    }

    override fun testPeriodic() {
    }
}