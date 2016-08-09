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

package com.rubylife.examples.lib;

import java.io.Serializable;

public class AbstractExampleItem implements Serializable {
    private static final long serialVersionUID = -6281280822803513663L;

    protected String exampleId;
    protected String context;
    private String parentId;
    private boolean collapsed;

    public AbstractExampleItem(String itemid) {
        if ("-".equals(itemid.substring(itemid.length()-1))) {
            this.collapsed = true;
            itemid = itemid.substring(0, itemid.length()-1);
        } else
            collapsed = false;
        
        this.exampleId    = itemid;

        // Determine parent node and context
        int lastdot = itemid.lastIndexOf(".");
        if (lastdot != -1) {
            parentId = itemid.substring(0, lastdot);
            context = itemid.substring(lastdot+1);
        } else
            parentId = null;
    }
    
    public String getExampleId() {
        return exampleId;
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public boolean isCollapsed() {
        return collapsed;
    }
}