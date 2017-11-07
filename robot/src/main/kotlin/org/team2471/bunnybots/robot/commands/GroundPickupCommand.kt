package org.team2471.bunnybots.robot.commands

import edu.wpi.first.wpilibj.command.Command
import org.team2471.bunnybots.robot.subsystems.Arm
import org.team2471.frc.lib.motion_profiling.MotionProfileAnimation
import org.team2471.frc.lib.motion_profiling.MotionProfileCurve
import org.team2471.frc.lib.motion_profiling.PlayAnimationCommand


class GroundPickupCommand : PlayAnimationCommand() {
    var animation1 = MotionProfileAnimation()
    var shoulderCurve = MotionProfileCurve(Arm.shoulderController, animation1)
    var wristCurve = MotionProfileCurve(Arm.wristController, animation1)


    init {
        requires(Arm)
        shoulderCurve.storeValue(0.0, 30.0)//Move into position
        wristCurve.storeValue(0.0, 90.0) //Once again, not actual values

        Arm.intake = 1000000.0//Suck 'em up
        animation = animation1

    }

}