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

/**
 * Created by fefendi on 2017-01-16.
 */
public class GeonamesIndexer extends AbstractIndexer {

    private static final transient Logger logger = LogManager.getLogger(GeonamesIndexer.class);

    public GeonamesIndexer() {
        super("localhost", 9300);
    }

    public static void main(String[] args) {
        GeonamesIndexer o = new GeonamesIndexer();
        try {
            o.readFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFile() throws IOException {
        Reader in = new FileReader("/data/download.geonames.org/cities1000.txt");
        Iterable<CSVRecord> records = CSVFormat.TDF.withHeader(Headers.class).withQuote(null).parse(in);
        int i = 0;
        for (CSVRecord record : records) {
            GeoName gl = new GeoName(
                    record.get(Headers.geonameid),
                    record.get(Headers.name),
                    record.get(Headers.asciiname),
                    record.get(Headers.alternatenames),
                    record.get(Headers.latitude),
                    record.get(Headers.longitude),
                    record.get(Headers.featureClass),
                    record.get(Headers.featureCode),
                    record.get(Headers.countryCode),
                    record.get(Headers.cc2),
                    record.get(Headers.admin1Code),
                    record.get(Headers.admin2Code),
                    record.get(Headers.admin3Code),
                    record.get(Headers.admin4Code),
                    record.get(Headers.population),
                    record.get(Headers.elevation),
                    record.get(Headers.dem),
                    record.get(Headers.timezone),
                    record.get(Headers.modificationDate));
            //logger.info(gl);
            int ppl = Integer.parseInt(gl.getPopulation());
            if ("P".equals(gl.getFeatureClass()) && ppl >= 100000) {
                System.out.println(gl);
                index(gl);
                i++;
            }
        }
        System.out.println(i + " cities found");
    }

    private void index(GeoName d) throws IOException {
        XContentBuilder xb = jsonBuilder()
                .startObject()
                .field("geonameid", d.getGeonameid())
                .field("name", d.getName())
                // Autocompete
                .startObject("name_suggest")
                .field("input", d.getName())
                .field("weight", d.getPopulation())

                .endObject()
                .field("asciiname", d.getAsciiname())
                .field("alternatenames", d.getAlternatenames())
                .field("location", d.getLocation())
                .field("featureClass", d.getFeatureClass())
                .field("featureCode", d.getFeatureCode())
                .field("countryCode", d.getCountryCode())
                .field("cc2", d.getCc2())
                .field("admin1Code", d.getAdmin1Code())
                .field("admin2Code", d.getAdmin2Code())
                .field("admin3Code", d.getAdmin3Code())
                .field("admin4Code", d.getAdmin4Code())
                .field("population", d.getPopulation())
                .field("elevation", d.getElevation())
                .field("dem", d.getDem())
                .field("timezone", d.getTimezone())
                .field("modificationDate", d.getModificationDate())
                .endObject();
        logger.debug("indexing document {}:\n", d);
        IndexResponse response = client.prepareIndex("test004", "GeoNames", d.getGeonameid()).setSource(xb).get();
    }
}


