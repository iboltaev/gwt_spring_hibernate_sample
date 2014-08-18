package ru.naumen.knowledgebase.client.utils;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;

import com.google.gwt.json.client.JSONObject;

import com.google.gwt.http.client.Request; 
import com.google.gwt.http.client.RequestBuilder; 
import com.google.gwt.http.client.RequestCallback; 
import com.google.gwt.http.client.RequestException; 
import com.google.gwt.http.client.Response;

/**
   This class gives us ability to communicate with almost any
   JSON REST service.
 */
public class DataLoader {
    public interface ObjectListener<T extends JavaScriptObject> {
        void onJson(T object);
    }

    public interface CompletionListener {
        void done();
    }

    public interface ErrorListener {
        void onError(String text);
    }

    public static <T extends JavaScriptObject> void load(
        String url,
        final ObjectListener listener,
        final ErrorListener error) 
    {
        RequestBuilder builder = new RequestBuilder(
            RequestBuilder.GET, url);

        try {
            builder.sendRequest(
                null,
                new RequestCallback() {
                    @Override
                    public void onError(Request request, Throwable e) {
                        if (error != null)
                            error.onError(e.getMessage());
                    }

                    @Override
                    public void onResponseReceived(
                        Request request, Response response)
                    {
                        if (response.getStatusCode() != 200) {
                            if (error != null)
                                error.onError("Could not fetch data: " + 
                                              response.getStatusText());
                        } else {
                            listener.onJson(
                                JsonUtils.
                                <JsArray<T>>safeEval(
                                    response.getText()));
                        }
                    }
                });
        } catch (RequestException e) {
            if (error != null)
                error.onError(e.getMessage());
        }
        
    }

    public static <T extends JavaScriptObject> void add(
        String url,
        T object,
        final ObjectListener listener,
        final ErrorListener error)
    {
        RequestBuilder builder = new RequestBuilder(
            RequestBuilder.POST, url);
        builder.setHeader("Accept", "application/json");
        builder.setHeader("Content-Type", "application/json");
        try {
            builder.sendRequest(
                new JSONObject(object).toString(),
                new RequestCallback() {
                    @Override
                    public void onError(Request req, Throwable e) {
                        if (error != null)
                            error.onError("Could not add chapter: " + e.getMessage());
                    }

                    @Override
                    public void onResponseReceived(
                        Request req, Response resp)
                    {
                        if (resp.getStatusCode() != 200) {
                            if (error != null)
                                error.onError("Could not add chapter: " + resp.getStatusText());
                        } else if (listener != null) {
                            listener.onJson(
                                JsonUtils.
                                <T>safeEval(
                                    resp.getText()));
                        }
                    }
                });
        } catch (RequestException e) {
            if (error != null)
                error.onError(e.getMessage());
        }
    }

    public static <T extends JavaScriptObject> void edit(
        String url,
        T object,
        final ObjectListener listener,
        final ErrorListener error)
    {
        RequestBuilder builder = new RequestBuilder(
            RequestBuilder.PUT, url);
        builder.setHeader("Accept", "application/json");
        builder.setHeader("Content-Type", "application/json");
        try {
            builder.sendRequest(
                new JSONObject(object).toString(),
                new RequestCallback() {
                    @Override
                    public void onError(Request req, Throwable e) {
                        if (error != null)
                            error.onError("Could not edit chapter: " + e.getMessage());
                    }

                    @Override
                    public void onResponseReceived(
                        Request req, Response resp)
                    {
                        if (resp.getStatusCode() != 200) 
                        {
                            if (error != null)
                                error.onError("Could not edit chapter: " + resp.getStatusText());
                        } else if (listener != null) {
                            listener.onJson(
                                JsonUtils.
                                <T>safeEval(
                                    resp.getText()));
                        }
                    }
                });
        } catch (RequestException e) {
            if (error != null)
                error.onError(e.getMessage());
        }
    }

    // let's allow send delete requests with body
    public static <T extends JavaScriptObject> void delete(
        String url,
        T object,
        final CompletionListener listener,
        final ErrorListener error)
    {
        RequestBuilder builder = new RequestBuilder(
            RequestBuilder.DELETE, url);
        builder.setHeader("Accept", "application/json");
        builder.setHeader("Content-Type", "application/json");
        try {
            builder.sendRequest(
                new JSONObject(object).toString(),
                new RequestCallback() {
                    @Override
                    public void onError(Request req, Throwable e) {
                        if (error != null)
                            error.onError("Could not delete chapter: " + e.getMessage());
                    }

                    @Override
                    public void onResponseReceived(
                        Request req, Response resp)
                    {
                        if (resp.getStatusCode() != 204 && resp.getStatusCode() != 404) {
                            if (error != null)
                                error.onError("Could not delete chapter: " + resp.getStatusText());
                        } else if (listener != null) {
                            listener.done();
                        }
                    }
                });
        } catch (RequestException e) {
            if (error != null)
                error.onError(e.getMessage());
        }
    }
}
