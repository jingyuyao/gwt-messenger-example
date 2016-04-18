package com.jingyu.example.shared;

import com.google.appengine.api.datastore.Entity;

public interface IEntityBuilder {
	public Entity create();
	public String getKind();
}
