package com.battlehack.smallsolution;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.textservice.SpellCheckerService;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.*;

public class Purchased extends Fragment {
    private Vendor v;
    private String code;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.purchased, container, false);
        Bundle args = getArguments();
        TextView tv = (TextView) rootView.findViewById(R.id.Message);
        v = (Vendor) args.getSerializable("vendor");
        code = args.getString("code");
        tv.setText(String.format(getString(R.string.purchased_result), v.item, v.name));
        v.getImageAsync((ImageView) rootView.findViewById(R.id.purchImg));

        // Clear old notifications
        NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();


        return rootView;

    }

}
