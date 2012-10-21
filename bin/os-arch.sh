#!/usr/bin/env bash

UNAME=$(uname -i)

if [ "$UNAME" == 'x86_64' ]; then
    echo 'amd64'
elif [ "$UNAME" == 'amd64' ]; then
    echo 'amd64'
else
    echo 'unknown'
fi
