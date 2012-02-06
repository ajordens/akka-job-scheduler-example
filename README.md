Introduction
============

A sample job scheduler application using the Java API for Akka, built by Maven.

This application provides a simple introduction into remote Java actors and the request/reply model in Akka.

Groovy + Remote Akka + Http/REST Akka + Maven

Dependencies
------------

Akka version = 1.3

Akka API = Java

Instructions
------------

1. Install Maven
2. $ mvn clean install
3. $ cd akka-job-scheduler-server ; mvn jetty:run
4. $ cd akka-job-scheduler-client ; mvn exec:exec -Dserver.host=localhost

The client can be run on multiple hosts (or multiple times on the same host).

To get a listing of jobs, http://localhost:8080/api/v1/jobs (assuming you're running the server locally)

To Do
-----

1. Add API for Submitting Jobs
2. Job Tracking
3. More real-world use case