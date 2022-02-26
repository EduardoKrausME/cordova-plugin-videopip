package com.eduardokraus.videopip;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.app.Activity;
import android.app.PictureInPictureParams;
import android.util.Rational;
import android.util.Log;
import android.os.Build;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class VideoPictureInPicture extends CordovaPlugin {
    private PictureInPictureParams.Builder pictureInPictureParamsBuilder = null;
    private CallbackContext onChanged_Callback = null;
    private final String TAG = "VideoPictureInPicture";
    private boolean hasPIPModeSuported = false;

    /**
     * @param cordova
     * @param webView
     */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        hasPIPModeSuported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O; // >= SDK 26 //Oreo
        if (hasPIPModeSuported) {
            try {
                Class.forName("android.app.PictureInPictureParams");
            } catch (Exception e) {
                hasPIPModeSuported = false;
            }
        }
    }

    /**
     * Inicializa o PiP
     */
    private void initializePip() {
        if (pictureInPictureParamsBuilder == null) {
            if (hasPIPModeSuported) {
                try {
                    pictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
                } catch (Exception e) {
                    pictureInPictureParamsBuilder = null;
                    String stackTrace = Log.getStackTraceString(e);
                    Log.d(TAG, stackTrace);
                }
            } else {
                Log.d(TAG, "PIP unavailable.");
            }
        }
    }

    /**
     * @param action          The action to execute.
     * @param args            The exec() arguments.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return bol
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("enterPip")) {
            Double width = args.getDouble(0);
            Double height = args.getDouble(1);
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    enterPip(width, height, callbackContext);
                }
            });
            return true;
        } else if (action.equals("isPip")) {
            boolean status = hasPIPModeSuported && pictureInPictureParamsBuilder != null && this.cordova.getActivity().isInPictureInPictureMode();
            callbackContext.success(status ? "true" : "false");
            return true;
        } else if (action.equals("onChanged")) {
            this.initializePip();
            if (onChanged_Callback == null) {
                onChanged_Callback = callbackContext; //save global callback for later callbacks
                PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT); //send no result to execute the callbacks later
                pluginResult.setKeepCallback(true); // Keep callback
            }
            return true;
        } else if (action.equals("isSupported")) {
            callbackContext.success(hasPIPModeSuported ? "true" : "false");
            return true;
        }
        return false;
    }

    /**
     * @param newConfig The new device configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (onChanged_Callback != null && hasPIPModeSuported) {
            try {
                boolean active = this.cordova.getActivity().isInPictureInPictureMode(); //>= SDK 26 //Oreo
                Log.d(TAG, "pipChanged " + active);
                if (active) {
                    this.callbackFunction(true, "true");
                } else {
                    this.callbackFunction(true, "false");
                }
            } catch (Exception e) {
                String stackTrace = Log.getStackTraceString(e);
                Log.d(TAG, "pipChanged ERR " + stackTrace);
                this.callbackFunction(false, stackTrace);
            }
        }
    }

    /**
     * Envia ao Cordova alterações de status
     *
     * @param isSuccess
     * @param status
     */
    private void callbackFunction(boolean isSuccess, String status) {
        if (isSuccess) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, status);
            result.setKeepCallback(true);
            onChanged_Callback.sendPluginResult(result);
        } else {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, status);
            result.setKeepCallback(true);
            onChanged_Callback.sendPluginResult(result);
        }
    }

    /**
     * Muda o app para em PiP
     *
     * @param width
     * @param height
     * @param callbackContext
     */
    private void enterPip(Double width, Double height, CallbackContext callbackContext) {
        try {
            this.initializePip();
            if (pictureInPictureParamsBuilder != null) {
                Activity activity = this.cordova.getActivity();
                boolean active = activity.isInPictureInPictureMode(); //>= SDK 26 //Oreo
                Log.d(TAG, "enterPip " + active);
                if (active) {
                    callbackContext.success("Already in picture-in-picture mode.");
                } else {
                    Context context = cordova.getActivity().getApplicationContext();
                    Intent openMainActivity = new Intent(context, activity.getClass());
                    openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    activity.startActivityIfNeeded(openMainActivity, 0);
                    Rational aspectRatio = new Rational(Integer.valueOf(width.intValue()), Integer.valueOf(height.intValue()));
                    pictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
                    activity.enterPictureInPictureMode(pictureInPictureParamsBuilder.build());
                    callbackContext.success("Scaled picture-in-picture mode started.");
                }
            } else {
                callbackContext.error("Picture-in-picture unavailable.");
            }
        } catch (Exception e) {
            String stackTrace = Log.getStackTraceString(e);
            Log.d(TAG, "enterPip ERR " + stackTrace);
            callbackContext.error(stackTrace);
        }
    }
}