package com.jingyu.example.server;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.jingyu.example.shared.Message;

/**
 * All code involving Entity must be only on the server side since
 * GWT can't use App Engine API.
 * 
 * Lets follow the style of:
 * public static Entity toEntity(T toConvert);
 * public static T convertClass(Entity entity);
 * @author Jingyu
 *
 */
public class EntityConverter {
    
    public static Entity toEntity(Message message){
        Entity entity = new Entity(Message.KIND);
        entity.setProperty("created", message.created);
        entity.setProperty("content", message.content);
        return entity;
    }
    
    public static Message message(Entity entity){
        Date created = (Date)entity.getProperty("created");
        String content = (String)entity.getProperty("content");
        return new Message(created, content);
    }
}
