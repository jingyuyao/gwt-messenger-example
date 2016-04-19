package com.jingyu.example.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * Note: GWT's definition of serialization is a bit different from the standard.
 * See: http://www.gwtproject.org/doc/latest/DevGuideServerCommunication.html#DevGuideSerializableTypes
 * @author Jingyu
 *
 */
public class Message implements Serializable{
    private static final long serialVersionUID = -8426648222828028064L;

    public static final String KIND = "Message";
    
    public Date created;
    public String content;
    
    /**
     * Empty constructor is needed by GWT
     */
    public Message(){
        this("");
    }
    
    public Message(String content){
        this(new Date(), content);
    }
    
    public Message(Date created, String content){
        this.created = created;
        this.content = content;
    }
}
