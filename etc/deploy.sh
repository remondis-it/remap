#!/bin/bash
echo "Decrypting Sign Key..."
openssl aes-256-cbc -pass "pass:$SECRET_ENV" -in ./etc/secring.enc -out ./etc/secring -d -a

FILENAME=./etc/secring
FILESIZE=$(stat -c%s "$FILENAME")
echo "Size of $FILENAME = $FILESIZE bytes."

echo "Starting task 'uploadArchives'..."
./gradlew uploadArchives -Prelease

rm ./etc/secring