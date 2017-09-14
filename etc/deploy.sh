#!/bin/bash
echo "Decrypting Sign Key..."

openssl aes-256-cbc -pass "pass:$SECRET_ENV" -in ./etc/my_key.enc -out ./etc/secring.gpg -d -a 2>&1

FILENAME=./etc/my_key.enc
FILESIZE=$(stat -c%s "$FILENAME")
echo "Size of $FILENAME = $FILESIZE bytes."

echo "Starting task 'uploadArchives'..."
./gradlew uploadArchives -Prelease

