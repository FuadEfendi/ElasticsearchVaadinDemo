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
