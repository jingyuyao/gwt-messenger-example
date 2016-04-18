package com.jingyu.example.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MessageServiceAsync {
	void addMessage(String message, AsyncCallback<Void> callback) throws RuntimeException;
}