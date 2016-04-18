package com.jingyu.example.server;

import com.google.appengine.api.datastore.Entity;

/**
 * An interface to build App Engine Entities using the builder pattern.
 *
 * @author Jingyu
 *
 */
public interface IEntityBuilder {
    public Entity create();

    public String getKind();

    public IEntityBuilder setProperty(String propertyName, Object value);
}
