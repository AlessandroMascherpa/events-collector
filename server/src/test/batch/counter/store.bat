curl -v -X POST  "http://localhost:8090/events-collector/events/counter/"  --data "&id=event-1&when=2021-05-11T22:29:03.000"
curl -v -X POST  "http://localhost:8090/events-collector/events/counter/"  --data "&id=event-2&when=2021-05-11T22:29:33.000"
curl -v -X POST  "http://localhost:8090/events-collector/events/counter/"  --data "&id=event-3&when=2021-05-11T22:29:34.000"
curl -v -X POST  "http://localhost:8090/events-collector/events/counter/"  --data "&id=event-2&when=2021-05-11T22:34:53.000"
curl -v -X POST  "http://localhost:8090/events-collector/events/counter/"  --data "&id=event-3&when=2021-05-11T22:36:08.000"
curl -v -X POST  "http://localhost:8090/events-collector/events/counter/"  --data "&id=event-3&when=2021-05-11T22:45:52.000"