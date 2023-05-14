#!/bin/sh
exec java -Xms512m -Xmx512m -Dspring.profiles.active=alpha -Dlogging.level.root=INFO \
  -Dnetworkaddress.cache.ttl=0 \
  -Duser.timezone=Asia/Seoul \
  -Djava.security.egd=file:/dev/./urandom \
  -XX:CompileThreshold=500 -XX:TieredStopAtLevel=1 \
  -jar waiting-gateway-0.0.1-SNAPSHOT.jar
