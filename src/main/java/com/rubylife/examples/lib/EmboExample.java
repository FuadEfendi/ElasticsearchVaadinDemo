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

package com.rubylife.examples.lib;

import com.rubylife.examples.MyUI;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;

/**
 * Example that is embedded in a browser frame or in a popup window. 
 * 
 * @author magi
 */
public class EmboExample extends BookExample {
    private static final long serialVersionUID = -5530635960986472866L;


    public enum EmbeddingType {FRAME, POPUP};
    
    String servletPath;
    EmbeddingType type;
    int width;
    int height;
    Class<? extends UI> exclass;

    public EmboExample(String exampleId, String shortName, Class<? extends UI> exclass, String servletPath, EmbeddingType type, int width, int height) {
        super(exampleId, shortName, exclass);
        this.servletPath = servletPath;
        this.type = type;
        this.width = width;
        this.height = height;
        this.exclass = exclass;
    }
    
    @Override
    public Component invokeExample() {
        VerticalLayout layout = new VerticalLayout();
        
        if (type == EmbeddingType.FRAME) {
            BrowserFrame frame = new BrowserFrame(description);
            frame.setSource(new ExternalResource(MyUI.APPCONTEXT + "/" + servletPath + "?restartApplication"));
            frame.setWidth("" + width + "px");
            frame.setHeight("" + height + "px");
            layout.addComponent(frame);
        } else {
            // TODO
            Button launch = new Button("Open Window");
            BrowserWindowOpener opener = new BrowserWindowOpener(exclass);
            opener.extend(launch);
        }

        return layout;
    }
}
