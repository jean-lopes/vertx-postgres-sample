#!/bin/sh
CPF=$(uuidgen | sed s/-//g | head -c 14)
curl -v -H 'Content-Type: application/json' --data "{\"cpf\":\"1\"}" -X POST localhost:8080/last_search

