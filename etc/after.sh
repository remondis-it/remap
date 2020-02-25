#!/bin/bash

echo "Syncing Maven Central repository"

version=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep "^[[:digit:]]*[[:digit:]].[[:digit:]]*[[:digit:]].[[:digit:]]*[[:digit:]]"`

echo "Syncing project version ${version}"

curl --verbose  --user "schuettec:${BINTRAY_API_KEY}" --request "POST" "https://api.bintray.com/maven_central_sync/schuettec/maven/com.remondis:remap/versions/${version}"

echo "Sync finished."