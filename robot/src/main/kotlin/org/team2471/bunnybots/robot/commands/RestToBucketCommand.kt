package org.team2471.bunnybots.robot.commands

import org.team2471.bunnybots.robot.subsystems.Arm
import org.team2471.frc.lib.motion_profiling.MotionProfileAnimation
import org.team2471.frc.lib.motion_profiling.MotionProfileCurve
import org.team2471.frc.lib.motion_profiling.PlayAnimationCommand


class RestToBucketCommand : PlayAnimationCommand(){
    var animation1 = MotionProfileAnimation()
    var shoulderCurve = MotionProfileCurve(Arm.shoulderController, animation1)
    var elbowCurve = MotionProfileCurve(Arm.wristController, animation1)

    init{
        requires(Arm)
        if (Arm.shoulderController.get()>40.0) {
            speed = 1.0
        } else{
            speed = -0.75
        }

        shoulderCurve.storeValue(0.0,30.0)//Move to a position...
        //IDK just filler values
        elbowCurve.storeValue(0.0,90.0)
        animation = animation1
    }

    override fun end(){

    }


}