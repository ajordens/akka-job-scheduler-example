package com.onesecondshy.akka.jobscheduler.actors

import akka.actor.UntypedActor
import akka.actor.ActorRef
import akka.config.Supervision

import com.onesecondshy.akka.jobscheduler.events.GetJob
import akka.actor.Actors
import com.onesecondshy.akka.jobscheduler.events.GetJobResult
import com.onesecondshy.akka.jobscheduler.events.UpdateJob
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Adam Jordens (adam@jordens.org)
 */
class JobServer extends UntypedActor {
    private Logger logger = LoggerFactory.getLogger(this.getClass())


    private ActorRef storage = null
    private JobManagement jobMgr = null
    private ClientManagement clientMgr = null
    public JobServer() {

        Supervision.FaultHandlingStrategy faultHandler = new Supervision.OneForOneStrategy(
                null, // exceptions to handle
                null, // max restart retries
                null) // within time in ms
        getContext().setFaultHandler(faultHandler)

        jobMgr = new JobManagement(getContext(), storage)
        clientMgr = new ClientManagement(getContext(), jobMgr)

        logger.info("Job server is starting up...")
    }

    public void preStart() {
        Actors.remote().start("0.0.0.0", 2552)
        Actors.remote().register("job:service", getContext())
        
        logger.info("Job server is started")
    }
    
    public void onReceive(final Object msg) {
        jobMgr.handleReceive(msg)
        clientMgr.handleReceive(msg)
    }

    public void postStop() {
        logger.info("Job server is shutting down...")
        jobMgr.shutdownSessions()
//        getContext().unlink(storage)
//        storage.stop()
    }

    /**
     * Implements user session management.
     */
    private class JobManagement {
        private ActorRef self = null
        private ActorRef storage = null
        private Map<String, ActorRef> sessions = new HashMap<String, ActorRef>()

        public JobManagement(ActorRef self, ActorRef storage) {
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
            } else if (msg instanceof UpdateJob) {
                def updateJob = (UpdateJob) msg
                println updateJob
            }
        }

        public void shutdownSessions() {
            for (ActorRef session: sessions.values()) {
                session.stop()
            }
        }
    }

    /**
     * Implements chat management, e.g. chat message dispatch.
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
