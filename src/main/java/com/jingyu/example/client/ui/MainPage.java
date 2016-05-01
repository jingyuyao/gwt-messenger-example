package com.jingyu.example.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.jingyu.example.client.ui.widgets.ChatBox;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by jingyu on 4/30/16.
 */
public class MainPage extends Composite implements RequiresResize {
    interface MyUiBinder extends UiBinder<MaterialPanel, MainPage> {
    }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

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
        chatBox.onResize();
    }
}
