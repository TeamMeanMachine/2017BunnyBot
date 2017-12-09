package org.team2471.bunnybots.robot

import edu.wpi.first.wpilibj.XboxController
import org.team2471.bunnybots.robot.subsystems.Arm
import org.team2471.bunnybots.robot.subsystems.Arm.emergencyMode
import org.team2471.bunnybots.robot.subsystems.Drive
import org.team2471.frc.lib.control.experimental.Command
import org.team2471.frc.lib.control.experimental.runWhen
import org.team2471.frc.lib.control.experimental.toggleWhen
import org.team2471.frc.lib.math.deadband
import org.team2471.frc.lib.math.squareWithSign

object Driver {
    private val controller = XboxController(0)

    val throttle: Double
        get() = -controller.getRawAxis(1)
                .deadband(0.2)
                .squareWithSign()

    val softTurn: Double
        get() = controller.getRawAxis(4)

    val hardTurn: Double
        get() = -controller.getRawAxis(2) + controller.getRawAxis(3)

    init {
        Command("Interrupt Drive", Drive) {  }.runWhen { controller.xButton }
    }
}

object CoDriver {
    private val controller = XboxController(1)

    val shoulder: Double
        get() = controller.getRawAxis(1)
                .deadband(0.2)
                .squareWithSign()

    val wrist: Double
        get() = controller.getRawAxis(5)
                .deadband(0.2)
                .squareWithSign()

    val intake: Double
        get() = -controller.getRawAxis(2) + controller.getRawAxis(3)

    val isSpitting get() = controller.getRawButton(3)

    val dipForFallenBucket get() = controller.yButton


    init {
        intakeBucketCommand.runWhen { controller.aButton }
        intakeFallenBucketCommand.runWhen { controller.bButton }
        cancelArmCommand.runWhen { controller.xButton }
        emergencyMode.toggleWhen { controller.backButton }
        Command("Increase Wrist Offset"){
            Arm.adjustWristOffest(5.0)
        }.runWhen { controller.pov == 0 }
        Command("Decrease Wrist Offset"){
            Arm.adjustWristOffest(-5.0)
        }.runWhen { controller.pov == 180 }
    }
}
