package org.team2471.bunnybots.robot


import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.IterativeRobot
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import org.team2471.bunnybots.robot.subsystems.Arm
import org.team2471.bunnybots.robot.subsystems.Drive
import org.team2471.frc.lib.control.experimental.Command
import org.team2471.frc.lib.control.experimental.CommandSystem

class Robot : IterativeRobot() {
    private lateinit var autoChooser: SendableChooser<Command>

    override fun robotInit() {
        CommandSystem.initCoroutineContext(newFixedThreadPoolContext(2, "Command Pool"))
        val alliance = DriverStation.getInstance().alliance
        when (alliance) {
            DriverStation.Alliance.Blue -> LEDController.send("blue")
            else -> LEDController.send("red")
        }
        LEDController.send("bounce")

        Drive
        Arm
        CoDriver

        autoChooser = SendableChooser()
        autoChooser.addDefault("Bolt", boltAuto)
        autoChooser.addObject("Buckets", bucketsAuto)
        SmartDashboard.putData("Auto Chooser", autoChooser)
    }

    override fun autonomousInit() {
        CommandSystem.isEnabled = true
        autoChooser.selected.invoke()

        LEDController.send("random")
    }

    override fun teleopInit() {
        CommandSystem.isEnabled = true
        LEDController.send("idle1")
        cancelArmCommand()
    }

    override fun teleopPeriodic() {
        if (DriverStation.getInstance().matchTime <= 30) {
            LEDController.send("fire")
        }
    }

    override fun testInit() {
        CommandSystem.isEnabled = true
        LEDController.send("red")
        LEDController.send("fire")
    }

    override fun disabledInit() {
        CommandSystem.isEnabled = false
        LEDController.send("bounce")
    }
}