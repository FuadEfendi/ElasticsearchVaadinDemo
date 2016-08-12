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

package ca.fe.examples.charts;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

import java.util.List;

/**
 * Created by fefendi on 2016-08-12.
 */
public class PersonalInterestsExample extends AbstractChartExample {
    private static final long serialVersionUID = 1L;

    private static final transient Logger logger = LogManager.getLogger(PersonalInterestsExample.class);

    String context;

    public void init(String context) {
        this.context = context;
        setCompositionRoot(new Label("Uninitialized"));
    }

    @Override
    public void attach() {
        VerticalLayout layout = new VerticalLayout();
        if ("myPersonalInterests".equals(context))
            myPersonalInterests(layout);
        else
            setCompositionRoot(new Label("Invalid Context"));
        setCompositionRoot(layout);
    }

    // BEGIN-EXAMPLE: charts.charttype.myPersonalInterests
    private void myPersonalInterests(VerticalLayout layout) {
        List<Bucket> buckets = aggregateByCountryAndPersonalInterests();
        for (Bucket b : buckets) {
            String country = b.getKeyAsString().trim();
            if (b == null) country = "<UNKNOWN>";
            Terms gender = b.getAggregations().get("genders");
            if (gender != null) {
                addHorizontalLayout(layout, country, gender);
            }
        }
    }

    private void addHorizontalLayout(VerticalLayout layout, String title, Terms gender) {
        if (gender.getBuckets().size() < 2) {
            logger.warn("too little genders for {}", title);
            return;
        }
        Label l = new Label(title);
        l.addStyleName(ValoTheme.LABEL_H2);
        layout.addComponent(l);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        Bucket genderBucket = gender.getBuckets().get(0);
        addChart(horizontalLayout, genderBucket);
        genderBucket = gender.getBuckets().get(1);
        addChart(horizontalLayout, genderBucket);
        layout.addComponent(horizontalLayout);
    }

    private void addChart(Layout layout, Bucket b) {
        Terms preferences = b.getAggregations().get("interests");
        Chart chart = new Chart(ChartType.PIE);
        chart.setWidth("100%");
        chart.setHeight("100%");
        Configuration conf = chart.getConfiguration();
        conf.setTitle("male".equals(b.getKeyAsString()) ? "Females" : "Males"); // Bug in MongoDB
        conf.setSubTitle("Active profiles: " + b.getDocCount());
        conf.getLegend().setEnabled(false);
        conf.setCredits(null);
        PlotOptionsPie options = new PlotOptionsPie();
        options.setAnimation(true);
        options.setInnerSize("0"); // Non-0 results in a donut
        options.setSize("40%");  // Default
        options.setCenter("50%", "50%"); // Default
        conf.setPlotOptions(options);
        DataSeries series = new DataSeries();
        for (Bucket preference : preferences.getBuckets()) {
            series.add(new DataSeriesItem(preference.getKeyAsString(), preference.getDocCount()));
        }
        conf.addSeries(series);
        layout.addComponent(chart);
    }

    private List<Bucket> aggregateByCountryAndPersonalInterests() {
        AbstractAggregationBuilder aggregation = AggregationBuilders
                .terms("countries")
                .field("locations.country")
                .minDocCount(1)
                .size(0)
                .subAggregation(
                        AggregationBuilders.terms("genders").field("gender").minDocCount(1).size(2)
                                .subAggregation(
                                        AggregationBuilders.terms("interests").field("myPersonalInterests")
                                                .minDocCount(1).size(0)));
        SearchResponse response = client
                .prepareSearch()
                .setIndices("avid3")
                .setTypes("profile")
                .setSize(0)
                .addAggregation(aggregation)
                .execute()
                .actionGet();
        Aggregations aggregations = response.getAggregations();
        Terms terms = aggregations.get("countries");
        return terms.getBuckets();
    }
    // END-EXAMPLE: charts.charttype.pie
}