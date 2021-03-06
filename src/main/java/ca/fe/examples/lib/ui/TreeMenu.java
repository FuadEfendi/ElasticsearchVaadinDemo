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

package ca.fe.examples.lib.ui;

import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class TreeMenu extends AbstractExampleMenu {
    private static final long serialVersionUID = 1L;

    public TreeMenu(Layout viewLayout, Label exampleTitle) {
        super(viewLayout, exampleTitle);
        addStyleName("examplemenu");
        setWidth("280px");
        setHeight("100%");
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        //Image logo = new Image(null, new ThemeResource("img/tokenizer-logo.png"));
        //logo.addStyleName("vaadinlogo");
        //content.addComponent(logo);
        Panel scrollpanel = new Panel("Examples");
        scrollpanel.setSizeFull();
        scrollpanel.addStyleName(Reindeer.PANEL_LIGHT);
        scrollpanel.addStyleName("menupanel");
        scrollpanel.addStyleName("scrollmenu");
        scrollpanel.setContent(menu);
        content.addComponent(scrollpanel);
        content.setExpandRatio(scrollpanel, 1.0f);
        setCompositionRoot(content);
    }
}
