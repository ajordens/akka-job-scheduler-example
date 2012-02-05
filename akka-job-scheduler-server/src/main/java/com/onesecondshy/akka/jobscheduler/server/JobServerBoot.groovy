package com.onesecondshy.akka.jobscheduler.server

import akka.actor.ActorRef
import akka.actor.Actors
import com.onesecondshy.akka.jobscheduler.server.actors.JobServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import akka.config.TypedActorConfigurator

/**
 * @author Adam Jordens (adam@jordens.org)
 */
class JobServerBoot {
    private Logger logger = LoggerFactory.getLogger(this.getClass())

    public final static TypedActorConfigurator configurator = new TypedActorConfigurator()

    JobServerBoot() {
        ActorRef server = Actors.actorOf(JobServer.class);
        server.start();
        logger.info("Job com.onesecondshy.akka.jobscheduler.server.Server has been started")
    }
}
