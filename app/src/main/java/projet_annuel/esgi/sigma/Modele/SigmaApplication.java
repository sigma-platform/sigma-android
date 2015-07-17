package projet_annuel.esgi.sigma.Modele;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import projet_annuel.esgi.sigma.R;

public class SigmaApplication extends Application
{
    private String jsonProjects;
    private int idTask;
    private int position;
    private boolean check;


    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public String getJsonProjects() {
        return jsonProjects;
    }

    public void setJsonProjects(String jsonProjects) {
        this.jsonProjects = jsonProjects;
    }


    public void UpdateTodo(View v){
        CheckBox cb = (CheckBox) v;
        position = Integer.parseInt(cb.getTag().toString()) + 1;
        if(cb.isChecked())
            check = true;
        else
            check = false;


        new UpdateTodo().execute();

    }

    private class UpdateTodo extends AsyncTask<Void, Void, Void> {

        private String message;
        private boolean good;
        @Override
        protected Void doInBackground(Void... params) {
            DefaultHttpClient httpclient = new DefaultHttpClient();

            SharedPreferences setting = getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
            String api_URL = getString(R.string.webservice).concat("/api/todo/" + position + "?token=" + setting.getString("Token", ""));


            try {

                URL url = new URL(getString(R.string.webservice).concat("/api/todo/" + position + "?token=" + setting.getString("Token", "")));


            String reponse = null;
            HttpEntity httpEntity = null;
            JSONObject update = null;

            Log.v("URLBITCH",api_URL);
            HttpPut httpPut = new HttpPut(String.valueOf(url));
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                if(check)
                    pairs.add(new BasicNameValuePair("done", "1"));
                else
                    pairs.add(new BasicNameValuePair("done", "0"));

                httpPut.setEntity(new UrlEncodedFormEntity(pairs));
                httpPut.setHeader(HTTP.CONTENT_TYPE,"application/x-www-form-urlencoded;charset=UTF-8");
                httpPut.setHeader("X-Requested-With", "XMLHttpRequest");


                HttpResponse response = httpclient.execute(httpPut);
                httpEntity = response.getEntity();
                reponse = EntityUtils.toString(httpEntity);


                update = new JSONObject(reponse);
                good = update.getBoolean("success");
                if(!good)
                    message = update.getString("error");

            }catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(!good){
                Log.v("Erreur ","erreur reseau la c est chelou" + message);
            }

        }
    }
}