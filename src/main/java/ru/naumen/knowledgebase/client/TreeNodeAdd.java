package ru.naumen.knowledgebase.client;

import ru.naumen.knowledgebase.client.utils.*;
import ru.naumen.knowledgebase.client.json.*;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class TreeNodeAdd extends TreeNodeForm {
    private ErrorListener onError;

    public TreeNodeAdd(
        final KnowledgeModel dataModel,
        final String parentId,
        boolean isChapter,
        final DataChangeListener dataListener,
        final ErrorListener onError,
        final ImageLib imageLib)
    {
        super(null, dataModel, onError, imageLib);

        if (!isChapter)
            showTextArea();

        this.onError = onError;
        HorizontalPanel buttonPanel = new HorizontalPanel();
        Button saveButton = new Button();

        addWidget("Actions: ", buttonPanel);
        buttonPanel.add(saveButton);
        saveButton.setText("Save");

        saveButton.addClickHandler(
            new ClickHandler() {
                @Override
                public void onClick(ClickEvent e) {
                    dataModel.add(
                        makeObject(0, parentId), 
                        new KnowledgeModel.ObjectListener() {
                            @Override
                            public void onJson(TreeNodeJson obj) {
                                if (dataListener != null)
                                    dataListener.onDataChanged(
                                        DataChangeListener.Action.ADD, obj);
                            }
                        },
                        onError);
                }
            });
    }
}
