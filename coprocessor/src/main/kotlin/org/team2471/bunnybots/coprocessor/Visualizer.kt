package org.team2471.bunnybots.coprocessor

import javafx.scene.chart.XYChart
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.XYSeries
import org.knowm.xchart.style.Styler
import org.team2471.frc.lib.math.Point2D

fun visualize(clusterMap: Map<Int, List<Point2D>>){

    val chart = XYChartBuilder().width(600).height(500)
            .title("Blobs").xAxisTitle("X").yAxisTitle("Y").build()

    chart.styler.defaultSeriesRenderStyle = XYSeries.XYSeriesRenderStyle.Scatter
    chart.styler.isChartTitleVisible = false
    chart.styler.legendPosition = Styler.LegendPosition.InsideSW
    chart.styler.markerSize = 16

}


