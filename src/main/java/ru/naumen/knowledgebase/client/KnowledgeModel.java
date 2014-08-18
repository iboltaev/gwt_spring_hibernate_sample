package ru.naumen.knowledgebase.client;

import ru.naumen.knowledgebase.client.utils.*;
import ru.naumen.knowledgebase.client.json.*;

import com.google.gwt.core.client.JsArray;

/**
   Represents remote service.
   We could use just GWT RPC, but the goal was to demonstrate server
   using Spring.
 */
public class KnowledgeModel {

    public KnowledgeModel(String baseUrl) {
        urlPrefix = baseUrl + "rest/treenode/";
    }

    public interface ObjectListener {
        void onJson(TreeNodeJson json);
    }

    public interface ArrayListener {
        void onJson(JsArray<TreeNodeJson> json);
    }

    public interface CompletionListener {
        void done();
    }

    private String urlPrefix;

    public void getChildren(Integer id, ArrayListener listener, ErrorListener err) {
        String strId = "";
        if (id != null)
            strId = id.toString();
        
        String url = urlPrefix + "get/" + strId;
        DataLoader.<JsArray<TreeNodeJson>>load(
            url,
            makeArrayListener(listener),
            makeErrorListener(err));
    }

    public void add(TreeNodeJson object, ObjectListener listener, ErrorListener err) {
        DataLoader.<TreeNodeJson>add(
            urlPrefix + "create/",
            object,
            makeObjectListener(listener),
            makeErrorListener(err));
    }

    public void edit(TreeNodeJson object, ObjectListener listener, ErrorListener err) {
        DataLoader.<TreeNodeJson>edit(
            urlPrefix + "edit/",
            object,
            makeObjectListener(listener),
            makeErrorListener(err));
    }

    public void delete(int id, CompletionListener listener, ErrorListener err) {
        DataLoader.<TreeNodeJson>delete(
            urlPrefix + "delete/" + Integer.toString(id),
            null,
            makeCompletionListener(listener),
            makeErrorListener(err));
    }

    private DataLoader.ErrorListener makeErrorListener(final ErrorListener er) {
        return new DataLoader.ErrorListener() {
            @Override
            public void onError(String text) { 
               if (er != null) 
                   er.onError(text);
            }
        };
    }

    private DataLoader.ObjectListener<TreeNodeJson> makeObjectListener(
        final ObjectListener listener)
    {
        return new DataLoader.ObjectListener<TreeNodeJson>() {
            @Override
            public void onJson(TreeNodeJson json) {
                listener.onJson(json);
            }
        };
    }

    private DataLoader.ObjectListener<JsArray<TreeNodeJson>> makeArrayListener(
        final ArrayListener listener)
    {
        return new DataLoader.ObjectListener<JsArray<TreeNodeJson>>() {
            @Override
            public void onJson(JsArray<TreeNodeJson> json) {
                listener.onJson(json);
            }
        };
    }

    private DataLoader.CompletionListener makeCompletionListener(
        final CompletionListener listener)
    {
        return new DataLoader.CompletionListener() {
            @Override
            public void done() {
                if (listener != null)
                    listener.done();
            }
        };
    }
}
