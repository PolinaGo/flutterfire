package io.flutter.plugins.firebaseml_example;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.plugins.shim.ShimPluginRegistry;
import io.flutter.plugins.firebaseml.FirebaseMLPlugin;
import io.flutter.plugins.imagepicker.ImagePickerPlugin;
import io.flutter.plugins.pathprovider.PathProviderPlugin;
import sq.flutter.tflite.TflitePlugin;

import io.flutter.plugins.firebase.core.FirebaseCorePlugin;

public class MainActivity extends FlutterActivity {
  @Override
  public void configureFlutterEngine(FlutterEngine flutterEngine) {
    flutterEngine.getPlugins().add(new FirebaseMLPlugin());

    final ShimPluginRegistry shimPluginRegistry = new ShimPluginRegistry(flutterEngine);
    PathProviderPlugin.registerWith(
        shimPluginRegistry.registrarFor("io.flutter.plugins.pathprovider.PathProviderPlugin"));
    ImagePickerPlugin.registerWith(
        shimPluginRegistry.registrarFor("io.flutter.plugins.imagepicker.ImagePickerPlugin"));
    TflitePlugin.registerWith(shimPluginRegistry.registrarFor("sq.flutter.tflite.TflitePlugin"));
    FirebaseCorePlugin.registerWith(shimPluginRegistry.registrarFor("io.flutter.plugins.firebase.core.FirebaseCorePlugin"));
  }
}
