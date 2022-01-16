package com.avtelma.backblelogger.rawparser.core.algorithm.enums

enum class EventsJsonSelector(eventID : String) {
          Stop("Stop"),
           Gas("Gas"),
        Breaks("Breaks"),
      LeftTurn("Left Turn"),
     RightTurn("Right Turn"),
      Obstacle("Obstacle")
}

//Stop(1),
//Gas(2),
//Breaks(3),
//LeftTurn(4),
//RightTurn(5),
//Obstacle(6)