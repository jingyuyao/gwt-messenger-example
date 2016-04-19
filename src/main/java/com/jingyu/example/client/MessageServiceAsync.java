package com.jingyu.example.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.jingyu.example.shared.Message;

public interface MessageServiceAsync {
    void addMessage(String content, AsyncCallback<Void> callback) throws RuntimeException;

    void listMessages(Date since, AsyncCallback<List<Message>> callback) throws RuntimeException;
}