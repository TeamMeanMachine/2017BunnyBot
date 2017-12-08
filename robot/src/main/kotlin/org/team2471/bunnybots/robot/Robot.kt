package org.team2471.bunnybots.robot


import edu.wpi.first.wpilibj.DriverStation
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
    val ds = DriverStation.getInstance().alliance
    override fun robotInit() {
        CommandSystem.initCoroutineContext(newFixedThreadPoolContext(2, "Command Pool"))
        if (ds == DriverStation.Alliance.Red) {
            LEDController.write("red")
        }

        else if (ds == DriverStation.Alliance.Blue){
            LEDController.write("blue")
        }
            LEDController.write("bounce")

        Drive
        Arm
        CoDriver
    }

    override fun robotPeriodic() {
        NetworkTable.getTable("LEDController").putNumber("Amperage", RobotMap.pdp.getCurrent(11))
        if (ds == DriverStation.Alliance.Red) {
            LEDController.write("red")
        }

        else if (ds == DriverStation.Alliance.Blue){
            LEDController.write("blue")
        }

        LEDController.write("bounce")
    }

    override fun autonomousInit() {
        CommandSystem.isEnabled = true
        SimpleAuto()
        LEDController.write("random")
    }

    override fun autonomousPeriodic() {
    }

    override fun teleopInit() {
        CommandSystem.isEnabled = true
        LEDController.write("idle1")
    }

    override fun teleopPeriodic() {
    }

    override fun testInit() {
        CommandSystem.isEnabled = true
        LEDController.write("idle2")

    }

    override fun testPeriodic() {
    }

    override fun disabledInit() {
        CommandSystem.isEnabled = false
    }
}