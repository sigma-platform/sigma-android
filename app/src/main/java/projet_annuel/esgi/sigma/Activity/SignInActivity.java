package projet_annuel.esgi.sigma.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import projet_annuel.esgi.sigma.R;


public class SignInActivity extends Activity {

    EditText editEmail = null;
    EditText editPass =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        editPass = (EditText) findViewById(R.id.edit_Password);
        editEmail = (EditText) findViewById(R.id.edit_Email);
        Button SignIn = (Button) findViewById(R.id.BTN_SignIn);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IsConnected().execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
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

    private class IsConnected extends AsyncTask<Void, Void, Void> {


        String message = "";
        String session = "";
        int idclient = 0;
        boolean good;

        @Override
        protected Void doInBackground(Void... arg0) {

            String api_URL = getString(R.string.webservice).concat("/api/auth/login");
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(api_URL);

            String reponse = null;
            HttpEntity httpEntity = null;
            JSONObject inscription = null;

            try {
                List<NameValuePair> infoUser = new ArrayList<NameValuePair>(2);
                infoUser.add(new BasicNameValuePair("email",((EditText) findViewById(R.id.edit_Email)).getText().toString() ));
                infoUser.add(new BasicNameValuePair("password", ((EditText) findViewById(R.id.edit_Password)).getText().toString() ));

                httppost.setEntity(new UrlEncodedFormEntity(infoUser));
                HttpResponse response = httpclient.execute(httppost);
                httpEntity  = response.getEntity();
                reponse = EntityUtils.toString(httpEntity);


                inscription = new JSONObject(reponse);
                boolean error = inscription.getBoolean("success");
                if(error){
                    JSONObject jsonObj = inscription.getJSONObject("payload");
                    good = true;
                    session = jsonObj.getString("token");
                    idclient = jsonObj.getInt("user_id");

                }
                else {
                    message = inscription.getString("error");
                    good = false;
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


        protected void onPostExecute(Void aVoid) {
            if(good) {
                SharedPreferences setting = getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = setting.edit();
                editor.putInt("IdClient", idclient);
                editor.putString("Token", session);
                editor.commit();
                SignInActivity.this.finish();
            }
            else{
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignInActivity.this);
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
