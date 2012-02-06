package com.onesecondshy.akka.jobscheduler.server.api

import javax.ws.rs.Produces
import javax.ws.rs.Path
import javax.ws.rs.GET
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.codehaus.jackson.map.ObjectMapper
import com.onesecondshy.akka.jobscheduler.server.actors.JobServer
import com.onesecondshy.akka.jobscheduler.common.events.UpdateJob

/**
 * @author Adam Jordens (adam@jordens.org)
 */
@Path("/api/v1/jobs")
class JobResource {
    private Logger logger = LoggerFactory.getLogger(this.getClass())

    private ObjectMapper objectMapper = new ObjectMapper()

    /**
     * @return A list of all jobs (available, running, completed)
     */
    @Produces("application/json")
    @GET
    String getAllJobs() {
        def jobs = []
        JobServer.poorManStorage.each {String jobId, UpdateJob message ->
            jobs << [
                    jobId: jobId,
                    results: message.results
            ]
        }

        return objectMapper.writeValueAsString(jobs)
    }
}
