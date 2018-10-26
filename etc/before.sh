#!/bin/bash

echo "Preparing Maven Settings"

cat <<EOF > ~/.m2/settings.xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          https://maven.apache.org/xsd/settings-1.0.0.xsd">
	<servers>
		<server>
			<id>bintray-schuettec-maven</id>
			<username>schuettec</username>
			<password>${BINTRAY_API_KEY}</password>
		</server>
	</servers>
</settings>
EOF

echo "Completed Maven Settings"