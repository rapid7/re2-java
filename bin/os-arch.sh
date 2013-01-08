#!/usr/bin/env bash

UNAME=$(uname -i)

# RHEL seems to use -m flag
if [ "$UNAME" == 'unknown' ]; then
    UNAME=$(uname -m)
fi

if [ "$UNAME" == 'x86_64' ]; then
    echo 'amd64'
elif [ "$UNAME" == 'amd64' ]; then
    echo 'amd64'
else
    echo 'unknown'
fi
