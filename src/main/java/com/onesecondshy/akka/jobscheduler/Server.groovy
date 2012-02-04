package com.onesecondshy.akka.jobscheduler

import akka.actor.ActorRef
import akka.actor.Actors
import com.onesecondshy.akka.jobscheduler.actors.JobServer

/**
 * @author Adam Jordens (adam@jordens.org)
 */
class Server {
    public static void main(String[] args) {
        println "Server"

        ActorRef server = Actors.actorOf(JobServer.class);
        server.start();
    }
}
