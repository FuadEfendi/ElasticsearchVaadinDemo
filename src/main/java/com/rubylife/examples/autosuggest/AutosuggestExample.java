package com.rubylife.examples.autosuggest;

import com.rubylife.examples.lib.AnyBookExampleBundle;
import com.vaadin.ui.*;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;
import com.zybnet.autocomplete.server.AutocompleteSuggestionPickedListener;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;

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
        CssLayout header = new CssLayout(searchLabel, search);
        header.addStyleName("header");
        BrowserFrame wikipediaPage = new BrowserFrame();
        CssLayout body = new CssLayout(wikipediaPage);
        body.addStyleName("body");
        layout.addComponents(header, body);
    }

    protected void handleSuggestionSelection(City suggestion) {
    }

    private void handleSearchQuery(AutocompleteField<City> field, String query) {
        try {
            List<String> cities = searchCities(query);
            //createPageButton.setVisible(cities.isEmpty());
            for (String c : cities) {
                City city = new City();
                city.setTitle(c);
                field.addSuggestion(city, city.getTitle());
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
}
