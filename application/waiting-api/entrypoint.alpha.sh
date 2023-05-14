#!/bin/sh
exec java -Xmx512m -Dspring.profiles.active=alpha -Dlogging.level.root=INFO \
  -Dnetworkaddress.cache.ttl=0 \
  -Duser.timezone=Asia/Seoul \
  -Djava.security.egd=file:/dev/./urandom \
  -jar waiting-api-0.0.1-SNAPSHOT.jar
