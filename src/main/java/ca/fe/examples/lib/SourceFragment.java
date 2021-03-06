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

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceFragment implements Serializable {
    private static final long serialVersionUID = -9205500105419131735L;

    private String       srcName;
    private String       srcCode;
    private int          srcWidth = 0;
    private int          fragmentPos;
    private List<String> forumLinks   = new LinkedList<String> ();
    private List<String> bookRefs     = new LinkedList<String> ();
    private List<String> exampleRefs  = new LinkedList<String> ();
    private List<Ref>    kbRefs       = new LinkedList<Ref> ();
    
    public class Ref implements Serializable {
        private static final long serialVersionUID = -6343463463453527775L;

        public String caption;
        public String ref;
        
        public Ref (String ref) {
            Pattern p = Pattern.compile("^\\s*([0-9]+)(\\s+.+)$");
            Matcher m = p.matcher(ref);
            if (! m.matches())
                throw new InvalidParameterException("KB Ref '" + ref + "' did not match regexp '" + p.pattern() + "'");
            this.ref = m.group(1);
            if (m.groupCount() > 1)
                this.caption = m.group(2);
            else
                this.caption = "#" + m.group(1);
        }
    }
    
    public SourceFragment(String srcname) {
        this.srcName = srcname;
    }
    
    public String getSrcName() {
        return srcName;
    }
    
    public String getSrcCode() {
        return srcCode;
    }
    
    public void setSrcCode(String srcCode) {
        this.srcCode = srcCode;
    }
    
    public int getSrcWidth() {
        return srcWidth;
    }
    
    public void setSrcWidth(int srcWidth) {
        this.srcWidth = srcWidth;
    }
    
    public List<String> getForumLinks() {
        return forumLinks;
    }
    
    public List<String> getBookRefs() {
        return bookRefs;
    }

    public List<String> getExampleRefs() {
        return exampleRefs;
    }
    
    public List<Ref> getKbRefs() {
        return kbRefs;
    }
    
    public int getFragmentPos() {
        return fragmentPos;
    }
    
    public void setFragmentPos(int fragmentPos) {
        this.fragmentPos = fragmentPos;
    }
}
