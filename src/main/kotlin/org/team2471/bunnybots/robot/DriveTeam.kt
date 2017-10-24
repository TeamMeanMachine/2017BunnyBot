package org.team2471.bunnybots.robot

import edu.wpi.first.wpilibj.XboxController

object Driver {
    private val controller = XboxController(0)

    val throttle: Double
        get() = controller.getRawAxis(1)

    val softTurn: Double
        get() = controller.getRawAxis(4)

    val hardTurn: Double
        get() = -controller.getRawAxis(3) + controller.getRawAxis(4)
}

object CoDriver {
    private val controller = XboxController(1)
}
