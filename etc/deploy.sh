#!/bin/bash
echo "Decrypting Sign Key..."

gpg --passphrase $envOssrhPassword -o ./etc/secring.gpg -d ./etc/sign.enc

echo "Checksumme ./etc/sign.enc" 
md5sum ./etc/my_key.enc
echo "Checksumme ./etc/secring.gpg" 
md5sum ./etc/secring.gpg


echo "Starting task 'uploadArchives'..."
./gradlew uploadArchives -Prelease

