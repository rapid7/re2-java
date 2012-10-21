#!/usr/bin/env bash

UNAME=$(uname -o)

if [[ "$UNAME" == *Linux* ]]; then
    echo 'Linux'
else
    echo 'unknown'
fi
