package com.jingyu.example.shared;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public abstract class EntityBuilder implements IEntityBuilder {
	private Entity entity;
	
	public EntityBuilder(){
		this(null);
	}
	
	public EntityBuilder(Key parent){
		entity = new Entity(getKind(), parent);
		entity.setProperty("created", new Date());
	}
	
	public Entity create(){
		return entity;
	}
	
	public abstract String getKind();
	
	protected IEntityBuilder setProperty(String propertyName, Object value){
		entity.setProperty(propertyName, value);
		return this;
	}
}
