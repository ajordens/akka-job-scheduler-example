package com.onesecondshy.akka.jobscheduler

import akka.actor.Actors
import akka.actor.ActorRef
import com.onesecondshy.akka.jobscheduler.events.GetJob
import com.onesecondshy.akka.jobscheduler.events.GetJobResult
import com.onesecondshy.akka.jobscheduler.events.UpdateJob
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Adam Jordens (adam@jordens.org)
 */
class Client {
    private Logger logger = LoggerFactory.getLogger(this.getClass())
    
    private final String name
    private final int numberOfWorkers
    private final ActorRef server

    Client(String hostname, String name, int numberOfWorkers) {
        this.name = name
        this.numberOfWorkers = numberOfWorkers
        this.server = Actors.remote().actorFor("job:service", hostname, 2552)
        
        logger.info("Connecting to ${hostname}")
    }

    public void run() {
        while(true) {
            logger.info("Looking for Jobs")

            GetJobResult result = (GetJobResult) server.sendRequestReply(new GetJob())

            Map<String, String> jobResults = [:]
            result.commandLines.each {
                try {
                    jobResults[it] = it.execute().text
                } catch (Exception e) {
                    logger.info("Unable to execute ${it}")
                    jobResults[it] = null
                }
            }

            server.sendOneWay(new UpdateJob(result.jobId, "COMPLETE", jobResults))
            Thread.sleep(15000)
        }
    }

    public static void main(String[] args) {
        def hostname = System.getProperty('server.host', 'localhost')
        new Client(hostname, "localhost[client]", 20).run()
    }
}
