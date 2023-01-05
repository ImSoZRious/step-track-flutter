import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:my_app/pigeon.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Startup Name Generator',
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Hello')
        ),
        body: const Center(child: Body())
      )
    );
  }
}

class Body extends StatefulWidget {
  const Body({Key? key}) : super(key: key);

  @override
  State<Body> createState() => _BodyState();
}

class _BodyState extends State<Body> {
  static const String stepsChannelName = 'PedometerService/SensorEvent';
  static const String activateChannelName = 'PedometerService/ActivateEvent';
  final Api2Host api = Api2Host();

  String statusMsg = "unregister";
  int steps = 0;

  EventChannel? stream;
  StreamSubscription? subscription;

  void start() async {
    try {
      // Check api status
      String pong = await api.ping("ping");
      if(pong != "pong") {
        throw Exception("ping failed");
      }

      setState(() { statusMsg = "registering"; });

      // request permission
      bool granted = await Permission.activityRecognition.request().isGranted;
      if(!granted) {
        throw Exception("user rejected permission request");
      }

      on(stepsChannelName, setSteps);
      on(activateChannelName, setSteps);

      await api.startService();

      setState(() { statusMsg = "steps: $steps"; }); }
    catch (err) {
      print("error: $err");
    }
  }

  void setSteps(steps) {
    setState(() {
      steps = steps;
      statusMsg = "steps: $steps";
    });
  }

  // helper function
  // subscription can be retrieve for further use.
  StreamSubscription on(String channelName, Function(dynamic) handler) {
    final stream = EventChannel(channelName);
    final subscription = stream.receiveBroadcastStream().listen(handler);
    return subscription;
  }

  @override
  Widget build(BuildContext context) {

    return Column(
      children: [
        Expanded(
            child: Align(
                alignment: const Alignment(0.0, 0.8),
                child: Text(statusMsg)
            )
        ),
        Expanded(
            child: Align(
              alignment: const Alignment(0.0, -1.0),
              child: TextButton(
                  onPressed: start,
                  child: const Text('Register')
              ),
            )
        ),
      ],
    );
  }
}
