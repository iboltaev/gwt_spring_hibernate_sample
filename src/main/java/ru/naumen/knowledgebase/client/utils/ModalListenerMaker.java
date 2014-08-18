package ru.naumen.knowledgebase.client.utils;

import ru.naumen.knowledgebase.client.json.*;

import ru.naumen.knowledgebase.client.DataChangeListener;
import ru.naumen.knowledgebase.client.ErrorListener;

import com.google.gwt.user.client.ui.PopupPanel;

public class ModalListenerMaker {
    public static DataChangeListener makeDataListener(
        final DataChangeListener dataListener,
        final PopupPanel modal) 
    {
        return new DataChangeListener() {
            @Override
            public void onDataChanged(Action a, TreeNodeJson object) {
                if (dataListener != null)
                    dataListener.onDataChanged(a, object);

                modal.hide();
            }
        };
    }

    public static ErrorListener makeErrorListener(
        final ErrorListener onError,
        final PopupPanel modal) 
    {
        return new ErrorListener() {
            @Override
            public void onError(String text) {
                if (onError != null)
                    onError.onError(text);

                modal.hide();
            }
        };
    }
}
