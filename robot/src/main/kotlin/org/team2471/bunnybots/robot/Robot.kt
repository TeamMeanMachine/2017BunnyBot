package org.team2471.bunnybots.robot


import edu.wpi.first.wpilibj.IterativeRobot
import org.team2471.bunnybots.robot.subsystems.Arm
import org.team2471.bunnybots.robot.subsystems.Drive

class Robot : IterativeRobot() {
    init {
        // use 2 threads in CommonPool instead of 1
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "2")
    }

    override fun robotInit() {
        Drive
        Arm
    }

    override fun robotPeriodic() {
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