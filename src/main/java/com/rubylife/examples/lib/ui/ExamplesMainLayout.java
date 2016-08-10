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
