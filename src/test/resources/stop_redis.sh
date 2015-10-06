#!/usr/bin/env bash
for file in `ls -d /tmp/redis*.pid`; do kill `cat ${file}`; done
for file in `ls -d /tmp/sentinel*.pid`; do kill `cat ${file}`; done
for file in `ls -d /tmp/sentinel*.conf`; do rm -f ${file}; done
