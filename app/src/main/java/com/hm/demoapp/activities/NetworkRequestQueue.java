package com.hm.demoapp.activities;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Nithya Pachiyappan on 3/16/2017.
 */

public class NetworkRequestQueue {
    Context context;
    String url ="http://www.google.com";
    private String result;

    NetworkRequestQueue(Context context){
        this.context = context;

        NetworkCall();

    }


  public void NetworkCall(){

      StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
              Log.e("response",response);
          }
      }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

          }
      }){

          @Override
          public byte[] getBody() throws AuthFailureError {
              return super.getBody();
          }

          @Override
          public Map<String, String> getHeaders() throws AuthFailureError {
              return super.getHeaders();
          }

          @Override
          public String getBodyContentType() {
              return super.getBodyContentType();
          }
      };

      RequestQueue request_queue = Volley.newRequestQueue(context);
      request_queue.add(request);
  }

    public void httpNetworkCall() throws IOException {
        URL url = null;
        try {
            url = new URL("http://www.android.com/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setReadTimeout(3000);
        urlConnection.setDoOutput(true);
        urlConnection.setConnectTimeout(3000);
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());

        readStream(in);

    }

    private void readStream(InputStream in) {

        int readSize = 0,numofchar = 0,maxLength=500;
        try {
            InputStreamReader isr = new InputStreamReader(in,"UTF-8");

            char[] buffer = new char[maxLength];
            while(numofchar < maxLength && readSize != -1){
                numofchar += readSize;
                try {
                    readSize = isr.read(buffer,numofchar,buffer.length-numofchar);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (numofchar != -1){
                numofchar = Math.min(numofchar,maxLength);
                result = new String(buffer, 0, numofchar);

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


}
