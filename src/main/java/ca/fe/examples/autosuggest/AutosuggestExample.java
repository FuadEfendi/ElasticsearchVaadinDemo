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

package ca.fe.examples.autosuggest;

import ca.fe.examples.MyUI;
import ca.fe.examples.lib.AnyBookExampleBundle;
import ca.fe.utils.CountryName;
import ca.fe.utils.GeoName;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;
import com.zybnet.autocomplete.server.AutocompleteSuggestionPickedListener;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.completion.context.CategoryQueryContext;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutosuggestExample extends CustomComponent implements AnyBookExampleBundle {

    private static final long serialVersionUID = 1L;

    private String indexName = "autocomplete";

    private final static String CITY_INDEX = "geonames-004";

    // BEGIN-EXAMPLE: autosuggest.city.basic
    private static Settings settings = Settings.builder()
            .put("cluster.name", "elasticsearch")
            .put("client.transport.sniff", true)
            .build();

    private Client client = null;

    static int myLazyQueryInstanceCounter = 0;

    public AutosuggestExample() {
        try {
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(
                            new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (UnknownHostException e) {
            new RuntimeException(e);
        }
    }
    // END-EXAMPLE: autosuggest.city.basic

    public void basic(VerticalLayout layout) {
        final AutocompleteField<GeoName> search = new AutocompleteField<GeoName>();
        search.setDelay(0);
        search.setMinimumQueryCharacters(1);
        search.setWidth("100%");
        search.setQueryListener(new AutocompleteQueryListener<GeoName>() {
            @Override
            public void handleUserQuery(AutocompleteField<GeoName> field, String query) {
                handleSearchQuery(field, query);
            }
        });
        search.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<GeoName>() {
            @Override
            public void onSuggestionPicked(GeoName page) {
                handleSuggestionSelection(page);
            }
        });
        Label searchLabel = new Label("Search City for");
        searchLabel.setWidth("100%");
        searchLabel.addStyleName("search-label");
        search.addStyleName("search");
        layout.addComponents(searchLabel, search);
    }

    protected void handleSuggestionSelection(GeoName suggestion) {
    }

    private void handleSearchQuery(AutocompleteField<GeoName> field, String query) {
        List<GeoName> cities = searchCities(query);
        //createPageButton.setVisible(cities.isEmpty());
        if (cities == null) return;
        for (GeoName c : cities) {
            field.addSuggestion(c, c.getName());
        }
    }

    // BEGIN-EXAMPLE: autosuggest.city.basic
    private List<GeoName> searchCities(String prefix) {
        List<GeoName> cities = new ArrayList<GeoName>();
        CompletionSuggestionBuilder completionSuggestionBuilder = SuggestBuilders.completionSuggestion("name_suggest").prefix(prefix).size(10);
        SearchResponse searchResponse = client.prepareSearch(CITY_INDEX).suggest(
                new SuggestBuilder().addSuggestion("foo", completionSuggestionBuilder)).setFetchSource(true).get();
        CompletionSuggestion completionSuggestion = searchResponse.getSuggest().getSuggestion("foo");
        CompletionSuggestion.Entry options = completionSuggestion.getEntries().get(0);
        for (CompletionSuggestion.Entry.Option option : options) {
            //cities.add(option.getText().toString());
            SearchHit hit = option.getHit();
            Map<String, Object> s = hit.getSource();
            GeoName city = new GeoName(s);
            MyUI.getLogger().debug("city suggestion retrieved: \n{}", city);
            cities.add(city);
        }
        return cities;
    }
    // END-EXAMPLE: autosuggest.city.basic

    // BEGIN-EXAMPLE: autosuggest.city.basicCityList
    public void basicCityList(VerticalLayout layout) {
        final Table table = new Table(null);
        table.setWidth(100, Unit.PERCENTAGE);
        table.setSelectable(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);
        table.setPageLength(25);
        table.setColumnReorderingAllowed(true);
        MyLazyQueryFactory myLazyQueryFactory = new MyLazyQueryFactory();
        LazyQueryContainer lazyQueryContainer = new LazyQueryContainer(myLazyQueryFactory, null, 100, false);
        lazyQueryContainer.addContainerProperty("name", String.class, "", true, true);
        lazyQueryContainer.addContainerProperty("profileCount", Integer.class, new Integer(0), true, true);
        //lazyQueryContainer.addContainerProperty("myField", String.class, null, true, sortable);
        table.setContainerDataSource(lazyQueryContainer);
        table.setVisibleColumns(new String[]{"name", "profileCount"});
        table.setColumnHeaders(new String[]{"City", "Number of profiles"});
        table.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(final Property.ValueChangeEvent event) {
                Object itemId = table.getValue();
                //BeanItem<City> o = (BeanItem<City>) table.getItem(itemId);
                // currentBean = o.getBean();
                /* EXAMPLE:
                 * Component newCrawledContentTabSheet = buildCrawledContentTabSheet();
                 * if (crawledContentTabSheet == null) {
                 * mainLayout.addComponent(newCrawledContentTabSheet);
                 * } else {
                 * mainLayout.replaceComponent(crawledContentTabSheet,
                 * newCrawledContentTabSheet);
                 * }
                 * crawledContentTabSheet = newCrawledContentTabSheet;
                 */
            }
        });
        layout.addComponent(table);
    }

    /**
     * QueryFactory instantiates new query whenever sort state changes or refresh is requested. Query can construct
     * for example named JPA query in constructor. Data loading starts by invocation of Query.size() method and after
     * this data is loaded in batches by invocations of Query.loadItems().
     */
    public class MyLazyQueryFactory implements QueryFactory {

        private org.vaadin.addons.lazyquerycontainer.QueryDefinition queryDefinition;

        public void setQueryDefinition(final org.vaadin.addons.lazyquerycontainer.QueryDefinition queryDefinition) {
            this.queryDefinition = queryDefinition;
        }

        @Override
        public Query constructQuery(QueryDefinition queryDefinition) {
            Object property = null;
            boolean ascending = true;
            if (queryDefinition.getSortPropertyIds() != null && queryDefinition.getSortPropertyIds().length > 0) {
                property = queryDefinition.getSortPropertyIds()[0];
                ascending = queryDefinition.getSortPropertyAscendingStates()[0];
            }
            return new MyLazyQuery(property, ascending);
        }
    }

    public class MyLazyQuery implements Query {

        private final List<City> cities = new ArrayList<>();

        public MyLazyQuery(Object sortPropertyId, boolean ascending) {
            MyUI.getLogger().debug("instance {} created", myLazyQueryInstanceCounter++);
            // Example: count of distinct cities
//            SearchResponse response = client.prepareSearch("avid3")
//                    .addAggregation(AggregationBuilders.cardinality("distinct_cities").field("locations.city"))
//                    .setSize(0)
//                    .execute().actionGet();
//            MyUI.getLogger().debug(response.toString());
//            Cardinality cardinality = response.getAggregations().get("distinct_cities");
//            numFound = cardinality.getValue();
//            MyUI.getLogger().debug("numFound: {}", numFound);
            // TODO: ES 2.3 does not support "pagination" for aggregation queries, so that we load all cities (175000):
            SearchResponse response = client.prepareSearch("avid3")
                    .addAggregation(AggregationBuilders.terms("popular_cities").field("locations.city")
                            .size(Integer.MAX_VALUE))
                    .setSize(0)
                    .execute().actionGet();
            MyUI.getLogger().trace(response.toString());
            Terms terms = response.getAggregations().get("popular_cities");
            for (Terms.Bucket bean : terms.getBuckets()) {
                City city = new City();
                city.setName(bean.getKeyAsString());
                city.setProfileCount((int) bean.getDocCount());
                //cities.add(new BeanItem<>(city));
                cities.add(city);
            }
            if (sortPropertyId == null) {
                // sorting by profile count (default)
            } else if (sortPropertyId.equals("profileCount") && ascending) {
                Collections.sort(cities, new City.ProfileCountComparator());
            } else if (sortPropertyId.equals("profileCount") && !ascending) {
                Collections.sort(cities, new City.ProfileCountComparatorDescending());
            } else if (sortPropertyId.equals("name") && ascending) {
                Collections.sort(cities, new City.NameComparator());
            } else if (sortPropertyId.equals("name") && !ascending) {
                Collections.sort(cities, new City.NameComparatorDescending());
            }
        }

        @Override
        public Item constructItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean deleteAllItems() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Item> loadItems(final int startIndex, final int count) {
            MyUI.getLogger().debug("loadItems() called... startIndex: {}, count: {}", startIndex, count);
            List<Item> subset = new ArrayList<>();
            for (int i = startIndex; i < startIndex + count; i++) {
                subset.add(new BeanItem<>(cities.get(i)));
            }
            return subset;
        }

        @Override
        public void saveItems(List<Item> list, List<Item> list1, List<Item> list2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return cities.size();
        }
    }
    // END-EXAMPLE: autosuggest.city.basicCityList

    // BEGIN-EXAMPLE: autosuggest.city.searchCitiesInContextUI
    private CountryName selectedCountry = null;

    public void searchCitiesInContextUI(VerticalLayout layout) {
        // "Country" field
        final AutocompleteField<CountryName> countryField = new AutocompleteField<>();
        countryField.setDelay(0);
        countryField.setMinimumQueryCharacters(1);
        countryField.setWidth("75%");
        countryField.setQueryListener(new AutocompleteQueryListener<CountryName>() {
            @Override
            public void handleUserQuery(AutocompleteField<CountryName> field, String query) {
                try {
                    List<CountryName> countries = searchCountries(query);
                    for (CountryName c : countries) {
                        field.addSuggestion(c, c.getCountry());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        countryField.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<CountryName>() {
            @Override
            public void onSuggestionPicked(CountryName cn) {
                selectedCountry = cn;
            }
        });
        Label countryLabel = new Label("Country:");
        countryLabel.setWidth("75%");
        countryLabel.addStyleName("countryField-label");
        countryField.addStyleName("countryField");
        layout.addComponents(countryLabel, countryField);
        // "City" field
        final AutocompleteField<GeoName> search = new AutocompleteField<>();
        search.setDelay(0);
        search.setMinimumQueryCharacters(1);
        search.setWidth("75%");
        search.setQueryListener(new AutocompleteQueryListener<GeoName>() {
            @Override
            public void handleUserQuery(AutocompleteField<GeoName> field, String query) {
                if (selectedCountry == null) return;
                try {
                    List<GeoName> cities = searchCities(query, selectedCountry.getIso());
                    for (GeoName c : cities) {
                        field.addSuggestion(c, c.getName());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        search.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<GeoName>() {
            @Override
            public void onSuggestionPicked(GeoName page) {
                handleSuggestionSelection(page);
            }
        });
        Label searchLabel = new Label("City:");
        searchLabel.setWidth("100%");
        searchLabel.addStyleName("countryField-label");
        search.addStyleName("countryField");
        layout.addComponents(searchLabel, search);
    }

    private List<CountryName> searchCountries(String prefix) {
        System.out.println("search called with " + prefix);
        List<CountryName> countries = new ArrayList<>();
        // following examples at https://github.com/elastic/elasticsearch/blob/master/core/src/test/java/org/elasticsearch/search/suggest/CompletionSuggestSearchIT.java
        CompletionSuggestionBuilder completionSuggestionBuilder = SuggestBuilders.completionSuggestion("country_suggest").prefix(prefix).size(10);
        SearchResponse searchResponse = client.prepareSearch("country-003").suggest(
                new SuggestBuilder().addSuggestion("foo", completionSuggestionBuilder)).setFetchSource(true).get();
        CompletionSuggestion completionSuggestion = searchResponse.getSuggest().getSuggestion("foo");
        CompletionSuggestion.Entry options = completionSuggestion.getEntries().get(0);
        for (CompletionSuggestion.Entry.Option option : options) {
            SearchHit hit = option.getHit();
            Map<String, Object> s = hit.getSource();
            CountryName cn = new CountryName(
                    (String) s.get("iso"),
                    (String) s.get("iso3"),
                    (String) s.get("isoNumeric"),
                    (String) s.get("fips"),
                    (String) s.get("country"),
                    (String) s.get("capital"),
                    (String) s.get("areaSqKm"),
                    (String) s.get("population"),
                    (String) s.get("continent"),
                    (String) s.get("tld"),
                    (String) s.get("currencyCode"),
                    (String) s.get("currencyName"),
                    (String) s.get("phone"),
                    (String) s.get("postalCodeFormat"),
                    (String) s.get("postalCodeRegex"),
                    (String) s.get("languages"),
                    (String) s.get("geonameid"),
                    (String) s.get("neighbours"),
                    (String) s.get("equivalentFipsCode")
            );
            countries.add(cn);
        }
        return countries;
    }

    private List<GeoName> searchCities(String prefix, String iso) {
        MyUI.getLogger().debug("searchCities called: {} {}", prefix, iso);
        List<GeoName> cities = new ArrayList<GeoName>();
        Map<String, List<? extends ToXContent>> contextMap = new HashMap<>();
        List<CategoryQueryContext> contexts = new ArrayList<>();
        final CategoryQueryContext.Builder builder = CategoryQueryContext.builder();
        builder.setCategory("countryCode_context");
        CategoryQueryContext categoryQueryContext = CategoryQueryContext.builder().setCategory(iso).build();
        List<? extends ToXContent> queryContextValues = Collections.singletonList(categoryQueryContext);
        Map<String, List<? extends ToXContent>> queryContexts =
                Collections.singletonMap("countryCode_context", queryContextValues);
        CompletionSuggestionBuilder completionSuggestionBuilder = SuggestBuilders
                .completionSuggestion("name_suggest").prefix(prefix).size(10).contexts(queryContexts);
        SearchResponse searchResponse = client.prepareSearch(CITY_INDEX).suggest(
                new SuggestBuilder().addSuggestion("foo", completionSuggestionBuilder)).setFetchSource(true).get();
        CompletionSuggestion completionSuggestion = searchResponse.getSuggest().getSuggestion("foo");
        CompletionSuggestion.Entry options = completionSuggestion.getEntries().get(0);
        for (CompletionSuggestion.Entry.Option option : options) {
            SearchHit hit = option.getHit();
            Map<String, Object> s = hit.getSource();
            GeoName city = new GeoName(s);
            MyUI.getLogger().debug("city suggestion retrieved: \n{}", city);
            cities.add(city);
        }
        return cities;
    }
    // END-EXAMPLE: autosuggest.city.searchCitiesInContextUI
}
