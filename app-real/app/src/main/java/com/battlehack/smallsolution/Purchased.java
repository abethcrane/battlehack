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

        //uiHelper = new UiLifecycleHelper(this, null);
        //uiHelper.onCreate(savedInstanceState);
    }

/*
    // facebook sdk witchcraft
    private UiLifecycleHelper uiHelper;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }


    public void facebook(String vendor_name) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean fb_share = sharedPref.getBoolean("fb_share", true);
        final Boolean fb_auto_share = sharedPref.getBoolean("fb_auto_share", false);
        final String fb_message = sharedPref.getString("fb_auto_share_description", "I just bought a copy of the big issue from "+vendor_name);
        final Activity a = this;
        if (fb_share) {
            // Start Facebook Login
            Session.openActiveSession(this, true, new Session.StatusCallback() {
            // Callback when session changes state
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    if (session.isOpened()) {
                        if (fb_auto_share) {
                            // Now that we're logged in, let's post a status
                            Bundle params = new Bundle();
                            params.putString("message", fb_message);
                            new Request(
                                    session,
                                    "/me/feed",
                                    params,
                                    HttpMethod.POST,
                                    new Request.Callback() {
                                        public void onCompleted(Response response) {
                                        // handle the result
                                        }
                                    }
                            ).executeAsync();
                        } else {
                            // else let them do a custom one
                            OpenGraphAction action = GraphObject.Factory.create(OpenGraphAction.class);
                            action.setProperty("magazine", "http://www.thebigissue.org.au");

                            FacebookDialog shareDialog = new FacebookDialog.OpenGraphActionDialogBuilder(a, action, "battlehack.buy", "magazine")
                                    .build();
                            uiHelper.trackPendingDialogCall(shareDialog.present());
                        }
                    }
                }
            });
        }
    }*/

}
