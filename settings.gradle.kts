rootProject.name = "catch-waiting-apps"
include("application:waiting-gateway")
findProject(":application:waiting-gateway")?.name = "waiting-gateway"
include("application:waiting-api")
findProject(":application:waiting-api")?.name = "waiting-api"
include("application:waiting-batch")
findProject(":application:waiting-batch")?.name = "waiting-batch"
include("application:waiting-worker")
findProject(":application:waiting-worker")?.name = "waiting-worker"
include("application:waiting-websocket")
findProject(":application:waiting-websocket")?.name = "waiting-websocket"

include("waiting-data")
include("waiting-shared")
include("waiting-event-handler")
include("waiting-shop-register")

include("client:client-pos")
findProject(":client:client-pos")?.name = "client-pos"
include("client:client-message-nhncloud")
findProject(":client:client-message-nhncloud")?.name = "client-message-nhncloud"
include("client:client-kafka")
findProject(":client:client-kafka")?.name = "client-kafka"
