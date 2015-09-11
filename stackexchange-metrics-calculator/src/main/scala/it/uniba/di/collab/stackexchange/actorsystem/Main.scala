package it.uniba.di.collab.stackexchange.actorsystem

import akka.actor.{Props, ActorSystem}
import it.uniba.di.collab.stackexchange.actorsystem.actors.Master
import it.uniba.di.collab.stackexchange.actorsystem.messages.Messages.Work

object Main {

  def main (args: Array[String]) {
    val system = ActorSystem("System")
    val mainActor = system.actorOf(Props(new Master))
    mainActor ! Work
  }
}
