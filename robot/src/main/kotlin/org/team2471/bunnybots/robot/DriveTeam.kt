package org.team2471.bunnybots.robot

import edu.wpi.first.wpilibj.XboxController
import org.team2471.bunnybots.robot.subsystems.intakeBucketCommand
import org.team2471.bunnybots.robot.subsystems.intakeFallenBucketCommand
import org.team2471.bunnybots.robot.subsystems.preIntakeFallenBucketCommand
import org.team2471.frc.lib.control.experimental.runWhen
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

    init {
        intakeBucketCommand.runWhen { controller.aButton }
        preIntakeFallenBucketCommand.runWhen { controller.bButton }
        intakeFallenBucketCommand.runWhen { controller.yButton }
    }
}
