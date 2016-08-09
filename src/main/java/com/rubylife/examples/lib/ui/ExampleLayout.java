/*
 * Licensed to Tokenizer under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Tokenizer licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

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
