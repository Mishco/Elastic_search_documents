package main;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class ElasticConnection {

    private RestHighLevelClient client = null;


    public ElasticConnection() {
        this.run();
    }

    private synchronized void run() {
        try {
            client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http"),
                            new HttpHost("localhost", 9201, "http")));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void stop() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RestHighLevelClient getConnectionClient() {
        return client;
    }
}
