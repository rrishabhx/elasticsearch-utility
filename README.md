# Elasticsearch Utility (esutility)

Integrate Elasticsearch (v5.2) **easily** with your project.


## Getting Started

### Prerequisites
**Libraries (build.gradle):**
```
dependencies {
    compile group: 'org.elasticsearch.client', name: 'transport', version: '5.2.2'
    compile group: 'org.json', name: 'json', version: '20180130'
    compile group: 'org.apache.logging.log4j', name: 'log4j-core', version: '2.8.2'
}
```

### Using esutility
#### Initialize Elasticsearch with CLUSTER_NAME, ELASTICSEARCH_IP and PORT
```
String CLUSTER_NAME = "test_cluster";
String ELASTICSEARCH_IP = "10.50.60.80";
int PORT = 1234;
ESConnection.initElasticsearch(CLUSTER_NAME, ELASTICSEARCH_IP, PORT);
```

#### Utilize Elasticsearch Queries APIs
##### Examples:
```
SearchResponse response = ESQueriesUtility.getSearchResponseForQuery("twitter", "tweet", matchAllQuery());
JSONArray jsonArray = ESQueriesUtility.getJSONArrayForQuery("twitter", "tweet", matchAllQuery());
```
