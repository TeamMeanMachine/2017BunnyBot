package org.team2471.bunnybots.coprocessor

import org.team2471.frc.lib.math.Point
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JPanel

object Visualizer : JFrame() {
    private var data: Map<Int, List<Point>>? = null
    private val size = 900
    private val FEET_TO_PIXELS = size/2/14
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
            g.color = colorMap.getOrPut(cluster) {
                Color(Math.random().toFloat(), Math.random().toFloat(), Math.random().toFloat())
            }
            points.forEach { point ->
                val radius = 3
                g.fillOval((size/2) + Math.round(point.x * FEET_TO_PIXELS).toInt() - radius/2,
                        (size/2)+ Math.round(point.y * FEET_TO_PIXELS).toInt() - radius/2, 10, 10 )
            }
        }
        g.color = Color.BLACK
        g.drawString("Min Points: $minPoints", 20, 60)
        g.drawString("Min Cluster Size: $minClusterSize", 20, 80)
        g.drawString("Frozen: $frozen", 20, 100)
    }

    object Options : JFrame() {
        init {
            title = "Visualizer Options"
            defaultCloseOperation = EXIT_ON_CLOSE

            isVisible = true

            setSize(500, 500)

            val toolBarPanel = JPanel(GridLayout(5,5))

            toolBarPanel.add(JButton("+ Min Points").apply {
                addActionListener {
                    minPoints++
                }
            })

            toolBarPanel.add(JButton("- Min Points").apply {
                addActionListener {
                    minPoints--
                }
            })

            toolBarPanel.add(JButton("+ Min Cluster Size").apply {
                addActionListener {
                    minClusterSize++
                }
            })

            toolBarPanel.add(JButton("- Min Cluster Size").apply {
                addActionListener {
                    minClusterSize--
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

