package it.uniba.di.collab.stackexchange.actorsystem

import akka.actor.{Props, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import akka.dispatch.ExecutionContexts._
import it.uniba.di.collab.stackexchange.actorsystem.actors.Master
import it.uniba.di.collab.stackexchange.actorsystem.messages.Messages.Start
import it.uniba.di.collab.stackexchange.utils.StringUtils._

import scala.concurrent.duration._

object Main {

  implicit val ec = global()

  def main (args: Array[String]) {
    val rawQuestionsFilePath = args(0)
    val outputFilePath = args(1)
    val numberOfWorkers = if(args.length >= 3) args(2).toIntOpt.getOrElse(10) else 10
    val system = ActorSystem("StackOverflow-System")
    val mainActor = system.actorOf(Props(new Master(rawQuestionsFilePath, outputFilePath, numberOfWorkers)), "Master")

    //mainActor ! Start

    implicit val timeout = Timeout(60 hours)

    val future = mainActor ? Start
    future.map { result =>
      println(result)
      system.shutdown()
    }

  }
}
