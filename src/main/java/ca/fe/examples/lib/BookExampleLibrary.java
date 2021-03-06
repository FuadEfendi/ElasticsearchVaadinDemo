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

package ca.fe.examples.lib;

import ca.fe.examples.MyUI;
import ca.fe.examples.autosuggest.AutosuggestExample;
import ca.fe.examples.charts.PersonalInterestsExample;
import ca.fe.examples.charts.UserPreferencesExample;
import ca.fe.examples.charts.WorldMapExample;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookExampleLibrary {
    private static BookExampleLibrary instance = null;

    /**
     * Gets the Book example library singleton instance.
     *
     * @return the singleton instance
     */
    synchronized public static BookExampleLibrary getInstance(File baseDirectory) {
        if (instance == null)
            instance = new BookExampleLibrary(baseDirectory);
        return instance;
    }

    /**
     * Returns all example items, including redirection items.
     *
     * @return array of example objects
     */
    public AbstractExampleItem[] getAllExamples() {
        return examples;
    }

    /**
     * Returns all example items, including redirection items.
     *
     * @return list of example objects
     */
    public List<AbstractExampleItem> getAllExamplesList() {
        return Arrays.asList(examples);
    }

    /**
     * Returns only actual example objects, no redirection items.
     *
     * @return list of BookExample objects
     */
    public List<BookExample> getExamples() {
        ArrayList<BookExample> exampleList = new ArrayList<BookExample>();
        for (int i = 0; i < examples.length; i++)
            if (examples[i] instanceof BookExample)
                exampleList.add((BookExample) examples[i]);
        return exampleList;
    }

    /**
     * Constructor.
     */
    private BookExampleLibrary(File baseDirectory) {
        MyUI.getLogger().info("Loading example data...");
        for (BookExample e : getExamples()) {
            e.loadExample(baseDirectory);
        }
        MyUI.getLogger().info("Loaded {} examples.", getExamples().size());
    }

    final AbstractExampleItem examples[] = {
            new ExampleCtgr("autosuggest", "Autosuggest"),
            new ExampleCtgr("autosuggest.city-", "City Autosuggest"),
            new BookExample("autosuggest.city.basic", "Basic City Autosuggest", AutosuggestExample.class),
            new BookExample("autosuggest.city.searchCitiesInContextUI", "City Autosuggest with Country Context", AutosuggestExample.class),
            new BookExample("autosuggest.city.basicCityList", "City Profiles Count", AutosuggestExample.class),
            new ExampleCtgr("charts", "Charts"),
            new ExampleCtgr("charts.charttype-", "Basic Aggregations"),
            new BookExample("charts.charttype.bubble", "World Map", WorldMapExample.class),
            new BookExample("charts.charttype.pie", "User Preferences", UserPreferencesExample.class),
            new BookExample("charts.charttype.myPersonalInterests", "Personal Interests", PersonalInterestsExample.class),


    };
}
