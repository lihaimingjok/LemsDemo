package com.pcjz.lems.business.base;

import com.squareup.otto.BasicBus;

/**
 * Created by Greak on 2017/9/30.
 */

public class MessageBus extends BasicBus{
    private static MessageBus bus;

    private MessageBus(){}

    public static MessageBus getBusInstance(){
        if (bus==null)
        {
            bus=new MessageBus();
        }
        return bus;
    }
}
