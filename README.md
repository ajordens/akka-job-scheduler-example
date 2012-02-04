Introduction
============

A sample job scheduler application using the Java API for Akka, built by Maven.

This application provides a simple introduction into remote Java actors and the request/reply model in Akka.

Dependencies
------------

Akka version = 1.3
Akka API = Java

Instructions
------------
# Install Maven
# mvn -Pserver clean compile exec:exec
# mvn -Pclient clean compile exec:exec -Dserver.host=localhost

The client can be run on multiple hosts (or multiple times on the same host).