package org.team2471.bunnybots.coprocessor

import jsat.SimpleDataSet
import jsat.classifiers.DataPoint
import jsat.clustering.HDBSCAN
import jsat.linear.DenseVector
import org.team2471.frc.lib.math.Point2D


fun cluster(points: List<Point2D>) {
    val hdbScan = HDBSCAN()
    hdbScan.minPoints = 3
    hdbScan.minClusterSize = 10

    val dataSet = SimpleDataSet(points.map { DataPoint(DenseVector.toDenseVec(it.x, it.y)) } )

    hdbScan.cluster(dataSet)

    dataSet.categories
}