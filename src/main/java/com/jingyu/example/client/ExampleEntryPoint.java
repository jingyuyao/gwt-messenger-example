package com.jingyu.example.client;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.jingyu.example.shared.Constants;
import com.jingyu.example.shared.Message;

public class ExampleEntryPoint implements EntryPoint {
    private final int REFRESH_RATE = 2000;
    private final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT);
    private final MessageServiceAsync messageService = GWT.create(MessageService.class);
    
    private final TextBox messageBox = new TextBox();
    private final Button sendButton = new Button("Send");
    private final FlexTable messageTable = new FlexTable();
    private final RootPanel rootPanel = RootPanel.get();
    private final Logger logger = Logger.getLogger("ExampleEntryPoint");
    private Timer refreshMessageTimer;
    private Date lastRefreshed;

    @Override
    public void onModuleLoad() {
        SendMessageHandler handler = new SendMessageHandler();
        messageBox.addKeyUpHandler(handler);
        sendButton.addClickHandler(handler);
        
        messageTable.setBorderWidth(1);

        rootPanel.add(new Label("Welcome to a simple messenger!"));
        rootPanel.add(messageTable);
        rootPanel.add(messageBox);
        rootPanel.add(sendButton);

        refreshMessageTimer = new Timer(){
            public void run(){
                refreshMessages();
            }
        };
        
        refreshMessages();
    }

    private void refreshMessages(){
        messageService.listMessages(lastRefreshed, new AsyncCallback<List<Message>>() {
            @Override
            public void onFailure(Throwable caught) {
                logger.log(Level.WARNING, "Woops... Can't retrieve latest messages. Error: " + caught.getMessage());
            }

            @Override
            public void onSuccess(List<Message> messages) {
                // Returned messages are sorted by created descending and we
                // want to display the latest message on the bottom.
                for (int i = messages.size() - 1; i >= 0; i--) {
                    handleNewMessage(messages.get(i));
                }
                refreshMessageTimer.schedule(REFRESH_RATE);
            }
        });
    }
    
    private void handleNewMessage(Message message){
        addMessage(message);
        if (lastRefreshed == null || message.created.after(lastRefreshed)){
            lastRefreshed = message.created;
        }
    }

    /**
     * Sends the text in {@link #messageBox messageBox} to the server and
     * add it to the bottom of {@link #messageTable messageTable} if successful.
     */
    private void sendMessage() {
        // final so it could be used inside the callback
        final String content = messageBox.getValue();

        if (content != "") {
            sendButton.setEnabled(false);
            messageService.addMessage(content, new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                    // Humm... GWT doesn't support String.format()
                    logger.log(Level.WARNING, "Oh no! Message did not send successfully. Error: " + caught.getMessage());
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
    private void addMessage(Message message) {
        if (messageTable.getRowCount() >= Constants.MESSAGE_LIST_LIMIT){
            messageTable.removeRow(0);
        }
        int row = messageTable.getRowCount();
        messageTable.setText(row, 0, DATE_FORMAT.format(message.created));
        messageTable.setText(row, 1, message.content);
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