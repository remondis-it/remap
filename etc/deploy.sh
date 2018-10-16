#!/bin/bash

echo "Starting deploy task..."
./gradlew bintrayUpload -Dbintray.user=${BINTRAY_USER} -Dbintray.key=${BINTRAY_API_KEY} --stacktrace