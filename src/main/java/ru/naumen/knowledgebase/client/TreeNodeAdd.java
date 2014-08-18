package ru.naumen.knowledgebase.client;

import ru.naumen.knowledgebase.client.utils.*;
import ru.naumen.knowledgebase.client.json.*;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Provides UI for tree items addition. 
 * Fires data update events. 
 * */
public class TreeNodeAdd extends PopupPanel {
    private ErrorListener onError;
    private TreeNodeForm form;

    public TreeNodeAdd(
        final KnowledgeModel dataModel,
        final String parentId,
        boolean isChapter,
        final DataChangeListener dataListener,
        final ErrorListener onError,
        final ImageLib imageLib)
    {
        form = new TreeNodeForm(
            null,
            dataModel,
            onError,
            imageLib);

        add(form);

        if (!isChapter)
            form.showTextArea();

        this.onError = onError;
        HorizontalPanel buttonPanel = new HorizontalPanel();
        Button saveButton = new Button();

        form.addWidget("Actions: ", buttonPanel);
        buttonPanel.add(saveButton);
        saveButton.setText("Save");

        saveButton.addClickHandler(
            new ClickHandler() {
                @Override
                public void onClick(ClickEvent e) {
                    dataModel.add(
                        form.makeObject(0, parentId), 
                        new KnowledgeModel.ObjectListener() {
                            @Override
                            public void onJson(TreeNodeJson obj) {
                                hide();
                                if (dataListener != null)
                                    dataListener.onDataChanged(
                                        DataChangeListener.Action.ADD, obj);
                            }
                        },
                        new ErrorListener() {
                            @Override
                            public void onError(String text) {
                                hide();
                                if (onError != null)
                                    onError.onError(text);
                            }
                        });
                }
            });
    }
}
