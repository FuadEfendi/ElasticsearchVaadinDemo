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
import com.vaadin.addon.charts.model.AxisTitle;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem3d;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.Marker;
import com.vaadin.addon.charts.model.PlotOptionsBubble;
import com.vaadin.addon.charts.model.TickPosition;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.GradientColor;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.VerticalLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fefendi on 2016-08-11.
 */
public class BubbleChartExample extends AbstractChartExample {
    private static final long serialVersionUID = 1L;

    private static final transient Logger logger = LogManager.getLogger(BubbleChartExample.class);

    String context;

    public void init(String context) {
        this.context = context;
        setCompositionRoot(new com.vaadin.ui.Label("Uninitialized"));
    }

    @Override
    public void attach() {
        VerticalLayout layout = new VerticalLayout();
        if ("bubble".equals(context))
            bubblechart(layout);
        else
            setCompositionRoot(new com.vaadin.ui.Label("Invalid Context"));
        setCompositionRoot(layout);
    }

    // BEGIN-EXAMPLE: charts.charttype.bubble
    void bubblechart(VerticalLayout layout) {
        // Create a bubble chart
        Chart chart = new Chart(ChartType.BUBBLE);
        chart.setWidth("640px");
        chart.setHeight("350px");
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Distribution of Users by Country");
        conf.getLegend().setEnabled(false);
        conf.getTooltip().setFormatter("this.point.name + ': ' + Math.round(this.point.z * this.point.z) + ' profiles'");
        // World map as background
        String url =
                VaadinServlet.getCurrent().getServletContext().getContextPath() +
                        "/VAADIN/themes/elasticsearch-vaadin-demo/img/neocreo_Blue_World_Map_640x.png";
        conf.getChart().setPlotBackgroundImage(url);
        // Show more bubbly bubbles with spherical color gradient
        PlotOptionsBubble plotOptions = new PlotOptionsBubble();
        Marker marker = new Marker();
        GradientColor color = GradientColor.createRadial(0.4, 0.3, 0.7);
        color.addColorStop(0.0, new SolidColor(255, 255, 255, 0.5));
        color.addColorStop(1.0, new SolidColor(170, 70, 67, 0.5));
        marker.setFillColor(color);
        plotOptions.setMarker(marker);
        conf.setPlotOptions(plotOptions);
        DataSeries series = new DataSeries("Countries");
        List<Bucket> buckets = searchCountries();
        for (Bucket bucket : buckets) {
            String country = bucket.getKeyAsString();
            long count = bucket.getDocCount();
            Coordinate pos = getCountryCoordinates(country);
            if (pos == null) {
                logger.warn("country coordinates not found: {}", country);
                continue;
            }
            DataSeriesItem3d item = new DataSeriesItem3d();
            item.setX(pos.longitude * Math.cos(pos.latitude / 2.0 * (Math.PI / 160)));
            item.setY(pos.latitude * 1.2);
            item.setZ(Math.sqrt(count));
            item.setName(country);
            series.add(item);
        }
        conf.addSeries(series);
        // Set the category labels on the axis correspondingly
        XAxis xaxis = new XAxis();
        xaxis.setTitle(new AxisTitle(null));
        xaxis.setExtremes(-180, 180);
        xaxis.setLabels(new Labels(false));
        xaxis.setLineWidth(0);
        xaxis.setLineColor(new SolidColor(0, 0, 0, 0.0)); // Invisible
        conf.addxAxis(xaxis);
        // Set the Y axis title
        YAxis yaxis = new YAxis();
        yaxis.setTitle("");
        yaxis.setExtremes(-90, 90);
        yaxis.setTickPosition(TickPosition.OUTSIDE);
        yaxis.setTickLength(1);
        yaxis.setTickInterval(100);
        yaxis.setTickColor(new SolidColor(0, 0, 0, 0.5)); // Invisible
        yaxis.setTickWidth(0);
        yaxis.setLabels(new Labels(false));
        yaxis.setLineWidth(0);
        yaxis.setLineColor(new SolidColor(0, 0, 0, 0.0)); // Invisible
        conf.addyAxis(yaxis);
        layout.addComponent(chart);
    }

