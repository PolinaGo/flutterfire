package io.flutter.plugins.firebaseml;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel.Result;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ModelManager {
  @RequiresApi(api = Build.VERSION_CODES.N)
  public static void handleModelManager(@NonNull MethodCall call, @NonNull final Result result) {

    switch (call.method) {
      case "FirebaseModelManager#download":
        download(call, result);
        break;
      case "FirebaseModelManager#getLatestModelFile":
        getLatestModelFile(call, result);
        break;
      case "FirebaseModelManager#isModelDownloaded":
        isModelDownloaded(call, result);
        break;
      default:
        result.notImplemented();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public static void download(@NonNull MethodCall call, @NonNull final Result result) {
    assert (call.argument("modelName") != null);
    assert (call.argument("conditions") != null);

    String modelName = call.argument("modelName");
    Map<String, Boolean> conditionsToMap = call.argument("conditions");
    final FirebaseCustomRemoteModel remoteModel =
        new FirebaseCustomRemoteModel.Builder(modelName).build();
    FirebaseModelDownloadConditions.Builder conditionsBuilder =
        new FirebaseModelDownloadConditions.Builder();

    if (conditionsToMap.get("requireCharging")) {
      conditionsBuilder.requireCharging();
    }
    if (conditionsToMap.get("requireDeviceIdle")) {
      conditionsBuilder.requireDeviceIdle();
    }
    if (conditionsToMap.get("requireWifi")) {
      conditionsBuilder.requireWifi();
    }

    FirebaseModelDownloadConditions conditions = conditionsBuilder.build();

    FirebaseModelManager modelManager;
    if (call.argument("app") == null) {
      modelManager = FirebaseModelManager.getInstance();
    } else {
      String app = call.argument("app");
      modelManager = FirebaseModelManager.getInstance(FirebaseApp.getInstance(app));
    }

    modelManager
        .download(remoteModel, conditions)
        .addOnSuccessListener(
            new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void v) {
                result.success(remoteModelToMap(remoteModel));
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception exception) {
                result.error("FirebaseModelManager", exception.getLocalizedMessage(), null);
              }
            });
  }

  private static void getLatestModelFile(@NonNull MethodCall call, @NonNull final Result result) {
    assert (call.argument("modelName") != null);
    String modelName = call.argument("modelName");

    final FirebaseCustomRemoteModel remoteModel =
        new FirebaseCustomRemoteModel.Builder(modelName).build();

    FirebaseModelManager modelManager;
    if (call.argument("app") == null) {
      modelManager = FirebaseModelManager.getInstance();
    } else {
      String app = call.argument("app");
      modelManager = FirebaseModelManager.getInstance(FirebaseApp.getInstance(app));
    }

    modelManager
        .getLatestModelFile(remoteModel)
        .addOnCompleteListener(
            new OnCompleteListener<File>() {
              @Override
              public void onComplete(@NonNull Task<File> task) {
                File modelFile = task.getResult();
                if (modelFile != null) {
                  result.success(modelFile.getAbsolutePath());
                } else {
                  result.error(
                      "FirebaseModelManager", task.getException().getLocalizedMessage(), null);
                }
              }
            });
  }

  private static void isModelDownloaded(@NonNull MethodCall call, @NonNull final Result result) {
    assert (call.argument("modelName") != null);
    String modelName = call.argument("modelName");

    final FirebaseCustomRemoteModel remoteModel =
        new FirebaseCustomRemoteModel.Builder(modelName).build();

    FirebaseModelManager modelManager;
    if (call.argument("app") == null) {
      modelManager = FirebaseModelManager.getInstance();
    } else {
      String app = call.argument("app");
      modelManager = FirebaseModelManager.getInstance(FirebaseApp.getInstance(app));
    }

    modelManager
        .isModelDownloaded(remoteModel)
        .addOnCompleteListener(
            new OnCompleteListener<Boolean>() {
              @Override
              public void onComplete(@NonNull Task<Boolean> task) {
                Boolean isModelDownloaded = task.getResult();
                if (isModelDownloaded != null) {
                  result.success(isModelDownloaded);
                } else {
                  result.error(
                      "FirebaseModelManager", task.getException().getLocalizedMessage(), null);
                }
              }
            });
  }

  private static Map<String, String> remoteModelToMap(FirebaseCustomRemoteModel model) {
    Map remoteModelToMap = new HashMap<String, String>();
    remoteModelToMap.put("modelName", model.getModelName());
    remoteModelToMap.put("modelHash", model.getModelHash());
    return remoteModelToMap;
  }
}
