#!/bin/sh
curl -v -H 'Content-Type: application/json' --data '{"cpf": "1", "kind": "CR"}' -X POST localhost:8080/last_transaction

