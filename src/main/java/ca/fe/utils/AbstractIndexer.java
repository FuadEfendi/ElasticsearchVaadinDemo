/*
 * Copyright 2016 Fuad Efendi <fuad@efendi.ca>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ca.fe.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by fefendi on 2016-08-09.
 */
public abstract class AbstractIndexer {
    protected MongoClient mongoClient;
    protected MongoDatabase db;
    protected final Client client;
    private final String host;
    private final int port;
    private final String dbname;
    private final String user;
    private final char[] password;

    public AbstractIndexer() {
        this.host = "10.31.44.225";
        this.port = 27017;
        this.user = "dev";
        this.dbname = "profile";
        this.password = "dev1234".toCharArray();
        Settings settings = Settings.settingsBuilder().put("cluster.name", "elasticsearch").put("client.transport.sniff", true).build();
        try {
            client = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.31.44.227"), 9300));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void finalize() {
        client.close();
    }

    protected void connectToMongo() {
        MongoCredential credential = MongoCredential.createCredential(user, dbname, password);
        ServerAddress server = new ServerAddress(host, port);
        mongoClient = new MongoClient(server, Arrays.asList(credential));
        db = mongoClient.getDatabase(dbname);
    }
}
