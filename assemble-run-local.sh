#!/bin/sh
docker rmi $(docker images -q)
mvn8 clean package -Dmaven.test.skip && ./run-local.sh