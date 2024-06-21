#!/bin/bash

./stop-dumpling-api.sh

./_mv-lib-new.sh

./start-dumpling-api.sh
