package ru.naumen.knowledgebase.client;

import ru.naumen.knowledgebase.client.json.*;

public interface DataChangeListener {
    public enum Action { EDIT, ADD, DELETE };

    void onDataChanged(Action action, TreeNodeJson object);
}
