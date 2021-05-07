@echo off
pushd  .\server\target
for %%j in ( events-collector*.jar ) do start java -jar "%%j"
popd
