#!/bin/sh
docker rm --force $(docker ps -aq)
