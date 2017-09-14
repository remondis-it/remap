#!/bin/bash

echo "Preparing gradle.properties"
cat <<EOF > ~/.gradle/gradle.properties

ossrhUser=${envOssrhUser}
ossrhPassword=${envOssrhPassword}

signing.keyId=${envSigningKeyId}
signing.password=${envSigningPassword}
signing.secretKeyRingFile=${envSigningSecretKeyRingFile}

echo "Completed gradle.properties"
EOF