package com.jingyu.example.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MessageServiceAsync {
    void addMessage(String message, AsyncCallback<Void> callback) throws RuntimeException;

    void listMessages(AsyncCallback<List<String>> callback) throws RuntimeException;
}