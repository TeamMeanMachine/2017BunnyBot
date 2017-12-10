package org.team2471.bunnybots.robot

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.team2471.bunnybots.robot.subsystems.Arm
import org.team2471.bunnybots.robot.subsystems.Drive
import org.team2471.frc.lib.control.experimental.Command
import org.team2471.frc.lib.control.experimental.suspendUntil

val SimpleAuto = Command("Simple Auto", Arm, Drive) {
    try {
        Arm.playAnimation(Arm.Animation.IDLE_TO_GRAB_UPRIGHT_BUCKET)

        repeat(3) {
            Arm.intake = 1.0
            Drive.driveDistance(2.5, 1.25)
            Arm.intake = 0.0

            Arm.playAnimation(Arm.Animation.GRAB_UPRIGHT_BUCKET_TO_SPIT)
            Arm.intake = -0.75

            delay(300)
            Arm.intake = 0.0
            Arm.playAnimation(Arm.Animation.QUICK_RESET_GRAB_UPRIGHT_BUCKET)
        }
        Arm.intake = 1.0

        Drive.driveDistance(17.5, 3.0, Drive.ShiftSetting.FORCE_HIGH)
        Arm.intake = 0.0
        Arm.playAnimation(Arm.Animation.GRAB_UPRIGHT_BUCKET_TO_DUMP)
        Arm.playAnimation(Arm.Animation.DUMP_TO_SPIT)
        delay(Long.MAX_VALUE)
    } finally {
        Arm.intake = 0.0
    }
}
val BoltAuto = Command("Bolt Auto", Arm, Drive) {
    val driveDistance = launch(coroutineContext) {
        Drive.driveDistance(51.5, 6.1, Drive.ShiftSetting.FORCE_HIGH)
    }

    val getBucket = launch(coroutineContext) {
        try {
            delay(2500)
            Arm.playAnimation(Arm.Animation.IDLE_TO_GRAB_UPRIGHT_BUCKET)
            Arm.intake = 1.0
            Arm.waitForBucket()
            Arm.intake = 0.0
        }finally {
            Arm.intake = 0.0
        }
    }
    suspendUntil {
        driveDistance.isCompleted || getBucket.isCompleted
    }
    driveDistance.cancel()
    getBucket.cancel()
    getBucket.join()
    Arm.playAnimation(Arm.Animation.GRAB_UPRIGHT_BUCKET_TO_DUMP)
    Arm.playAnimation(Arm.Animation.DUMP_TO_SPIT)
    delay(Long.MAX_VALUE)
}