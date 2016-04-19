package com.jingyu.example.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.jingyu.example.shared.Message;

@RemoteServiceRelativePath("message")
public interface MessageService extends RemoteService {
    void addMessage(String content) throws RuntimeException;

    List<Message> listMessages(Date since) throws RuntimeException;
}
