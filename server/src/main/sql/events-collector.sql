
-- events-collector.sql
--
-- to create the database;
--

-- create the database;
-- (with psql, use '\c events-collector' to use the database)
--
CREATE DATABASE "events-collector" WITH
  ENCODING = 'UTF8'
  ;

-- create table for counter events;
--
CREATE TABLE IF NOT EXISTS counters
(
  application VARCHAR( 255 )  NOT NULL,
  event_id    VARCHAR( 255 )  NOT NULL,
  date        TIMESTAMP       NOT NULL
);

-- create table for timer events;
--
CREATE TABLE IF NOT EXISTS timers
(
  application VARCHAR( 255 )  NOT NULL,
  event_id    VARCHAR( 255 )  NOT NULL,
  date        TIMESTAMP       NOT NULL,
  elapsed     FLOAT8          NOT NULL
);

-- create table for log in/out events;
--
CREATE TABLE IF NOT EXISTS loginout
(
  application VARCHAR( 255 )  NOT NULL,
  username    VARCHAR( 255 )  NOT NULL,
  session_id  VARCHAR( 255 )  NOT NULL,
  date        TIMESTAMP       NOT NULL,
  "in"        BOOLEAN         NOT NULL
);
