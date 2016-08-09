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

import com.rubylife.examples.lib.SourceFragment;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

/** Source code listing. */
public class SourceListing extends CustomComponent {
    private static final long serialVersionUID = -1864980807288021761L;

    VerticalLayout layout = new VerticalLayout();
    Label srcview;
    
    /**
     * @param caption caption for the source listing box
     * @param srcCode the source code
     */
    public SourceListing(String caption, String url, final SourceFragment fragment) {
        setSizeUndefined(); // Layout size is also set with custom setWidth()

        // Source caption
        HorizontalLayout titlebar = new HorizontalLayout();
        titlebar.setWidth("100%");
        Label captionLabel = new Label(caption);
        captionLabel.addStyleName("sourcecaption");
        captionLabel.setSizeUndefined();
        titlebar.addComponent(captionLabel);
        titlebar.setComponentAlignment(captionLabel, Alignment.BOTTOM_LEFT);
        
        // Link to source repository
        String filename = url.substring(url.lastIndexOf('/') + 1);
        if (fragment.getFragmentPos() > 0)
            url = url + "#L" + fragment.getFragmentPos();
        Link srcLink = new Link(filename, new ExternalResource(url));
        srcLink.setTargetName("_new");
        srcLink.setDescription("Click link to repository open source file in new window");
        titlebar.addComponent(srcLink);
        titlebar.setComponentAlignment(srcLink, Alignment.BOTTOM_RIGHT);
        
        layout.addComponent(titlebar);
        
        // The actual source code listing
        srcview = new Label(fragment.getSrcCode(), ContentMode.PREFORMATTED);
        srcview.addStyleName("sourcecode");
        srcview.setWidth("-1");
        layout.addComponent(srcview);

        final NativeSelect mode = new NativeSelect();
        mode.addItem("Plain");
        mode.addItem("DocBook");
        mode.addItem("JavaDoc");
        mode.addItem("MarkDown");
        mode.setValue("Plain");
        mode.setNullSelectionAllowed(false);
        mode.setMultiSelect(false);
        
        layout.addComponent(mode);
        layout.setComponentAlignment(mode, Alignment.MIDDLE_RIGHT);

        mode.addValueChangeListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = 2161991423208388790L;

            public void valueChange(ValueChangeEvent event) {
                String selected = (String)mode.getValue();
                
                if ("Plain".equals(selected)) {
                    srcview.setValue(fragment.getSrcCode());
                } else if ("DocBook".equals(selected)) {
                    String trimmed = fragment.getSrcCode().trim();
                    String dbcode = "<programlisting><?pocket-size 65% ?><![CDATA[" +
                                    trimmed + "]]></programlisting>\n";
                    srcview.setValue(dbcode);
                } else if ("JavaDoc".equals(selected)) {
                    String trimmed = "     * " + fragment.getSrcCode().trim().replace("\n", "\n     * ");
                    String dbcode = "     * <pre>\n" +
                                    trimmed + "\n     * </pre>\n";
                    srcview.setValue(dbcode);
                } else if ("MarkDown".equals(selected)) {
                    String trimmed = "    " + fragment.getSrcCode().trim().replace("\n", "\n    ");
                    srcview.setValue(trimmed);
                }
            }
        });
        mode.setImmediate(true);
        
        setCompositionRoot(layout);
    }
    
    /** Set width for both the component and its root layout. */
    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        if (layout != null)
            layout.setWidth(width);
        if (srcview != null)
            srcview.setWidth(width);
    }
    
    /** Set width for both the component and its root layout. */
    @Override
    public void setWidth(float width, Unit unit) {
        super.setWidth(width, unit);
        if (layout != null)
            layout.setWidth(width, unit);
        if (srcview != null)
            srcview.setWidth(width, unit);
    }
}