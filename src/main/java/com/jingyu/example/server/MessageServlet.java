package com.jingyu.example.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.jingyu.example.client.MessageService;
import com.jingyu.example.shared.Constants;
import com.jingyu.example.shared.Message;

@SuppressWarnings("serial")
public class MessageServlet extends RemoteServiceServlet implements MessageService {
    private static final String LAST_MESSAGE_UPDATE_KEY = "lastMessageUpdate";

    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private final MemcacheService cache;
    
    public MessageServlet(){
        super();
        cache = MemcacheServiceFactory.getMemcacheService();
        cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.WARNING));
        
        if (!cache.contains(LAST_MESSAGE_UPDATE_KEY)){
            Query latestMessage = new Query(Message.KIND)
                    .addSort("created", Query.SortDirection.DESCENDING)
                    .addProjection(new PropertyProjection("created", Date.class));
            
            PreparedQuery pq = datastore.prepare(latestMessage);
            
            for (Entity entity : pq.asList(FetchOptions.Builder.withLimit(1))){
                Date created = (Date) entity.getProperty("created");
                cache.put(LAST_MESSAGE_UPDATE_KEY, created);
            }
        }
    }

    @Override
    public void addMessage(String content) throws RuntimeException {
        Message message = new Message(content);

        cache.put(LAST_MESSAGE_UPDATE_KEY, message.created);
        datastore.put(EntityConverter.toEntity(message));
    }

    /**
     * @param since: Blocks frequent access to the database.
     */
    @Override
    public List<Message> listMessages(Date since) throws RuntimeException {
        if (since != null && cache.contains(LAST_MESSAGE_UPDATE_KEY)){
            Date lastUpdate = (Date) cache.get(LAST_MESSAGE_UPDATE_KEY);
            if (since.after(lastUpdate)){
                return Collections.emptyList();
            }
        }
        
        Query listMessages = new Query(Message.KIND)
                .addSort("created", Query.SortDirection.DESCENDING);
        
        if (since != null){
            Filter sinceFilter = new FilterPredicate("created", FilterOperator.GREATER_THAN, since);
            listMessages.setFilter(sinceFilter);
        }

        PreparedQuery pq = datastore.prepare(listMessages);

        List<Message> messages = new ArrayList<Message>(Constants.MESSAGE_LIST_LIMIT);
        for (Entity entity : pq.asList(FetchOptions.Builder.withLimit(Constants.MESSAGE_LIST_LIMIT))) {
            messages.add(EntityConverter.message(entity));
        }

        return messages;
    }
}
