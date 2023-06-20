# Events Collector

## Description

Web API to collect information about events and produce some statistical data.
The latter are returned in JSON format or as spreadsheet file.

The service keeps all data in memory, at present there is not a persistent storage handling.

Events handled by service are:

 - events counting
 - events timing
 - log in/out in a system


Client library is also provided, both for ready to use library and for usage example.


### OpenDOC

Web API online [documentation](http://localhost:8090/events-collector/swagger-ui/) is also available
when the service is running. 


### Events counting

The end point to keep track of events occurrences is: **/events-collector/events/counter/**, with the following verbs:

 - **POST** to store a single event, with the following fields as JSON object:
     - **application** the system where the event occurred
     - **id** the event identifier
     - **when** the event date, it is the format: `yyyy-MM-dd'T'HH:mm:ss.SSS`, for example: 2021-03-10T14:45:03.527

 - **GET** to get the events list and, per each event, how many times it occurred
 
The end point **/events-collector/events/counter/counters.xlsx**, with verb **GET**, returns the events list as spreadsheet file.


### Events timing

The end point to keep track of event timings is: **/events-collector/events/timing/**, with the following verbs:

 - **POST** to store a timing event, with the following fields as JSON object:
     - **application** the system where the event occurred
     - **id** the event identifier
     - **when** the event date, it is the format: `yyyy-MM-dd'T'HH:mm:ss.SSS`, for example: 2021-03-10T14:45:03.527
     - **elapsed** how much time the event needed (in milliseconds)

 - **GET** to get the events list and, per each event, how many times it occurred and the minimum, maximum and average time needed
 
The end point **/events-collector/events/timing/timings.xlsx**, with verb **GET**, returns the events list as spreadsheet file.


### Log in/out events

The end points that keep track of users activity in a system is: **/events-collector/events/log-in-out/**, with the following verbs:
 
 - **POST** to store new log in/out event, with the following fields as JSON object:
     - **application** in which system the event occurred
     - **username** the end user account
     - **in** boolean value to say whether the user logged in or out
     - **when** the event date, it is the format: `yyyy-MM-dd'T'HH:mm:ss.SSS`, for example: 2021-03-10T14:45:03.527

 - **GET** to get a list of log in and out events; to avoid to get all events the list can be filtered with query parameters:
     - **application** to get only the events linked to the given application name
     - **after** to get only events after or at the given date/time
     - **before** to get only events before or at the given date/time
   All the above parameters are optional, but, if defined, they are checked in **and**.

The end point **/events-collector/events/log-in-out/logs-in-out.xlsx**, with verb **GET**, returns list of log in and put events as spreadsheet file.
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


By default, the server keeps the values in memory up to **1000** items.
It is also available to store values in a persistent storage, at present in the following databases:
 - **Postgres**

To change the storage you can update the ```application.yml``` configuration file or set the following environment variables:

 - ```EVENTS_COLLECTOR_REPOSITORY_TYPE``` set to ```postgres```
 - ```EVENTS_COLLECTOR_REPOSITORY_URL``` set to the database URL as: ```r2dbc:postgresql://<username>:<password>@<hostname>:5432/events-collector```;
   where **<_username_>**, **<_password_>** and **<_hostname_>** must be replaced with your database configuration.


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


## Intellij IDEA

Intellij IDE does not deal correctly (at writing time) with the maven extension for project versioning according
the project git history handled by the maven extension [jgitver](https://jgitver.github.io/) in multi-module projects.

To avoid problems at project import time, follow the path: **Settings** > **Build** ... > **Build Tools** > **Maven** > **Importing**

to open the importing setting page and in the **VM Options for importer** entry field set:

   ```
      -Djgitver.skip=true  -Dversioning.disable=true
   ```


## Usage

In directories tree under `server/src/test/batch/` there are some usage examples with **curl**.
They are:

 - **counter** with examples for the event counter end point, they are:
    + **store.bat** to store some event
    + **list.bat** to list the stored events
 - **timer** with examples for the timiner end point, they are:
    + **store.bat** to store some event
    + **list.bat** to list the stored events
 - **loginout** with examples for log in/out end point, they are:
    + **store.bat**, to store some events, both log in and out events;
    + **list.bat**, to list all stored events; with a number as argument, you can show one single record;
    + **application.bat**, to list all events filtered by the application name given on command line;

Statistical data returned as spreadsheet file can be opened directly in spreadsheet applications like MS Excel.
To do so, just open the **Open** dialog box used to load files and, in the **File name** entry field, type the end point URL, for example: http://localhost:8090/events-collector/events/log-in-out/logs-in-out.xlsx, and then open the file.
You can filter the events list as well by appending the URL with query parameters as seen before.
