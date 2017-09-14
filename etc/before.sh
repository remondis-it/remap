#!/bin/bash

echo "Preparing gradle.properties"

echo "MD5sum of not existing variable"
echo -n "$NOTEXISTING" | md5sum

cat <<EOF > ~/.gradle/gradle.properties

ossrhUser=${envOssrhUser}
ossrhPassword=${envOssrhPassword}

signing.keyId=${envSigningKeyId}
signing.password=${envSigningPassword}
signing.secretKeyRingFile=./etc/secring.gpg

EOF

echo "Completed gradle.properties"

