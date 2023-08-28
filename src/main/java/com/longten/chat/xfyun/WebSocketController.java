package com.longten.chat.xfyun;

import com.longten.chat.common.R;
import com.longten.chat.manager.ChannelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

    @Autowired
    private ChannelManager channelManager;
    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/stream")
    public void handleStreamMessage(String message) {
        System.out.println("Received message: " + message);
        template.convertAndSend("/user/message/1", message);
    }

    @PostMapping("/upload-audio")
    public R<Void> uploadAudio(@RequestBody byte[] audioData, @RequestBody Long userId) {
        channelManager.getSToTClient(userId).send(audioData);
        return R.success();
    }

    @MessageMapping("/audio")
    public R<Void> voiceChat(byte[] audioData) {
        System.out.println(audioData);
        return R.success();
    }
}
