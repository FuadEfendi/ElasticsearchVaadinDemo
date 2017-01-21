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
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class CityAutosuggestIndexer {
	MongoClient mongoClient;
	String host;
	int port;
	String dbname;
	MongoDatabase db;
	String user;
	char[] password;
	private final Client client;

	public static void main(String[] args) {
		CityAutosuggestIndexer o = new CityAutosuggestIndexer();
		o.host = "10.31.44.225";
		o.port = 27017;
		o.user = "dev";
		o.dbname = "profile";
		o.password = "dev1234".toCharArray();
		try {
			o.process();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private CityAutosuggestIndexer() {
		Settings settings = Settings.builder()
				.put("cluster.name", "elasticsearch")
				.put("client.transport.sniff", true)
				.build();
		try {
			client = new PreBuiltTransportClient(settings)
					.addTransportAddress(
							new InetSocketTransportAddress(InetAddress.getByName("10.31.44.227"), 9300));
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	private void process() throws IOException {
		Terms terms = searchCities();
		int i = 0;
		for (Terms.Bucket city : terms.getBuckets()) {
			i++;
			System.out.println(city.getKeyAsString() + " " + city.getDocCount());
			XContentBuilder xb = jsonBuilder().startObject().startObject("city-suggest").field("input", city.getKeyAsString()).field("weight", city.getDocCount()).endObject().endObject();
			
			System.out.println(xb.toString());
			
			IndexResponse response = client.prepareIndex("autocomplete", "city", "c-" + i).setSource(xb).get();
		}
	}

	private Terms searchCities() {
		AbstractAggregationBuilder aggregation = AggregationBuilders.terms("cities").field("locations.city").minDocCount(1).size(100000000);
		SearchResponse response = client.prepareSearch().setIndices("avid3").setTypes("profile").setSize(0).addAggregation(aggregation).execute().actionGet();
		Aggregations aggregations = response.getAggregations();
		Terms terms = aggregations.get("cities");
		return terms;
	}

	private void processMongo() {
		connectToMongo();
		MongoCollection<Document> collection = db.getCollection("member");
		DistinctIterable<String> docs = collection.distinct("locations.city", String.class);
		int i = 0;
		for (String doc : docs) {
			System.out.println(i++ + doc);
		}
	}

	public void connectToMongo() {
		MongoCredential credential = MongoCredential.createCredential(user, dbname, password);
		ServerAddress server = new ServerAddress(host, port);
		mongoClient = new MongoClient(server, Arrays.asList(credential));
		db = mongoClient.getDatabase(dbname);
	}

	@Override
	protected void finalize() {
		client.close();
	}
}
