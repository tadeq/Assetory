# Assetory
[![Build Status](https://travis-ci.com/tadeq/Assetory.svg?branch=master)](https://travis-ci.com/tadeq/Assetory)  

Assetory is a system supporting IT assets inventory. This repository contains backend application implemented using Spring Boot and Elasticsearch.
## Prerequisites
* [Elasticsearch 5.6.16](https://www.elastic.co/downloads/past-releases/elasticsearch-5-6-16?fbclid=IwAR1Sl5omtFpABRCbjQa6xJVUMK9ujPdEEUECETpI9GtJvhNEuFggP_ap-XI)
* [ElasticSearch Head](https://chrome.google.com/webstore/detail/elasticsearch-head/ffmkiejjmecolpfloofpjologoblkegm) - Chrome extension that simplify browsing Elasticsearch state
## Run
1. [Start Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/starting-elasticsearch.html)
2. Start Assetory using Gradle in project main directory
```bash
gradle run
```
## Development
By default application will run on port 5444.
1. Swagger REST Api
http://localhost:5444/swagger-ui.html#/
2. Start ElasticSearch Head in Chrome and connect to port 9200.
![screen](/src/main/resources/ElasticsearchHead.png)