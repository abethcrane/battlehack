package com.battlehack.smallsolution;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CodeFragment extends Fragment {

    private String code;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.code_fragment, container, false);
        Bundle args = getArguments();
        TextView tv = (TextView) rootView.findViewById(R.id.code_dis);
        code = args.getString("code");
        tv.setText(code);
        return rootView;
    }
}
