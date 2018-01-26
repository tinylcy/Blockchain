#!/bin/bash

mvn clean

mvn compile

mvn exec:java -Djava.net.preferIPv4Stack=true
