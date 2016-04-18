package com.jingyu.example.server;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

/**
 * Subclasses can create builders by providing methods like this:
 * 
 * <pre>
 * <code>
 * public IEntityBuilder propertyName(String value){
 *     return setProperty("propertyName", value);
 * }
 * </code>
 * </pre>
 * 
 * @author Jingyu
 *
 */
public abstract class EntityBuilder implements IEntityBuilder {
    private Entity entity;

    public EntityBuilder() {
        this(null);
    }

    public EntityBuilder(Key parent) {
        entity = new Entity(getKind(), parent);
        entity.setProperty("created", new Date());
    }

    @Override
    public final Entity create() {
        return entity;
    }

    @Override
    public final IEntityBuilder setProperty(String propertyName, Object value) {
        entity.setProperty(propertyName, value);
        return this;
    }

    @Override
    public abstract String getKind();

}
