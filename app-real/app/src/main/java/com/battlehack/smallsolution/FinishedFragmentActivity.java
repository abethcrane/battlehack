package com.battlehack.smallsolution;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class FinishedFragmentActivity extends FragmentActivity {

    private FinishedFragmentAdapter adapter;
    private ViewPager mViewPager;
    private Vendor v;
    private String code;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finished_fragment_activity);
        Intent i = getIntent();
        v = (Vendor) i.getSerializableExtra("vendor");
        code = i.getStringExtra("code");
        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        adapter = new FinishedFragmentAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.other, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.action_settings:
                Intent i = new Intent(getApplicationContext(), Settings.class);
                startActivity(i);
                break;
            default:
                break;
        }
        return true;
    }

    private class FinishedFragmentAdapter extends FragmentPagerAdapter {

        public FinishedFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    Fragment fragment = new Purchased();
                    Bundle args = new Bundle();
                    args.putSerializable("vendor", v);
                    args.putString("code", code);
                    fragment.setArguments(args);
                    return fragment;
                case 1:
                    fragment = new CodeFragment();
                    args = new Bundle();
                    args.putString("code", code);
                    fragment.setArguments(args);
                    return fragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

}
