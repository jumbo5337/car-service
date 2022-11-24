#!/usr/bin/env sh

echo 'GET APP INFO'
result=$(curl -sS --location --request GET 'http://localhost:8080/api/v1/app-info')
jq .  <<< $result
echo '-------------------------------------'

echo 'ADD NEW CONNECTOR FOR EXISTING CHARGE POINT'
result=$(curl -sS --location  --request PUT 'http://localhost:8080/api/v1/admin/connector?chargePoint=1' \
         --header 'Authorization: Basic YWRtaW46YWRtaW4=' )
jq .  <<< $result
echo '-------------------------------------'

echo 'ADD NEW CONNECTOR FOR ABSENT CHARGE POINT'

result=$(curl -sS --location --request PUT 'http://localhost:8080/api/v1/admin/connector?chargePoint=999' \
         --header 'Authorization: Basic YWRtaW46YWRtaW4=' )
jq .  <<< $result
echo '-------------------------------------'

echo 'ADD NEW CONNECTOR WITHOUT AUTH'
result=$(curl -sS --location --request PUT 'http://localhost:8080/api/v1/admin/connector?chargePoint=999')
jq .  <<< $result
echo '-------------------------------------'

echo 'GET SESSIONS LIST'
result=$(curl -sS --location --request GET 'http://localhost:8080/api/v1/admin/sessions' \
         --header 'Authorization: Basic YWRtaW46YWRtaW4=')
jq .  <<< $result
echo '-------------------------------------'
echo 'INIT SESSION OK'
result=$(curl -sS --location --request POST 'http://localhost:8080/api/v1/session/init' \
         --header 'Authorization: Basic VmFzeWE6MTIzNA==' \
         --header 'Content-Type: application/json' \
         --data-raw '{
             "connector": 1,
             "rfidNumber": 1,
             "startMeter": 0.1
         }')

jq .  <<< $result
id=$(jq ".id"  <<< $result | tr -d \")
echo "$id"
echo '-------------------------------------'
echo 'RETRY SESSION OK'
result=$(curl -sS --location --request POST 'http://localhost:8080/api/v1/session/init' \
         --header 'Authorization: Basic VmFzeWE6MTIzNA==' \
         --header 'Content-Type: application/json' \
         --data-raw '{
             "connector": 1,
             "rfidNumber": 1,
             "startMeter": 0.1
         }')
jq .  <<< $result
echo '-------------------------------------'
echo 'GET SESSIONS LIST'
result=$(curl -sS --location --request GET 'http://localhost:8080/api/v1/admin/sessions' \
         --header 'Authorization: Basic YWRtaW46YWRtaW4=')
jq .  <<< $result
echo '-------------------------------------'
echo 'COMPLETE SESSION OK'
result=$(curl -sS --location --request POST "http://localhost:8080/api/v1/session/${id}/complete" \
         --header 'Authorization: Basic VmFzeWE6MTIzNA==' \
         --header 'Content-Type: application/json' \
         --data-raw '{
             "connector": 1,
             "rfidNumber": 1,
             "endMeter": 0.1
         }')

jq .  <<< $result

echo 'COMPLETE SESSION RETRY OK'

result=$(curl -sS --location --request POST "http://localhost:8080/api/v1/session/${id}/complete" \
         --header 'Authorization: Basic VmFzeWE6MTIzNA==' \
         --header 'Content-Type: application/json' \
         --data-raw '{
             "connector": 1,
             "rfidNumber": 1,
             "endMeter": 0.1
         }')

jq .  <<< $result

echo '-------------------------------------'
echo 'GET SESSIONS LIST'
result=$(curl -sS --location --request GET 'http://localhost:8080/api/v1/admin/sessions' \
         --header 'Authorization: Basic YWRtaW46YWRtaW4=')
jq .  <<< $result

echo '-------------------------------------'
echo 'INIT SESSION OK 2'
result=$(curl -sS --location --request POST 'http://localhost:8080/api/v1/session/init' \
         --header 'Authorization: Basic VmFzeWE6MTIzNA==' \
         --header 'Content-Type: application/json' \
         --data-raw '{
             "connector": 1,
             "rfidNumber": 1,
             "startMeter": 0.5,
             "startTime": "2020-12-01T12:22"
         }')

jq .  <<< $result
id=$(jq ".id"  <<< $result | tr -d \")

echo '-------------------------------------'
echo 'COMPLETE SESSION NOT OK'
result=$(curl -sS --location --request POST "http://localhost:8080/api/v1/session/$id/complete" \
         --header 'Authorization: Basic VmFzeWE6MTIzNA==' \
         --header 'Content-Type: application/json' \
         --data-raw '{
             "connector": 1,
             "rfidNumber": 1,
             "endMeter": 0.1,
             "endTime": "2020-12-01T12:30"
         }')

jq .  <<< $result
echo '-------------------------------------'
echo 'GET SESSIONS LIST'
result=$(curl -sS --location --request GET 'http://localhost:8080/api/v1/admin/sessions' \
         --header 'Authorization: Basic YWRtaW46YWRtaW4=')
jq .  <<< $result

echo '-------------------------------------'
echo 'COMPLETE SESSION OK'
result=$(curl -sS --location --request POST "http://localhost:8080/api/v1/session/$id/complete" \
         --header 'Authorization: Basic VmFzeWE6MTIzNA==' \
         --header 'Content-Type: application/json' \
         --data-raw '{
             "connector": 1,
             "rfidNumber": 1,
             "endMeter": 0.9,
             "endTime": "2020-12-01T12:30"
         }')

jq .  <<< $result
id=$(jq '.id'  <<< $result)

