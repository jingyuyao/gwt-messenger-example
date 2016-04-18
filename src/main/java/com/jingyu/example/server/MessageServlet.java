package com.jingyu.example.server;

import java.util.ArrayList;
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
    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static Query listMessages = new Query(MessageBuilder.KIND)
            .addSort("created", Query.SortDirection.DESCENDING)
            .addProjection(new PropertyProjection("message", String.class));
    private static int LIST_MESSAGES_LIMIT = 10;

    @Override
    public void addMessage(String message) throws RuntimeException {
        MessageBuilder builder = new MessageBuilder();
        Entity newMessage = builder.message(message).create();
        datastore.put(newMessage);
    }

    @Override
    public List<String> listMessages() throws RuntimeException {
        List<String> messages = new ArrayList<String>(LIST_MESSAGES_LIMIT);
        PreparedQuery pq = datastore.prepare(listMessages);

        for (Entity entity : pq.asList(FetchOptions.Builder.withLimit(10))) {
            messages.add((String) entity.getProperty("message"));
        }

        return messages;
    }
}
