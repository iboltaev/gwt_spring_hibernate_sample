package ru.naumen.knowledgebase.client;

import ru.naumen.knowledgebase.client.utils.*;
import ru.naumen.knowledgebase.client.json.*;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RichTextArea;

import com.sencha.gxt.widget.core.client.form.HtmlEditor;

/**
 *  TreeNodeForm shows data contents on form and makes new data objects,
 *  filled with form data.
 */
public class TreeNodeForm extends Composite {
    
    private class RichText extends VerticalPanel {
        HtmlEditor textArea = new HtmlEditor();
        public RichText() {
            add(textArea);
        }

        public String getText() { return textArea.getValue(); }
        public void setText(String text) { textArea.setValue(text); }
    }

    protected TextBox briefName = new TextBox();
    protected TextBox fullName = new TextBox();
    protected ListBox childrenList; // optional
    protected RichText textArea; // optional
    
    private FlexTable tableLayout = new FlexTable();
    private CaptionPanel captionPanel = new CaptionPanel();

    private Integer id;
    private String baseUrl;
    private ErrorListener onError;

    private NodeTextJson textData;

    private KnowledgeModel dataModel;

    private ImageLib imageLib;
    
    public TreeNodeForm(
        TreeNodeJson data, 
        final KnowledgeModel dataModel,
        ErrorListener onError,
        ImageLib imageLib) 
    {
        super();

        this.id = data == null ? null : data.getId();
        this.baseUrl = baseUrl;
        this.onError = onError;
        this.textData = data == null ? null : data.getText();
        this.dataModel = dataModel;
        this.imageLib = imageLib;

        briefName.setText(data != null ? data.getBriefName() : "");
        fullName.setText(data != null ? data.getName() : "");

        initWidget(captionPanel);
        Label label = new Label();
        Label label2 = new Label();
        captionPanel.add(tableLayout);
        tableLayout.setWidget(1,0,label);
        label.setText("Brief name");
        label.setWordWrap(true);
        tableLayout.setWidget(1,1,briefName);
        tableLayout.setWidget(2,0,label2);
        label2.setText("Full name");
        label2.setWordWrap(true);
        tableLayout.setWidget(2,1,fullName);
    }

    public void showChildren() {
        if (textArea != null) {
            textArea.removeFromParent();
            textArea = null;
        }

        childrenList = new ListBox(true);
        addWidget("Children: ", childrenList);
        childrenList.setWidth("250px");
        childrenList.setSelectedIndex(-1);
        childrenList.setVisibleItemCount(3);

        // load children list
        dataModel.getChildren(
            id,
            new KnowledgeModel.ArrayListener() {
                @Override
                public void onJson(JsArray<TreeNodeJson> data) {
                    for (int i = 0 ; i < data.length(); i++) {
                        TreeNodeJson json = data.get(i);
                        childrenList.insertItem(
                            json.getBriefName(), 0);
                    }
                }
            },
            onError);

        tableLayout.setWidget(
            0, 1, 
            new Image(imageLib.folderBig().getSafeUri().asString()));
    }

    public void showTextArea() {
        if (childrenList != null) {
            childrenList.removeFromParent();
            childrenList = null;
        }

        textArea = new RichText();
        textArea.setText(textData == null ? "" : textData.getText());
        addWidget("Text: ", textArea);

        tableLayout.setWidget(
            0, 1, 
            new Image(imageLib.fileBig().getSafeUri().asString()));
    }

    public void setCaptionText(String caption) { 
        captionPanel.setCaptionText(caption); 
    }

    public void addWidget(String label, Widget w) {
        int row = tableLayout.getRowCount();
        tableLayout.setWidget(row, 0, new Label(label));
        tableLayout.setWidget(row, 1, w);
    }

    public TreeNodeJson makeObject(int id, String parentId) {
        TreeNodeJson object = JavaScriptObject
                .createObject()
                .<TreeNodeJson>cast();
        object.setId(id);
        object.setParentId(parentId);
        object.setBriefName(briefName.getText());
        object.setName(fullName.getText());
        
        if (textArea != null) {
            try {
                NodeTextJson data = JavaScriptObject
                        .createObject()
                        .<NodeTextJson>cast();
                data.setId(textData == null ? 0 : textData.getId());
                data.setText(textArea.getText());
                object.setText(data);
            } catch (Exception e) {
                onError.onError(e.getMessage());
            }
        }

        return object;
    }
}
