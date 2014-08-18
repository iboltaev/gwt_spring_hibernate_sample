package ru.naumen.knowledgebase.client.json;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

public class TreeNodeJson extends JavaScriptObject {
    protected TreeNodeJson() {}

    public final native int getId() /*-{ if(this.id == null) return -1; else return this.id; }-*/;
    public final native void setId(int id) /*-{ this.id = id; }-*/;


    public final native String getParentId() /*-{ return this.parentId; }-*/;
    public final native void setParentId(String parentId) /*-{ this.parentId = parentId; }-*/;


    public final native String getBriefName() /*-{ return this.briefName; }-*/;
    public final native void setBriefName(String brief) /*-{ this.briefName = brief; }-*/;


    public final native String getName() /*-{ return this.name; }-*/;
    public final native void setName(String name) /*-{ this.name = name; }-*/;


    public final native NodeTextJson getText() /*-{ return this.nodeText; }-*/;
    public final native void setText(NodeTextJson text) /*-{ this.nodeText = text; }-*/;

    public final boolean isChapter() { return getText() == null; }
}
