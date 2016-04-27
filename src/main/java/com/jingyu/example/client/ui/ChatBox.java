package com.jingyu.example.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.jingyu.example.client.MessageService;
import com.jingyu.example.client.MessageServiceAsync;
import com.jingyu.example.shared.Constants;
import com.jingyu.example.shared.Message;
import gwt.material.design.client.ui.*;

import java.util.Date;
import java.util.List;

/**
 * A self contained chat widget.
 * Created by jingyu on 4/25/16.
 */
public class ChatBox extends Composite implements RequiresResize {
    interface MyUiBinder extends UiBinder<HTMLPanel, ChatBox> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private static DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);

    private final MessageServiceAsync messageService = GWT.create(MessageService.class);
    private Timer refreshMessageTimer;
    private boolean inputLocked = false;
    private Date lastRefreshed;

    @UiField
    MaterialNavBrand title;

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    MaterialCollection messages;

    @UiField
    MaterialTextBox input;

    public ChatBox(){
        initWidget(uiBinder.createAndBindUi(this));

        title.setText("A simple messenger!");

        onResize();

        refreshMessageTimer = new Timer(){
            public void run(){
                refreshMessages();
            }
        };

        refreshMessages();
    }

    @UiHandler("input")
    public void keyUpHandler(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            sendMessage();
        }
    }

    private void sendMessage() {
        if (inputLocked) {
            return;
        }

        // final so it could be used inside the callback
        final String content = input.getValue();

        if (content != "") {
            inputLocked = true;
            messageService.addMessage(content, new SendMessageCallback());
        }
    }

    private void addMessage(Message message) {
        String date = DATE_FORMAT.format(message.created);
        String content = SafeHtmlUtils.htmlEscape(message.content);
        MaterialLabel dateLabel = new MaterialLabel(date);
        MaterialLabel contentLabel = new MaterialLabel(content);
        MaterialCollectionItem newMessageItem = new MaterialCollectionItem();
        newMessageItem.add(dateLabel);
        newMessageItem.add(contentLabel);

        messages.add(newMessageItem);
    }

    private void refreshMessages(){
        messageService.listMessages(lastRefreshed, new RefreshMessagesCallback());
    }

    private void handleNewMessage(Message message){
        addMessage(message);
        if (lastRefreshed == null || message.created.after(lastRefreshed)) {
            lastRefreshed = message.created;
        }
    }

    /**
     * TODO: Stop being lazy
     */
    @Override
    public void onResize() {
        int totalHeight = Window.getClientHeight();
        scrollPanel.setHeight(String.valueOf(totalHeight * 0.7) + "px");
    }

    private class SendMessageCallback implements AsyncCallback<Void> {
        @Override
        public void onFailure(Throwable caught) {
            MaterialToast.fireToast("Unable to send message");
            inputLocked = false;
        }

        @Override
        public void onSuccess(Void result) {
            input.setValue("");
            inputLocked = false;
        }
    }

    private class RefreshMessagesCallback implements AsyncCallback<List<Message>> {
        @Override
        public void onFailure(Throwable caught) {
            MaterialToast.fireToast("Can't retrieve latest messages...");
            refreshMessageTimer.schedule(Constants.MESSAGE_REFRESH_RATE * 3);
        }

        @Override
        public void onSuccess(List<Message> messages) {
            // Returned messages are sorted by created descending and we
            // want to display the latest message on the bottom.
            for (int i = messages.size() - 1; i >= 0; i--) {
                handleNewMessage(messages.get(i));
            }
            if (messages.size() > 0) {
                scrollPanel.scrollToBottom();
            }
            refreshMessageTimer.schedule(Constants.MESSAGE_REFRESH_RATE);
        }
    }
}
