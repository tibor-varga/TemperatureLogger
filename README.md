# TemperatureLogger

## create index in elastisearch
curl -i -H'Content-Type: application/json' -X PUT -d '{
  "mappings": {
    "temperature": {
      "properties": {
        "timestamp": {
          "type": "long",
          "copy_to": "datetime"
        },
        "datetime": {
          "type": "date",
          "store": true
        },
        "temperature": {
          "type": "float"
        },
        "sensorid" : {
          "type": "text"
       }
      }
    }
  }
}
' 'https://elastisearch.endpoint/temperature'

##delete index if it is necessary
curl -i -H'Content-Type: application/json' -X DELETE 'https://elastisearch.endpoint/temperature'