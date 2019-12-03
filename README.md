# Assetory
[![Build Status](https://travis-ci.com/tadeq/Assetory.svg?branch=master)](https://travis-ci.com/tadeq/Assetory)  

Assetory is a system supporting IT assets inventory. This repository contains backend application implemented using Spring Boot and Elasticsearch.
## Prerequisites
* [Elasticsearch 7.2.0](https://www.elastic.co/downloads/past-releases/elasticsearch-7-2-0)
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

## Web app
Repository containing React application connected with this server can be found [here](https://github.com/lukkulig/Assetory-React)

## Client app
Repository containing application which sends computers' data to server can be found [here](https://github.com/tadeq/Assetory-client)