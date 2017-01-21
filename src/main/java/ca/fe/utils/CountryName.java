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
 * Created by fefendi on 2017-01-20. <br/>
 * As per <a href="http://download.geonames.org/export/dump/countryInfo.txt" >http://download.geonames.org/export/dump/countryInfo.txt</a> retrieved on 2017.01.20
 */
public class CountryName {
    public CountryName(
            String iso,
            String iso3,
            String isoNumeric,
            String fips,
            String country,
            String capital,
            String areaSqKm,
            String population,
            String continent,
            String tld,
            String currencyCode,
            String currencyName,
            String phone,
            String postalCodeFormat,
            String postalCodeRegex,
            String languages,
            String geonameid,
            String neighbours,
            String equivalentFipsCode) {
        this.iso = iso;
        this.iso3 = iso3;
        this.isoNumeric = isoNumeric;
        this.fips = fips;
        this.country = country;
        this.capital = capital;
        this.areaSqKm = areaSqKm;
        this.population = population;
        this.continent = continent;
        this.tld = tld;
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.phone = phone;
        this.postalCodeFormat = postalCodeFormat;
        this.postalCodeRegex = postalCodeRegex;
        this.languages = languages;
        this.geonameid = geonameid;
        this.neighbours = neighbours;
        this.equivalentFipsCode = equivalentFipsCode;
    }

    /**
     * ISO
     */
    private String iso;

    /**
     * ISO3
     */
    private String iso3;

    /**
     * ISO-Numeric
     */
    private String isoNumeric;

    /**
     * fips
     */
    private String fips;

    /**
     * Country
     */
    private String country;

    /**
     * Capital
     */
    private String capital;

    /**
     * Area(in sq km)
     */
    private String areaSqKm;

    /**
     * Population
     */
    private String population;

    /**
     * Continent
     */
    private String continent;

    /**
     * tld
     */
    private String tld;

    /**
     * CurrencyCode
     */
    private String currencyCode;

    /**
     * CurrencyName
     */
    private String currencyName;

    /**
     * Phone
     */
    private String phone;

    /**
     * Postal Code Format
     */
    private String postalCodeFormat;

    /**
     * Postal Code Regex
     */
    private String postalCodeRegex;

    /**
     * Languages
     */
    private String languages;

    /**
     * geonameid
     */
    private String geonameid;

    /**
     * neighbours
     */
    private String neighbours;

    /**
     * EquivalentFipsCode
     */
    private String equivalentFipsCode;

    public String getIso() {
        return iso;
    }

    public String getIso3() {
        return iso3;
    }

    public String getIsoNumeric() {
        return isoNumeric;
    }

    public String getFips() {
        return fips;
    }

    public String getCountry() {
        return country;
    }

    public String getCapital() {
        return capital;
    }

    public String getAreaSqKm() {
        return areaSqKm;
    }

    public String getPopulation() {
        return population;
    }

    public String getContinent() {
        return continent;
    }

    public String getTld() {
        return tld;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getPhone() {
        return phone;
    }

    public String getPostalCodeFormat() {
        return postalCodeFormat;
    }

    public String getPostalCodeRegex() {
        return postalCodeRegex;
    }

    public String getLanguages() {
        return languages;
    }

    public String getGeonameid() {
        return geonameid;
    }

    public String getNeighbours() {
        return neighbours;
    }

    public String getEquivalentFipsCode() {
        return equivalentFipsCode;
    }

    @Override
    public String toString() {
//        return new ToStringBuilder(this)
//                .append("iso", iso)
//                .append("iso3", iso3)
//                .append("isoNumeric", isoNumeric)
//                .append("fips", fips)
//                .append("country", country)
//                .append("capital", capital)
//                .append("areaSqKm", areaSqKm)
//                .append("population", population)
//                .append("continent", continent)
//                .append("tld", tld)
//                .append("currencyCode", currencyCode)
//                .append("currencyName", currencyName)
//                .append("phone", phone)
//                .append("postalCodeFormat", postalCodeFormat)
//                .append("postalCodeRegex", postalCodeRegex)
//                .append("languages", languages)
//                .append("geonameid", geonameid)
//                .append("neighbours", neighbours)
//                .append("equivalentFipsCode", equivalentFipsCode)
//                .toString();
//
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

enum CountryHeaders {
    /**
     * ISO
     */
    ISO,

    /**
     * ISO3
     */
    ISO3,

    /**
     * ISO-Numeric
     */
    ISO_Numeric,

    /**
     * fips
     */
    fips,

    /**
     * Country
     */
    Country,

    /**
     * Capital
     */
    Capital,

    /**
     * Area(in sq km)
     */
    Area_in_sq_km,

    /**
     * Population
     */
    Population,

    /**
     * Continent
     */
    Continent,

    /**
     * tld
     */
    tld,

    /**
     * CurrencyCode
     */
    CurrencyCode,

    /**
     * CurrencyName
     */
    CurrencyName,

    /**
     * Phone
     */
    Phone,

    /**
     * Postal Code Format
     */
    Postal_Code_Format,

    /**
     * Postal Code Regex
     */
    Postal_Code_Regex,

    /**
     * Languages
     */
    Languages,

    /**
     * geonameid
     */
    geonameid,

    /**
     * neighbours
     */
    neighbours,

    /**
     * EquivalentFipsCode
     */
    EquivalentFipsCode
}