# Events Collector

## Description

Web API to collect information about events and produce some statistical data.
The latter are returned in JSON format or as spreadsheet file.

The service keeps all data in memory, at present there is not a persistent storage handling.

Events handled by service are:

 - events counting
 - log in/out in a system


Client library is also provided, both for ready to use and as example.


### OpenDOC

Web API online documentation is available at address: http://localhost:8090/events-collector/swagger-ui/


### Events counting

The end point to keep track of events occurrences is: **/events-collector/events/counter/**, with the following verbs:

 - **POST** to store a single event, with the following fields in URL encoded form:
     - **id** the event identifier
     - **when** the event date, it is the format: `yyyy-MM-dd'T'HH:mm:ss.SSS`, for example: 2021-03-10T14:45:03.527

 - **GET** to get the events identifiers list and, per each event, how many times it occurred
 
The end point **/events-collector/events/counter/spreadsheet-ml**, with verb **GET**, returns the events list as spreadsheet file.


### Log in/out events

The end points that keep track of users activity in a system is: **/events-collector/events/logs-in-out/**, with the following verbs:
 
 - **POST** to store new log in/out event, with the following fields in URL encoded form:
     - **username** the end user account
     - **application** in which system the event occurred
     - **in** boolean value to say whether the user logged in or out
     - **when** the event date, it is the format: `yyyy-MM-dd'T'HH:mm:ss.SSS`, for example: 2021-03-10T14:45:03.527

 - **GET** to get a list of log in and out events; to avoid to get all events the list can be filtered with query parameters:
     - **application** to get only the events linked to the given application name
     - **after** to get only events after or equal the given date/time
     - **before** to get only events before or equal the given date/time
   All the above parameters are optional, but, if defined, they are checked in **and**.

The end point **/events-collector/events/logs-in-out/spreadsheet-ml**, with verb **GET**, returns list of log in and put events as spreadsheet file.
For this end point, the query parameters: **application**, **after** and **before** are also available.


## Build

To build both the service and client libraries, simply compile they from the root directory:

   ```sh
   mvn  -Dmaven.test.skip=true  clean  package
   ```

You can test thw whole library as well with:

   ```sh
   mvn  clean  test
   ```
   
At present, you cannot run the command:

   ```sh
   mvn  clean  package
   ```

because it fails when testing the sub module: `events-collector-client`.


## Installation

### Server

The jar file can be copied in any directory and run with

   ```sh
   java  -jar events-collector-server.jar
   ```

To change the default TCP port, you can set the environment variable:

   ```sh
   export EVENTS_COLLECTOR_PORT=9080
   ```

or you can copy the file ```application.yml``` in the same directory where the jar is stored and update the configuration file as needed.
The external configuration file overwrites the internal one.


### As a service on Linux system

Before the application is set as service, set the jar owner and mode:

   ```sh
   chown  user:group events-collector-server.jar
   chmode  500  events-collector-server.jar
   ```

Then we configure the service according the distribution.


#### System V Init

Create the symbolic link to the jar using full paths

   ```sh
   ln -s  /full/path/to/events-collector-server.jar  /etc/init.d/events-collector-server.jar
   ```

Hence we can run the application as service:

   ```sh
   service start events-collector-server.jar
   ```

The service supports the standard service commands: _start_, _stop_, _restart_ and _status_.
Moreover:

 - the service runs under the user who owns the jar
 - the application process ID is tracked in ```/var/run/events-collector/events-collector-server.pid```
 - console logs are written in: ```/var/log/events-collector-server.log```
 
 
#### Systemd

In the directory ```/etc/systemd/system``` create the file ```events-collector.service``` with

    [Unit]
    Description=Web API to collect information about events and produce statistical data.
    After=syslog.target
    
    [Service]
    User=user
    ExecStart=/full/path/to/events-collector-server.jar SuccessExitStatus=143 
    
    [Install] 
    WantedBy=multi-user.target

Where ```user``` is the application owner.
Check out the [Service unit configuration](https://www.freedesktop.org/software/systemd/man/systemd.service.html) for the full list of available options.


#### Upstart

The Upstart event-based service manager is set in the system if, under the ```/etc/init/``` there are jobs with name starting with: "```upstart```".

Create job ```events-collector.conf``` with:

    # Place in /home/{user}/.config/upstart
    
    description "Web API to collect information about events and produce statistical data"
    
    respawn # attempt service restart if stops abruptly
    
    exec java -jar /full/path/to/events-collector-server.jar

More information can be found at [upstart getting started](http://upstart.ubuntu.com/getting-started.html) and in [upstart intro and cookbook](http://upstart.ubuntu.com/cookbook/).


### Client

To install in your local repository the client library, from the project root directory, run the command:

   ```sh
   mvn  install  -am -pl :events-collector-client
   ```


## Usage

In directories tree under `server/src/test/batch/` there are some usage examples with **curl**.
They are:

 - **counter** with examples for the event counter end point, they are:
    + **store.bat** to store some event
    + **list.bat** to list the stored events
 - **loginout** with examples for log in/out end point, they are:
    + **store.bat**, to store some events, both log in and out events;
    + **list.bat**, to list all stored events; with a number as argument, you can show one single record;
    + **application.bat**, to list all events filtered by the application name given on command line;

Statistical data returned as spreadsheet file can be opened directly in spreadsheet applications like MS Excel.
To do so, just open the **Open** dialog box used to load files and, in the **File name** entry field, type the end point URL: http://localhost:8090/events-collector/events/logs-in-out/spreadsheet-ml, and then open the file.
You can filter the events list as well by following URL with query parameters.
