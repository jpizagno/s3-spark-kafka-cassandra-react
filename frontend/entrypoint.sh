#!/bin/bash

# recreate node_modules sub-dir
npm install

# need pm2 to run npm in background
npm install pm2 -g

# start app
pm2 --name FrontendLogDisplay start npm -- start

echo "finished pm2 start.... sleeping forever"
sleep infinity