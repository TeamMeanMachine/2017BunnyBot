package org.team2471.bunnybots.coprocessor

import org.team2471.frc.lib.math.Point
import java.awt.Color
import java.awt.Graphics
import javax.swing.JFrame

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
        g.fillOval(size/2 - 30/2, size/2 - 30/2, 30, 30)
        val clusterMap = data ?: return
        clusterMap.forEach { cluster, points ->
            g.color = colorMap.getOrPut(cluster) {
                Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat())
            }
            points.forEach { point ->
                val radius = 10
                g.fillOval((size/2) + Math.round(point.x * FEET_TO_PIXELS).toInt() - radius/2,
                        (size/2)+ Math.round(point.y * FEET_TO_PIXELS).toInt() - radius/2, 10, 10 )
            }
        }

    }


}

fun main(args: Array<String>) {
    Visualizer
}
