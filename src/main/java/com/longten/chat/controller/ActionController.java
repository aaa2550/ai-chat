package com.longten.chat.controller;

import com.longten.chat.common.R;
import com.longten.chat.manager.ChannelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/action")
public class ActionController {

    @Autowired
    private ChannelManager channelManager;

    @RequestMapping
    public R<Void> start(Long userId) {
        channelManager.getSToTClient(userId);
        return R.success();
    }
}
