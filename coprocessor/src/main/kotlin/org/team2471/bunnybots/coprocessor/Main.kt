package org.team2471.bunnybots.coprocessor

import io.scanse.sweep.SweepDevice
import io.scanse.sweep.SweepSample
import javafx.scene.chart.Axis
import javafx.scene.chart.NumberAxis
import org.team2471.frc.lib.math.Point
import java.lang.Math.*
import javafx.scene.chart.ScatterChart



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

    for(scan in sweep.scans()) {
        visualize2(cluster(scan.map { it.toPoint() }))
        println("Objects: ${scan.size}")
//        scan.forEach { println(it.angle/1000.0) }
//        Thread.sleep(Long.MAX_VALUE)



    }
}