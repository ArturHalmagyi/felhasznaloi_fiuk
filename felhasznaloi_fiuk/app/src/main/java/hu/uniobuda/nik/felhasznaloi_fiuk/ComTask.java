package hu.uniobuda.nik.felhasznaloi_fiuk;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kristof on 2015.04.23..
 */
public class ComTask extends AsyncTask<String, Void, String> {

    String SERVER_IP;
    Handler myHandler;
    private onConnectionListener onConnectionListener;
    List<NameValuePair>  lData; // átküldésre kijelölt adatok itten lesznek


    public ComTask(String SERVER_IP) {
        this.SERVER_IP = SERVER_IP;
        this.myHandler = new Handler(Looper.getMainLooper());
        lData = new ArrayList<NameValuePair>();


    }

    public ComTask() {
        this.myHandler = new Handler(Looper.getMainLooper());
        lData = new ArrayList<NameValuePair>();
    }

    // postMessages: szerver kommunikáció után ez szól
    private void postDownloadSuccess(final String response){
        this.myHandler.post(new Runnable() {
            @Override
            public void run() {
                if(onConnectionListener != null)
                    onConnectionListener.onDownloadSuccess(response);
            }
        });
    }
    private void postDownloadFail(final String errorMessage){
        this.myHandler.post(new Runnable() {
            @Override
            public void run() {
                if(onConnectionListener != null)
                    onConnectionListener.onDownloadFail(errorMessage);
            }
        });
    }
    // ConnectionListener
    public void setOnConnectionListener(onConnectionListener onConnectionListener){
        this.onConnectionListener=onConnectionListener;
    }

    private String postDataToServer(String[] params) throws IOException{
        HttpParams httpParameters = new BasicHttpParams();
        // set the connection timeout and socket timeout parameters (milliseconds)
        HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
        HttpConnectionParams.setSoTimeout(httpParameters, 5000);
        // Adatok összeállítása lData-ba
        String muvelet=params[0];

        lData.add(new BasicNameValuePair("muvelet", muvelet));
        switch (muvelet){
            // 0: GetMenu
            // 1: GetTables
            case "2": // SetNewOrder
                lData.add(new BasicNameValuePair("asztal", params[1]));
                lData.add(new BasicNameValuePair("adat", params[2]));
                break;
            case "3": // SendPayRequestToServer
                lData.add(new BasicNameValuePair("asztal", params[1]));
                break;
            case "4": // Auth
                lData.add(new BasicNameValuePair("user", params[1]));
                lData.add(new BasicNameValuePair("pass", params[2]));
        }



        HttpClient client = new DefaultHttpClient(httpParameters);
        HttpPost httpPost = new HttpPost(SERVER_IP);
        httpPost.setEntity(new UrlEncodedFormEntity(lData));
        String resultStr ="";
        try {
            HttpResponse Response = client.execute(httpPost);
            if(Response.getStatusLine().getStatusCode() == 200){
                InputStream is = Response.getEntity().getContent();
                resultStr = StreamConverter(is).trim();
                is.close();
                return resultStr;
            }
        } catch (IOException e) {
            e.printStackTrace();
            postDownloadFail(e.getLocalizedMessage());
        }
        return resultStr;
    }
    // beérkező stream olvasása String-re
    private String StreamConverter(InputStream is) throws IOException, UnsupportedEncodingException{
        Reader reader = null;
        reader = new InputStreamReader(is, "UTF-8");
        int len = 0; String outStr="";
        char[] buffer = new char[1024];
        while((len=reader.read(buffer)) != -1)
            outStr = new String(buffer,0,len);
        return outStr;
    }


    @Override
    protected String doInBackground(String... params) {
        try {
            return postDataToServer(params); // Beérkező paraméterek átadása param[0]:művelet mindig
        } catch (IOException e) {
            postDownloadFail(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return "No response. :(";
    }

    @Override
    protected void onPostExecute(String s) {
        postDownloadSuccess(s); // szerver kommunikáció után
    }

// Interface
    public interface onConnectionListener{
        void onDownloadSuccess(String response);
        void onDownloadFail(String errorMessage);
    }


}
