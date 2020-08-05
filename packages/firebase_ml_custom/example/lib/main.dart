// Copyright 2020, the Chromium project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

import 'dart:async';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:tflite/tflite.dart';
import 'package:image_picker/image_picker.dart';
import 'package:firebase_ml_custom/firebase_ml_custom.dart';
import 'package:path_provider/path_provider.dart';

void main() {
  runApp(
    MaterialApp(
      home: MyApp(),
    ),
  );
}

/// Widget with a future function that initiates actions from FirebaseML
class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final picker = ImagePicker();
  File _image;
  List<Map<dynamic, dynamic>> _labels;
  //When the model is ready, _loaded changes to trigger the screen state change.
  Future<String> _loaded = loadModel();

  // Triggers selection of an image and the consequent inference
  Future<void> getImageLabels() async {
    final pickedFile = await picker.getImage(source: ImageSource.gallery);
    final image = File(pickedFile.path);
    if (image == null) {
      return;
    }
    var labels = List<Map>.from(await Tflite.runModelOnImage(
      path: image.path,
      imageStd: 127.5,
    ));
    setState(() {
      _labels = labels;
      _image = image;
    });
  }

  static Future<String> loadModel() async {
    var modelFile = await loadModelFromFirebase();
    return await loadTFLiteModel(modelFile);
  }

  // Download custom model from the Firebase console and return its file
  // located on the mobile device
  static Future<File> loadModelFromFirebase() async {

    // Create model with a name that is specified in the Firebase console
    var model = FirebaseCustomRemoteModel('mobilenet_v1_1_0_224');

    // Specify conditions when the model can be downloaded.
    // If there is no wifi access when the app is started,
    // this app will continue loading until the conditions are satisfied.
    var conditions = FirebaseModelDownloadConditions(androidRequireWifi: true, iosAllowCellularAccess: false);

    // Create model manager associated with default Firebase App instance.
    var modelManager = FirebaseModelManager.instance;

    // Begin downloading and wait until the model is downloaded successfully.
    await modelManager.download(model, conditions);
    assert(await modelManager.isModelDownloaded(model) == true);

    // Get latest model file to use it for inference by the interpreter.
    var modelFile = await modelManager.getLatestModelFile(model);
    assert(modelFile != null);
    return modelFile;
  }

  // Load the model into some TF Lite interpreter.
  // In this case interpreter provided by tflite plugin
  static Future<String> loadTFLiteModel(File modelFile) async {
    var appDirectory = await getApplicationDocumentsDirectory();
    var labelsData =
        await rootBundle.load("assets/labels_mobilenet_v1_224.txt");
    var labelsFile =
        await File(appDirectory.path + "_labels_mobilenet_v1_224.txt")
            .writeAsBytes(labelsData.buffer.asUint8List(
                labelsData.offsetInBytes, labelsData.lengthInBytes));

    assert(await Tflite.loadModel(
          model: modelFile.path,
          labels: labelsFile.path,
          isAsset: false,
        ) ==
        "success");
    return "Model is loaded";
  }

  // Shows image selection screen only when the model is ready to be used.
  Widget readyScreen() {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Firebase ML Custom example app'),
      ),
      body: Column(
        children: [
          _image != null
              ? Image.file(_image)
              : Text('Please select image to analyze.'),
          Column(
            children: _labels != null
                ? _labels.map((label) {
                    return Text("${label["label"]}");
                  }).toList()
                : [],
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: getImageLabels,
        child: Icon(Icons.add),
      ),
    );
  }

  // In case of error shows unrecoverable error screen.
  Widget errorScreen() {
    return Scaffold(
      body: Center(
        child: Text("Error loading model. Sorry about that :("),
      ),
    );
  }

  // In case of long loading shows loading screen until either model is ready or
  // error is received.
  Widget loadingScreen() {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.only(bottom:20.0),
              child: CircularProgressIndicator(),
            ),
            Text("Please make sure that you are using wifi."),
          ],
        ),
      ),
    );
  }

  // Shows different screens based on the state of the custom model.
  Widget build(BuildContext context) {
    return DefaultTextStyle(
      style: Theme.of(context).textTheme.headline2,
      textAlign: TextAlign.center,
      child: FutureBuilder<String>(
        future: _loaded,
        builder: (BuildContext context, AsyncSnapshot<String> snapshot) {
          if (snapshot.hasData) {
            return readyScreen();
          } else if (snapshot.hasError) {
            return errorScreen();
          } else {
            return loadingScreen();
          }
        },
      ),
    );
  }
}
