package com.sahajamit.api;

import com.sahajamit.config.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SSEClient {
    private static final Logger logger = LoggerFactory.getLogger(SSEClient.class);
    private BlockingQueue<JSONObject> messageQueue = new LinkedBlockingQueue<>();
    private EventSource eventSource;

    private volatile boolean shouldStop = false;

    public void connectToStream() {
//        String pipedreamUrl = "https://api.pipedream.com/sources/dc_eKu7BZw/sse";
//        String authToken = "Bearer 4d6dc4f542020376efdd35b840ad311c";
        String pipedreamUrl = ConfigUtil.getString("sse_url");
        String authToken = ConfigUtil.getString("sse_token");
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(pipedreamUrl)
                .header("Authorization", authToken)
                .header("Cache-Control", "no-cache")
                .build();

        EventSource.Factory factory = EventSources.createFactory(client);
        eventSource = factory.newEventSource(request, new EventSourceListener() {
            @Override
            public void onOpen(EventSource eventSource, Response response) {
                System.out.println("SSE connection opened.");
            }
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                log.info("Received event: ",data);
                if (!data.equals("1") && !data.isEmpty()) {
                    JSONObject eventData = new JSONObject(data);
                    messageQueue.offer(eventData); // Add message to the queue
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                System.out.println("SSE connection closed.");
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                System.err.println("SSE connection failed: " + t.getMessage());
            }
        });
    }

    public JSONObject findMessageByUserIdAndStatus(String userId, String status) throws InterruptedException {
        while (!shouldStop) {
            JSONObject eventData = messageQueue.poll(10, TimeUnit.SECONDS);
            if (eventData != null) {
                if (eventData.getJSONObject("event").has("body")) {
                    JSONObject eventBody = eventData.getJSONObject("event").getJSONObject("body");
                    if (eventBody.has("event_type") && eventBody.getString("event_type").equals("user.updated")) {
                        JSONObject userData = eventBody.getJSONObject("data");
                        if (userData.getString("user_id").equals(userId) && userData.getString("status").equals(status)) {
                            this.stop(); // Signal to stop the thread
                            return eventData;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void stop() {
        shouldStop = true;
        if (eventSource != null) {
            eventSource.cancel();  // Cancel the SSE connection
        }
    }
}
