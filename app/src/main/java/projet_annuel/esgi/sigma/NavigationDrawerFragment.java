package projet_annuel.esgi.sigma;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {

    private RecyclerView recyclerView;

    public static final String PREF_FILE_NAME="txtPref";
    public static final String KEY_USER="user_learned_drawer";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View containerView;
    private ProjectListAdapter adapter;
    private String jsonProjects;

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreference(getActivity(),KEY_USER,"false"));
        if(savedInstanceState!=null){
            mFromSavedInstanceState = true;
        }
    }

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    public  void getData(){
        jsonProjects = getArguments().getString("Projects");
        List<ProjectData> data = new ArrayList<>();
        int icons = R.mipmap.ic_launcher;
        String[] titles = null;
        try {
            JSONObject json = new JSONObject(jsonProjects);
            JSONArray arrayJson = json.getJSONArray("payload");
            titles = new String[arrayJson.length()];

            for(int i = 0;i<arrayJson.length();i++){
                try {
                    JSONObject row = arrayJson.getJSONObject(i);
                    titles[i] = row.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(String str : titles){
            data.add(new ProjectData(str,icons));
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        return layout;
    }


    public void setUp(int drawerId,DrawerLayout dr,Toolbar tool) {
        mDrawerLayout = dr;
        containerView = getActivity().findViewById(drawerId);

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),dr,tool,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!mUserLearnedDrawer){
                    mUserLearnedDrawer = true;
                    saveToPreference(getActivity(),KEY_USER,mUserLearnedDrawer+"");
                }
                getActivity().invalidateOptionsMenu();
            }

        };
        if(!mUserLearnedDrawer && mFromSavedInstanceState){
            mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable(){

            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreference(Context context,String preferencesName,String preferenceValue){
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(preferencesName,preferenceValue);
        editor.apply();

    }

    public static String readFromPreference(Context context,String preferencesName,String preferenceValue){
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE_NAME,context.MODE_PRIVATE);
        return sharedPref.getString(preferencesName,preferenceValue);

    }
}
