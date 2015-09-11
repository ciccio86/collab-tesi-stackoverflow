package it.uniba.di.collab.stackexchange.actorsystem.actors

import akka.actor.Actor

class Worker(number: Int) extends Actor {

  def receive = {
    case str: String =>
      println( str + s" -- Actor #$number")
    case _           =>
      sender ! "Unknown message!"
  }
}
