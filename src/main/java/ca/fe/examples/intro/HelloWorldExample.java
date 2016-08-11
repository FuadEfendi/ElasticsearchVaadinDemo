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

package ca.fe.examples.intro;

import ca.fe.examples.lib.BookExampleBundle;
import ca.fe.examples.MyUI;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.VerticalLayout;

public class HelloWorldExample extends CustomComponent implements BookExampleBundle {
    private static final long serialVersionUID = -4292553844521293140L;

    public static final String helloworldDescription =
            "<h1>Hello World</h1>" +
            "<p>You make a basic Vaadin application by extending the <b>UI</b> component and implementing the init() method.</p>" +
            "<ul>" +
            "  <li>The caption of the root component is usually shown in title bar of the browser window or as the caption of the tab.</li>" +
            "  <li>The user interface is built by adding components to the root component.</li>" +
            "</ul>";
    
    public void init(String context) {
        VerticalLayout layout = new VerticalLayout();
        
        Embedded embedded = new Embedded();
        embedded.setSource(new ExternalResource(MyUI.APPCONTEXT + "/helloworld?restartApplication"));
        embedded.setType(Embedded.TYPE_BROWSER);
        embedded.setWidth("200px");
        embedded.setHeight("100px");
        layout.addComponent(embedded);

        setCompositionRoot(layout);
    }
}
