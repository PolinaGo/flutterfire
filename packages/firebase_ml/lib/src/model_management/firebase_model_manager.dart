// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:async';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:firebase_core/firebase_core.dart';

import 'firebase_remote_model.dart';
import 'firebase_model_download_conditions.dart';

/// The user downloads a remote model with [FirebaseModelManager].
///
/// The model name is the key for a model,
/// and should be consistent with the name of the model
/// that has been uploaded to the Firebase console.
///
/// https://firebase.google.com/docs/reference/android/com/google/
/// firebase/ml/common/modeldownload/FirebaseModelManager
class FirebaseModelManager {

  /// Returns the [FirebaseStorage] instance, initialized with a custom
  /// [FirebaseApp] if [app] is specified. Otherwise the instance will be
  /// initialized with the default [FirebaseApp].
  ///
  /// The [FirebaseModelManager] instance is a singleton for fixed [app].
  ///
  /// The [app] argument is the custom [FirebaseApp].
  FirebaseModelManager(this.app) {
    if (_initialized) return;
    _initialized = true;
  }

  FirebaseModelManager._({this.app});

  /// Means for communication with native platform code
  @visibleForTesting
  static const MethodChannel channel =
  MethodChannel('plugins.flutter.io/firebase_ml');

  static bool _initialized = false;

  /// Returns the [FirebaseStorage] instance, initialized with the default
  /// [FirebaseApp].
  static final FirebaseModelManager instance = FirebaseModelManager._();

  /// The [FirebaseApp] instance to which this [FirebaseModelManager] belongs.
  ///
  /// If null, the default [FirebaseApp] is used.
  final FirebaseApp app;

  /// Initiates the download of remoteModel if the download hasn't begun.
  Future<void> download(FirebaseRemoteModel model,
      FirebaseModelDownloadConditions conditions) async {

    var modelMap = Map<String, String>.from(await channel.invokeMethod(
        "FirebaseModelManager#download",
        { 'app': app?.name,
          'modelName': model.modelName, 'conditions': conditions.toMap()}));
    model.modelHash = modelMap['modelHash'];
  }

  /// Returns the [File] containing the latest model for the remote model name.
  Future<File> getLatestModelFile(FirebaseRemoteModel model) async {
    var modelPath = await channel.invokeMethod(
        "FirebaseModelManager#getLatestModelFile",
        { 'app': app?.name,
          'modelName': model.modelName});
    return File(modelPath);
  }

  /// Returns whether the given [FirebaseRemoteModel] is currently downloaded.
  Future<bool> isModelDownloaded(FirebaseRemoteModel model) {
    return channel.invokeMethod("FirebaseModelManager#isModelDownloaded",
        { 'app': app?.name,
          'modelName': model.modelName});
  }
}
