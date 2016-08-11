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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class CountryIndexer extends AbstractIndexer {
    private static final transient Logger logger = LogManager.getLogger(CountryIndexer.class);

    public static void main(String[] args) {
        CountryIndexer o = new CountryIndexer();
        try {
            o.process();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CountryIndexer() {
        super();
    }

    private void process() throws IOException {
        Terms terms = searchCountries();
        int i = 0;
        for (Terms.Bucket bucket : terms.getBuckets()) {
            i++;
            XContentBuilder xb = jsonBuilder()
                    .startObject()
                    .startObject("country")
                    .field("input", bucket.getKeyAsString())
                    .field("weight", bucket.getDocCount())
                    .endObject()
                    .endObject();
            logger.debug("indexing document {}:\n{}", i, xb.string());
            IndexResponse response = client.prepareIndex("country", "country", "country-" + i).setSource(xb).get();
        }
    }

    private Terms searchCountries() {
        AbstractAggregationBuilder aggregation = AggregationBuilders.terms("countries").field("locations.country").minDocCount(1).size(0);
        SearchResponse response = client.prepareSearch().setIndices("avid3").setTypes("profile").setSize(0).addAggregation(aggregation).execute().actionGet();
        Aggregations aggregations = response.getAggregations();
        Terms terms = aggregations.get("countries");
        return terms;
    }
}
