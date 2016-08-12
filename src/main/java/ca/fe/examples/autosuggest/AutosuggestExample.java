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
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutosuggestExample extends CustomComponent implements AnyBookExampleBundle {

    private static final long serialVersionUID = 1L;

    // BEGIN-EXAMPLE: autosuggest.city.basic
    private static Settings settings = Settings.settingsBuilder()
            .put("cluster.name", "elasticsearch")
            .put("client.transport.sniff", true)
            .build();

    private static Client client = null;

    static int myLazyQueryInstanceCounter = 0;

    static {
        try {
            client = TransportClient
                    .builder()
                    .settings(settings)
                    .build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.31.44.227"), 9300));
        } catch (UnknownHostException e) {
            new RuntimeException(e);
        }
    }
    // END-EXAMPLE: autosuggest.city.basic

    public AutosuggestExample() {
    }

    public void basic(VerticalLayout layout) {
        final AutocompleteField<City> search = new AutocompleteField<City>();
        search.setDelay(0);
        search.setMinimumQueryCharacters(1);
        search.setWidth("100%");
        search.setQueryListener(new AutocompleteQueryListener<City>() {
            @Override
            public void handleUserQuery(AutocompleteField<City> field, String query) {
                handleSearchQuery(field, query);
            }
        });
        search.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<City>() {
            @Override
            public void onSuggestionPicked(City page) {
                handleSuggestionSelection(page);
            }
        });
        Label searchLabel = new Label("Search City for");
        searchLabel.setWidth("100%");
        searchLabel.addStyleName("search-label");
        search.addStyleName("search");
        layout.addComponents(searchLabel, search);
    }

    protected void handleSuggestionSelection(City suggestion) {
    }

    private void handleSearchQuery(AutocompleteField<City> field, String query) {
        try {
            List<String> cities = searchCities(query);
            //createPageButton.setVisible(cities.isEmpty());
            for (String c : cities) {
                City city = new City();
                city.setName(c);
                field.addSuggestion(city, city.getName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // BEGIN-EXAMPLE: autosuggest.city.basic
    private List<String> searchCities(String prefix) {
        List<String> cities = new ArrayList<String>();
        String suggestionName = "city";
        CompletionSuggestionBuilder completionSuggestionBuilder = new CompletionSuggestionBuilder(suggestionName);
        SuggestResponse suggestResponse = client
                .prepareSuggest("autocomplete")
                .setSuggestText(prefix)
                .addSuggestion(completionSuggestionBuilder.field("city-suggest").size(10))
                .execute()
                .actionGet();
        Suggest suggest = suggestResponse.getSuggest();
        Suggest.Suggestion suggestion = suggest.getSuggestion(suggestionName);
        List<Suggest.Suggestion.Entry> list = suggestion.getEntries();
        for (Suggest.Suggestion.Entry entry : list) {
            List<Suggest.Suggestion.Entry.Option> options = entry.getOptions();
            for (Suggest.Suggestion.Entry.Option option : options) {
                cities.add(option.getText().toString());
            }
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
    public void searchCitiesInContextUI(VerticalLayout layout) {
        // "Country" field
        final AutocompleteField<Country> countryField = new AutocompleteField<>();
        countryField.setDelay(0);
        countryField.setMinimumQueryCharacters(1);
        countryField.setWidth("75%");
        countryField.setQueryListener(new AutocompleteQueryListener<Country>() {
            @Override
            public void handleUserQuery(AutocompleteField<Country> field, String query) {
                try {
                    List<Country> countries = searchCountries(query);
                    for (Country c : countries) {
                        field.addSuggestion(c, c.getName());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        countryField.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<Country>() {
            @Override
            public void onSuggestionPicked(Country page) {
                //createPageButton.setVisible(cities.isEmpty());
            }
        });
        Label countryLabel = new Label("Country:");
        countryLabel.setWidth("75%");
        countryLabel.addStyleName("countryField-label");
        countryField.addStyleName("countryField");
        layout.addComponents(countryLabel, countryField);
        // "City" field
        final AutocompleteField<City> search = new AutocompleteField<>();
        search.setDelay(0);
        search.setMinimumQueryCharacters(1);
        search.setWidth("75%");
        search.setQueryListener(new AutocompleteQueryListener<City>() {
            @Override
            public void handleUserQuery(AutocompleteField<City> field, String query) {
                try {
                    List<City> cities = searchCities(query, countryField.getText());
                    for (City c : cities) {
                        field.addSuggestion(c, c.getName());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        search.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<City>() {
            @Override
            public void onSuggestionPicked(City page) {
                handleSuggestionSelection(page);
            }
        });
        Label searchLabel = new Label("City:");
        searchLabel.setWidth("100%");
        searchLabel.addStyleName("countryField-label");
        search.addStyleName("countryField");
        layout.addComponents(searchLabel, search);
    }

    private List<Country> searchCountries(String prefix) {
        List<Country> countries = new ArrayList<>();
        CompletionSuggestionBuilder completionSuggestionBuilder = new CompletionSuggestionBuilder("country");
        SuggestResponse suggestResponse = client
                .prepareSuggest("country")
                .setSuggestText(prefix)
                .addSuggestion(completionSuggestionBuilder.field("country").size(5))
                .execute()
                .actionGet();
        Suggest suggest = suggestResponse.getSuggest();
        Suggest.Suggestion suggestion = suggest.getSuggestion("country");
        List<Suggest.Suggestion.Entry> list = suggestion.getEntries();
        for (Suggest.Suggestion.Entry entry : list) {
            List<Suggest.Suggestion.Entry.Option> options = entry.getOptions();
            for (Suggest.Suggestion.Entry.Option option : options) {
                Country c = new Country();
                c.setName(option.getText().toString());
                countries.add(c);
            }
        }
        return countries;
    }

    private List<City> searchCities(String prefix, String country) {
        List<City> cities = new ArrayList<>();
        CompletionSuggestionBuilder completionSuggestionBuilder = new CompletionSuggestionBuilder("city");
        SuggestResponse suggestResponse = client
                .prepareSuggest("city-province-country")
                .setSuggestText(prefix)
                .addSuggestion(completionSuggestionBuilder.field("city").size(10).addContextField("country", country))
                .execute()
                .actionGet();
        Suggest suggest = suggestResponse.getSuggest();
        MyUI.getLogger().debug("suggest:\n{}", suggest);
        Suggest.Suggestion suggestion = suggest.getSuggestion("city");
        List<Suggest.Suggestion.Entry> list = suggestion.getEntries();
        for (Suggest.Suggestion.Entry entry : list) {
            List<Suggest.Suggestion.Entry.Option> options = entry.getOptions();
            for (Suggest.Suggestion.Entry.Option option : options) {
                CompletionSuggestion.Entry.Option o = (CompletionSuggestion.Entry.Option) option;
                City c = new City();
                c.setName(o.getText().toString());
                c.setCity((String) o.getPayloadAsMap().get("city"));
                c.setProvince((String) o.getPayloadAsMap().get("province"));
                c.setCountry((String) o.getPayloadAsMap().get("country"));
                cities.add(c);
                MyUI.getLogger().debug(c);
            }
        }
        return cities;
    }
    // END-EXAMPLE: autosuggest.city.searchCitiesInContextUI
}
