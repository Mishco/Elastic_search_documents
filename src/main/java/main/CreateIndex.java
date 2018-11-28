package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.People;
import model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateIndex {
    private static final Logger LOGGER = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private static final String INDEX = "persondata";
    private static final String TYPE = "person";

    private RestHighLevelClient client;
    private static ObjectMapper objectMapper = new ObjectMapper();

    public CreateIndex(RestHighLevelClient connectedClient) {
        this.client = connectedClient;
    }


    public IndexRequest getIndexExample() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "miso");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out ElasticSearch");
        IndexRequest indexRequest = new IndexRequest("posts", "doc", "1").source(jsonMap);
        return indexRequest;
    }

    public Person insertPerson(Person person) {
        person.setPersonId(UUID.randomUUID().toString());
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("personId", person.getPersonId());
        dataMap.put("name", person.getName());
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, person.getPersonId()).source(dataMap);
        try {
            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            LOGGER.info("Result inserting person: " + response.getResult());
        } catch (ElasticsearchException e) {
            LOGGER.error(e.getDetailedMessage());
        } catch (java.io.IOException ex) {
            LOGGER.error(ex.getMessage());
        }

        return person;
    }

    public Person getPersonById(String id) {
        GetRequest getPersonRequest = new GetRequest(INDEX, TYPE, id);
        GetResponse getResponse = null;
        try {
            getResponse = client.get(getPersonRequest, RequestOptions.DEFAULT);
        } catch (java.io.IOException e) {
            LOGGER.error(e.getMessage());
        }
        return getResponse != null ?
                objectMapper.convertValue(getResponse.getSourceAsMap(), Person.class) : null;
    }


    public People getPersonByName(String name) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder qbm = QueryBuilders.matchQuery("name", name);
        searchSourceBuilder.query(qbm);
        SearchRequest searchRequest = new SearchRequest(INDEX);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            People people = new People();
            for (SearchHit hit : searchResponse.getHits()) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Person p = objectMapper.convertValue(sourceAsMap, Person.class);
                people.addToList(p);
            }
            return people;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return new People();
        }
    }

    public People getAllPersons() {
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        if (searchResponse == null) {
            LOGGER.info("No persons are inserted");
            return new People();
        }
        SearchHits searchHits = searchResponse.getHits();
        People people = new People();

        for (SearchHit hit : searchHits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Person p = objectMapper.convertValue(sourceAsMap, Person.class);
            people.addToList(p);
        }
        return people;
    }

    public void deletePersonById(String id) {
        DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
        try {
            DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
            LOGGER.info("Delete person: " + deleteResponse.status());
        } catch (java.io.IOException e) {
            LOGGER.error(e.getMessage());
        }

    }

    public Person updatePersonById(String id, Person person) {
        UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, id)
                .fetchSource(true);    // Fetch Object after its update
        try {
            String personJson = objectMapper.writeValueAsString(person);
            updateRequest.doc(personJson, XContentType.JSON);
            UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
            return objectMapper.convertValue(updateResponse.getGetResult().sourceAsMap(), Person.class);
        } catch (java.io.IOException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Unable to update person");
        return null;
    }


}
