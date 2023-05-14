#!/bin/sh
exec java -javaagent:/dd-java-agent.jar \
  -Ddd.trace.agent.url=http://catch-waiting-datadog-agent.catch-waiting-prod.local:8126  \
  -Ddd.profiling.enabled=true \
  -Ddd.service=catch-waiting-worker \
  -Ddd.logs.injection=true \
  -Ddd.env=CATCH-WAITING-REAL \
  -Ddd.trace.header.tags=x-assigned-ip:assigned-ip \
  -Ddd.http.client.tag.query-string=true \
  -Dnetworkaddress.cache.ttl=0 \
  -Duser.timezone=Asia/Seoul \
  -Xmx512m -Dspring.profiles.active=real -Dlogging.level.root=INFO -Djava.security.egd=file:/dev/./urandom \
  -jar waiting-worker-0.0.1-SNAPSHOT.jar
