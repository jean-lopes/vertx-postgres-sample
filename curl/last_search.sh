#!/bin/sh
CPF=$(uuidgen | sed s/-//g | head -c 14)
curl -s -S -H 'Content-Type: application/json' --data "{\"cpf\":\"$CPF\"}" -X POST localhost:8080/last_search

