package com.publicobject.pictures;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Collections;
import java.util.List;

public class PicturesActivity extends Activity {
  private PictureListLoader pictureListLoader = new PictureListLoader();
  private LinearLayout progress;
  private TextView error;
  private ListView picturesList;
  private PictureListAdapter adapter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    progress = (LinearLayout) findViewById(R.id.progress);
    picturesList = (ListView) findViewById(R.id.picturesList);
    error = (TextView) findViewById(R.id.error);

    adapter = new PictureListAdapter();
    picturesList.setAdapter(adapter);

    error.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        pictureListLoader.load(getApp());
      }
    });
  }

  @Override protected void onResume() {
    super.onResume();
    pictureListLoader.setTarget(this);
    pictureListLoader.load(getApp());
  }

  @Override protected void onPause() {
    pictureListLoader.setTarget(null);
    super.onPause();
  }

  private PicturesApp getApp() {
    return (PicturesApp) getApplication();
  }

  public static class PictureListLoader {
    PicturesActivity target;

    public void setTarget(PicturesActivity target) {
      this.target = target;
    }

    public void load(final PicturesApp app) {
      target.progress.setVisibility(View.VISIBLE);
      target.picturesList.setVisibility(View.GONE);
      target.error.setVisibility(View.GONE);

      RequestQueue requestQueue = app.getRequestQueue();
      final Gson gson = app.getGson();

      Response.Listener<String> listener = new Response.Listener<String>() {
        @Override public void onResponse(String s) {
          if (target == null) return;

          target.progress.setVisibility(View.GONE);
          target.picturesList.setVisibility(View.VISIBLE);
          target.error.setVisibility(View.GONE);

          List<String> pictures = gson.fromJson(s, new TypeToken<List<String>>() {}.getType());
          target.adapter.setPictureFileNames(pictures);
        }
      };
      Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override public void onErrorResponse(VolleyError volleyError) {
          if (target == null) return;

          target.error.setVisibility(View.VISIBLE);
          target.progress.setVisibility(View.GONE);
          target.picturesList.setVisibility(View.GONE);

          target.error.setText("Error: " + volleyError.getMessage());
        }
      };
      requestQueue.add(new StringRequest(PicturesApp.PICTURE_SERVER, listener, errorListener));
    }
  }

  public class PictureListAdapter extends BaseAdapter {
    private List<String> pictureFileNames = Collections.emptyList();

    public void setPictureFileNames(List<String> pictureFileNames) {
      this.pictureFileNames = pictureFileNames;
      notifyDataSetChanged();
    }

    @Override public int getCount() {
      return pictureFileNames.size();
    }

    @Override public Object getItem(int position) {
      return pictureFileNames.get(position);
    }

    @Override public long getItemId(int position) {
      return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
      LinearLayout view = convertView != null
          ? (LinearLayout) convertView
          : (LinearLayout) getLayoutInflater().inflate(R.layout.pictureitem, parent, false);
      TextView text = (TextView) view.findViewById(R.id.text);
      NetworkImageView picture = (NetworkImageView) view.findViewById(R.id.picture);

      PicturesApp app = getApp();

      String pictureFileName = pictureFileNames.get(position);
      text.setText(pictureFileName);
      if (isImage(pictureFileName)) {
        picture.setImageResource(R.drawable.loading);
        picture.setImageUrl(app.fileToUrl(pictureFileName), app.getImageLoader());
      } else {
        picture.setImageUrl(null, app.getImageLoader());
        picture.setImageResource(R.drawable.doc);
      }

      return view;
    }
  }

  private boolean isImage(String pictureFileName) {
    return pictureFileName.endsWith(".png")
        || pictureFileName.endsWith(".gif")
        || pictureFileName.endsWith(".jpg")
        || pictureFileName.endsWith(".jpeg");
  }
}
