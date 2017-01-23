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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by fefendi on 2017-01-18.
 */
public class GeoName {

    /**
     * integer id of record in geonames database
     */
    String geonameid;

    /**
     * name of geographical point (utf8) varchar(200)
     */
    String name;

    /**
     * name of geographical point in plain ascii characters, varchar(200)
     */
    String asciiname;

    /**
     * alternatenames, comma separated, ascii names automatically transliterated, convenience attribute from alternatename table, varchar(10000)
     */
    String alternatenames;

    /**
     * latitude in decimal degrees (wgs84)
     */
    String latitude;

    /**
     * longitude in decimal degrees (wgs84)
     */
    String longitude;

    /**
     * see http://www.geonames.org/export/codes.html, char(1)
     */
    String featureClass;

    /**
     * see http://www.geonames.org/export/codes.html, varchar(10)
     */
    String featureCode;

    /**
     * ISO-3166 2-letter country code, 2 characters
     */
    String countryCode;

    /**
     * alternate country codes, comma separated, ISO-3166 2-letter country code, 200 characters
     */
    String cc2;

    /**
     * fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
     */
    String admin1Code;

    /**
     * code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80)
     */
    String admin2Code;

    /**
     * code for third level administrative division, varchar(20)
     */
    String admin3Code;

    /**
     * code for fourth level administrative division, varchar(20)
     */
    String admin4Code;

    /**
     * bigint (8 byte int)
     */
    String population;

    /**
     * in meters, integer
     */
    String elevation;

    /**
     * digital elevation model, srtm3 or gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer. srtm processed by cgiar/ciat.
     */
    String dem;

    /**
     * the iana timezone id (see file timeZone.txt) varchar(40)
     */
    String timezone;

    /**
     * date of last modification in yyyy-MM-dd format
     */
    String modificationDate;

    public GeoName(
            String geonameid,
            String name,
            String asciiname,
            String alternatenames,
            String latitude,
            String longitude,
            String featureClass,
            String featureCode,
            String countryCode,
            String cc2,
            String admin1Code,
            String admin2Code,
            String admin3Code,
            String admin4Code,
            String population,
            String elevation,
            String dem,
            String timezone,
            String modificationDate) {
        this.geonameid = geonameid;
        this.name = name;
        this.asciiname = asciiname;
        this.alternatenames = alternatenames;
        this.latitude = latitude;
        this.longitude = longitude;
        this.featureClass = featureClass;
        this.featureCode = featureCode;
        this.countryCode = countryCode;
        this.cc2 = cc2;
        this.admin1Code = admin1Code;
        this.admin2Code = admin2Code;
        this.admin3Code = admin3Code;
        this.admin4Code = admin4Code;
        this.population = population;
        this.elevation = elevation;
        this.dem = dem;
        this.timezone = timezone;
        this.modificationDate = modificationDate;
    }

    public String getGeonameid() {
        return geonameid;
    }

    public String getName() {
        return name;
    }

    public String getAsciiname() {
        return asciiname;
    }

    public String getAlternatenames() {
        return alternatenames;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getFeatureClass() {
        return featureClass;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCc2() {
        return cc2;
    }

    public String getAdmin1Code() {
        return admin1Code;
    }

    public String getAdmin2Code() {
        return admin2Code;
    }

    public String getAdmin3Code() {
        return admin3Code;
    }

    public String getAdmin4Code() {
        return admin4Code;
    }

    public String getPopulation() {
        return population;
    }

    public String getElevation() {
        return elevation;
    }

    public String getDem() {
        return dem;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    /**
     * Geo-point expressed as a string with the format: "lat,lon".
     */
    public String getLocation() {
        return latitude + "," + longitude;
    }

    @Override
    public String toString() {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            json = sw.toString();
        }
        return json;
    }
}

enum Headers {
    /**
     * integer id of record in geonames database
     */
    geonameid,
    /**
     * name of geographical point (utf8) varchar(200)
     */
    name,
    /**
     * name of geographical point in plain ascii characters, varchar(200)
     */
    asciiname,

    /**
     * alternatenames, comma separated, ascii names automatically transliterated, convenience attribute from alternatename table, varchar(10000)
     */
    alternatenames,
    /**
     * latitude in decimal degrees (wgs84)
     */
    latitude,
    /**
     * longitude in decimal degrees (wgs84)
     */
    longitude,
    /**
     * see http://www.geonames.org/export/codes.html, char(1)
     */
    featureClass,
    /**
     * see http://www.geonames.org/export/codes.html, varchar(10)
     */
    featureCode,
    /**
     * ISO-3166 2-letter country code, 2 characters
     */
    countryCode,
    /**
     * alternate country codes, comma separated, ISO-3166 2-letter country code, 200 characters
     */
    cc2,
    /**
     * fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
     */
    admin1Code,
    /**
     * code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80)
     */
    admin2Code,
    /**
     * code for third level administrative division, varchar(20)
     */
    admin3Code,
    /**
     * code for fourth level administrative division, varchar(20)
     */
    admin4Code,
    /**
     * bigint (8 byte int)
     */
    population,
    /**
     * in meters, integer
     */
    elevation,
    /**
     * digital elevation model, srtm3 or gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer. srtm processed by cgiar/ciat.
     */
    dem,
    /**
     * the iana timezone id (see file timeZone.txt) varchar(40)
     */
    timezone,
    /**
     * date of last modification in yyyy-MM-dd format
     */
    modificationDate
}
