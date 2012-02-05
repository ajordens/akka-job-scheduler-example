package com.onesecondshy.akka.jobscheduler.server.api

import javax.ws.rs.Produces
import javax.ws.rs.Path
import javax.ws.rs.GET
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.codehaus.jackson.map.ObjectMapper

/**
 * @author Adam Jordens (adam@jordens.org)
 */
@Path("/com.onesecondshy.akka.jobscheduler.server.api/v1/jobs")
class JobResource {
    private Logger logger = LoggerFactory.getLogger(this.getClass())

    private ObjectMapper objectMapper = new ObjectMapper()

    @Produces("application/json")
    @GET
    String getRunningJobs() {
        def jobs = [
                [
                        jobId: UUID.randomUUID().toString(),
                        status: 'RUNNING',
                        lastUpdated: new Date()
                ]
        ]

        return objectMapper.writeValueAsString(jobs)
    }
}
