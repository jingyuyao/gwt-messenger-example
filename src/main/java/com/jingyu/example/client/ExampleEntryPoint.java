package com.jingyu.example.client;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class ExampleEntryPoint implements EntryPoint {
	private final MessageServiceAsync messageService = GWT.create(MessageService.class);

	private final TextBox messageBox = new TextBox();
	private final Button sendButton = new Button("Send");
	private final FlexTable messageTable = new FlexTable();

	@Override
	public void onModuleLoad() {
		final RootPanel rootPanel = RootPanel.get();

		SendMessageHandler handler = new SendMessageHandler();
		messageBox.addKeyUpHandler(handler);
		sendButton.addClickHandler(handler);

		rootPanel.add(new Label("Welcome to a simple messenger!"));
		rootPanel.add(messageBox);
		rootPanel.add(sendButton);
		rootPanel.add(messageTable);
	}

	private void sendMessage() {
		// final so it could be used inside the callback
		final String message = messageBox.getValue();

		if (message != "") {
			sendButton.setEnabled(false);
			messageService.addMessage(message, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					// Humm... GWT doesn't support String.format()
					Window.alert("Oh no! Message did not send successfully. Error: %s" + caught.getMessage());
					sendButton.setEnabled(true);
				}

				@Override
				public void onSuccess(Void result) {
					messageBox.setValue("");
					messageTable.setText(messageTable.getRowCount(), 0, message);
					sendButton.setEnabled(true);
				}

			});
		}
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