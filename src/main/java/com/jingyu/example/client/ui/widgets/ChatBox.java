package com.jingyu.example.client.ui.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.jingyu.example.client.MessageService;
import com.jingyu.example.client.MessageServiceAsync;
import com.jingyu.example.shared.Constants;
import com.jingyu.example.shared.Message;
import gwt.material.design.client.ui.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * A self contained chat widget.
 * Created by jingyu on 4/25/16.
 */
public class ChatBox extends Composite implements RequiresResize {
    interface MyUiBinder extends UiBinder<MaterialPanel, ChatBox> {
    }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private static DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);

    private Logger logger = Logger.getLogger("ChatBox");
    private final MessageServiceAsync messageService = GWT.create(MessageService.class);
    private Timer refreshMessageTimer;
    private boolean inputLocked = false;
    private Date lastRefreshed;
    private int retryMultiplier = 2;
    private boolean firstResponse = false;

    @UiField
    MaterialLabel title;

    @UiField
    MaterialProgress loader;

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    MaterialCollection messages;

    @UiField
    MaterialTextBox input;

    public ChatBox() {
        initWidget(uiBinder.createAndBindUi(this));

        refreshMessageTimer = new Timer() {
            public void run() {
                refreshMessages();
            }
        };

        refreshMessages();
    }

    /**
     * This method is called as the last step of a widget's rendering process.
     * We must perform a resize at the last possible moment because we need
     * every element to have its height **calculated** in the DOM before we can set
     * the scroll element's size correctly.
     * <see>http://stackoverflow.com/questions/8959643/how-to-know-when-a-widget-is-being-rendered</see>
     */
    @Override
    protected void onLoad() {
        super.onLoad();
        // Just because GWT tells us the widget is attached to the DOM doesn't
        // mean the widget's width and height are calculated yet. We need to wait
        // until the browser's layout engine finishes rendering the widget before
        // calling a resize since this widget uses percent units for its width and height.
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onResize();
            }
        });
    }

    /**
     * We must explicitly define the height of a scrolling element in order to make it work.
     * I wrapped the ScrollPanel in a div and programmatically set its height to the parent div.
     */
    @Override
    public void onResize() {
        // Hide the ScrollPanel so the parent height resets.
        scrollPanel.setVisible(false);
        int parentHeight = scrollPanel.getParent().getOffsetHeight();
        logger.info("ChatBox parent: " + String.valueOf(parentHeight));
        scrollPanel.setHeight(String.valueOf(parentHeight) + "px");
        scrollPanel.setVisible(true);
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

        if (!content.equals("")) {
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

    private void refreshMessages() {
        messageService.listMessages(lastRefreshed, new RefreshMessagesCallback());
    }

    private void handleNewMessage(Message message) {
        addMessage(message);
        if (lastRefreshed == null || message.created.after(lastRefreshed)) {
            lastRefreshed = message.created;
        }
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
            refreshMessageTimer.schedule(Constants.MESSAGE_REFRESH_RATE * retryMultiplier);
            retryMultiplier++;
        }

        @Override
        public void onSuccess(List<Message> messages) {
            if (!firstResponse){
                firstResponse = true;
                loader.setVisible(false);
            }
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
