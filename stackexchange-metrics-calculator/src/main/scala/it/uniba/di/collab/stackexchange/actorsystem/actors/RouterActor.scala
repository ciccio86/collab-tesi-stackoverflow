package it.uniba.di.collab.stackexchange.actorsystem.actors

import akka.actor.{Props, Actor}
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}

/**
 * Created by francesco on 22/09/15.
 */
class RouterActor(numberOfWorkers: Int) extends Actor {
  var router = {
    val routees = Vector.fill(numberOfWorkers) {
      val r = context.actorOf(Props[Worker])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case message => this.router.route(message, sender())
  }
}
