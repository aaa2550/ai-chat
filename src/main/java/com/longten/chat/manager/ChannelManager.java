package com.longten.chat.manager;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.longten.chat.xfyun.SToTClient;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static com.longten.chat.utils.XfyunUtils.getHandShakeParams;

@Slf4j
@Component
public class ChannelManager {
    private LoadingCache<Long, SToTClient> sToTClientCache;
    private URI uri;
    private DraftWithOrigin draftWithOrigin;
    @Autowired
    private SimpMessagingTemplate template;

    public ChannelManager(@Value("xfyun.url") String url, @Value("xfyun.app-id") String appId, @Value("xfyun.secret-key") String secretKey) {
        this.sToTClientCache = Caffeine.newBuilder()
                .maximumSize(100_000)
                .expireAfterAccess(3, TimeUnit.SECONDS)
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .build(this::createWithConnectSToTClient);

        try {
            this.uri = new URI("ws://" + url + getHandShakeParams(appId, secretKey));
            this.draftWithOrigin = new DraftWithOrigin("http://" + url);
        } catch (URISyntaxException e) {
            log.error("build ChannelManager error.", e);
        }
    }

    public SToTClient getSToTClient(Long userId) {
        return sToTClientCache.get(userId);
    }

    public void invalidateSToTClient(Long userId) {
        sToTClientCache.invalidate(userId);
    }

    private SToTClient createWithConnectSToTClient(Long userId) {
        SToTClient client = new SToTClient(template, uri, draftWithOrigin, userId);
        client.connect();
        return client;
    }

    public static class DraftWithOrigin extends Draft_17 {
        private String originUrl;

        public DraftWithOrigin(String originUrl) {
            this.originUrl = originUrl;
        }

        @Override
        public Draft copyInstance() {
            return new DraftWithOrigin(this.originUrl);
        }

        @Override
        public ClientHandshakeBuilder postProcessHandshakeRequestAsClient(ClientHandshakeBuilder request) {
            super.postProcessHandshakeRequestAsClient(request);
            request.put("Origin", this.originUrl);
            return request;
        }
    }


}
