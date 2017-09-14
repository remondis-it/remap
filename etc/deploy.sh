#!/bin/bash
echo "Decrypting Sign Key..."

openssl aes-256-cbc -pass "pass:$MY_SECRET_ENV" -in ./etc/my_key.enc -out ./etc/secring.gpg -d -a 2>&1


FILENAME=./etc/my_key.enc
FILESIZE=$(stat -c%s "$FILENAME")
echo "Size of $FILENAME = $FILESIZE bytes."

FILENAME=./etc/secring.gpg
FILESIZE=$(stat -c%s "$FILENAME")
echo "Size of $FILENAME = $FILESIZE bytes."


echo "Checksumme ./etc/my_key.enc" 
md5sum ./etc/my_key.enc
echo "Checksumme ./etc/secring.gpg" 
md5sum ./etc/secring.gpg
echo "Checksumme of secret" 
echo -n "$MY_SECRET_ENV" | md5sum

echo "Starting task 'uploadArchives'..."
./gradlew uploadArchives -Prelease

