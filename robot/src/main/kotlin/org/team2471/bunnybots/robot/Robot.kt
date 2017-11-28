package org.team2471.bunnybots.robot


import edu.wpi.first.wpilibj.IterativeRobot
import edu.wpi.first.wpilibj.networktables.NetworkTable
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.team2471.bunnybots.robot.subsystems.Arm
import org.team2471.bunnybots.robot.subsystems.Drive
import org.team2471.frc.lib.control.experimental.Command
import java.util.concurrent.TimeUnit

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
        NetworkTable.getTable("LEDController").putNumber("Amperage", RobotMap.pdp.getCurrent(11))
    }

    override fun autonomousInit() {
    }

    override fun autonomousPeriodic() {
    }

    override fun teleopInit() {
        runBlocking(CommonPool) { Arm.moveToPose(Arm.Pose.IDLE).invokeAndJoin(coroutineContext) }

        // pick up bucket
        Command(Arm) {
            try {
                Arm.animateToPose(Arm.Pose.GRAB_UPRIGHT_BUCKET, 1.0).invokeAndJoin(coroutineContext)
                Arm.intake = 1.0
                suspendUntil {
                    val current = Arm.intakeCurrent
                    println("Current: $current")
                    current > 20.0
                }
                println("Has bucket")
                Arm.intake = 0.0
                delay(2L, TimeUnit.SECONDS)
                Arm.animateToPose(Arm.Pose.DUMP, 2.0).invokeAndJoin(coroutineContext)
                suspendUntil { CoDriver.isSpitting }
                Arm.animateToPose(Arm.Pose.SPIT, 1.0).invokeAndJoin(coroutineContext)
                Arm.intake = -0.35
                delay(500L, TimeUnit.MILLISECONDS)
            } finally {
                Arm.intake = 0.0
            }
        }//.invoke(CommonPool)
    }

    override fun teleopPeriodic() {
    }

    override fun testInit() {
    }

    override fun testPeriodic() {
    }
}