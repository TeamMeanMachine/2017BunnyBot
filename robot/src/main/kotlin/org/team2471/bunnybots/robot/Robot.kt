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

    companion object {
     var lastCommand : String = ""
    }
    fun sendCommand(command : String){
        if (lastCommand != command){
            LEDController.write(command)
        }
    }
    override fun robotInit() {
        CommandSystem.initCoroutineContext(newFixedThreadPoolContext(2, "Command Pool"))
        val ds = DriverStation.getInstance().alliance
        if (ds == DriverStation.Alliance.Red) {
            sendCommand("red")
        }
        else if (ds == DriverStation.Alliance.Blue){
            sendCommand("blue")
        }
        else{
            sendCommand("red")
        }
        sendCommand("bounce")

        Drive
        Arm
        CoDriver
    }

    override fun robotPeriodic() {


    }


    override fun autonomousInit() {
        CommandSystem.isEnabled = true
        SimpleAuto()
        sendCommand("random")
    }

    override fun autonomousPeriodic() {
    }

    override fun teleopInit() {
        CommandSystem.isEnabled = true
        sendCommand("idle1")

    }

    override fun teleopPeriodic() {
        if (DriverStation.getInstance().matchTime <= 30) {
            sendCommand("fire")

        }
    }

    override fun testInit() {
        CommandSystem.isEnabled = true
        sendCommand("red")
        sendCommand("fire")

    }

    override fun testPeriodic() {
    }

    override fun disabledInit() {
        CommandSystem.isEnabled = false
        sendCommand("bounce")
    }
}