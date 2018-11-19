# TemperatureLogger
Experiment to create an AWS IOT based temperature monitor system.
Main components:
  - DS18B20 Temperatur-Sensor-Tester Interface Rev. C from Diamex (https://www.amazon.de/gp/product/B01MXL7B0I/ref=oh_aui_detailpage_o01_s00?ie=UTF8&psc=1)
  - AZDelivery 5 x 1M Kabel DS18B20 digitaler Edelstahl Temperatursensor  (https://www.amazon.de/gp/product/B075FYYLLV/ref=oh_aui_detailpage_o03_s00?ie=UTF8&psc=1)
  - Raspberry Pi 3
  - AWS IOT Base Things with Shadow
  - Elasticsearch 
  - Kibana

## Sample aws-iot-sdk-samples.properties
``` 
# Client endpoint, e.g. <prefix>.iot.us-east-1.amazonaws.com
clientEndpoint=

# Client ID, unique client ID per connection
clientId=xxxxx

# Thing name
thingName=TemperatureSensor
thingName2=TemperatureSensor2
sensorId=
sensorId2=

#keyAlgorithm=
awsAccessKeyId=
awsSecretAccessKey=

#millisecs
sampleRate=1000
```

## create index in elastisearch
```curl -i -H'Content-Type: application/json' -X PUT -d '{
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
```
## delete index if it is necessary
```
curl -i -H'Content-Type: application/json' -X DELETE 'https://elastisearch.endpoint/temperature'
```

## Known problems
After many hours work, breaks the MQTT connection, see https://github.com/eclipse/paho.mqtt.java/issues/358 