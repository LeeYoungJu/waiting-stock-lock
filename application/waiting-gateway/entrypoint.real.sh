#!/bin/sh
exec java -javaagent:/dd-java-agent.jar \
  -Ddd.trace.agent.url=http://catch-waiting-datadog-agent.catch-waiting-prod.local:8126  \
  -Ddd.profiling.enabled=true \
  -Ddd.profiling.ddprof.enabled=true \
  -Ddd.profiling.ddprof.cpu.enabled=true \
  -Ddd.service=catch-waiting-gateway \
  -Ddd.logs.injection=true \
  -Ddd.env=CATCH-WAITING-REAL \
  -Ddd.trace.header.tags=x-assigned-ip:assigned-ip \
  -Ddd.http.client.tag.query-string=true \
  -Dnetworkaddress.cache.ttl=0 \
  -XX:+UseZGC -Xms512m -Xmx512m -Dspring.profiles.active=real -Dlogging.level.root=INFO -Djava.security.egd=file:/dev/./urandom \
  -XX:CompileThreshold=500 -XX:TieredStopAtLevel=1 \
  -Duser.timezone=Asia/Seoul \
  -jar waiting-gateway-0.0.1-SNAPSHOT.jar
