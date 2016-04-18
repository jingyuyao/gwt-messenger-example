package com.jingyu.example.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.jingyu.example.client.MessageService;

@SuppressWarnings("serial")
public class MessageServlet extends RemoteServiceServlet implements MessageService {
    public static final String MESSAGE_KIND = "Message";
    private static final int LIST_MESSAGES_LIMIT = 10;

    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static Query listMessages = new Query(MESSAGE_KIND)
            .addSort("created", Query.SortDirection.DESCENDING)
            .addProjection(new PropertyProjection("message", String.class));

    @Override
    public void addMessage(String message) throws RuntimeException {
        Entity newMessage = new Entity(MESSAGE_KIND);
        newMessage.setProperty("created", new Date());
        newMessage.setProperty("message", message);

        datastore.put(newMessage);
    }

    @Override
    public List<String> listMessages() throws RuntimeException {
        List<String> messages = new ArrayList<String>(LIST_MESSAGES_LIMIT);
        PreparedQuery pq = datastore.prepare(listMessages);

        for (Entity entity : pq.asList(FetchOptions.Builder.withLimit(LIST_MESSAGES_LIMIT))) {
            messages.add((String) entity.getProperty("message"));
        }

        return messages;
    }
}
