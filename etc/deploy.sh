#!/bin/bash
echo "Decrypting Sign Key..."
openssl aes-256-cbc -pass "pass:$SECRET_ENV" -in ./etc/secring.enc -out ./etc/secring -d -a

echo "Starting task 'uploadArchives'..."
./gradlew uploadArchives

rm ./etc/secring