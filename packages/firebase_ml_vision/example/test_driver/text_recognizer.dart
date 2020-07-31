// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

part of 'firebase_ml_vision.dart';

void textRecognizerTests() {
  group('$TextRecognizer', () {
    final TextRecognizer recognizer = FirebaseVision.instance.textRecognizer();

    // TODO(bparrishMines): Unskip this test when text is recognized on ios.
    // Related issues:
    // https://github.com/FirebaseExtended/flutterfire/issues/1912
    // https://github.com/FirebaseExtended/flutterfire/issues/2770
    test('processImage', () async {
      final String tmpFilename = await _loadImage('assets/test_text.png');
      final FirebaseVisionImage visionImage =
          FirebaseVisionImage.fromFilePath(tmpFilename);

      final VisionText text = await recognizer.processImage(visionImage);

      expect(text.text, 'TEXT');
    }, skip: (Platform.isIOS));

    test('close', () {
      expect(recognizer.close(), completes);
    });
  });
}
