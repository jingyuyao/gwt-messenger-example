package com.jingyu.example.server;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.jingyu.example.client.MessageService;
import com.jingyu.example.shared.MessageBuilder;

@SuppressWarnings("serial")
public class MessageServlet extends RemoteServiceServlet implements MessageService{
	private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@Override
	public void addMessage(String message) throws RuntimeException{
		MessageBuilder builder = new MessageBuilder();
		Entity newMessage = builder.message(message).create();
		datastore.put(newMessage);
	}
}
