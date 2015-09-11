package it.uniba.di.collab.stackexchange.actorsystem.actors

import akka.actor.{Props, Actor}
import akka.routing.{ ActorRefRoutee, RoundRobinRoutingLogic, Router }
import it.uniba.di.collab.stackexchange.actorsystem.messages.Messages._

class Master extends Actor {
  var router = {
    val routees = Vector.tabulate(5) { n =>
      val r = context.actorOf(Props(new Worker(n+1)))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case Work =>
      (1 to 200).foreach(n =>
        this.router.route(s"Message #$n", sender())
      )

  }
}
