package com.jingyu.example.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.jingyu.example.client.ui.widgets.ChatBox;
import gwt.material.design.client.ui.MaterialColumn;

/**
 * Created by jingyu on 4/30/16.
 */
public class MainPage extends Composite implements RequiresResize {
    interface MyUiBinder extends UiBinder<HTMLPanel, MainPage> {
    }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    Element header;

    @UiField
    MaterialColumn mainColumn;

    @UiField
    ChatBox chatBox;

    public MainPage() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onResize();
            }
        });
    }

    @Override
    public void onResize() {
        // This is nasty... There's gotta be a better way.
        int totalHeight = Window.getClientHeight();
        int headerHeight = header.getOffsetHeight();
        int mainColumnHeight = totalHeight - headerHeight;
        mainColumn.setHeight(String.valueOf(mainColumnHeight) + "px");
        chatBox.onResize();
    }
}
