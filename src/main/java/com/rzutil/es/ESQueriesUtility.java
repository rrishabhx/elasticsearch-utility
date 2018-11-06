package com.rzutil.es;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkIndexByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ESQueriesUtility {
    private static TransportClient client = ESConnection.getInstance().getClient();

    private ESQueriesUtility() {
    }


    /**
     * Index document on ES
     */
    public static boolean indexDocument(String index, String type, String esDocId, String jsonString) {
        IndexResponse response;
        if (esDocId == null) {
            response = client.prepareIndex(index, type)
                    .setSource(jsonString)
                    .get();
        } else {
            response = client.prepareIndex(index, type, esDocId)
                    .setSource(jsonString)
                    .get();
        }

        refreshElasticsearchIndex(index);

        return (response.status().getStatus() >= 200) && (response.status().getStatus() <= 299);
    }


    /**
     * Deletes single document from ES
     */
    public static boolean deleteDocument(String index, String type, String esDocId) {
        DeleteResponse response = client.prepareDelete(index, type, esDocId).get();
        refreshElasticsearchIndex(index);

        return (response.status().getStatus() >= 200) && (response.status().getStatus() <= 299);
    }


    /**
     * @return total deleted documents
     */
    public static boolean deleteOnQueryMatch(String index, String type, QueryBuilder query) {

        DeleteByQueryRequestBuilder deleteByQueryRequest = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(client);

        deleteByQueryRequest.source().setIndices(index).setTypes(type);

        BulkIndexByScrollResponse response = deleteByQueryRequest.filter(query).get();

        refreshElasticsearchIndex(index);

        return response.getDeleted() > 0;

    }


    /**
     * Deletes the index
     */
    public static void deleteIndex(String index) {
        client.admin().indices().prepareDelete(index).get();
        refreshElasticsearchIndex(index);
    }


    /**
     * @return JSONObject for docId
     */
    public static JSONObject getJsonForId(String index, String type, String docId) {
        GetResponse response = client.prepareGet(index, type, docId).get();
        return new JSONObject(response.getSourceAsString());
    }


    /**
     * @return single JSONObject at 0th index
     */
    public static JSONObject getJsonForQuery(String index, String type, QueryBuilder query) {
        SearchResponse response = client.prepareSearch(index).setTypes(type)
                .setQuery(query)
                .get();

        JSONObject documentJson = new JSONObject();
        if (response.getHits().totalHits() != 0) {
            documentJson = new JSONObject(response.getHits().getAt(0).getSourceAsString());
        }

        return documentJson;
    }


    /**
     * @return SearchResponse for Query
     */
    public static SearchResponse getSearchResponseForQuery(String index, String type, QueryBuilder query) {
        return client.prepareSearch(index).setTypes(type)
                .setSize(10000)
                .setQuery(query)
                .get();
    }


    /**
     * @return SearchResponse (with specified sourceFields) for Query
     */
    public static SearchResponse getSearchResponseForQuery(String index, String type, QueryBuilder query, String[] sourceFields) {
        return client.prepareSearch(index).setTypes(type)
                .setSize(10000)
                .setQuery(query)
                .setFetchSource(sourceFields, null)
                .get();
    }


    /**
     * @return SearchResponse (in ascending or descending order) for Query
     */
    public static SearchResponse getSortedSearchResponseForQuery(String index, String type, QueryBuilder query, String sortField, SortOrder order) {
        return client.prepareSearch(index).setTypes(type)
                .setSize(10000)
                .setQuery(query)
                .addSort(sortField, order)
                .get();
    }


    /**
     * @return JSONArray for Query
     */
    public static JSONArray getJSONArrayForQuery(String index, String type, QueryBuilder query) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        SearchResponse response = getSearchResponseForQuery(index, type, query);

        for (int i = 0; i < response.getHits().totalHits(); i++) {
            jsonArray.put(new JSONObject(response.getHits().getAt(i).getSourceAsString()));
        }

        return jsonArray;
    }


    /**
     * @return JSONArray (with specified sourceFields) for Query
     */
    public static JSONArray getJSONArrayForQuery(String index, String type, QueryBuilder query, String[] sourceFields) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        SearchResponse response = getSearchResponseForQuery(index, type, query, sourceFields);

        for (int i = 0; i < response.getHits().totalHits(); i++) {
            jsonArray.put(new JSONObject(response.getHits().getAt(i).getSourceAsString()));
        }

        return jsonArray;
    }


    /**
     * @return Sorted JSONArray for Query
     */
    public static JSONArray getSortedJSONArrayForQuery(String index, String type, QueryBuilder query, String sortField, SortOrder order) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        SearchResponse response = getSortedSearchResponseForQuery(index, type, query, sortField, order);

        for (int i = 0; i < response.getHits().totalHits(); i++) {
            jsonArray.put(new JSONObject(response.getHits().getAt(i).getSourceAsString()));
        }

        return jsonArray;
    }


    /**
     * @return Bulk JSONArray utilizing Scroll API (Useful in fetching large amount of documents)
     */
    public static JSONArray getBulkJSONArrayForQuery(String index, String type, QueryBuilder query) {
        JSONArray responseJsonArray = new JSONArray();

        SearchResponse scrollResp = client.prepareSearch(index).setTypes(type)
                .setScroll(new TimeValue(60000))
                .setQuery(query)
                .setSize(100).get(); //max of 100 hits will be returned for each scroll
        //Scroll until no hits are returned
        do {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                responseJsonArray.put(new JSONObject(hit.getSourceAsString()));
            }

            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(new TimeValue(60000))
                    .get();
        }
        while (scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.

        return responseJsonArray;
    }


    /**
     * @return SearchResponse for Aggregation
     */
    public static SearchResponse getSearchResponseForAggregation(String index, String type, QueryBuilder query, AggregationBuilder agg) {
        return client.prepareSearch(index).setTypes(type)
                .setSize(0)
                .setQuery(query)
                .addAggregation(agg)
                .get();

    }


    /**
     * @return Id of document at 0th index in elasticsearch
     */
    public static String getDocumentId(String index, String type, QueryBuilder query) {
        SearchResponse response = client.prepareSearch(index).setTypes(type)
                .setQuery(query)
                .get();

        return response.getHits().getAt(0).getId();
    }


    /**
     * Updates document on elasticsearch
     */
    public static boolean updateDocument(String index, String type, String id, String updateObject) {
        UpdateResponse response = client.prepareUpdate(index, type, id)
                .setDoc(updateObject)
                .get();

        refreshElasticsearchIndex(index);

        return (response.status().getStatus() >= 200) && (response.status().getStatus() <= 299);
    }


    /**
     * Refresh Elasticsearch index
     */
    public static void refreshElasticsearchIndex(String index) {
        client.admin().indices()
                .prepareRefresh(index)
                .get();
    }


    /**
     * Creates Mapping for an index OR creates index with specified mapping if it doesn't exist
     */
    public static void createMapping(String index, String type, String propertiesInJsonFormat) {
        boolean indexExists = client.admin().indices()
                .prepareExists(index)
                .get().isExists();

        if (indexExists) {
            client.admin().indices()
                    .preparePutMapping(index)
                    .setType(type)
                    .setSource(propertiesInJsonFormat)
                    .get();
        } else {
            client.admin().indices()
                    .prepareCreate(index)
                    .addMapping(type, propertiesInJsonFormat)
                    .get();
        }

    }


    /**
     * @return true if there is at least a single document on ES matching the query param
     */
    public static boolean checkIfExists(String index, String type, QueryBuilder query) {
        SearchResponse response = client.prepareSearch(index).setTypes(type)
                .setQuery(query)
                .get();

        return response.getHits().totalHits() > 0;
    }
}
