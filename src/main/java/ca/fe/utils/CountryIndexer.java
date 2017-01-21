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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class CountryIndexer extends AbstractIndexer {
    private static final transient Logger logger = LogManager.getLogger(CountryIndexer.class);

    public static void main(String[] args) {
        CountryIndexer o = new CountryIndexer();
        try {
            o.readFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CountryIndexer() {
        super("localhost", 9300);
    }

    private void readFile() throws IOException {
        Reader in = new FileReader("/data/download.geonames.org/countryInfo.txt");
        Iterable<CSVRecord> records = CSVFormat.TDF.withHeader(CountryHeaders.class).withQuote(null).withIgnoreEmptyLines(true).withCommentMarker('#').parse(in);
        int i = 0;
        for (CSVRecord record : records) {
            CountryName c = new CountryName(
                    record.get(CountryHeaders.ISO),
                    record.get(CountryHeaders.ISO3),
                    record.get(CountryHeaders.ISO_Numeric),
                    record.get(CountryHeaders.fips),
                    record.get(CountryHeaders.Country),
                    record.get(CountryHeaders.Capital),
                    record.get(CountryHeaders.Area_in_sq_km),
                    record.get(CountryHeaders.Population),
                    record.get(CountryHeaders.Continent),
                    record.get(CountryHeaders.tld),
                    record.get(CountryHeaders.CurrencyCode),
                    record.get(CountryHeaders.CurrencyName),
                    record.get(CountryHeaders.Phone),
                    record.get(CountryHeaders.Postal_Code_Format),
                    record.get(CountryHeaders.Postal_Code_Regex),
                    record.get(CountryHeaders.Languages),
                    record.get(CountryHeaders.geonameid),
                    record.get(CountryHeaders.neighbours),
                    record.get(CountryHeaders.EquivalentFipsCode));
            System.out.println(c);
            index(c);
            i++;
        }
        System.out.println(i + " countries indexed");
    }

    private void index(CountryName d) throws IOException {
        XContentBuilder xb = jsonBuilder()
                .startObject()
                .field("iso", d.getIso())
                .field("iso3", d.getIso3())
                .field("isoNumeric", d.getIsoNumeric())
                .field("fips", d.getFips())
                .field("country", d.getCountry())
                // Autocompete
                .startObject("country_suggest")
                .field("input", d.getCountry())
                .field("weight", d.getPopulation())
                .endObject()
                .field("capital", d.getCapital())
                .field("areaSqKm", d.getAreaSqKm())
                .field("population", d.getPopulation())
                .field("continent", d.getContinent())
                .field("tld", d.getTld())
                .field("currencyCode", d.getCurrencyCode())
                .field("currencyName", d.getCurrencyName())
                .field("phone", d.getPhone())
                .field("postalCodeFormat", d.getPostalCodeFormat())
                .field("postalCodeRegex", d.getPostalCodeRegex())
                .field("languages", d.getLanguages())
                .field("geonameid", d.getGeonameid())
                .field("neighbours", d.getNeighbours())
                .field("equivalentFipsCode", d.getEquivalentFipsCode())
                .endObject();
        logger.debug("indexing document {}:\n", d);
        IndexResponse response = client.prepareIndex("country-003", "CountryNames", d.getGeonameid()).setSource(xb).get();
    }



/*
    // kept here as an example of old approach with ES v.2
    private Terms searchCountries() {
        AbstractAggregationBuilder aggregation = AggregationBuilders.terms("countries").field("locations.country").minDocCount(1).size(0);
        SearchResponse response = client.prepareSearch().setIndices("avid3").setTypes("profile").setSize(0).addAggregation(aggregation).execute().actionGet();
        Aggregations aggregations = response.getAggregations();
        Terms terms = aggregations.get("countries");
        return terms;
    }
*/
}
