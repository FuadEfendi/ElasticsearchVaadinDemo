package com.rubylife.examples;

import com.rubylife.examples.lib.BookExampleLibrary;
import com.rubylife.examples.lib.MyCustomServlet;
import com.rubylife.examples.lib.ui.ExamplesMainLayout;
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
//@Theme("mytheme")
@Widgetset("com.rubylife.MyAppWidgetset")
@Theme("book-examples")
@Title("Rubylife UI Examples")
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

    //@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @WebServlet(value = { "/*", "/VAADIN/*" }, name = "MyUI", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends MyCustomServlet {
    }




}
