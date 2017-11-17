package org.team2471.bunnybots.coprocessor

import jsat.SimpleDataSet
import jsat.classifiers.DataPoint
import jsat.clustering.DBSCAN
import jsat.linear.DenseVector
import org.team2471.frc.lib.math.Point
import java.util.*


var minPoints = 4
var epsilon = 0.75

fun cluster(points: List<Point>): Map<Int, List<Point>> {
    val dbScan = DBSCAN()

    val dataSet = SimpleDataSet(points.map { DataPoint(DenseVector.toDenseVec(it.x, it.y)) })

    val designations = dbScan.cluster(dataSet, epsilon, minPoints, IntArray(points.size))

    val result = HashMap<Int, MutableList<Point>>()

    designations.forEachIndexed { index, cluster ->
        val list = result[cluster]
        if (list == null) result[cluster] = arrayListOf(points[index])
        else list.add(points[index])
    }

    return result

}

//data class BucketResult(val center: Point, )

fun bucketRating(points: List<Point>): Double {
    val midPoint = points.reduce { acc, point -> acc + point } / points.size.toDouble()

    val averageRadius = points.map { point -> point.distance(midPoint) }.average()

    return if (averageRadius > 0.2 && averageRadius < 0.35) {
        1.0
    } else {
        0.0
    }
}