package org.team2471.bunnybots.coprocessor

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.ScatterChart
import javafx.scene.chart.XYChart
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.XYSeries
import org.knowm.xchart.style.Styler
import org.team2471.frc.lib.math.Point
import java.awt.Color
import java.awt.Graphics
import java.lang.management.PlatformLoggingMXBean
import javax.swing.JFrame

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


fun visualize2(clusterMap: Map<Int, List<Point>>){
    val xAxis = NumberAxis(0.0, 10.0, 1.0)
    val yAxis = NumberAxis(-100.0, 500.0, 100.0)
    val sc = ScatterChart<Number, Number>(xAxis, yAxis)
    clusterMap.forEach { cluster, points ->
        val series = XYChart.Series<Number, Number>()
        series.name = if (cluster == -1) "Noise" else "Cluster $cluster"
        series.data.addAll(points.map { XYChart.Data<Number, Number>(it.x, it.y) })
        sc.data.add(series)

        val jFrame = JFrame("A very good name")
        val panel = JFXPanel()
        jFrame.add(panel)
        jFrame.setSize(400, 400)
        jFrame.isVisible = true
        jFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        Platform.runLater {
            panel.scene = Scene(sc)
        }
    }
}

object Visualizer : JFrame() {
    private var data: Map<Int, List<Point>>? = null
    private val size = 900
    private val FEET_TO_PIXELS = size/2/10
    private val colorMap = HashMap<Int, Color>()
    init {
        title = "LIDAR Visualizer"
        defaultCloseOperation = EXIT_ON_CLOSE

        setSize(size, size)
        isVisible = true
    }

    fun update(clusterMap: Map<Int, List<Point>>) {
        data = clusterMap
        repaint()
    }

    override fun paint(g: Graphics) {
        g.clearRect(0, 0, size, size)
        g.color = Color.RED
        g.fillOval(size/2, size/2, 10, 10)
        val clusterMap = data ?: return
        clusterMap.forEach { cluster, points ->
            g.color = colorMap.getOrPut(cluster) {
                Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat())
            }
            points.forEach { point ->
                g.fillOval((size/2) + Math.round(point.x * FEET_TO_PIXELS).toInt(),
                        (size/2)+ Math.round(point.y * FEET_TO_PIXELS).toInt(), 10, 10 )
            }
        }

    }


}

fun main(args: Array<String>) {
    Visualizer
}
