package com.publicobject.pictures;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

public class PicturesApp extends Application {
  static final String PICTURE_SERVER = "http://10.0.2.2:8910/";

  private RequestQueue requestQueue;
  private ImageLoader imageLoader;
  private Gson gson;

  @Override public void onCreate() {
    super.onCreate();

    requestQueue = Volley.newRequestQueue(this);

    ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
      LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(20);

      @Override public void putBitmap(String key, Bitmap value) {
        imageCache.put(key, value);
      }

      @Override public Bitmap getBitmap(String key) {
        return imageCache.get(key);
      }
    };

    imageLoader = new ImageLoader(requestQueue, imageCache);

    gson = new Gson();
  }

  public RequestQueue getRequestQueue() {
    return requestQueue;
  }

  public Gson getGson() {
    return gson;
  }

  public String fileToUrl(String fileName) {
    return PICTURE_SERVER + fileName;
  }

  public ImageLoader getImageLoader() {
    return imageLoader;
  }
}
