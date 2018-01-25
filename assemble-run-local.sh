#!/bin/sh
JAVA_HOME=/usr/lib/jvm/java-8-oracle mvn package -Dmaven.test.skip && ./run-local.sh && docker ps
