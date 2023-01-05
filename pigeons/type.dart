import 'package:pigeon/pigeon.dart';

class SensorEvent {
  int? startTime;
  int? endTime;
  int? steps;
}

@HostApi()
abstract class Api2Host {
  @async
  String ping(String message);

  @async
  void startService();
}