package org.team2471.bunnybots.coprocessor

import io.scanse.sweep.SweepDevice
import io.scanse.sweep.SweepSample
import org.team2471.frc.lib.math.Point
import java.lang.Math.*

object Bucket {
    val circumference = 33
    val radius = circumference / 2
    val diameter = radius * 2
    val curvature = 1 / radius
}

val sweep = SweepDevice("COM3")

fun sqr(n: Double) = n * n

fun SweepSample.distanceTo(secondSample: SweepSample): Double {
    val a = this.distance.toDouble()
    val b = secondSample.distance.toDouble()
    val theta = toRadians(secondSample.angle.toDouble() - this.angle)
    return sqrt(sqr(a) + sqr(b) - 2 * a * b * cos(theta))
}

val CM_TO_FT = 30.48
fun SweepSample.toPoint(): Point = Point(Math.cos(toRadians(angle/1000.0)) * distance / CM_TO_FT,
        Math.sin(toRadians(angle/1000.0)) * distance / CM_TO_FT)

fun main(args: Array<String>) {
    sweep.motorSpeed = 1
    sweep.sampleRate = 1000
    sweep.startScanning()

    Visualizer

    for(scan in sweep.scans()) {
        Visualizer.update(cluster(scan.map { it.toPoint() }))
//        println("Objects: ${scan.size}")
//        scan.forEach { println(it.angle/1000.0) }
//        Thread.sleep(Long.MAX_VALUE)



    }
}