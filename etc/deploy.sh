#!/bin/bash
echo "Decrypting Sign Key..."

gpg --passphrase $envOssrhPassword -o original_file.txt -d sign.enc

echo "Checksumme ./etc/my_key.enc" 
md5sum ./etc/my_key.enc
echo "Checksumme ./etc/secring.gpg" 
md5sum ./etc/secring.gpg


echo "Starting task 'uploadArchives'..."
./gradlew uploadArchives -Prelease

