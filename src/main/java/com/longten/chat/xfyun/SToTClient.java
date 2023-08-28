package com.longten.chat.xfyun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.longten.chat.utils.XfyunUtils;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Objects;


@Slf4j
public class SToTClient extends WebSocketClient {

    private SimpMessagingTemplate template;
    private Long userId;

    public SToTClient(SimpMessagingTemplate template, URI serverUri, Draft protocolDraft, Long userId) {
        super(serverUri, protocolDraft);
        this.template = template;
        this.userId = userId;
        if(serverUri.toString().contains("wss")){
            trustAllHosts(this);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info("连接建立成功");
    }

    @Override
    public void onMessage(String msg) {
        JSONObject msgObj = JSON.parseObject(msg);
        String action = msgObj.getString("action");
        if (Objects.equals("started", action)) {
            // 握手成功
            log.info("握手成功！sid: {}", msgObj.getString("sid"));
        } else if (Objects.equals("result", action)) {
            // 转写结果
            log.info("result: " + XfyunUtils.getContent(msgObj.getString("data")));
            template.convertAndSend("/user/message/" + userId, msg);
        } else if (Objects.equals("error", action)) {
            // 连接发生错误
            System.out.println("Error: " + msg);
            System.exit(0);
        }
    }

    @Override
    public void onError(Exception e) {
        log.info("连接发生错误：" + e.getMessage() + ", " + new Date());
        e.printStackTrace();
        System.exit(0);
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        log.info("链接关闭");
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        log.info("服务端返回：" + new String(bytes.array(), StandardCharsets.UTF_8));
    }

    public void trustAllHosts(SToTClient appClient) {
        log.info("wss");
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
                // TODO Auto-generated method stub

            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            appClient.setSocket(sc.getSocketFactory().createSocket());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}