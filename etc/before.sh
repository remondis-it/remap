#!/bin/bash

echo "Preparing gradle.properties"
cat <<EOF > ~/.gradle/gradle.properties

ossrhUser=${envOssrhUser}
ossrhPassword=${envOssrhPassword}

signing.keyId=${envSigningKeyId}
signing.password=${envSigningPassword}
signing.secretKeyRingFile=./etc/secring.gpg

echo "Completed gradle.properties"
EOF