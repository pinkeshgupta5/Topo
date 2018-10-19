package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.utils.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by User on 12/16/2017.
 */

public class EmptyToposActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_emptytopo);
        ButterKnife.bind(this);
        Utility.startPermissionActivity(EmptyToposActivity.this, 124);
    }

    @OnClick(R.id.starttopo_button)
    void CreateTopo()
    {
        Intent i = new Intent(EmptyToposActivity.this, MapActivity.class);
        startActivity(i);
    }
}
