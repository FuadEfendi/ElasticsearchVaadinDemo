package com.rubylife.examples.autosuggest;

import com.rubylife.examples.MyUI;
import com.rubylife.examples.lib.AnyBookExampleBundle;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.*;
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
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class AutosuggestExample extends CustomComponent implements AnyBookExampleBundle {

    private static final long serialVersionUID = 1L;

    // BEGIN-EXAMPLE: autosuggest.city.basic
    private static Settings settings = Settings.settingsBuilder()
            .put("cluster.name", "elasticsearch")
            .put("client.transport.sniff", true)
            .build();

    private static Client client = null;

    static List<Item> items = new ArrayList<Item>();

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

    public void basic(VerticalLayout layout) throws UnknownHostException {
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

    // BEGIN-EXAMPLE: autosuggest.city.search
    public void basicCityList(VerticalLayout layout) throws UnknownHostException {
        buildTable(layout);
    }
    // END-EXAMPLE: autosuggest.city.search

    private void buildTable(Layout layout) {
        final Table table = new Table(null);
        table.setWidth(100, Unit.PERCENTAGE);
        table.setSelectable(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(false);
        MyLazyQueryFactory myLazyQueryFactory = new MyLazyQueryFactory();
        LazyQueryContainer lazyQueryContainer = new LazyQueryContainer(myLazyQueryFactory, null, 25, false);
        lazyQueryContainer.addContainerProperty("name", String.class, "", true, false);
        lazyQueryContainer.addContainerProperty("profileCount", Integer.class, new Integer(0), true, false);
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
                //LOG.warn(currentBean.toString());
                /*
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

    public class MyLazyQueryFactory implements org.vaadin.addons.lazyquerycontainer.QueryFactory {

        private org.vaadin.addons.lazyquerycontainer.QueryDefinition queryDefinition;

        public void setQueryDefinition(final org.vaadin.addons.lazyquerycontainer.QueryDefinition queryDefinition) {
            this.queryDefinition = queryDefinition;
        }

        @Override
        public org.vaadin.addons.lazyquerycontainer.Query constructQuery(org.vaadin.addons.lazyquerycontainer.QueryDefinition queryDefinition) {
            return new MyLazyQuery();
        }
    }

    public class MyLazyQuery implements org.vaadin.addons.lazyquerycontainer.Query {

        long numFound = 0;

        public MyLazyQuery() {
            SearchResponse response = client.prepareSearch("avid3")
                    .addAggregation(AggregationBuilders.cardinality("distinct_cities").field("locations.city"))
                    .setSize(0)
                    .execute().actionGet();
            MyUI.getLogger().debug(response.toString());
            Cardinality cardinality = response.getAggregations().get("distinct_cities");
            numFound = cardinality.getValue();
            MyUI.getLogger().debug("numFound: {}", numFound);
        }

        @Override
        public Item constructItem() {
            PropertysetItem item = new PropertysetItem();
            return item;
        }

        @Override
        public boolean deleteAllItems() {
            throw new UnsupportedOperationException();
        }


        @Override
        public List<Item> loadItems(final int startIndex, final int count) {
            MyUI.getLogger().debug("loadItems() called... startIndex: {}, count: {}", startIndex, count);

            if (items.size() == 0) {
                SearchResponse response = client.prepareSearch("avid3")
                        .addAggregation(AggregationBuilders.terms("popular_cities").field("locations.city").size(Integer.MAX_VALUE))
                        .setSize(0)
                        .execute().actionGet();
                MyUI.getLogger().debug(response.toString());
                Terms terms = response.getAggregations().get("popular_cities");

                for (Terms.Bucket bean : terms.getBuckets()) {
                    City city = new City();
                    city.setName(bean.getKeyAsString());
                    city.setProfileCount((int) bean.getDocCount());
                    items.add(new BeanItem<City>(city));
                }
            }

            List<Item> subset = new ArrayList<Item>();

            for (int i = startIndex; i < startIndex + count; i++) {
                subset.add(items.get(i));
            }

            return subset;

        }

        @Override
        public void saveItems(List<Item> list, List<Item> list1, List<Item> list2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return (int) numFound;
        }
    }
}
