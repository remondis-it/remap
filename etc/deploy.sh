#!/bin/bash
echo "Decrypting Sign Key..."

gpg --passphrase $envOssrhPassword -o ./etc/secring.gpg -d ./etc/sign.enc

echo "Starting task 'uploadArchives'..."
./gradlew uploadArchives -Prelease

