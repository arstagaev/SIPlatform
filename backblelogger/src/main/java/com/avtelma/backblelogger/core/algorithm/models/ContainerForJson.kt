package com.avtelma.avtsiplatformv3.core.algorithm.models

data class ContainerForJson(
    val time : Float,
    var Breaks_Power: Int, var Gas_Power : Int,var Turn_Power : Int,var Obstacle_Power: Int,
     //      BLACK               RED                  GREEN BLUE            ORANGE
    var Duration :Int
)
