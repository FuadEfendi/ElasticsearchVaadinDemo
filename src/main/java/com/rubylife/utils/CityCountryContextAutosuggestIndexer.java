/*
 * Licensed to Tokenizer under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Tokenizer licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.rubylife.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class CityCountryContextAutosuggestIndexer extends AbstractIndexer {

    private static final transient Logger logger = LogManager.getLogger(CityCountryContextAutosuggestIndexer.class);

    public static void main(String[] args) {
        CityCountryContextAutosuggestIndexer o = new CityCountryContextAutosuggestIndexer();
        try {
            o.process();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CityCountryContextAutosuggestIndexer() {
        super();
    }

    private void process() throws IOException {
        Terms terms = searchCities();
        int i = 0;
        int j = 0;
        int k = 0;
        for (Terms.Bucket country : terms.getBuckets()) {
            i++;
            String countryName = country.getKeyAsString();
            if (countryName == null) countryName = "";
            countryName = countryName.trim();
            Terms provinceTerms = country.getAggregations().get("myProvince");
            if (provinceTerms != null) {
                for (Terms.Bucket province : provinceTerms.getBuckets()) {
                    j++;
                    String provinceName = province.getKeyAsString();
                    if (provinceName == null) provinceName = "";
                    provinceName = provinceName.trim();
                    Terms cityTerms = province.getAggregations().get("myCity");
                    if (cityTerms != null) {
                        for (Terms.Bucket city : cityTerms.getBuckets()) {
                            k++;
                            String cityName = city.getKeyAsString();
                            if (cityName == null) cityName = "";
                            cityName = cityName.trim();
                            String suggest = cityName;
                            if (provinceName.length() > 0) {
                                suggest = suggest + ", " + provinceName;
                            }
                            XContentBuilder xb = jsonBuilder()
                                    .startObject()
                                    .startObject("city-country-context")
                                    .field("input", suggest)
                                    .field("weight", city.getDocCount())
                                    .startObject("context")
                                    .field("country", countryName)
                                    .endObject().endObject().endObject();
                            logger.debug("indexing document:\n{}", xb.string());
                            IndexResponse response = client.prepareIndex("autocomplete", "city-country-context", "ccc-" + i + "-" + j + "-" + k).setSource(xb).get();
                        }
                    } else {
                        logger.warn("empty list of cities for {} {}", countryName, provinceName);
                    }
                }
            } else {
                logger.warn("empty list of provinces for {}", countryName);
            }
        }
    }

    /**
     * Find all distinct City, State, Country triplets with scores. Use Aggregations (Country) with Sub-Aggregations (State)
     * with Sub-Sub-Aggregations (City).
     *
     * @return
     */
    private Terms searchCities() {
        AbstractAggregationBuilder aggregation =
                AggregationBuilders.terms("myCountry").field("locations.country").minDocCount(1).size(0)
                        .subAggregation(AggregationBuilders.terms("myProvince").field("locations.state").minDocCount(1).size(0)
                                .subAggregation(AggregationBuilders.terms("myCity").field("locations.city").minDocCount(1).size(0)));
        logger.info("aggregation query generated: \n{}", XContentHelper.toString(aggregation));
        SearchResponse response = client.prepareSearch().setIndices("avid3").setTypes("profile").setSize(0).addAggregation(aggregation).execute().actionGet();
        logger.info("response received: \n{}", response);
        Aggregations aggregations = response.getAggregations();
        Terms terms = aggregations.get("myCountry");
        return terms;
    }
}
