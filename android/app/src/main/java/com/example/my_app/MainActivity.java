package com.example.my_app;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;

import io.flutter.Log;
import io.flutter.embedding.android.FlutterActivity;

public class MainActivity extends FlutterActivity {
    private Api api;
    private PedometerService service;
    private EventEmitter emitter;
    private ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.i("Binder", "Bound service.");
            PedometerService.MyBinder myBinder = (PedometerService.MyBinder) binder;
            service = myBinder.getService();
            service.setCallback(MainActivity.this);
            emitter.emitEvent(EventType.ActivateEvent, service.getSteps());
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.i("Binder", "Unbound service.");
            service.removeCallback();
            service = null;
        }
    };

    public class Api implements Pigeon.Api2Host {

        private MainActivity activity = MainActivity.this;

        @Override
        public void ping(String message, Pigeon.Result<String> result) {
            Log.i("API", "ping");

            if(!message.equals("ping")) {
                Log.e("API", String.format("expected \"ping\", get \"%s\"", message));
                result.error(new Error("expected ping"));
                return;
            }

            result.success("pong");
        }

        @Override
        public void startService(Pigeon.Result<Void> result) {
            Log.i("Api", "start service called");
            activity.bindService();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = new Api();
        emitter = new EventEmitter(getFlutterEngine());

        Pigeon.Api2Host.setup(getFlutterEngine().getDartExecutor().getBinaryMessenger(), api);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unBindService();
    }

    public boolean isServiceBounded() {
        return service != null;
    }

    public void bindService() {
        if(!isServiceBounded()) {
            getContext().startService(new Intent(getContext(), PedometerService.class));

            getContext().bindService(new Intent(getContext(), PedometerService.class), connection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    public void emitEvent(EventType eventType, Object eventData) {
        emitter.emitEvent(eventType, eventData);
    }

    public void unBindService() {
        if(isServiceBounded()) {
            unbindService(connection);
        }
    }
}
