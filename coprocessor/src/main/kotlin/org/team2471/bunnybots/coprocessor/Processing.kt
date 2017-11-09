package org.team2471.bunnybots.coprocessor

import jsat.SimpleDataSet
import jsat.classifiers.DataPoint
import jsat.clustering.HDBSCAN
import jsat.linear.DenseVector
import org.team2471.frc.lib.math.Point
import java.util.*


fun cluster(points: List<Point>): Map<Int, List<Point>> {
    val hdbScan = HDBSCAN()
    hdbScan.minPoints = 10
    hdbScan.minClusterSize = 3

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