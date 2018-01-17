#!/bin/sh
export CONFIG_SERVICE_PASSWORD=secret
export NOTIFICATION_SERVICE_PASSWORD=secret
export OFFICEMAP_SERVICE_PASSWORD=secret
export MONGODB_PASSWORD=secret
export KEYSTORE_PASSWORD=foobar
export API_CLIENT_PASSWORD=clientpassword
docker-compose -f docker-compose.yml -f docker-compose.dev.yml down