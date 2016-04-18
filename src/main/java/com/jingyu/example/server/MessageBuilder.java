package com.jingyu.example.server;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

/**
 * Consider using a library like Objectify for better ORM support.
 *
 * @author Jingyu
 *
 */
public class MessageBuilder extends EntityBuilder {
    public static final String KIND = "Message";

    public MessageBuilder() {
        super();
    }

    public MessageBuilder(Key parent) {
        super(parent);
    }

    public IEntityBuilder user(User user) {
        return setProperty("user", user);
    }

    public IEntityBuilder message(String message) {
        return setProperty("message", message);
    }

    @Override
    public String getKind() {
        return KIND;
    }
}
