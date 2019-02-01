#!/bin/sh
CPF=$(uuidgen | sed s/-//g | head -c 14)
curl -v -H 'Content-Type: application/json' --data @transactions.json -X POST localhost:8080/transactions

