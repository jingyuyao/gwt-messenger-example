package com.jingyu.example.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("message")
public interface MessageService extends RemoteService {
    void addMessage(String message) throws RuntimeException;

    List<String> listMessages(Date since) throws RuntimeException;
}
