package com.example.a603936.wearapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedActivity extends Activity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    ArrayList<HashMap<String, String>> articleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_main);
        articleList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list_view);
        new GetContacts().execute();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(FeedActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                /*BufferedReader jsonReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.data_json)));
                StringBuilder jsonBuilder = new StringBuilder();
                for (String line = null; (line = jsonReader.readLine()) != null;) {
                    jsonBuilder.append(line).append("\n");
                }
                //Parse Json
                JSONTokener tokener = new JSONTokener(jsonBuilder.toString());
                JSONArray jsonArray = new JSONArray(tokener);
                JSONObject jsonObj = new JSONObject(jsonBuilder.toString());*/

                JSONObject jsonObj = new JSONObject(httpActivity().toString());
                JSONArray jsonArray = jsonObj.getJSONArray("atricles");

                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");

                        // tmp hash map for single contact
                        HashMap<String, String> article = new HashMap<>();

                        // adding each child node to HashMap key => value
                        article.put("id", id);
                        article.put("name", name);

                        // adding contact to contact list
                        articleList.add(article);
                    }
                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            ListAdapter adapter = new SimpleAdapter(
                    FeedActivity.this, articleList,
                    R.layout.list_item, new String[]{"name"}, new int[]{R.id.name});

            lv.setAdapter(adapter);
        }
    }

    public static JSONObject httpActivity() {
        try {
            Gson gson = new Gson();
            URL url = new URL("http://10.223.66.126:8084/articles/6059465/wear/5");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String test = br.readLine().replace("{\"articles\":", "");
            test = test.replace("}]}", "}]");
//            System.out.println(test);

            Type listType = new TypeToken<List<Article>>() {
            }.getType();
            List<Article> articleList = (List<Article>) gson.fromJson(test, listType);

            JSONArray jsonArray = new JSONArray();
            for (Article article : articleList) {
                JSONObject articleObj = new JSONObject();
                articleObj.put("id", article.getId());
                articleObj.put("name", article.getHeadline());
                jsonArray.put(articleObj);
            }
            JSONObject finalObj = new JSONObject();
            finalObj.put("atricles", jsonArray);
            conn.disconnect();
            return finalObj;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}