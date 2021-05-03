#!/bin/bash
exec java -javaagent:/var/newrelic/newrelic.jar -Dnewrelic.config.app_name="$NEW_RELIC_APP_NAME" -Dnewrelic.config.license_key=NEW_RELIC_LICENSE_KEY  -jar api-springboot.jar