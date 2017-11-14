package org.team2471.bunnybots.coprocessor

import jsat.SimpleDataSet
import jsat.classifiers.DataPoint
import jsat.clustering.HDBSCAN
import jsat.linear.DenseVector
import jsat.linear.distancemetrics.EuclideanDistance
import jsat.linear.distancemetrics.SquaredEuclideanDistance
import org.team2471.frc.lib.math.Point
import java.util.*


var minPoints = 10
var minClusterSize = 3

fun cluster(points: List<Point>): Map<Int, List<Point>> {
    val hdbScan = HDBSCAN(minPoints)
    hdbScan.minClusterSize = minClusterSize

    val dataSet = SimpleDataSet(points.map { DataPoint(DenseVector.toDenseVec(it.x, it.y)) } )

    val designations = try {
        hdbScan.cluster(dataSet, IntArray(points.size))
    } catch(e: Exception) {
        println(e.message)
        IntArray(points.size) { -1 }
    }

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


    return 0.0
}