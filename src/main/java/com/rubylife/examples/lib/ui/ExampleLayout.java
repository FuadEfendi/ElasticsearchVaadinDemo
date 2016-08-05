package com.rubylife.examples.lib.ui;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class ExampleLayout extends VerticalLayout {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3624125034743059087L;
    
    public final Label exampleTitle = new Label();
    public final VerticalLayout exampleLayout = new VerticalLayout();
    
    public ExampleLayout() {
        this.setSizeFull();
        this.initTitlebar();
        this.initExamplePanel();
    }
    
    private void initTitlebar() {
        HorizontalLayout titlebar = new HorizontalLayout();
        titlebar.addStyleName("titlebar");
        titlebar.setWidth("100%");
        
        Label title = new Label("Basic UI Examples");
        title.addStyleName("title");
        title.setWidthUndefined();
        
        titlebar.addComponent(title);
        
        exampleTitle.setWidthUndefined();
        titlebar.addComponent(exampleTitle);
        titlebar.setComponentAlignment(exampleTitle, Alignment.MIDDLE_RIGHT);
        
        this.addComponent(titlebar);
    }
    
    private void initExamplePanel() {
        Panel examplePanel = new Panel();
        examplePanel.addStyleName("viewpanel");
        examplePanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        examplePanel.setSizeFull();
        
        exampleLayout.addStyleName("viewlayout");
        exampleLayout.setSpacing(true);
        exampleLayout.setMargin(true);
        examplePanel.setContent(exampleLayout);
        
        this.addComponent(examplePanel);
        this.setExpandRatio(examplePanel, 1.0f);
    }

}
