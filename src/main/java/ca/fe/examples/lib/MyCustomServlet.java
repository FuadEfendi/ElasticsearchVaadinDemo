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

package ca.fe.examples.lib;

import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

public class MyCustomServlet extends VaadinServlet
        implements SessionInitListener, SessionDestroyListener {
    private static final long serialVersionUID = -2419953872823694670L;
    private static final transient Logger logger = Logger.getLogger(MyCustomServlet.class.getName());

    public MyCustomServlet() {
    }

    @Override
    protected void servletInitialized()
            throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(this);
        getService().addSessionDestroyListener(this);
    }

    @Override
    public void sessionInit(SessionInitEvent event)
            throws ServiceException {
        logger.info("Session started: " +
                event.getSession().hashCode());
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        logger.info("Session destroyed: " +
                event.getSession().hashCode());
    }
    // BEGIN-EXAMPLE: advanced.urifragment.basic

    /**
     * Provide crawlable content
     */
    @Override
    protected void service(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {
        String fragment = request
                .getParameter("_escaped_fragment_");
        if (fragment != null) {
            response.setContentType("text/html");
            Writer writer = response.getWriter();
            writer.append("<html><body>" +
                    "<p>Here is some crawlable " +
                    "content about " + fragment + "</p>");
            // A list of all crawlable pages
            String items[] = {"mercury", "venus",
                    "earth", "mars"};
            writer.append("<p>Index of all content:</p><ul>");
            for (String item : items) {
                String url = request.getContextPath() +
                        request.getServletPath() +
                        request.getPathInfo() + "#!" + item;
                writer.append("<li><a href='" + url + "'>" +
                        item + "</a></li>");
            }
            writer.append("</ul></body>");
        } else
            super.service(request, response);
    }
    // END-EXAMPLE: advanced.urifragment.basic
}
