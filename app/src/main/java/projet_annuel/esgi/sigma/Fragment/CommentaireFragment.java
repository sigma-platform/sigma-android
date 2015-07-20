package projet_annuel.esgi.sigma.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import projet_annuel.esgi.sigma.Modele.Comment;
import projet_annuel.esgi.sigma.Modele.ListCommentAdapter;
import projet_annuel.esgi.sigma.Modele.ListTodoAdapter;
import projet_annuel.esgi.sigma.Modele.Task;
import projet_annuel.esgi.sigma.R;


public class CommentaireFragment extends Fragment {

    private int IdTask;
    private String[] lstCom;
    private String[] lstDate;
    private String[] lstInd;
    private String[] lstName;
    private ListView lstComment;
    private Button ajout;
    private EditText com;
    private ArrayList listC;
    private int position;

    private OnFragmentInteractionListener mListener;


    public static CommentaireFragment newInstance(String param1, String param2) {
        CommentaireFragment fragment = new CommentaireFragment();
        return fragment;
    }

    public CommentaireFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_commentaire, container, false);
        new LoadComment().execute();
        IdTask = getArguments().getInt("Id");
        ajout = (Button) v.findViewById(R.id.btn_Ajout);
        com = (EditText) v.findViewById(R.id.editCom);
        ajout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!com.getText().toString().equals(""))
                    new AjoutComment().execute();
                else {
                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                    adb.setTitle("Error");
                    adb.setMessage("You must write something");
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    adb.show();

                }
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(Uri uri);
    }

    public class LoadComment extends AsyncTask<Void, Void, Void> {

        boolean good;

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences setting = getActivity().getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
            String api_URL = getString(R.string.webservice).concat("/api/task/" + IdTask + "/comment?token=" + setting.getString("Token", ""));
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(api_URL);

            String reponse = null;
            HttpEntity httpEntity = null;
            try {
                HttpResponse response = null;
                response = httpclient.execute(httpGet);
                httpEntity = response.getEntity();
                reponse = EntityUtils.toString(httpEntity);
                JSONObject update = new JSONObject(reponse);
                good = update.getBoolean("success");
                if (good) {
                    JSONArray js = update.getJSONArray("payload");
                    lstDate = new String[js.length()];
                    lstCom = new String[js.length()];
                    lstInd = new String[js.length()];
                    lstName = new String[js.length()];
                    for (int i = 0; i < js.length(); i++) {
                        JSONObject objectInArray = js.getJSONObject(i);
                        lstDate[i] = objectInArray.getString("created_at");
                        lstCom[i] = objectInArray.getString("content");
                        lstInd[i] = objectInArray.getString("id");
                        lstName[i] = objectInArray.getJSONObject("user").getString("firstname") + " " +objectInArray.getJSONObject("user").getString("lastname");
                        Log.v("TestBoucle",lstName[i]);
                    }
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (good) {
                Log.v("ON entre","PAr la garnde porte");
                if (lstCom != null) {
                    lstComment = (ListView) getView().findViewById(R.id.lstCom);
                    listC = new ArrayList();
                    for (int i = 0; i < lstCom.length; i++) {
                        Log.v("IZI",lstName[i]);
                        listC.add(new Comment(lstCom[i], lstDate[i], lstName[i]));
                    }


                    lstComment.setAdapter(new ListCommentAdapter(getActivity().getApplicationContext(), listC));
                    lstComment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            remove(position);
                        }}
                    );
                }
            }
        }

    }

    private void remove(int position) {
        this.position = position;
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Are you sure ?");
        adb.setMessage("Do you want to delete?");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new DeleteComment().execute();
            }
        });
        adb.show();


    }

    public class AjoutComment extends AsyncTask<Void, Void, Void> {

        private boolean good;

        @Override
        protected Void doInBackground(Void... params) {
            DefaultHttpClient httpclient = new DefaultHttpClient();

            SharedPreferences setting = getActivity().getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
            String api_URL = getString(R.string.webservice).concat("/api/comment?token=" + setting.getString("Token", ""));


            try {

                String reponse = null;
                HttpEntity httpEntity = null;
                JSONObject update = null;

                Log.v("URLBITCH", api_URL);
                HttpPost httpPost = new HttpPost(api_URL);
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("content", com.getText().toString()));
                pairs.add(new BasicNameValuePair("task_id", IdTask + ""));
                pairs.add(new BasicNameValuePair("user_id",setting.getInt("IdClient",0)+""));


                httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httpPost.setHeader("X-Requested-With", "XMLHttpRequest");

                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse response = httpclient.execute(httpPost);
                httpEntity = response.getEntity();
                reponse = EntityUtils.toString(httpEntity);


                update = new JSONObject(reponse);
                good = update.getBoolean("success");
                if(!good)
                    Log.v("CESPASKADO",update.getString("error"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (good) {
                new LoadComment().execute();
            } else {
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Error");
                adb.setMessage("I Dont Know Why it doesnt add");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                adb.show();
            }
        }
    }

    public class DeleteComment extends AsyncTask<Void, Void, Void> {

        private boolean good;

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences setting = getActivity().getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
            String api_URL = getString(R.string.webservice).concat("/api/comment/" + lstInd[position] + "?token=" + setting.getString("Token", ""));
            HttpClient httpclient = new DefaultHttpClient();
            HttpDelete httpDelete = new HttpDelete(api_URL);
            httpDelete.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httpDelete.setHeader("X-Requested-With", "XMLHttpRequest");

            String reponse = null;
            HttpEntity httpEntity = null;
            try {
                HttpResponse response = null;
                response = httpclient.execute(httpDelete);
                httpEntity = response.getEntity();
                reponse = EntityUtils.toString(httpEntity);
                JSONObject update = new JSONObject(reponse);
                good = update.getBoolean("success");

            } catch (JSONException e) {
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
            if(good)
                new LoadComment().execute();
            else {
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Error");
                adb.setMessage("I Dont Know Why it doesnt delete");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                adb.show();
            }
        }
    }
}
