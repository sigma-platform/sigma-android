package projet_annuel.esgi.sigma.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import projet_annuel.esgi.sigma.Fragment.NavigationDrawerFragment;
import projet_annuel.esgi.sigma.Fragment.TaskFragment;
import projet_annuel.esgi.sigma.Fragment.TaskListFragment;
import projet_annuel.esgi.sigma.Modele.SigmaApplication;
import projet_annuel.esgi.sigma.R;


public class ContentActivity extends ActionBarActivity {

    public NavigationDrawerFragment dlDrawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        SigmaApplication app = (SigmaApplication) getApplication();
        String tab = app.getJsonProjects();
        JSONObject update = null;
        try {
            update = new JSONObject(tab);
            JSONObject jsonObj = update.getJSONObject("Data");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.getBackground().setAlpha(240);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);



        dlDrawer = (NavigationDrawerFragment) findViewById(R.id.drawer_layout);

        dlDrawer.setupDrawerConfiguration((ListView) findViewById(R.id.lvDrawer), toolbar,
                R.layout.item_layout, R.id.flContent);

        dlDrawer.addNavItem("Tache", "Tache", TaskFragment.class);
        dlDrawer.addNavItem("Projet", "Projet", TaskListFragment.class);
        dlDrawer.addNavItem("Deconnexion","Deconnexion",null);

        if (savedInstanceState == null) {
            dlDrawer.selectDrawerItem(0);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
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

    public void deconnexion(){
        new SignOut().execute();
    }

    private class SignOut extends AsyncTask<Void, Void, Void> {


        String message = "";
        boolean good;

        @Override
        protected Void doInBackground(Void... arg0) {


            HttpClient httpclient = new DefaultHttpClient();

            SharedPreferences setting = getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
            String api_URL = getString(R.string.webservice).concat("/api/auth/logout?token=" +setting.getString("Token","") );
            HttpGet httpGet = new HttpGet(api_URL);

            String reponse = null;
            HttpEntity httpEntity = null;
            JSONObject signout = null;

            try {
                HttpResponse response = httpclient.execute(httpGet);
                httpEntity  = response.getEntity();
                reponse = EntityUtils.toString(httpEntity);


                signout = new JSONObject(reponse);
                good = signout.getBoolean("success");
                message = signout.getString("error");

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


        protected void onPostExecute(Void aVoid) {
            if(good) {
                SharedPreferences setting = getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = setting.edit();
                editor.putInt("IdClient", 0);
                editor.putString("Token", "");
                editor.commit();
                Intent intent = new Intent(ContentActivity.this,SignInActivity.class);
                startActivity(intent);
            }
            else{
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ContentActivity.this);
                alertDialogBuilder.setMessage(message);

                alertDialogBuilder.setPositiveButton("ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }

    }



}
