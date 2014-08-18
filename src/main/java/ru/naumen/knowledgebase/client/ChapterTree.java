package ru.naumen.knowledgebase.client;

import ru.naumen.knowledgebase.client.json.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Composite;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Element;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.IconCellDecorator;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;

import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;

import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import java.lang.StringBuilder;

/**
 * Represents tree data structure using Model as data soure.
 * Can handle model updates at the root tree.
 * */
public class ChapterTree extends Composite {

    public static interface ClickHandler {
        void onClick(ChapterModel data);
    }

    public static interface Renderer {
        void render(ChapterModel data, SafeHtmlBuilder sb);
    }

    // we need this to make using of CellTreeMessages available.
    // It seems CellTree does not accept null in resources parameter
    public static interface TreeResources extends CellTree.Resources {
    }

    private Renderer renderer;
    private ErrorListener listener;
    private ClickHandler clickHandler;
    private CellTree tree;

    private KnowledgeModel dataModel;

    private ListDataProvider<ChapterModel> rootProvider = 
            new ListDataProvider<ChapterModel>();

    public ChapterTree(
        KnowledgeModel dataModel,
        Renderer renderer, 
        ErrorListener listener) 
    {
        this.dataModel = dataModel;
        this.listener = listener;
        this.renderer = renderer;

        tree = new CellTree(
            new Model(),
            null,
            (TreeResources)GWT.create(TreeResources.class),
            new CellTree.CellTreeMessages() {
                public String emptyTree() { return ""; }
                public String showMore() { return "Need more data"; }
            });

        initWidget(tree);
    }

    public void setClickHandler(ClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public void update(
        DataChangeListener.Action action,
        TreeNodeJson object)
    {
        if (action == DataChangeListener.Action.ADD) {
            rootProvider.getList().add(
                new ChapterModel(
                    object,
                    Integer.toString(rootProvider.getList().size() + 1),
                    null,
                    rootProvider));
        } else if (action == DataChangeListener.Action.DELETE) {
            removeItem(rootProvider.getList(), object.getId());
        } else {
            if (listener != null)
                listener.onError("Could not edit root");
        }
    }
    
    /**
     * Model of single node item, contains actual data.
     * Can handle model updates.
     */
    public static class ChapterModel {
        private TreeNodeJson json;
        private ListDataProvider<ChapterModel> parentProvider;
        private ListDataProvider<ChapterModel> provider;
        private String number = "";
        
        public ChapterModel(TreeNodeJson json) { this.json = json; }

        public ChapterModel(
            TreeNodeJson json, 
            String number,
            ListDataProvider<ChapterModel> provider,
            ListDataProvider<ChapterModel> parentProvider) 
        {
            this.json = json;
            this.number = number;
            this.provider = provider;
            this.parentProvider = parentProvider;
        }

        public TreeNodeJson json() { return this.json; }

        public void setParentDataProvider(
            ListDataProvider<ChapterModel> prov)
        {
            parentProvider = prov;
        }

        public void setDataProvider(
            ListDataProvider<ChapterModel> prov)
        {
            provider = prov;
        }

        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number;}
        public void setLastNum(int num) {
            int lastDot = number.lastIndexOf('.');
            if (lastDot > 0)
                number = number.substring(0, lastDot) + "." 
                        + Integer.toString(num);
            else
                number = Integer.toString(num);
        }

        public NodeTextJson getText() { return json.getText(); }

        public void update(DataChangeListener.Action action, TreeNodeJson newVal)
        {
            if (action == DataChangeListener.Action.DELETE) {
                removeItem(parentProvider.getList(), newVal.getId());
            } else if (action == DataChangeListener.Action.EDIT) {
                json = newVal;
                parentProvider.refresh();
            } else if (provider != null) {
                provider.getList().add(
                    new ChapterModel(
                        newVal, 
                        getNumber() + "." + Integer.toString(
                            provider.getList().size() + 1),
                        null, 
                        provider));
            }
        }
    }

    /**
     *  Represents tree data model, fetches data.
     */
    private class Model implements TreeViewModel {
        @Override
        public <T> NodeInfo<?> getNodeInfo(T value) {
            final ChapterModel model = (ChapterModel)value;
            final String id = model == null 
                    ? "" 
                    : Integer.toString(model.json().getId());

            final ListDataProvider<ChapterModel> provider = model == null 
                    ? rootProvider
                    : new ListDataProvider<ChapterModel>();

            if (model != null)
                model.setDataProvider(provider);

            final Cell<ChapterModel> cell = new ChapterCell();

            final String numPrefix = model == null 
                    ? "" 
                    : model.getNumber() + ".";

            dataModel.getChildren(
                model == null ? null : model.json().getId(),
                new KnowledgeModel.ArrayListener() {
                    @Override
                    public void onJson(JsArray<TreeNodeJson> data) {
                        int size = data.length();
                        for (int i = 0; i < size; i++)
                            provider.getList().add(
                                new ChapterModel(
                                    data.get(i), 
                                    numPrefix + Integer.toString(i + 1),
                                    null, 
                                    provider));
                    }
                },
                listener);

            return new DefaultNodeInfo<ChapterModel>(
                provider,
                cell);
        }

        @Override
        public boolean isLeaf(Object value) {
            ChapterModel model = (ChapterModel)value;
            if (value == null)
                return false;

            return !model.json().isChapter();
        }
    }

    /**
     * Represents controller layer, handles user events and render requests.
     * */
    private class ChapterCell extends AbstractCell<ChapterModel> {
        ChapterCell() {
            super("click");
        }

        @Override
        public void render(Context context, ChapterModel value, SafeHtmlBuilder sb) {
            if (value == null)
                return;

            renderer.render(value, sb);
        }

        @Override
        public void onBrowserEvent(
            Context context, Element parent, ChapterModel value, 
            NativeEvent event, ValueUpdater<ChapterModel> valueUpdater) 
        {
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
            if (value == null)
                return;

            if (clickHandler != null)
                clickHandler.onClick(value);
        }
    }

    private static boolean removeItem(List<ChapterModel> list, int id) {
        Iterator<ChapterModel> it = list.iterator();
        boolean result = false;
        int counter = 1;
        while (it.hasNext()) {
            ChapterModel model = it.next();
            if (model.json().getId() == id) {
                it.remove();
                result = true;
            } else  {
                if (result == true)
                    model.setLastNum(counter);
                counter++;
            }
        }
        return result;
    }

    private static String makePath(
        TreeNode node, 
        StringBuilder builder) 
    {
        if (node == null)
            return "/" + builder.toString();
        else
            return makePath(
                node.getParent(), 
                builder.insert(0, ((ChapterModel)node.getValue()).json().getBriefName() + "/"));
    }
}
