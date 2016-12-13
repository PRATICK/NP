#!/bin/bash

make -f makefile

java -cp ./bin:./sqlite-jdbc.jar:./java-getopt.jar Server $1 "test.db"

