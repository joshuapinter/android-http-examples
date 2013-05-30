package com.publicobject.pictures;

import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

public class PicturesApp extends Application {
  static final String PICTURE_SERVER = "http://10.0.2.2:8910/";

  private RequestQueue requestQueue;
  private Gson gson;

  @Override public void onCreate() {
    super.onCreate();

    requestQueue = Volley.newRequestQueue(this);
    gson = new Gson();
  }

  public RequestQueue getRequestQueue() {
    return requestQueue;
  }

  public Gson getGson() {
    return gson;
  }
}
