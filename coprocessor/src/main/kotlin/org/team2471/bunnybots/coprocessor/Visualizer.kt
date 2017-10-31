package org.team2471.bunnybots.coprocessor

import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.XYSeries
import org.knowm.xchart.style.Styler
import org.team2471.frc.lib.math.Point

fun visualize(clusterMap: Map<Int, List<Point>>){

    val chart = XYChartBuilder().width(1080).height(1000)
            .title("Blobs").xAxisTitle("X").yAxisTitle("Y").build()

    chart.styler.defaultSeriesRenderStyle = XYSeries.XYSeriesRenderStyle.Scatter
    chart.styler.isChartTitleVisible = false
    chart.styler.legendPosition = Styler.LegendPosition.InsideSW
    chart.styler.markerSize = 6

    clusterMap.forEach {cluster, points ->
        val label = if (cluster == -1) "Noise" else "Cluster $cluster"
        chart.addSeries(label, points.map { it.x }.toDoubleArray(), points.map { it.y }.toDoubleArray())
    }
    SwingWrapper(chart).displayChart()

}


