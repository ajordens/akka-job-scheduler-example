package com.onesecondshy.akka.jobscheduler.server.actors

import akka.actor.UntypedActor
import akka.actor.ActorRef
import akka.config.Supervision

import com.onesecondshy.akka.jobscheduler.common.events.GetJob
import akka.actor.Actors
import com.onesecondshy.akka.jobscheduler.common.events.GetJobResult
import com.onesecondshy.akka.jobscheduler.common.events.UpdateJob
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * JobServer runs as 'job:service' on 0.0.0.0:2552.
 *
 * Responsible for:
 * - delivering available jobs to clients
 * - updating job states (AVAILABLE -> RUNNING -> COMPLETED)
 *
 * @author Adam Jordens (adam@jordens.org)
 */
class JobServer extends UntypedActor {
    private Logger logger = LoggerFactory.getLogger(this.getClass())

    private ActorRef storage = null
    private JobManagement jobMgr = null
    private ClientManagement clientMgr = null

    static ConcurrentHashMap poorManStorage = new ConcurrentHashMap<String, UpdateJob>()
    
    public JobServer() {
        Supervision.FaultHandlingStrategy faultHandler = new Supervision.OneForOneStrategy(
                null, // exceptions to handle
                null, // max restart retries
                null) // within time in ms
        getContext().setFaultHandler(faultHandler)

        jobMgr = new JobManagement(getContext(), poorManStorage)
        clientMgr = new ClientManagement(getContext(), jobMgr)
    }

    /**
     * Register a 'job:service' on 0.0.0.0:2552
     */
    @Override
    public void preStart() {
        Actors.remote().start("0.0.0.0", 2552)
        Actors.remote().register("job:service", getContext())
    }

    @Override
    public void onReceive(final Object msg) {
        jobMgr.handleReceive(msg)
        clientMgr.handleReceive(msg)
    }

    @Override
    public void postStop() {
        logger.info("Job server is shutting down...")
        jobMgr.shutdownSessions()
//        getContext().unlink(storage)
//        storage.stop()
    }

    /**
     * Job Management.
     *
     * - Delivering new jobs to client
     * - Updating job states based on client responses
     */
    private class JobManagement {
        private ActorRef self = null
        private Map<String, ActorRef> sessions = new HashMap<String, ActorRef>()
        private ConcurrentHashMap<String, UpdateJob> storage

        public JobManagement(ActorRef self, ConcurrentHashMap<String, UpdateJob> storage) {
            this.self = self
            this.storage = storage
        }

        public ActorRef getSession(String username) {
            return sessions.get(username)
        }

        public void handleReceive(final Object msg) {
            if (msg instanceof GetJob) {
                def getJob = (GetJob) msg

                getContext().replyUnsafe(
                        new GetJobResult(
                                UUID.randomUUID().toString(),
                                [
                                        'uname -a',
                                        'ls -al /tmp',
                                ]
                        )
                )
                
                logger.info("Found 1 available job")
            } else if (msg instanceof UpdateJob) {
                def updateJob = (UpdateJob) msg
                storage.putIfAbsent(updateJob.jobId, updateJob)
                logger.info("Job ${updateJob.jobId} has been updated")
            }
        }

        public void shutdownSessions() {
            for (ActorRef session: sessions.values()) {
                session.stop()
            }
        }
    }

    /**
     * Client management.
     *
     * - Tracking what job clients are available and what jobs are running on each
     */
    private class ClientManagement {
        private ActorRef self = null
        private JobManagement jobMgr = null

        public ClientManagement(ActorRef self, JobManagement sessionMgr) {
            this.self = self
            this.jobMgr = sessionMgr
        }

        public void handleReceive(final Object msg) {
            // do nothing
        }
    }
}
