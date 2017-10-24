package org.team2471.bunnybots.coprocessor

import io.scanse.sweep.SweepDevice

val sweep = SweepDevice("/dev/ttyUSB0")

fun main(args: Array<String>) {
    for(scan in sweep.scans()) {
        // do stuff with scan
    }
}