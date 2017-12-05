package org.team2471.bunnybots.robot

import kotlinx.coroutines.experimental.delay
import org.team2471.bunnybots.robot.subsystems.Arm
import org.team2471.bunnybots.robot.subsystems.Drive
import org.team2471.frc.lib.control.experimental.Command
import org.team2471.frc.lib.control.experimental.suspendUntil


val SimpleAuto = Command("Simple Auto", Arm, Drive) {
    try {
        Drive.driveStraight(.2)
        repeat(3) {
            Arm.playAnimation((Arm.Animation.IDLE_TO_GRAB_UPRIGHT_BUCKET))
            Arm.intake = 1.0
            suspendUntil {
                val current = Arm.intakeCurrent
                println("Current: $current")
                current > AMPERAGE_LIMIT
            }
            println("Has bucket")
            Arm.intake = 0.0

            Arm.playAnimation(Arm.Animation.GRAB_UPRIGHT_BUCKET_TO_DUMP)
            Arm.playAnimation(Arm.Animation.DUMP_TO_SPIT)
            Arm.intake = -0.75
            delay(600)
            Arm.playAnimation(Arm.Animation.QUICK_RESET_GRAB_UPRIGHT_BUCKET)
        }
    } finally {
        Arm.intake = 0.0
        Drive.driveStraight(0.0)
    }
}