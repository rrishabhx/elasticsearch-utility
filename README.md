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
1. Index document
```  
JSONObject json = new JSONObject();
json.put("name", "John Doe")
json.put("tweet", "Hello world")

 // Replace 'null' with a string to give a specific doc Id. Otherwise ES will assign a random doc Id
boolean status = ESQueriesUtility.indexDocument(indexName, type, null, json.toString());
```

2. Create mapping
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

3. Find doc Id of element at 0th index
```
String docId = ESQueriesUtility.getDocumentId(indexName, type, queryBuilder);
```

4. Delete document
```
boolean status = ESQueriesUtility.deleteDocument(indexName, type, docId);
```

5. Delete on query match
```
boolean status = ESQueriesUtility.deleteOnQueryMatch(indexName, type, queryBuilder);
```

6. Delete Index
```
ESQueriesUtility.deleteIndex(indexName);
```

7. Get JSONObject for ES doc ID
```
JSONObject json = ESQueriesUtility.getJsonForId(indexName, type, docId);
```

8. Get JSONObject at 0th index
```
JSONObject json = ESQueriesUtility.getJsonForQuery(indexName, type, queryBuilder);
```

9. Get SearchResponse for query
```
SearchResponse response = ESQueriesUtility.getSearchResponseForQuery(indexName, type, queryBuilder);
```

10. Get SearchResponse with specified source fields for query
```
SearchResponse response = ESQueriesUtility.getSearchResponseForQuery(indexName, type, queryBuilder, new String[] {"name"});
```

11. Get sorted SearchResponse for query
```
SearchResponse response = ESQueriesUtility.getSortedSearchResponseForQuery(indexName, type, queryBuilder, "name", SortOrder.ASC);
```

12. Get JSONArray for query
```
JSONArray jsonArray = ESQueriesUtility.getJSONArrayForQuery(indexName, type, queryBuilder);
```

13. Get JSONArray with specified source fields for query
```
JSONArray jsonArray = ESQueriesUtility.getJSONArrayForQuery(indexName, type, queryBuilder, new String[] {"name"});
```

14. Get sorted JSONArray for query
```
JSONArray jsonArray = ESQueriesUtility.getSortedJSONArrayForQuery(indexName, type, queryBuilder, "name", SortOrder.ASC);
```

15. Get JSONArray utilizing Scroll API (Useful in fetching large amount of documents)
```
JSONArray jsonArray = ESQueriesUtility.getBulkJSONArrayForQuery(indexName, type, queryBuilder);
```

16. Get SearchResponse for aggregation
```
SearchResponse response = ESQueriesUtility.getSearchResponseForAggregation(indexName, type, queryBuilder, aggregationBuilder);
```

17. Update document
```
JSONObject updateJson = new JSONObject();
updateJson.put("name", "Daenerys Targaryen");

String updateJsonAsString = updateJson.toString();

boolean status = ESQueriesUtility.updateDocument(indexName, type, docId, updateJsonAsString);
```

18. Refresh Elasticsearch index
```
ESQueriesUtility.refreshElasticsearchIndex(indexName);
```

19. Check if there is at least a single document on ES matching the query param
```
boolean exists = ESQueriesUtility.checkIfExists(indexName, type, queryBuilder);
```