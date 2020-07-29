# Example of Firebase Machine Learning Custom

Demonstrates how to use the firebase_ml_custom plugin.

## Usage
##### Preparation
This example uses the *image_picker* plugin to get images from the device gallery. If using an iOS
device you will have to configure your project with the correct permissions seen under iOS
configuration [here](https://pub.dartlang.org/packages/image_picker).

The example also uses the *tflite* plugin to perform inference. If using an Android device you may need to modify your `android/app/build.gradle` file as specified [here](https://pub.dartlang.org/packages/tflite).

##### Running
In order to run this example app you first need to **upload a tflite model to the Firebase console.**

This app uses `mobilenet_v1_1.0_224.tflite` label recognition model, which you can find in the `assets` folder.
Please upload this model in the Firebase console under the name `mobilenet_v1_1_0_224`.

## Getting Started
For help getting started with Flutter, view our online
[documentation.](https://flutter.io/)
