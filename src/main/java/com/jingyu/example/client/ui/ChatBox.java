package com.jingyu.example.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.jingyu.example.client.MessageService;
import com.jingyu.example.client.MessageServiceAsync;
import com.jingyu.example.shared.Constants;
import com.jingyu.example.shared.Message;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A self contained chat widget.
 * Created by jingyu on 4/25/16.
 */
public class ChatBox extends Composite {
    interface MyUiBinder extends UiBinder<FlowPanel, ChatBox> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private final Logger logger = Logger.getLogger("ChatBox");
    private final MessageServiceAsync messageService = GWT.create(MessageService.class);
    private final ListDataProvider<Message> dataProvider;
    private Timer refreshMessageTimer;
    private boolean inputLocked = false;
    private Date lastRefreshed;

    @UiField
    Label title;

    @UiField(provided = true)
    final CellList<Message> messages;

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    TextBox input;

    public ChatBox(){
        messages = new CellList<>(new MessageCell());
        initWidget(uiBinder.createAndBindUi(this));

        title.setText("Welcome to a simple messenger!");

        dataProvider = new ListDataProvider<>();
        dataProvider.addDataDisplay(messages);

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
        List<Message> list = dataProvider.getList();
        list.add(message);
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

    private class SendMessageCallback implements AsyncCallback<Void> {
        @Override
        public void onFailure(Throwable caught) {
            // Humm... GWT doesn't support String.format()
            logger.log(Level.WARNING, "Oh no! Message did not send successfully. Error: " + caught.getMessage());
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
            logger.warning("Woops... Can't retrieve latest messages. Error: " + caught.getMessage());
        }

        @Override
        public void onSuccess(List<Message> messages) {
            // Returned messages are sorted by created descending and we
            // want to display the latest message on the bottom.
            for (int i = messages.size() - 1; i >= 0; i--) {
                handleNewMessage(messages.get(i));
            }
            // Using a scheduler to prevent scrolling too early.
            Scheduler.get().scheduleDeferred(new ScrollBottomCommand());
            refreshMessageTimer.schedule(Constants.MESSAGE_REFRESH_RATE);
        }
    }

    private class ScrollBottomCommand implements Scheduler.ScheduledCommand{
        @Override
        public void execute() {
            messages.setVisibleRange(0, dataProvider.getList().size());
            scrollPanel.scrollToBottom();
        }
    }
}
