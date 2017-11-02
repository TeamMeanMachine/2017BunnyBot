package org.team2471.bunnybots.robot.commands

import org.team2471.bunnybots.robot.subsystems.Arm
import org.team2471.frc.lib.motion_profiling.MotionProfileAnimation
import org.team2471.frc.lib.motion_profiling.MotionProfileCurve
import org.team2471.frc.lib.motion_profiling.PlayAnimationCommand


class DumpBucketCommand : PlayAnimationCommand(){
    var animation1 = MotionProfileAnimation()
    var shoulderCurve = MotionProfileCurve(Arm.shoulderController, animation1)
    var wristCurve = MotionProfileCurve(Arm.wristController, animation1)

    init {
        requires(Arm)
        shoulderCurve.storeValue(0.0, 90.0)//move to position
        wristCurve.storeValue(0.0, 50.0)//not actual vals, i just have no idea what to set them to

        wristCurve.storeValue(1.0, 100.0)//Dump bucket

        shoulderCurve.storeValue(2.25, 130.0) //prepare to shoot bucket
        wristCurve.storeValue(2.25, 150.0)

        Arm.intake = -10000000000.0 //MEGA BUCKET MISSILE LAUNCHER
        animation = animation1

    }

}