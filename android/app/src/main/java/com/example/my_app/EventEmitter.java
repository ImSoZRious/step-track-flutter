package com.example.my_app;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.StreamHandler;
import io.flutter.Log;

import java.util.HashMap;
import java.util.Objects;

public class EventEmitter {
    private HashMap<EventType, EventChannel.EventSink> map;

    private static String channelNamePrefix = "PedometerService/";

    public EventEmitter(FlutterEngine flutterEngine) {
        map = new HashMap<EventType, EventChannel.EventSink>();

        for(EventType eventType : EventType.values()) {
            new EventChannel(Objects.requireNonNull(flutterEngine).getDartExecutor(), channelNamePrefix + eventType.toString())
                    .setStreamHandler(new StreamHandler() {
                            @Override
                            public void onListen(Object args, final EventChannel.EventSink events) {
                                Log.i("EventEmitter", String.format("Channel %s started listening.", eventType.toString()));
                                map.put(eventType, events);
                            }

                            @Override
                            public void onCancel(Object args) {
                                Log.i("EventEmitter", String.format("Channel %s is canceled.", eventType.toString()));
                                map.remove(eventType);
                            }
                        }
                    );
       }
    }

    public void emitEvent(EventType eventType, Object eventData) {
        EventChannel.EventSink sink = map.get(eventType);

        if (sink != null) {
            Log.i(
                    "EventEmitter",
                    String.format("emit %s %s",
                            eventType.toString(),
                            eventData.toString()
                    )
            );
            sink.success(eventData);
        }
    }
}
