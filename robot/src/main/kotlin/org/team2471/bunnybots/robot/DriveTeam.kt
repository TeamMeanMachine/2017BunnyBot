package org.team2471.bunnybots.robot

import edu.wpi.first.wpilibj.XboxController
import org.team2471.frc.lib.util.deadband
import org.team2471.frc.lib.util.squaredWithSign

object Driver {
    private val controller = XboxController(0)

    val throttle: Double
        get() = -controller.getRawAxis(1)
                .deadband(0.2)
                .squaredWithSign()

    val softTurn: Double
        get() = controller.getRawAxis(4)

    val hardTurn: Double
        get() = -controller.getRawAxis(2) + controller.getRawAxis(3)
}

object CoDriver {
    private val controller = XboxController(1)
}
