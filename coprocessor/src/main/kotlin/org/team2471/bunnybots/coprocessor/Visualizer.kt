package org.team2471.bunnybots.coprocessor

import org.team2471.frc.lib.math.Point
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel

fun Double.roundTo(places: Int) = Math.round(this * Math.pow(10.0, places.toDouble())) / Math.pow(10.0, places.toDouble())

fun Graphics.fillCircle(x: Int, y: Int, radius: Int) {
    fillOval((radius/2) + x - radius/2,
            (radius/2)+ y - radius/2, radius, radius)

}

object Visualizer : JFrame() {

    private var data: Map<Int, List<Point>>? = null
    private val size = 900
    private val FEET_TO_PIXELS = size/2/18
    private val colorMap = HashMap<Int, Color>()

    private var frozen = false


    init {
        title = "LIDAR Visualizer"
        defaultCloseOperation = EXIT_ON_CLOSE

        setSize(size, size)
        isVisible = true

        Options
    }

    fun update(clusterMap: Map<Int, List<Point>>) {
        if(!frozen) data = clusterMap
        repaint()
    }

    override fun paint(g: Graphics) {
        g.clearRect(0, 0, size, size)
        g.color = Color.RED
        g.fillOval(size/2 - 30/2, size/2 - 30/2, 30, 30)
        val clusterMap = data ?: return
        clusterMap.forEach { cluster, points ->
            val midPoint = points.reduce { acc, point -> acc + point } / points.size.toDouble()


            g.color = colorMap.getOrPut(cluster) {
                Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat())
            }
            val radius = 10
            points.forEach { point ->
                g.fillOval((size/2) + Math.round(point.x * FEET_TO_PIXELS).toInt() - radius/2,
                        (size/2)+ Math.round(point.y * FEET_TO_PIXELS).toInt() - radius/2, radius, radius)
            }

            if(cluster == -1) return@forEach
            val bucketRating = bucketRating(points)

            with(midPoint) {
                g.color = Color.BLACK
                g.fillOval((size/2) + Math.round(x * FEET_TO_PIXELS).toInt() - radius/2,
                        (size/2)+ Math.round(y * FEET_TO_PIXELS).toInt() - radius/2, (radius * 1.5).toInt(), (radius * 1.5).toInt())

                g.fillOval((size/2) + Math.round(x * FEET_TO_PIXELS).toInt() - radius/2,
                        (size/2)+ Math.round(y * FEET_TO_PIXELS).toInt() - radius/2, (radius * 1.5).toInt(), (radius * 1.5).toInt())

                val boundingRadius = (points.first().distance(points.last()) * FEET_TO_PIXELS).toInt()
                g.color = Color.RED

                g.color = Color.BLACK
                g.drawString("Distance: ${distance(Point.ORIGIN).roundTo(2)}", (size/2) + x.toInt() * FEET_TO_PIXELS,
                        size/2 + y.toInt() * FEET_TO_PIXELS + 15)
                g.drawString("Points: ${points.size}", (size/2) + x.toInt() * FEET_TO_PIXELS,
                        size/2 + y.toInt() * FEET_TO_PIXELS + 30)
                g.drawString("Bucket Rating: $bucketRating", (size/2) + x.toInt() * FEET_TO_PIXELS,
                        size/2 + y.toInt() * FEET_TO_PIXELS + 45)

            }

        }
        g.color = Color.BLACK
        g.drawString("Min Points: $minPoints", 20, 60)
        g.drawString("Epsilon: $epsilon", 20, 80)
        g.drawString("Frozen: $frozen", 20, 100)
    }

    object Options : JFrame() {
        init {
            title = "Visualizer Options"
            defaultCloseOperation = EXIT_ON_CLOSE

            isVisible = true

            setSize(500, 500)

            val toolBarPanel = JPanel(GridLayout(5,5))

            toolBarPanel.add(JButton("Min Points +").apply {
                addActionListener {
                    minPoints++
                }
            })

            toolBarPanel.add(JButton("Min Points -").apply {
                addActionListener {
                    minPoints--
                }
            })

            toolBarPanel.add(JButton("Epsilon +").apply {
                addActionListener {
                    epsilon += 0.25
                }
            })

            toolBarPanel.add(JButton("Epsilon -").apply {
                addActionListener {
                    epsilon -= 0.25
                }
            })

            toolBarPanel.add(JButton("Freeze").apply {
                addActionListener {
                    Visualizer.frozen = !Visualizer.frozen
                }
            })

            add(toolBarPanel, BorderLayout.NORTH)
        }
    }

}

