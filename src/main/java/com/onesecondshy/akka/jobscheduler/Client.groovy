package com.onesecondshy.akka.jobscheduler

import akka.actor.Actors
import akka.actor.ActorRef
import com.onesecondshy.akka.jobscheduler.events.GetJob
import com.onesecondshy.akka.jobscheduler.events.GetJobResult
import com.onesecondshy.akka.jobscheduler.events.UpdateJob

/**
 * @author Adam Jordens (adam@jordens.org)
 */
class Client {
    private final String name
    private final int numberOfWorkers
    private final ActorRef server

    Client(String name, int numberOfWorkers) {
        this.name = name
        this.numberOfWorkers = numberOfWorkers
        this.server = Actors.remote().actorFor("job:service", "192.168.1.110", 2552)
    }

    public void run() {
        while(true) {
            println "Looking for Jobs"

            GetJobResult result = (GetJobResult) server.sendRequestReply(new GetJob())

            Map<String, String> jobResults = [:]
            result.commandLines.each {
                try {
                    jobResults[it] = it.execute().text
                } catch (Exception e) {
                    println("Unable to execute ${it}")
                    jobResults[it] = null
                }
            }

            server.sendOneWay(new UpdateJob(result.jobId, "COMPLETE", jobResults))
            Thread.sleep(15000)
        }
    }

    public static void main(String[] args) {
        println "Client"
        
        new Client("localhost[client]", 20).run()
        
    }
}
