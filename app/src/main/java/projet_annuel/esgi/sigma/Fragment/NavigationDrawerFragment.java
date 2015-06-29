package projet_annuel.esgi.sigma.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.ArrayList;

import projet_annuel.esgi.sigma.Activity.ContentActivity;
import projet_annuel.esgi.sigma.R;

public class NavigationDrawerFragment extends DrawerLayout {
    private ActionBarDrawerToggle drawerToggle;
    private ListView lvDrawer;
    private Toolbar toolbar;
    private ArrayAdapter<String> drawerAdapter;
    private ArrayList<FragmentNavItem> drawerNavItems;
    private int drawerContainerRes;

    public NavigationDrawerFragment(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NavigationDrawerFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationDrawerFragment(Context context) {
        super(context);
    }

    public void setupDrawerConfiguration(ListView drawerListView, Toolbar drawerToolbar, int drawerItemRes, int drawerContainerRes) {

        drawerNavItems = new ArrayList<NavigationDrawerFragment.FragmentNavItem>();
        drawerAdapter = new ArrayAdapter<String>(getActivity(), drawerItemRes,
                new ArrayList<String>());
        this.drawerContainerRes = drawerContainerRes;
        lvDrawer = drawerListView;
        toolbar = drawerToolbar;
        lvDrawer.setAdapter(drawerAdapter);
        lvDrawer.setOnItemClickListener(new FragmentDrawerItemListener());

        drawerToggle = setupDrawerToggle();
        setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    // addNavItem("First", "First Fragment", FirstFragment.class)
    public void addNavItem(String navTitle, String windowTitle, Class<? extends Fragment> fragmentClass) {
        drawerAdapter.add(navTitle);
        drawerNavItems.add(new FragmentNavItem(windowTitle, fragmentClass));
    }


    public void selectDrawerItem(int position) {

        if(position == 1){
            ContentActivity act = (ContentActivity) getActivity();
            act.deconnexion();
        }else {
            FragmentNavItem navItem = drawerNavItems.get(position);
            Fragment fragment = null;
            try {
                fragment = navItem.getFragmentClass().newInstance();
                Bundle args = navItem.getFragmentArgs();
                if (args != null) {
                    fragment.setArguments(args);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(drawerContainerRes, fragment).commit();

            lvDrawer.setItemChecked(position, true);
            setTitle(navItem.getTitle());
            closeDrawer(lvDrawer);
        }
    }


    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }


    private FragmentActivity getActivity() {
        return (FragmentActivity) getContext();
    }

    private ActionBar getSupportActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    private void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    public  void setTitle2(CharSequence title){
        getSupportActionBar().setTitle(title);
    }

    private class FragmentDrawerItemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectDrawerItem(position);
        }
    }

    private class FragmentNavItem {
        private Class<? extends Fragment> fragmentClass;
        private String title;
        private Bundle fragmentArgs;

        public FragmentNavItem(String title, Class<? extends Fragment> fragmentClass) {
            this(title, fragmentClass, null);
        }

        public FragmentNavItem(String title, Class<? extends Fragment> fragmentClass, Bundle args) {
            this.fragmentClass = fragmentClass;
            this.fragmentArgs = args;
            this.title = title;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return fragmentClass;
        }

        public String getTitle() {
            return title;
        }

        public Bundle getFragmentArgs() {
            return fragmentArgs;
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(getActivity(), this, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    public boolean isDrawerOpen() {
        return isDrawerOpen(lvDrawer);
    }
}