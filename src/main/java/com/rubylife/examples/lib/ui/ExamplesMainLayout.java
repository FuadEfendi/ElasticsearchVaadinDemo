package com.rubylife.examples.lib.ui;

import com.rubylife.examples.lib.AbstractExampleItem;
import com.vaadin.server.Responsive;
import com.vaadin.ui.HorizontalLayout;

import java.util.List;

public class ExamplesMainLayout extends HorizontalLayout {
    
    /**
     * 
     */
    private static final long serialVersionUID = -5136190283773415103L;

    public ExamplesMainLayout(List<AbstractExampleItem> examples) {
        this.setSizeFull();
        Responsive.makeResponsive(this);
        
        ExampleLayout example = new ExampleLayout();
        TreeMenu menu = new TreeMenu(example.exampleLayout, example.exampleTitle);
        menu.buildMenu(examples);
        this.addComponents(menu, example);
        this.setExpandRatio(example, 1.0f);
    }

}
