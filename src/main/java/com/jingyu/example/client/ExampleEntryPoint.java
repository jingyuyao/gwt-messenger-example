package com.jingyu.example.client;

/*
 * Remember not to use any App Engine imports! They cannot be compiled to Javascript
 */
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.jingyu.example.client.ui.ChatBox;

public class ExampleEntryPoint implements EntryPoint {
    private final RootLayoutPanel rootPanel = RootLayoutPanel.get();

    @Override
    public void onModuleLoad() {
        ChatBox chatBox = new ChatBox();
        rootPanel.add(chatBox);
    }
}