package com.jingyu.example.client;

import java.util.Date;
import java.util.List;

/*
 * Remember not to use any App Engine imports! They cannot be compiled to Javascript
 */
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.jingyu.example.shared.Constants;

public class ExampleEntryPoint implements EntryPoint {
    private final MessageServiceAsync messageService = GWT.create(MessageService.class);
    private final int REFRESH_RATE = 2000;

    private final TextBox messageBox = new TextBox();
    private final Button sendButton = new Button("Send");
    private final FlexTable messageTable = new FlexTable();
    private final RootPanel rootPanel = RootPanel.get();
    private Timer refreshMessageTimer;
    private Date lastRefreshed;

    @Override
    public void onModuleLoad() {
        SendMessageHandler handler = new SendMessageHandler();
        messageBox.addKeyUpHandler(handler);
        sendButton.addClickHandler(handler);

        rootPanel.add(new Label("Welcome to a simple messenger!"));
        rootPanel.add(messageTable);
        rootPanel.add(messageBox);
        rootPanel.add(sendButton);

        refreshMessages(lastRefreshed);

        refreshMessageTimer = new Timer(){
            public void run(){
                refreshMessages(lastRefreshed);
            }
        };

        refreshMessageTimer.scheduleRepeating(REFRESH_RATE);
    }

    private void refreshMessages(Date since){
        messageService.listMessages(since, new AsyncCallback<List<String>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Woops... Can't retrieve latest messages. Error: " + caught.getMessage());
            }

            @Override
            public void onSuccess(List<String> messages) {
                if (!messages.isEmpty()){
                    lastRefreshed = new Date();
                }
                
                // Returned messages are sorted by created descending and we
                // want to display the latest message on the bottom.
                for (int i = messages.size() - 1; i >= 0; i--) {
                    addMessage(messages.get(i));
                }
            }
        });
    }

    /**
     * Sends the text in {@link #messageBox messageBox} to the server and
     * add it to the bottom of {@link #messageTable messageTable} if successful.
     */
    private void sendMessage() {
        // final so it could be used inside the callback
        final String message = messageBox.getValue();

        if (message != "") {
            sendButton.setEnabled(false);
            messageService.addMessage(message, new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                    // Humm... GWT doesn't support String.format()
                    Window.alert("Oh no! Message did not send successfully. Error: " + caught.getMessage());
                    sendButton.setEnabled(true);
                }

                @Override
                public void onSuccess(Void result) {
                    messageBox.setValue("");
                    sendButton.setEnabled(true);
                }

            });
        }
    }

    /**
     * Adds a message to the table. Removes oldest message if table size
     * exceeds {@link Constants#MESSAGE_LIST_LIMIT MESSAGE_LIST_LIMIT}.
     * @param message
     */
    private void addMessage(String message) {
        if (messageTable.getRowCount() >= Constants.MESSAGE_LIST_LIMIT){
            messageTable.removeRow(0);
        }
        messageTable.setText(messageTable.getRowCount(), 0, message);
    }

    // Create a handler for the sendButton and nameField
    class SendMessageHandler implements ClickHandler, KeyUpHandler {
        /**
         * Fired when the user clicks on the sendButton.
         */
        public void onClick(ClickEvent event) {
            sendMessage();
        }

        /**
         * Fired when the user types in the messageBox.
         */
        public void onKeyUp(KeyUpEvent event) {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                sendMessage();
            }
        }
    }
}