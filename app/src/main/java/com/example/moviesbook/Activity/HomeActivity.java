package com.example.moviesbook.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.moviesbook.R;
import com.example.moviesbook.fragments.Chats;
import com.example.moviesbook.fragments.Feed;
import com.example.moviesbook.fragments.Profile;
import com.example.moviesbook.fragments.Search;
import com.example.moviesbook.fragments.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setIcon(R.drawable.search_frag));
        tabs.addTab(tabs.newTab().setIcon(R.drawable.home));
        tabs.addTab(tabs.newTab().setIcon(R.drawable.chats_frag));
        tabs.addTab(tabs.newTab().setIcon(R.drawable.profile));
       // SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        //viewPager.setAdapter(sectionsPagerAdapter);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Search());
        adapter.addFragment(new Feed());
        adapter.addFragment(new Chats());
        adapter.addFragment(new Profile());

        //Setting adapter
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        //SlidingTabStrip in TabLayout
        ViewGroup slidingTabStrip = (ViewGroup)tabs.getChildAt(0);
        //second tab in SlidingTabStrip
        View tab1 = slidingTabStrip.getChildAt(1);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tab1.getLayoutParams();
        layoutParams.weight = 2;
        tab1.setLayoutParams(layoutParams);

        View tab2 = slidingTabStrip.getChildAt(2);
        layoutParams = (LinearLayout.LayoutParams) tab2.getLayoutParams();
        layoutParams.weight = 2;
        tab2.setLayoutParams(layoutParams);


        ActionBar bar = getSupportActionBar();
        bar.setElevation(0);


        viewPager.setCurrentItem(1);

    }


    /// idk what the hell is this but the project crashes when i delete it
    ///// don't use this, scroll down
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Log_out:
                //TODO
                return true;
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_frag_menu, menu);
        return true;
    }

    ////This works just fine, don't delete
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Log_out:
                new AlertDialog.Builder(this)
                        //.setTitle("Log out")
                        .setMessage("Are you sure you want to Log out?")
                        //.setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(HomeActivity.this, "Logged out.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                                startActivity(intent);
                                SharedPreferences mPrefs = getSharedPreferences("user",MODE_PRIVATE);
                                SharedPreferences.Editor editor = mPrefs.edit();
                                editor.clear();
                                editor.apply();
                                finish();
                            }})
                        .setNegativeButton("Cancel", null).show();

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    class PagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}