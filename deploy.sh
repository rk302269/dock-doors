#!/usr/bin/env bash

image_name="gcr.io/bootcamp-102288/dock-doors-controller:$1"

docker build -t $image_name .

gcloud docker -- push $image_name

## create deployment.yml

cat template.yml | sed "s|{{image_name}}|$image_name|" > deployment.yml

kubectl apply -f deployment.yml
