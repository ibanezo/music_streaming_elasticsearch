package com.springboot.elasticsearch.musicstreaming.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.elasticsearch.musicstreaming.document.SongDocument;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.springboot.elasticsearch.musicstreaming.util.Constant.INDEX;
import static com.springboot.elasticsearch.musicstreaming.util.Constant.TYPE;

@Service
@Slf4j
public class SongService {

    private RestHighLevelClient client;


    /*
    ObjectMapper to convert our SongDocument Object to a Map object.
    This is needed because when you send the SongDocument object to Elasticsearch
    using the HighLevel Rest Client, you will need to send it either in the form of a Map,
    or a string in the form of JSON. A string in the form of JSON is not readable and looks messy,
    which is why I’m using ObjectMapper. In order to use the ObjectMapper, let’s add the dependency to the POM file.
    * */
    private ObjectMapper objectMapper;

    @Autowired
    public SongService(@Qualifier("restHighLevelClient") RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public String createSongDocument(SongDocument document) throws IOException {
        UUID uuid = UUID.randomUUID();
        document.setId(uuid.toString());

        IndexRequest indexRequest = new IndexRequest(INDEX)
                .id(document.getId())
                .source(convertSongDocumentToMap(document), XContentType.JSON);

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse.getResult().name();

//        return "created";
    }

    public String updateSong(SongDocument document) throws Exception {

        SongDocument resultDocument = findById(document.getId());

        UpdateRequest updateRequest = new UpdateRequest(INDEX,resultDocument.getId());

        updateRequest.doc(convertSongDocumentToMap(document));
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);

        return updateResponse.getResult().name();
    }

    public List<SongDocument> findAll() throws Exception {

        SearchRequest searchRequest = buildSearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    public SongDocument findById(String id) throws Exception {
        // TODO add try blocks
//        try {
            GetRequest getRequest = new GetRequest(INDEX, id);
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            Map<String, Object> resultMap = getResponse.getSource();

//        } catch (ElasticsearchException exception) {
//            if (exception.status() == RestStatus.CONFLICT) {
//                // do smth
//            }
//        }
        return convertMapToSongDocument(resultMap);
    }

    private Map<String, Object> convertSongDocumentToMap(SongDocument songDocument) {
        return objectMapper.convertValue(songDocument, Map.class);
    }

    private SongDocument convertMapToSongDocument(Map<String, Object> map) {
        return objectMapper.convertValue(map, SongDocument.class);
    }

    private SearchRequest buildSearchRequest(String index) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        return searchRequest;
    }

    private List<SongDocument> getSearchResult(SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();
        List<SongDocument> profileDocuments = new ArrayList<>();
        for (SearchHit hit : searchHit) {
            profileDocuments
                    .add(objectMapper
                            .convertValue(hit
                                    .getSourceAsMap(), SongDocument.class));
        }
        return profileDocuments;
    }

    // Finding Song by name, with fuzzines included
    public List<SongDocument> findSongByName(String name) throws Exception{


        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(INDEX);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MatchQueryBuilder matchQueryBuilder = QueryBuilders
                .matchQuery("name",name)
                .fuzziness(Fuzziness.AUTO)
                .operator(Operator.AND);

        searchSourceBuilder.query(matchQueryBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse =
                client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);

    }

    public String deleteSongDocument(String id) throws Exception {

        DeleteRequest deleteRequest = new DeleteRequest(INDEX, id);
        DeleteResponse response = client.delete(deleteRequest,RequestOptions.DEFAULT);

        return response
                .getResult()
                .name();

    }
}
