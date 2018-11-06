package com.rzutil.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ESConnection {
    private static ESConnection instance;
    private TransportClient client;
    private boolean esConnectionStatus;

    private ESConnection(String clusterName, String ip, int port) {
        esConnectionStatus = false;
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .build();
        try {
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), port));
            esConnectionStatus = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param clusterName - Elasticsearch cluster name
     * @param ip - IP of coordinating node
     * @param port - Port of coordinating node
     */
    public static void initElasticsearch(String clusterName, String ip, int port) {
        instance = new ESConnection(clusterName, ip, port);
    }


    public static ESConnection getInstance() {
        return instance;
    }

    public TransportClient getClient() {
        return client;
    }

    public boolean esConnectionStatus() {
        return esConnectionStatus;
    }

}