    private List<Bucket> searchCountries() {
        AbstractAggregationBuilder aggregation = AggregationBuilders
                .terms("countries")
                .field("locations.country")
                .minDocCount(1)
                .size(0);
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
    // END-EXAMPLE: charts.charttype.bubble

    class Coordinate implements Serializable {
        private static final long serialVersionUID = 1L;

        public double longitude;
        public double latitude;

        public Coordinate(double lat, double lng) {
            this.longitude = -lng;
            this.latitude = lat;
        }
    }

    static Map<String, Coordinate> countryCoordinates;

    Coordinate getCountryCoordinates(String name) {
        if (countryCoordinates == null)
            countryCoordinates = createCountryCoordinates();
        return countryCoordinates.get(name);
    }

    public Map<String, Coordinate> createCountryCoordinates() {
        HashMap<String, Coordinate> coords = new HashMap<String, Coordinate>();
        coords.put("Afghanistan", new Coordinate(33.00, -65.00));
        coords.put("Akrotiri", new Coordinate(34.62, -32.97));
        coords.put("Albania", new Coordinate(41.00, -20.00));
        coords.put("Algeria", new Coordinate(28.00, -3.00));
        coords.put("American Samoa", new Coordinate(-14.33, 170.00));
        coords.put("Andorra", new Coordinate(42.50, -1.50));
        coords.put("Angola", new Coordinate(-12.50, -18.50));
        coords.put("Anguilla", new Coordinate(18.25, 63.17));
        coords.put("Antarctica", new Coordinate(-90.00, -0.00));
        coords.put("Antigua and Barbuda", new Coordinate(17.05, 61.80));
        coords.put("Arctic Ocean", new Coordinate(90.00, -0.00));
        coords.put("Argentina", new Coordinate(-34.00, 64.00));
        coords.put("Armenia", new Coordinate(40.00, -45.00));
        coords.put("Aruba", new Coordinate(12.50, 69.97));
        coords.put("Ashmore and Cartier Islands", new Coordinate(-12.23, -123.08));
        coords.put("Atlantic Ocean", new Coordinate(0.00, 25.00));
        coords.put("Australia", new Coordinate(-27.00, -133.00));
        coords.put("Austria", new Coordinate(47.33, -13.33));
        coords.put("Österreich", new Coordinate(47.33, -13.33));
        coords.put("Azerbaijan", new Coordinate(40.50, -47.50));
        coords.put("Bahamas, The", new Coordinate(24.25, 76.00));
        coords.put("Bahrain", new Coordinate(26.00, -50.55));
        coords.put("Baker Island", new Coordinate(0.22, 176.47));
        coords.put("Bangladesh", new Coordinate(24.00, -90.00));
        coords.put("Barbados", new Coordinate(13.17, 59.53));
        coords.put("Bassas da India", new Coordinate(-21.50, -39.83));
        coords.put("Belarus", new Coordinate(53.00, -28.00));
        coords.put("Беларусь", new Coordinate(53.00, -28.00));
        coords.put("Belgium", new Coordinate(50.83, -4.00));
        coords.put("Belgique", new Coordinate(50.83, -4.00));
        coords.put("Belize", new Coordinate(17.25, 88.75));
        coords.put("Benin", new Coordinate(9.50, -2.25));
        coords.put("Bermuda", new Coordinate(32.33, 64.75));
        coords.put("Bhutan", new Coordinate(27.50, -90.50));
        coords.put("Bolivia", new Coordinate(-17.00, 65.00));
        coords.put("Bosnia and Herzegovina", new Coordinate(44.00, -18.00));
        coords.put("Botswana", new Coordinate(-22.00, -24.00));
        coords.put("Bouvet Island", new Coordinate(-54.43, -3.40));
        coords.put("Brazil", new Coordinate(-10.00, 55.00));
        coords.put("Brasil", new Coordinate(-10.00, 55.00));
        coords.put("British Virgin Islands", new Coordinate(18.50, 64.50));
        coords.put("Brunei", new Coordinate(4.50, -114.67));
        coords.put("Bulgaria", new Coordinate(43.00, -25.00));
        coords.put("Burkina Faso", new Coordinate(13.00, 2.00));
        coords.put("Burma", new Coordinate(22.00, -98.00));
        coords.put("Burundi", new Coordinate(-3.50, -30.00));
        coords.put("Cambodia", new Coordinate(13.00, -105.00));
        coords.put("Cameroon", new Coordinate(6.00, -12.00));
        coords.put("Canada", new Coordinate(60.00, 95.00));
        coords.put("Cape Verde", new Coordinate(16.00, 24.00));
        coords.put("Cayman Islands", new Coordinate(19.50, 80.50));
        coords.put("Central African Republic", new Coordinate(7.00, -21.00));
        coords.put("Chad", new Coordinate(15.00, -19.00));
        coords.put("Chile", new Coordinate(-30.00, 71.00));
        coords.put("China", new Coordinate(35.00, -105.00));
        coords.put("中国", new Coordinate(35.00, -105.00));
        coords.put("Christmas Island", new Coordinate(-10.50, -105.67));
        coords.put("Clipperton Island", new Coordinate(10.28, 109.22));
        coords.put("Cocos (Keeling) Islands", new Coordinate(-12.50, -96.83));
        coords.put("Colombia", new Coordinate(4.00, 72.00));
        coords.put("Comoros", new Coordinate(-12.17, -44.25));
        coords.put("Congo, Democratic Republic of the", new Coordinate(0.00, -25.00));
        coords.put("Congo, Republic of the", new Coordinate(-1.00, -15.00));
        coords.put("Cook Islands", new Coordinate(-21.23, 159.77));
        coords.put("Coral Sea Islands", new Coordinate(-18.00, -152.00));
        coords.put("Costa Rica", new Coordinate(10.00, 84.00));
        coords.put("Crimea", new Coordinate(49.00, -33.00)); //zzz
        coords.put("Croatia", new Coordinate(45.17, -15.50));
        coords.put("Cuba", new Coordinate(21.50, 80.00));
        coords.put("Cyprus", new Coordinate(35.00, -33.00));
        coords.put("Czech Republic", new Coordinate(49.75, -15.50));
        coords.put("Česká republika", new Coordinate(49.75, -15.50));
        coords.put("Côte d'Ivoire", new Coordinate(8.00, 5.00));
        coords.put("Denmark", new Coordinate(56.00, -10.00));
        coords.put("Danmark", new Coordinate(56.00, -10.00));
        coords.put("Dhekelia", new Coordinate(34.98, -33.75));
        coords.put("Djibouti", new Coordinate(11.50, -43.00));
        coords.put("Dominica", new Coordinate(15.42, 61.33));
        coords.put("Dominican Republic", new Coordinate(19.00, 70.67));
        coords.put("East Timor", new Coordinate(-8.83, -125.92));
        coords.put("Ecuador", new Coordinate(-2.00, 77.50));
        coords.put("Egypt", new Coordinate(27.00, -30.00));
        coords.put("El Salvador", new Coordinate(13.83, 88.92));
        coords.put("Equatorial Guinea", new Coordinate(2.00, -10.00));
        coords.put("Eritrea", new Coordinate(15.00, -39.00));
        coords.put("Estonia", new Coordinate(59.00, -26.00));
        coords.put("Ethiopia", new Coordinate(8.00, -38.00));
        coords.put("Europa Island", new Coordinate(-22.33, -40.37));
        coords.put("Falkland Islands (Islas Malvinas)", new Coordinate(-51.75, 59.00));
        coords.put("Faroe Islands", new Coordinate(62.00, 7.00));
        coords.put("Fiji", new Coordinate(-18.00, -175.00));
        coords.put("Finland", new Coordinate(64.00, -26.00));
        coords.put("Suomi", new Coordinate(64.00, -26.00));
        coords.put("France", new Coordinate(46.00, -2.00));
        coords.put("French Guiana", new Coordinate(4.00, 53.00));
        coords.put("French Polynesia", new Coordinate(-15.00, 140.00));
        coords.put("Gabon", new Coordinate(-1.00, -11.75));
        coords.put("Gambia, The", new Coordinate(13.47, 16.57));
        coords.put("Gaza Strip", new Coordinate(31.42, -34.33));
        coords.put("Georgia", new Coordinate(42.00, -43.50));
        coords.put("Germany", new Coordinate(51.00, -9.00));
        coords.put("Deutschland", new Coordinate(51.00, -9.00));
        coords.put("Ghana", new Coordinate(8.00, 2.00));
        coords.put("Gibraltar", new Coordinate(36.13, 5.35));
        coords.put("Glorioso Islands", new Coordinate(-11.50, -47.33));
        coords.put("Greece", new Coordinate(39.00, -22.00));
        coords.put("Ελλάδα", new Coordinate(39.00, -22.00));
        coords.put("Greenland", new Coordinate(72.00, 40.00));
        coords.put("Grenada", new Coordinate(12.12, 61.67));
        coords.put("Guadeloupe", new Coordinate(16.25, 61.58));
        coords.put("Guam", new Coordinate(13.47, -144.78));
        coords.put("Guatemala", new Coordinate(15.50, 90.25));
        coords.put("Guernsey", new Coordinate(49.47, 2.58));
        coords.put("Guinea", new Coordinate(11.00, 10.00));
        coords.put("Guinea-Bissau", new Coordinate(12.00, 15.00));
        coords.put("Guyana", new Coordinate(5.00, 59.00));
        coords.put("Haiti", new Coordinate(19.00, 72.42));
        coords.put("Heard Island and McDonald Islands", new Coordinate(-53.10, -72.52));
        coords.put("Holy See (Vatican City)", new Coordinate(41.90, -12.45));
        coords.put("Honduras", new Coordinate(15.00, 86.50));
        coords.put("Hong Kong", new Coordinate(22.25, -114.17));
        coords.put("香港", new Coordinate(22.25, -114.17));
        coords.put("Howland Island", new Coordinate(0.80, 176.63));
        coords.put("Hungary", new Coordinate(47.00, -20.00));
        coords.put("Magyarország", new Coordinate(47.00, -20.00));
        coords.put("Iceland", new Coordinate(65.00, 18.00));
        coords.put("India", new Coordinate(20.00, -77.00));
        coords.put("Indian Ocean", new Coordinate(-20.00, -80.00));
        coords.put("Indonesia", new Coordinate(-5.00, -120.00));
        coords.put("Iran", new Coordinate(32.00, -53.00));
        coords.put("Iraq", new Coordinate(33.00, -44.00));
        coords.put("Ireland", new Coordinate(53.00, 8.00));
        coords.put("Israel", new Coordinate(31.50, -34.75));
        coords.put("ישראל", new Coordinate(31.50, -34.75));
        coords.put("Italy", new Coordinate(42.83, -12.83));
        coords.put("Italia", new Coordinate(42.83, -12.83));
        coords.put("Jamaica", new Coordinate(18.25, 77.50));
        coords.put("Jan Mayen", new Coordinate(71.00, 8.00));
        coords.put("Japan", new Coordinate(36.00, -138.00));
        coords.put("日本国", new Coordinate(36.00, -138.00));
        coords.put("日本", new Coordinate(36.00, -138.00));
        coords.put("Jarvis Island", new Coordinate(-0.38, 160.02));
        coords.put("Jersey", new Coordinate(49.25, 2.17));
        coords.put("Johnston Atoll", new Coordinate(16.75, 169.52));
        coords.put("Jordan", new Coordinate(31.00, -36.00));
        coords.put("Juan de Nova Island", new Coordinate(-17.05, -42.75));
        coords.put("Kazakhstan", new Coordinate(48.00, -68.00));
        coords.put("Казахстан", new Coordinate(48.00, -68.00));
        coords.put("Kenya", new Coordinate(1.00, -38.00));
        coords.put("Kingman Reef", new Coordinate(6.38, 162.42));
        coords.put("Kiribati", new Coordinate(1.42, -173.00));
        coords.put("Korea, North", new Coordinate(40.00, -127.00));
        coords.put("Korea, South", new Coordinate(37.00, -127.50));
        coords.put("한국", new Coordinate(37.00, -127.50));
        coords.put("Kuwait", new Coordinate(29.50, -45.75));
        coords.put("Kyrgyzstan", new Coordinate(41.00, -75.00));
        coords.put("Laos", new Coordinate(18.00, -105.00));
        coords.put("Latvia", new Coordinate(57.00, -25.00));
        coords.put("Lebanon", new Coordinate(33.83, -35.83));
        coords.put("Lesotho", new Coordinate(-29.50, -28.50));
        coords.put("Liberia", new Coordinate(6.50, 9.50));
        coords.put("Libya", new Coordinate(25.00, -17.00));
        coords.put("Liechtenstein", new Coordinate(47.27, -9.53));
        coords.put("Lithuania", new Coordinate(56.00, -24.00));
        coords.put("Luxembourg", new Coordinate(49.75, -6.17));
        coords.put("Macau", new Coordinate(22.17, -113.55));
        coords.put("澳門", new Coordinate(22.17, -113.55));
        coords.put("Macedonia, Republic of", new Coordinate(41.83, -22.00));
        coords.put("Madagascar", new Coordinate(-20.00, -47.00));
        coords.put("Malawi", new Coordinate(-13.50, -34.00));
        coords.put("Malaysia", new Coordinate(2.50, -112.50));
        coords.put("Maldives", new Coordinate(3.25, -73.00));
        coords.put("Mali", new Coordinate(17.00, 4.00));
        coords.put("Malta", new Coordinate(35.83, -14.58));
        coords.put("Man, Isle of", new Coordinate(54.25, 4.50));
        coords.put("Marshall Islands", new Coordinate(9.00, -168.00));
        coords.put("Martinique", new Coordinate(14.67, 61.00));
        coords.put("Mauritania", new Coordinate(20.00, 12.00));
        coords.put("Mauritius", new Coordinate(-20.28, -57.55));
        coords.put("Mayotte", new Coordinate(-12.83, -45.17));
        coords.put("Mexico", new Coordinate(23.00, 102.00));
        coords.put("México", new Coordinate(23.00, 102.00));
        coords.put("Micronesia, Federated States of", new Coordinate(6.92, -158.25));
        coords.put("Midway Islands", new Coordinate(28.20, 177.37));
        coords.put("Moldova", new Coordinate(47.00, -29.00));
        coords.put("Monaco", new Coordinate(43.73, -7.40));
        coords.put("Mongolia", new Coordinate(46.00, -105.00));
        coords.put("Montenegro", new Coordinate(42.50, -19.30));
        coords.put("Montserrat", new Coordinate(16.75, 62.20));
        coords.put("Morocco", new Coordinate(32.00, 5.00));
        coords.put("Mozambique", new Coordinate(-18.25, -35.00));
        coords.put("Namibia", new Coordinate(-22.00, -17.00));
        coords.put("Nauru", new Coordinate(-0.53, -166.92));
        coords.put("Navassa Island", new Coordinate(18.42, 75.03));
        coords.put("Nepal", new Coordinate(28.00, -84.00));
        coords.put("Netherlands", new Coordinate(52.50, -5.75));
        coords.put("Nederland", new Coordinate(52.50, -5.75));
        coords.put("Niederlande", new Coordinate(52.50, -5.85));
        coords.put("Netherlands Antilles", new Coordinate(12.25, 68.75));
        coords.put("New Caledonia", new Coordinate(-21.50, -165.50));
        coords.put("New Zealand", new Coordinate(-41.00, -174.00));
        coords.put("Nicaragua", new Coordinate(13.00, 85.00));
        coords.put("Niger", new Coordinate(16.00, -8.00));
        coords.put("Nigeria", new Coordinate(10.00, -8.00));
        coords.put("Niue", new Coordinate(-19.03, 169.87));
        coords.put("Norfolk Island", new Coordinate(-29.03, -167.95));
        coords.put("Northern Mariana Islands", new Coordinate(15.20, -145.75));
        coords.put("Norway", new Coordinate(62.00, -10.00));
        coords.put("Norge", new Coordinate(62.00, -10.00));
        coords.put("Oman", new Coordinate(21.00, -57.00));
        coords.put("Pacific Ocean", new Coordinate(0.00, 160.00));
        coords.put("Pakistan", new Coordinate(30.00, -70.00));
        coords.put("Palau", new Coordinate(7.50, -134.50));
        coords.put("Palmyra Atoll", new Coordinate(5.88, 162.08));
        coords.put("Panama", new Coordinate(9.00, 80.00));
        coords.put("Papua New Guinea", new Coordinate(-6.00, -147.00));
        coords.put("Paracel Islands", new Coordinate(16.50, -112.00));
        coords.put("Paraguay", new Coordinate(-23.00, 58.00));
        coords.put("Peru", new Coordinate(-10.00, 76.00));
        coords.put("Perú", new Coordinate(-10.00, 76.00));
        coords.put("Philippines", new Coordinate(13.00, -122.00));
        coords.put("Pilipinas", new Coordinate(13.00, -122.00));
        coords.put("Pitcairn Islands", new Coordinate(-25.07, 130.10));
        coords.put("Polska", new Coordinate(52.00, -20.00));
        coords.put("Portugal", new Coordinate(39.50, 8.00));
        coords.put("Puerto Rico", new Coordinate(18.25, 66.50));
        coords.put("Qatar", new Coordinate(25.50, -51.25));
        coords.put("Romania", new Coordinate(46.00, -25.00));
        coords.put("Россия", new Coordinate(60.00, -100.00));
        coords.put("Rwanda", new Coordinate(-2.00, -30.00));
        coords.put("Réunion", new Coordinate(-21.10, -55.60));
        coords.put("Saint Barthelemy", new Coordinate(18.50, 63.42));
        coords.put("Saint Helena", new Coordinate(-15.93, 5.70));
        coords.put("Saint Kitts and Nevis", new Coordinate(17.33, 62.75));
        coords.put("Saint Lucia", new Coordinate(13.88, 60.97));
        coords.put("Saint Martin", new Coordinate(18.08, 63.95));
        coords.put("Saint Pierre and Miquelon", new Coordinate(46.83, 56.33));
        coords.put("Saint Vincent and the Grenadines", new Coordinate(13.25, 61.20));
        coords.put("Samoa", new Coordinate(-13.58, 172.33));
        coords.put("San Marino", new Coordinate(43.77, -12.42));
        coords.put("Saudi Arabia", new Coordinate(25.00, -45.00));
        coords.put("Senegal", new Coordinate(14.00, 14.00));
        coords.put("Serbia and Montenegro", new Coordinate(44.00, -21.00));
        coords.put("Seychelles", new Coordinate(-4.58, -55.67));
        coords.put("Sierra Leone", new Coordinate(8.50, 11.50));
        coords.put("Singapore", new Coordinate(1.37, -103.80));
        coords.put("新加坡", new Coordinate(1.37, -103.80));
        coords.put("Slovakia", new Coordinate(48.67, -19.50));
        coords.put("Slovenia", new Coordinate(46.12, -14.82));
        coords.put("Solomon Islands", new Coordinate(-8.00, -159.00));
        coords.put("Somalia", new Coordinate(10.00, -49.00));
        coords.put("South Africa", new Coordinate(-29.00, -24.00));
        coords.put("South Georgia and the South Sandwich Islands", new Coordinate(-54.50, 37.00));
        coords.put("Spain", new Coordinate(40.00, 4.00));
        coords.put("España", new Coordinate(40.00, 4.00));
        coords.put("Spratly Islands", new Coordinate(8.63, -111.92));
        coords.put("Sri Lanka", new Coordinate(7.00, -81.00));
        coords.put("Sudan", new Coordinate(15.00, -30.00));
        coords.put("Suriname", new Coordinate(4.00, 56.00));
        coords.put("Svalbard", new Coordinate(78.00, -20.00));
        coords.put("Swaziland", new Coordinate(-26.50, -31.50));
        coords.put("Sweden", new Coordinate(62.00, -15.00));
        coords.put("Sverige", new Coordinate(62.00, -15.00));
        coords.put("Switzerland", new Coordinate(47.00, -8.00));
        coords.put("Schweiz", new Coordinate(47.00, -8.00));
        coords.put("Syria", new Coordinate(35.00, -38.00));
        coords.put("São Tomé and Príncipe", new Coordinate(1.00, -7.00));
        coords.put("Taiwan", new Coordinate(23.50, -121.00));
        coords.put("台灣", new Coordinate(23.50, -121.00));
        coords.put("Tajikistan", new Coordinate(39.00, -71.00));
        coords.put("Tanzania", new Coordinate(-6.00, -35.00));
        coords.put("Thailand", new Coordinate(15.00, -100.00));
        coords.put("ประเทศไทย", new Coordinate(15.00, -100.00));
        coords.put("Togo", new Coordinate(8.00, -1.17));
        coords.put("Tokelau", new Coordinate(-9.00, 172.00));
        coords.put("Tonga", new Coordinate(-20.00, 175.00));
        coords.put("Trinidad and Tobago", new Coordinate(11.00, 61.00));
        coords.put("Tromelin Island", new Coordinate(-15.87, -54.42));
        coords.put("Tunisia", new Coordinate(34.00, -9.00));
        coords.put("Turkey", new Coordinate(39.00, -35.00));
        coords.put("Türkiye", new Coordinate(39.00, -35.00));
        coords.put("Turkmenistan", new Coordinate(40.00, -60.00));
        coords.put("Turks and Caicos Islands", new Coordinate(21.75, 71.58));
        coords.put("Tuvalu", new Coordinate(-8.00, -178.00));
        coords.put("Uganda", new Coordinate(1.00, -32.00));
        coords.put("Ukraine", new Coordinate(49.00, -32.00));
        coords.put("Україна", new Coordinate(49.00, -32.00));
        coords.put("United Arab Emirates", new Coordinate(24.00, -54.00));
        coords.put("United Kingdom", new Coordinate(54.00, 2.00));
        coords.put("United States", new Coordinate(38.00, 97.00));
        coords.put("Uruguay", new Coordinate(-33.00, 56.00));
        coords.put("Uzbekistan", new Coordinate(41.00, -64.00));
        coords.put("Vanuatu", new Coordinate(-16.00, -167.00));
        coords.put("Venezuela", new Coordinate(8.00, 66.00));
        coords.put("Vietnam", new Coordinate(16.00, -106.00));
        coords.put("Virgin Islands", new Coordinate(18.33, 64.83));
        coords.put("Wake Island", new Coordinate(19.28, -166.65));
        coords.put("Wallis and Futuna", new Coordinate(-13.30, 176.20));
        coords.put("West Bank", new Coordinate(32.00, -35.25));
        coords.put("Western Sahara", new Coordinate(24.50, 13.00));
        coords.put("Yemen", new Coordinate(15.00, -48.00));
        coords.put("Zambia", new Coordinate(-15.00, -30.00));
        coords.put("Zimbabwe", new Coordinate(-20.00, -30.00));
        coords.put("", new Coordinate(-75.00, -50.00));
        coords.put("CA", new Coordinate(-75.00, -60.00));
        return coords;
    }
}
