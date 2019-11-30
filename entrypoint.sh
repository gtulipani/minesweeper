#!/bin/sh

exec java -server ${JAVA_OPTS} -jar ${WORKSPACE}/minesweeper.jar ${EXTRA_ARG} $*
