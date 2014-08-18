package ru.naumen.knowledgebase.client;

import ru.naumen.knowledgebase.client.utils.*;
import ru.naumen.knowledgebase.client.json.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ImageResourceRenderer;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.CaptionPanel;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import java.lang.StringBuilder;

/**
 * Main client class, glues it all together.
 * */
public class KnowledgeBase implements EntryPoint {

    private static final String REST_URL = GWT.getHostPageBaseURL() + "rest/";

    private Label errorLabel = new Label();
    private ImageLib images = GWT.create(Images.class);

    private ImageResourceRenderer imageRenderer;
    
    public static interface Images extends ImageLib {
        @Source("images/folder.png")
        ImageResource folder();

        @Source("images/folder_big.png")
        ImageResource folderBig();

        @Source("images/file.png")
        ImageResource file();

        @Source("images/file_big.png")
        ImageResource fileBig();
    }

    public void onModuleLoad() {
        final KnowledgeModel dataModel = new KnowledgeModel(
            GWT.getHostPageBaseURL());

        RootPanel mainPanel = RootPanel.get("mainContainer");
        VerticalPanel vLayout = new VerticalPanel();
        HorizontalPanel hPanel = new HorizontalPanel();

        mainPanel.add(hPanel);

        final SimplePanel editorPanel = new SimplePanel();

        this.imageRenderer = new ImageResourceRenderer();

        final ChapterTree tree = new ChapterTree(
            dataModel,
            new ChapterTree.Renderer() {
                @Override
                public void render(
                    ChapterTree.ChapterModel model,
                    SafeHtmlBuilder sb)
                {
                    sb.append(
                        imageRenderer.render(
                            model == null || model.getText() == null
                            ? images.folder()
                            : images.file()));

                    sb.appendEscaped(model.getNumber() + " \"" + model.json().getBriefName() + "\"");
                }
            },
            makeErrorListener());

        tree.setClickHandler(
            new ChapterTree.ClickHandler() {
                @Override
                public void onClick(
                    final ChapterTree.ChapterModel model) 
                {
                    editorPanel.clear();

                    TreeNodeEdit ed = new TreeNodeEdit(
                        model.json(),
                        dataModel,
                        new DataChangeListener() {
                            @Override
                            public void onDataChanged(
                                DataChangeListener.Action a, TreeNodeJson obj) 
                            {
                                model.update(a, obj);
                            }
                        },
                        makeErrorListener(),
                        images);

                    editorPanel.add(ed);;
                }
            });

        hPanel.add(tree);
        hPanel.add(vLayout);

        vLayout.add(editorPanel);
        vLayout.add(new ButtonPanel(tree, dataModel));
        vLayout.add(errorLabel);
    }

    private class ButtonPanel extends CaptionPanel {
        private HorizontalPanel rootButtons = new HorizontalPanel();
        
        ButtonPanel(final ChapterTree tree, final KnowledgeModel dataModel) {
            add(rootButtons);

            rootButtons.add(
                makeAddButton(
                    true, 
                    "Add root chapter",
                    tree,
                    dataModel));

            rootButtons.add(
                makeAddButton(
                    false,
                    "Add root article",
                    tree,
                    dataModel));
        }

        private Button makeAddButton(
            final boolean isChapter,
            String caption,
            final ChapterTree tree,
            final KnowledgeModel dataModel) 
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
                                "null",
                                isChapter,
                                ModalListenerMaker.makeDataListener(
                                    new DataChangeListener() {
                                        @Override
                                        public void onDataChanged(
                                            DataChangeListener.Action a, 
                                            TreeNodeJson data)
                                        {
                                            tree.update(a, data);
                                        }
                                    }, modalPanel),
                                ModalListenerMaker.makeErrorListener(
                                    makeErrorListener(),
                                    modalPanel),
                                images));

                        modalPanel.center();
                    }
                });
        }
    };

    private ErrorListener makeErrorListener() {
        return new ErrorListener() {
            @Override
            public void onError(String text) { errorLabel.setText(text); }
        };
    }
}
