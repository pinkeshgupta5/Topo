package com.dhivi.inc.topo.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AccessList;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.presenter.AccessListActivity;
import com.dhivi.inc.topo.presenter.AuthentacationActivity;
import com.dhivi.inc.topo.presenter.EditProfileActivity;
import com.dhivi.inc.topo.presenter.FeedBackActivity;
import com.dhivi.inc.topo.presenter.HelpActivity;
import com.dhivi.inc.topo.utils.CircleTransform;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Manoj on 10/28/2017.
 */

public class SettingsFragment extends Fragment {

    @BindView(R.id.profile_imageview)
    ImageView profile_imageview;
    @BindView(R.id.name_textview)
    TextView name_textview;
    @BindView(R.id.ph_textview)
    TextView ph_textview;
    @BindView(R.id.email_textview)
    TextView email_textview;
    @BindView(R.id.version_textview)
    TextView version_textview;


    APIInterface apiInterface;
    String TAG = SettingsFragment.class.getSimpleName();

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        ButterKnife.bind(this, view);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        setVersionNumber();
        Utility.showProgressDlg(getActivity(),null);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getprofileData();
    }

    @OnClick(R.id.access_layout)
    void callingAccessList()
    {
     /*   Intent i = new Intent(getActivity(), AccessListActivity.class);
        startActivity(i);*/
    }

    @OnClick(R.id.help_layout)
    void callingHelp() {
        Intent i = new Intent(getActivity(), HelpActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.feedback_layout)
    void callingFeedback() {
        Intent i = new Intent(getActivity(), FeedBackActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.privacy_layout)
    void callingPrivacy() {

    }

    @OnClick(R.id.terms_layout)
    void callingTerms() {

    }

    @OnClick(R.id.logout_layout)
    void callingLogout() {
        Utility.showProgressDlg(getActivity(),null);
        getLogoutMethoed();
    }

    @OnClick(R.id.profile_layout)
    void profile_layout() {
        Intent i = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(i);
    }

    void setVersionNumber() {
        Utility utility = new Utility();
        version_textview.setText(getString(R.string.version) + " " + utility.getVersionNumber(getActivity()));
    }

    void getprofileData() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doProfileView("app", StoreDetails.getTopoUserId(getContext()),"ime1","ime2",utility.getDeviceName(),"andrpod",utility.getDeviceModel(),"browsername","browserversion","yes",utility.getVersionNumber(getActivity()),"tracklat","tracllon",StoreDetails.getLoginkey(getActivity()),utility.getDeviceIpAdress(getActivity()),utility.getCurrentDate(), "home");

        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                Logger.i(TAG,"data response"+response.body().toString());


                if (data.getResult().toLowerCase().equals("failed")) {
                    Utility.errorDialog(getActivity(), data.getMsg());
                } else {
                  if(data.getProfileView()!=null)
                  {
                      name_textview.setText(data.getProfileView().getTopo_user_name());
                      ph_textview.setText(data.getProfileView().getTopo_user_mobile());
                      email_textview.setText(data.getProfileView().getTopo_user_email());
                      if(data.getProfileView().getTopo_image_url()!=null&&!data.getProfileView().getTopo_image_url().equals(""))
                      {
                          Picasso.with(getActivity()).load(getString(R.string.profile_image)+data.getProfileView().getTopo_image_url()).error(R.drawable.defalut_profile).transform(new CircleTransform()).into(profile_imageview);
                      }else
                      {
                          Picasso.with(getActivity()).load(R.drawable.defalut_profile).into(profile_imageview);
                      }

                  }

                }

                Logger.i(TAG, "forgot Response::" + data.getMsg());
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }

    void getLogoutMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doLogout("app", StoreDetails.getTopoUserId(getContext()),"ime1","ime2",utility.getDeviceName(),"andrpod",utility.getDeviceModel(),"browsername","browserversion","yes",utility.getVersionNumber(getActivity()),"tracklat","tracllon",StoreDetails.getLoginkey(getActivity()),utility.getDeviceIpAdress(getActivity()),utility.getCurrentDate(), "home");

        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.showProgressDlg(getActivity(),null);
                AppResponseData data = response.body();
                Logger.i(TAG,"data response"+response.body().toString());


                if (data.getResult().toLowerCase().equals("failed")) {
                    errorDialog(getActivity(), data.getMsg());
                } else {

                    StoreDetails.setLoginKey(getActivity(),null);
                    StoreDetails.setContactsKey(getActivity(),null);
                    Intent i = new Intent(getActivity(), AuthentacationActivity.class);
                    startActivity(i);
                    getActivity().finish();

                }

                Logger.i(TAG, "forgot Response::" + data.getMsg());
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.showProgressDlg(getActivity(),null);
                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }

    public  void errorDialog(Context con, String error) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(con, R.style.AboutDialog));
        alertDialog.setTitle("");

        alertDialog.setMessage(error);
        alertDialog.setPositiveButton(con.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();
    }
}

