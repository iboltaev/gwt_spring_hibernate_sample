package ru.naumen.knowledgebase.client;

import ru.naumen.knowledgebase.client.utils.*;
import ru.naumen.knowledgebase.client.json.*;

import com.google.gwt.core.client.JsArray;

import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class TreeNodeEdit extends TreeNodeForm {
    private ErrorListener onError;
    private int id = 0;
    private String parentId = "null";

    public TreeNodeEdit(        
        final TreeNodeJson data,
        final KnowledgeModel dataModel,
        final DataChangeListener dataListener,
        final ErrorListener onError,
        final ImageLib imageLib) 
    {
        super(data, dataModel, onError, imageLib);

        boolean isChapter = data == null || data.isChapter()
                ? true
                : false; 

        this.onError = onError;
        this.id = data != null ? data.getId() : 0;
        this.parentId = data != null ? data.getParentId() : "null";

        // let's initialize form
        if (isChapter)
            showChildren();
        else
            showTextArea();

        // let's initialize button panel..
        HorizontalPanel buttonPanel = new HorizontalPanel();
        Button saveButton = new Button();
        Button deleteButton = new Button();

        addWidget("Actions: ", buttonPanel);
        buttonPanel.add(saveButton);
        saveButton.setText("Save");
        buttonPanel.add(deleteButton);
        deleteButton.setText("Delete");

        saveButton.addClickHandler(
            new ClickHandler() {
                @Override
                public void onClick(ClickEvent e) {
                    dataModel.edit(
                        makeObject(id, parentId),
                        new KnowledgeModel.ObjectListener() {
                            @Override
                            public void onJson(TreeNodeJson object) {
                                if (dataListener != null)
                                    dataListener.onDataChanged(
                                        DataChangeListener.Action.EDIT, 
                                        object);
                            }
                        },
                        onError);
                }
            });

        deleteButton.addClickHandler(
            new ClickHandler() {
                @Override
                public void onClick(ClickEvent e) {
                    dataModel.delete(
                        id,
                        new KnowledgeModel.CompletionListener() {
                            @Override
                            public void done() {
                                if (dataListener != null)
                                    dataListener.onDataChanged(
                                        DataChangeListener.Action.DELETE,
                                        makeObject(id, parentId));
                            }
                        },
                        onError);
                }
            });

        // only chapters allowed to insert child articles
        if (isChapter) {
            buttonPanel.add(
                makeAddButton(
                    "Add chapter",
                    true,
                    dataModel,
                    dataListener,
                    imageLib));

            buttonPanel.add(
                makeAddButton(
                    "Add article",
                    false,
                    dataModel,
                    dataListener,
                    imageLib));
        }
    }

    private Button makeAddButton(
        String caption,
        final boolean isChapter,
        final KnowledgeModel dataModel,
        final DataChangeListener dataListener,
        final ImageLib imageLib) 
    {
        return new Button(
            caption,
            new ClickHandler() {
                @Override
                public void onClick(ClickEvent e) {
                    PopupPanel modalPanel = new PopupPanel(false, true);
                    modalPanel.setWidget(
                        new TreeNodeAdd(
                            dataModel,
                            id == 0 ? "null" : Integer.toString(id),
                            isChapter,
                            ModalListenerMaker.makeDataListener(
                                dataListener, modalPanel),
                            ModalListenerMaker.makeErrorListener(
                                onError, modalPanel),
                            imageLib));

                    modalPanel.center();
                }
            });
    }
}
