package projet_annuel.esgi.sigma.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import projet_annuel.esgi.sigma.R;
import projet_annuel.esgi.sigma.Modele.SigmaApplication;
import projet_annuel.esgi.sigma.Modele.TaskDelegate;

//the first activity started by the application
public class MainActivity extends Activity implements TaskDelegate {

    private String jsonProject;
    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_appbar);

        SharedPreferences setting = getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
        if(setting.getString("Token","").equals("") ) {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            connected = true;
        }
        else{
            new LoadProjectsData(this).execute();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void taskCompletionResult(Boolean result) {
        if (!result) {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(intent);
            connected = true;
        } else {
            SigmaApplication app = (SigmaApplication) getApplication();
            app.setJsonProjects(jsonProject);
            Intent intent = new Intent(MainActivity.this,ContentActivity.class);
            startActivity(intent);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // Task who load all the list of project
    private class LoadProjectsData extends AsyncTask<Void, Void, Void> {

        boolean good;
        String message = "";

        private TaskDelegate delegate;

        public LoadProjectsData(TaskDelegate delegate) {
            this.delegate = delegate;
        }

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences setting = getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
            String api_URL = getString(R.string.webservice).concat("/api/project/user/?token=" + setting.getString("Token", ""));
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(api_URL);
            String reponse = null;
            HttpEntity httpEntity = null;
            JSONObject listJSON = null;

            try {
                HttpResponse response = httpclient.execute(httpGet);
                httpEntity = response.getEntity();
                reponse = EntityUtils.toString(httpEntity);


                listJSON = new JSONObject(reponse);
                good = listJSON.getBoolean("success");
                if (good) {
                    jsonProject = reponse;
                } else {
                    message = listJSON.getString("error");
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(good) {
                SigmaApplication app = (SigmaApplication) getApplication();
                app.setJsonProjects(jsonProject);
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        }
    }
}