package com.onesecondshy.akka.jobscheduler.server


import akka.util.AkkaLoader
import akka.actor.DefaultBootableActorLoaderService

/**
 * @author Adam Jordens (adam@jordens.org)
 */
class Server {
    public static void main(String[] args) {
        def loader = new AkkaLoader()
        loader.boot(true, new DefaultBootableActorLoaderService())
    }
}
