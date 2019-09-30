# Elasticsearch Utility (esutility)
Integrate Elasticsearch (v5.2) **easily** with your project.

**esutility** is a Java utility for Elasticsearch that makes it easier to connect to Elasticsearch cluster.

**esutility**'s API is meant to be intuitive, flexible and concise for all Elasticsearch query operations.


## Prerequisites
**Libraries (build.gradle):**
```
dependencies {
    compile group: 'org.elasticsearch.client', name: 'transport', version: '5.2.2'
    compile group: 'org.json', name: 'json', version: '20180130'
    compile group: 'org.apache.logging.log4j', name: 'log4j-core', version: '2.8.2'
}
```

## Using esutility
### Initialize Elasticsearch with cluster name, elasticsearch ip and port
```
String CLUSTER_NAME = "test_cluster";
String ELASTICSEARCH_IP = "10.50.60.80";
int PORT = 1234;
ESConnection.initElasticsearch(CLUSTER_NAME, ELASTICSEARCH_IP, PORT);
```

### Elasticsearch Queries APIs
```
//Sample Elasticsearch index name, type and QueryBuilder
String indexName = "twitter";
String type = "tweet";
QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", "John Doe");
```
#### Index document
```  
HashMap<String, Object> docMap = new HashMap<>();
docMap.put("name", "John Doe")
docMap.put("tweet", "Hello world")

 // Replace 'null' with a string to give a specific doc Id. Otherwise ES will assign a random doc Id
boolean status = ESQueriesUtility.indexDocument(indexName, type, null, docMap);
```

#### Delete document
```
boolean status = ESQueriesUtility.deleteDocument(indexName, type, docId);
```

#### Delete on query match
```
boolean status = ESQueriesUtility.deleteOnQueryMatch(indexName, type, queryBuilder);
```

#### Delete Index
```
ESQueriesUtility.deleteIndex(indexName);
```

#### Get JSONObject for ES doc ID
```
JSONObject json = ESQueriesUtility.getJsonForId(indexName, type, docId);
```

#### Find doc Id of element at 0th index
```
String docId = ESQueriesUtility.getDocumentId(indexName, type, queryBuilder);
```

#### Get JSONObject at 0th index
```
JSONObject json = ESQueriesUtility.getJsonForQuery(indexName, type, queryBuilder);
```

#### Get SearchResponse for query
```
SearchResponse response = ESQueriesUtility.getSearchResponseForQuery(indexName, type, queryBuilder);
```

#### Get SearchResponse with specified source fields for query
```
SearchResponse response = ESQueriesUtility.getSearchResponseForQuery(indexName, type, queryBuilder, new String[] {"name"});
```

#### Get sorted SearchResponse for query
```
SearchResponse response = ESQueriesUtility.getSortedSearchResponseForQuery(indexName, type, queryBuilder, "name", SortOrder.ASC);
```

#### Get JSONArray for query
```
JSONArray jsonArray = ESQueriesUtility.getJSONArrayForQuery(indexName, type, queryBuilder);
```

#### Get JSONArray with specified source fields for query
```
JSONArray jsonArray = ESQueriesUtility.getJSONArrayForQuery(indexName, type, queryBuilder, new String[] {"name"});
```

#### Get sorted JSONArray for query
```
JSONArray jsonArray = ESQueriesUtility.getSortedJSONArrayForQuery(indexName, type, queryBuilder, "name", SortOrder.ASC);
```

#### Get JSONArray utilizing Scroll API (Useful in fetching large amount of documents)
```
JSONArray jsonArray = ESQueriesUtility.getBulkJSONArrayForQuery(indexName, type, queryBuilder);
```

#### Get SearchResponse for aggregation
```
SearchResponse response = ESQueriesUtility.getSearchResponseForAggregation(indexName, type, queryBuilder, aggregationBuilder);
```

#### Update document
```
HashMap<String, Object> updateFields = new HashMap<>();
updateFields.put("name", "Daenerys Targaryen");

boolean status = ESQueriesUtility.updateDocument(indexName, type, docId, updateFields);
```

### Update by Query
```
HashMap<String, Object> updateFields = new HashMap<>();
updateFields.put("name", "John Snow");

boolean status = ESQueriesUtility.updateDataOnQueryMatch(indexName, type, matchAllQuery(), updateFields)
```


#### Refresh Elasticsearch index
```
ESQueriesUtility.refreshElasticsearchIndex(indexName);
```

#### Create mapping
```
XContentBuilder mappingBuilder = jsonBuilder()
                .startObject()
                .field("properties").startObject();

mappingBuilder.field("name")
        .startObject()
        .field("type", "keyword")
        .endObject();

mappingBuilder.field("tweet")
        .startObject()
        .field("type", "text")
        .endObject();

mappingBuilder.endObject().endObject();

ESQueriesUtility.createMapping(indexName, type, mappingBuilder.string());
```

#### Check if there is at least a single document on ES matching the query param
```
boolean exists = ESQueriesUtility.checkIfExists(indexName, type, queryBuilder);
```
