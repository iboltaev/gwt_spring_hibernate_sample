package ru.naumen.knowledgebase.client;

import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;

public interface ImageLib extends ClientBundle {
    ImageResource folder();

    ImageResource folderBig();

    ImageResource file();

    ImageResource fileBig();
}
