package com.jingyu.example.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.jingyu.example.client.MessageService;
import com.jingyu.example.shared.Constants;

@SuppressWarnings("serial")
public class MessageServlet extends RemoteServiceServlet implements MessageService {
    public static final String MESSAGE_KIND = "Message";
    private static final String LAST_MESSAGE_UPDATE_KEY = "lastMessageUpdate";

    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private final Cache cache;
    
    public MessageServlet(){
        super();
        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
        } catch (CacheException e) {
            throw new IllegalStateException("Cannot initialize cache.");
        }
        
        Query latestMessage = new Query(MESSAGE_KIND)
                .addSort("created", Query.SortDirection.DESCENDING)
                .addProjection(new PropertyProjection("created", Date.class));
        
        PreparedQuery pq = datastore.prepare(latestMessage);
        
        for (Entity entity : pq.asList(FetchOptions.Builder.withLimit(1))){
            Date created = (Date) entity.getProperty("created");
            cache.put(LAST_MESSAGE_UPDATE_KEY, created);
        }
    }

    @Override
    public void addMessage(String message) throws RuntimeException {
        Entity newMessage = new Entity(MESSAGE_KIND);
        Date created = new Date();
        newMessage.setProperty("created", created);
        newMessage.setProperty("message", message);

        cache.put(LAST_MESSAGE_UPDATE_KEY, created);
        datastore.put(newMessage);
    }

    /**
     * @param since: Blocks frequent access to the database.
     */
    @Override
    public List<String> listMessages(Date since) throws RuntimeException {
        if (since != null && cache.containsKey(LAST_MESSAGE_UPDATE_KEY)){
            Date lastUpdate = (Date) cache.get(LAST_MESSAGE_UPDATE_KEY);
            if (since.after(lastUpdate)){
                return Collections.emptyList();
            }
        }
        
        List<String> messages = new ArrayList<String>(Constants.MESSAGE_LIST_LIMIT);
        Query listMessages = new Query(MESSAGE_KIND)
                .addSort("created", Query.SortDirection.DESCENDING)
                .addProjection(new PropertyProjection("message", String.class));
        
        if (since != null){
            Filter sinceFilter = new FilterPredicate("created", FilterOperator.GREATER_THAN_OR_EQUAL, since);
            listMessages.setFilter(sinceFilter);
        }

        PreparedQuery pq = datastore.prepare(listMessages);

        for (Entity entity : pq.asList(FetchOptions.Builder.withLimit(Constants.MESSAGE_LIST_LIMIT))) {
            messages.add((String) entity.getProperty("message"));
        }

        return messages;
    }
}
