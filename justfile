alias gt := gen_type

gen_type:
    flutter pub run pigeon \
    --input pigeons/type.dart \
    --dart_out lib/pigeon.dart \
    --java_out android/app/src/main/java/com/example/my_app/Pigeon.java \
    --java_package 'com.example.my_app'