// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.firebasemlvision;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import io.flutter.plugin.common.MethodChannel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TextRecognizer implements Detector {
  private final FirebaseVisionTextRecognizer recognizer;

  TextRecognizer(FirebaseVision vision, Map<String, Object> options) {
    final String modelType = (String) options.get("modelType");
    if (modelType.equals("onDevice")) {
      recognizer = vision.getOnDeviceTextRecognizer();
    } else if (modelType.equals("cloud")) {
      recognizer = vision.getCloudTextRecognizer();
    } else {
      final String message = String.format("No model for type: %s", modelType);
      throw new IllegalArgumentException(message);
    }
  }

  @Override
  public void handleDetection(final FirebaseVisionImage image, final MethodChannel.Result result) {
//    final FirebaseVisionDocumentTextRecognizer recognizer2 = FirebaseVision.getInstance().getCloudDocumentTextRecognizer(
//            new FirebaseVisionCloudDocumentRecognizerOptions.Builder()
//                    .setLanguageHints(Arrays.asList("en"))
//                    .enforceCertFingerprintMatch()
//                    .build()
//    );
//    recognizer2
//          .processImage(image)
//          .addOnSuccessListener(
//            new OnSuccessListener<FirebaseVisionDocumentText>() {
//              @Override
//              public void onSuccess(FirebaseVisionDocumentText firebaseDocumentVisionText) {
//                Map<String, Object> visionDocumentTextData = new HashMap<>();
//                visionDocumentTextData.put("text", firebaseDocumentVisionText.getText());
//
//                List<Map<String, Object>> allBlockData = new ArrayList<>();
//                for (FirebaseVisionDocumentText.Block block : firebaseDocumentVisionText.getBlocks()) {
//                  Map<String, Object> blockData = new HashMap<>();
//                  addDataDocument(
//                          blockData,
//                          block.getBoundingBox(),
//                          block.getConfidence(),
//                          block.getRecognizedBreak(),
//                          block.getRecognizedLanguages(),
//                          block.getText());
//
//                  List<Map<String, Object>> allParagraphData = new ArrayList<>();
//                  for (FirebaseVisionDocumentText.Paragraph paragraph : block.getParagraphs()) {
//                    Map<String, Object> paragraphData = new HashMap<>();
//                    addDataDocument(
//                            paragraphData,
//                            paragraph.getBoundingBox(),
//                            paragraph.getConfidence(),
//                            paragraph.getRecognizedBreak(),
//                            paragraph.getRecognizedLanguages(),
//                            paragraph.getText());
//
//                    List<Map<String, Object>> allWordData = new ArrayList<>();
//                    for (FirebaseVisionDocumentText.Word word : paragraph.getWords()) {
//                      Map<String, Object> wordData = new HashMap<>();
//                      addDataDocument(
//                              wordData,
//                              word.getBoundingBox(),
//                              word.getConfidence(),
//                              word.getRecognizedBreak(),
//                              word.getRecognizedLanguages(),
//                              word.getText());
//
//                      List<Map<String, Object>> allSymbolData = new ArrayList<>();
//                      for (FirebaseVisionDocumentText.Symbol symbol : word.getSymbols()) {
//                        Map<String, Object> symbolData = new HashMap<>();
//                        addDataDocument(
//                                symbolData,
//                                symbol.getBoundingBox(),
//                                symbol.getConfidence(),
//                                symbol.getRecognizedBreak(),
//                                symbol.getRecognizedLanguages(),
//                                symbol.getText());
//
//                        allSymbolData.add(symbolData);
//                      }
//
//                      wordData.put("symbols", allSymbolData);
//                      allWordData.add(wordData);
//                    }
//                    paragraphData.put("words", allWordData);
//                    allParagraphData.add(paragraphData);
//                  }
//                  blockData.put("paragraphs", allParagraphData);
//                  allBlockData.add(blockData);
//                }
//
//                visionDocumentTextData.put("blocks", allBlockData);
//                Log.d("bananas", visionDocumentTextData.toString());
//              }
//            })
//            .addOnFailureListener(
//                    new OnFailureListener() {
//                      @Override
//                      public void onFailure(@NonNull Exception exception) {
//                        result.error("documentTextRecognizerError", exception.getLocalizedMessage(), null);
//                      }
//                    });
//    try {
//      recognizer2.close();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
    recognizer
        .processImage(image)
        .addOnSuccessListener(
            new OnSuccessListener<FirebaseVisionText>() {
              @Override
              public void onSuccess(FirebaseVisionText firebaseVisionText) {
                Map<String, Object> visionTextData = new HashMap<>();
                visionTextData.put("text", firebaseVisionText.getText());

                List<Map<String, Object>> allBlockData = new ArrayList<>();
                for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                  Map<String, Object> blockData = new HashMap<>();
                  addData(
                      blockData,
                      block.getBoundingBox(),
                      block.getConfidence(),
                      block.getCornerPoints(),
                      block.getRecognizedLanguages(),
                      block.getText());

                  List<Map<String, Object>> allLineData = new ArrayList<>();
                  for (FirebaseVisionText.Line line : block.getLines()) {
                    Map<String, Object> lineData = new HashMap<>();
                    addData(
                        lineData,
                        line.getBoundingBox(),
                        line.getConfidence(),
                        line.getCornerPoints(),
                        line.getRecognizedLanguages(),
                        line.getText());

                    List<Map<String, Object>> allElementData = new ArrayList<>();
                    for (FirebaseVisionText.Element element : line.getElements()) {
                      Map<String, Object> elementData = new HashMap<>();
                      addData(
                          elementData,
                          element.getBoundingBox(),
                          element.getConfidence(),
                          element.getCornerPoints(),
                          element.getRecognizedLanguages(),
                          element.getText());

                      allElementData.add(elementData);
                    }
                    lineData.put("elements", allElementData);
                    allLineData.add(lineData);
                  }
                  blockData.put("lines", allLineData);
                  allBlockData.add(blockData);
                }

                visionTextData.put("blocks", allBlockData);
                result.success(visionTextData);
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception exception) {
                result.error("textRecognizerError", exception.getLocalizedMessage(), null);
              }
            });
  }

  private void addData(
      Map<String, Object> addTo,
      Rect boundingBox,
      Float confidence,
      Point[] cornerPoints,
      List<RecognizedLanguage> languages,
      String text) {

    if (boundingBox != null) {
      addTo.put("left", (double) boundingBox.left);
      addTo.put("top", (double) boundingBox.top);
      addTo.put("width", (double) boundingBox.width());
      addTo.put("height", (double) boundingBox.height());
    }

    addTo.put("confidence", confidence == null ? null : (double) confidence);

    List<double[]> points = new ArrayList<>();
    if (cornerPoints != null) {
      for (Point point : cornerPoints) {
        points.add(new double[] {(double) point.x, (double) point.y});
      }
    }
    addTo.put("points", points);

    List<Map<String, Object>> allLanguageData = new ArrayList<>();
    for (RecognizedLanguage language : languages) {
      Map<String, Object> languageData = new HashMap<>();
      languageData.put("languageCode", language.getLanguageCode());
      allLanguageData.add(languageData);
    }
    addTo.put("recognizedLanguages", allLanguageData);

    addTo.put("text", text);
  }

  private void addDataDocument(
          Map<String, Object> addTo,
          Rect boundingBox,
          Float confidence,
          FirebaseVisionDocumentText.RecognizedBreak recognizedBreak,
          List<RecognizedLanguage> languages,
          String text) {

    if (boundingBox != null) {
      addTo.put("left", (double) boundingBox.left);
      addTo.put("top", (double) boundingBox.top);
      addTo.put("width", (double) boundingBox.width());
      addTo.put("height", (double) boundingBox.height());
    }

    addTo.put("confidence", confidence == null ? null : (double) confidence);
    addTo.put("recognizedBreakType", recognizedBreak == null ? null : recognizedBreak.getDetectedBreakType());
    addTo.put("recognizedBreakPrefix", recognizedBreak == null ? null : recognizedBreak.getIsPrefix());

    List<Map<String, Object>> allLanguageData = new ArrayList<>();
    for (RecognizedLanguage language : languages) {
      Map<String, Object> languageData = new HashMap<>();
      languageData.put("languageCode", language.getLanguageCode());
      allLanguageData.add(languageData);
    }
    addTo.put("recognizedLanguages", allLanguageData);

    addTo.put("text", text);
  }

  @Override
  public void close() throws IOException {
    recognizer.close();
  }
}
