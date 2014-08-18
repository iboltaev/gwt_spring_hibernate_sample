package ru.naumen.knowledgebase.client.json;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

public class NodeTextJson extends JavaScriptObject {
    protected NodeTextJson() {}

    public final native int getId() /*-{ if(this.id == null) return -1; else return this.id; }-*/;

    public final native void setId(int id) /*-{ this.id = id; }-*/;

    public final native String getText() /*-{ return this.text; }-*/;

    public final native void setText(String text) /*-{ this.text = text; }-*/;

    public final native void clearText() /*-{ this.text = null; }-*/;
}
