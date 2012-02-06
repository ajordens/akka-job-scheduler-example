package com.onesecondshy.akka.jobscheduler.client

import akka.actor.Actors
import akka.actor.ActorRef
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.onesecondshy.akka.jobscheduler.common.events.GetJobResult
import com.onesecondshy.akka.jobscheduler.common.events.GetJob
import com.onesecondshy.akka.jobscheduler.common.events.UpdateJob

/**
 * Connect to a JobServer and runs any available jobs.
 *
 * @author Adam Jordens (adam@jordens.org)
 */
class Client {
    private Logger logger = LoggerFactory.getLogger(this.getClass())

    private final String name
    private final ActorRef server

    /**
     * @param hostname Job server hostname
     * @param name Name of this client node, should be unique.
     */
    Client(String hostname, String name) {
        this.name = name
        this.server = Actors.remote().actorFor("job:service", hostname, 2552)

        logger.info("Connecting Client[${name}] to Server[${hostname}:2552]")
    }

    /**
     * Periodically check with the job server for any new jobs.
     */
    public void run() {
        while (true) {
            logger.info("Looking for new jobs")

            try {
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
            } catch (Exception e) {
                logger.error("Unable to get job", e)
            }

            Thread.sleep(15000)
        }
    }

    /**
     * Start the client.
     *
     * - server hostname passed in via a 'server.host' system property, defaults to 'localhost'
     *
     * @param args No supported command-line arguments
     */
    public static void main(String[] args) {
        def hostname = System.getProperty('server.host', 'localhost')
        new Client(hostname, "${java.net.InetAddress.getLocalHost().getHostAddress()}").run()
    }
}
