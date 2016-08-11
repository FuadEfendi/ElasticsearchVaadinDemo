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

package ca.fe.examples;

import ca.fe.examples.lib.BookExampleLibrary;
import ca.fe.examples.lib.MyCustomServlet;
import ca.fe.examples.lib.ui.ExamplesMainLayout;
import com.vaadin.annotations.*;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import java.io.File;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Widgetset("ca.fe.MyAppWidgetset")
@Theme("book-examples")
@Title("Vaadin + Elasticsearch Examples")
@Push
public class MyUI extends UI {

    private static final transient Logger logger = LogManager.getLogger(MyUI.class);

    public static final String APPCONTEXT = "";

    public static Logger getLogger() {
        return MyUI.logger;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        logger.info("MaxInactiveInterval " + VaadinSession.getCurrent()
                .getSession().getMaxInactiveInterval());
        File baseDir = VaadinService.getCurrent().getBaseDirectory();
        ExamplesMainLayout mainLayout = new ExamplesMainLayout(
                BookExampleLibrary.getInstance(baseDir).getAllExamplesList());
        setContent(mainLayout);
    }

    @WebServlet(value = {"/*", "/VAADIN/*"}, name = "MyUI", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends MyCustomServlet {
    }
}
