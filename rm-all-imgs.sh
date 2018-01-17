#!/bin/sh
docker rmi --force $(docker images -q)
