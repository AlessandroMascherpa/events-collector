@echo off
setlocal
set EVENTS_COLLECTOR_REPOSITORY_TYPE=postgres
set EVENTS_COLLECTOR_REPOSITORY_URL=r2dbc:postgresql://postgres:password@localhost:5432/events-collector

pushd  .\server\target
for %%j in ( events-collector*.jar ) do start java -jar "%%j"
popd
