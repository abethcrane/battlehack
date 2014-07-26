package com.battlehack.smallsolution;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.textservice.SpellCheckerService;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.*;

public class Purchased extends Activity {
    // Initializing variables
    EditText inputName;
    EditText inputEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchased);
        TextView tv = (TextView) findViewById(R.id.Message);
        Intent i = getIntent();
        tv.setText(String.format(getString(R.string.purchased_result), i.getStringExtra("name"), i.getStringExtra("code")));

        //uiHelper = new UiLifecycleHelper(this, null);
        //uiHelper.onCreate(savedInstanceState);
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
