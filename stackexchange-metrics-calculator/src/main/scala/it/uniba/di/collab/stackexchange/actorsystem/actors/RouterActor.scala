package it.uniba.di.collab.stackexchange.actorsystem.actors

import akka.actor.{ActorLogging, Props, Actor}
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}

/**
 * Created by francesco on 22/09/15.
 */
class RouterActor(numberOfWorkers: Int) extends Actor with ActorLogging{
  var router = {
    val routees = Vector.tabulate(numberOfWorkers) { n =>
      val r = context.actorOf(Props[Worker], s"Worker_$n")
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case message => this.router.route(message, sender())
  }
}
