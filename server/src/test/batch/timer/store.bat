curl -v -X POST  "http://localhost:8090/events-collector/events/timing/"  --data "&application=app-dot&id=timer-1&when=2021-05-21T22:05:03.000&elapsed=1.0"

curl -v -X POST  "http://localhost:8090/events-collector/events/timing/"  --data "&application=app-dot&id=timer-2&when=2021-05-21T22:15:20.000&elapsed=2.0"
curl -v -X POST  "http://localhost:8090/events-collector/events/timing/"  --data "&application=app-dot&id=timer-2&when=2021-05-21T22:55:22.000&elapsed=4.0"

curl -v -X POST  "http://localhost:8090/events-collector/events/timing/"  --data "&application=app-org&id=timer-3&when=2021-05-22T21:34:45.000&elapsed=1"
curl -v -X POST  "http://localhost:8090/events-collector/events/timing/"  --data "&application=app-org&id=timer-3&when=2021-05-22T21:52:36.000&elapsed=2"
curl -v -X POST  "http://localhost:8090/events-collector/events/timing/"  --data "&application=app-org&id=timer-3&when=2021-05-22T21:02:12.000&elapsed=3"
