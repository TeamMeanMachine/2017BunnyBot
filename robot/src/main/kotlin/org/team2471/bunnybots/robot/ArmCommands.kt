package org.team2471.bunnybots.robot

import kotlinx.coroutines.experimental.delay
import org.team2471.bunnybots.robot.subsystems.Arm
import org.team2471.frc.lib.control.experimental.Command
import org.team2471.frc.lib.control.experimental.suspendUntil

// commands
const val AMPERAGE_LIMIT = 21.0
val intakeBucketCommand = Command("Intake Bucket", Arm) {
    try {
        Arm.playAnimation(Arm.Animation.IDLE_TO_GRAB_UPRIGHT_BUCKET)
        Arm.intake = 1.0
        suspendUntil {
            val current = Arm.intakeCurrent
            current > AMPERAGE_LIMIT
        }
        LEDController.send("grab")
        Arm.intake = 0.0

        Arm.playAnimation(Arm.Animation.GRAB_UPRIGHT_BUCKET_TO_DUMP)

        Arm.playAnimation(Arm.Animation.DUMP_TO_SPIT)
        Arm.intake = -1.0
        delay(600)

        Arm.playAnimation(Arm.Animation.SPIT_TO_IDLE)
    } finally {
        Arm.intake = 0.0
    }
}

val intakeFallenBucketCommand = Command("Intake Fallen Bucket", Arm) {
    try {
        Arm.playAnimation(Arm.Animation.IDLE_TO_PRE_GRAB_FALLEN_BUCKET)
        Arm.intake = 0.5
        suspendUntil { CoDriver.dipForFallenBucket }
        Arm.intake = 1.0
        Arm.playAnimation(Arm.Animation.PRE_GRAB_TO_GRAB_FALLEN_BUCKET)
        suspendUntil {
            if (Arm.intakeCurrent > AMPERAGE_LIMIT) return@suspendUntil true

            if (!CoDriver.dipForFallenBucket) {
                Arm.playAnimation(Arm.Animation.BACK_TO_PRE_GRAB_FALLEN_BUCKET)
                suspendUntil { CoDriver.dipForFallenBucket }
                Arm.playAnimation(Arm.Animation.PRE_GRAB_TO_GRAB_FALLEN_BUCKET)
            }
            false
        }
        delay(600)
        Arm.intake = 0.0

        Arm.playAnimation(Arm.Animation.GRAB_FALLEN_BUCKET_TO_DUMP)

        Arm.playAnimation(Arm.Animation.DUMP_TO_SPIT)
        Arm.intake = -0.75
        delay(600)

        Arm.playAnimation(Arm.Animation.SPIT_TO_IDLE)
    } finally {
        Arm.intake = 0.0
    }
}

val cancelArmCommand = Command("Interrupt Arm", Arm) {
    try {
        Arm.intake = -1.0
        delay(700)
    } finally {
        Arm.intake = 0.0
    }
}

