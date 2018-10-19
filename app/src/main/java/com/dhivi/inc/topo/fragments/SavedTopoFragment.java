package com.dhivi.inc.topo.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.adapter.InstantToposAdapter;
import com.dhivi.inc.topo.adapter.MyTopoListAdapter;
import com.dhivi.inc.topo.adapter.SearchResultAdapter;
import com.dhivi.inc.topo.adapter.TopoRequestAdapter;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.plugin.holder.Topos;
import com.dhivi.inc.topo.presenter.InstatntTopoActivity;
import com.dhivi.inc.topo.presenter.RequestTopoActivity;
import com.dhivi.inc.topo.presenter.SearchActivity;
import com.dhivi.inc.topo.presenter.ViewTopoActivity;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Manoj on 10/30/2017.
 */

public class SavedTopoFragment extends Fragment {

    @BindView(R.id.savedtopo_recyclerview)
    RecyclerView savedtopo_recyclerview;
    @BindView(R.id.location_imageview)
    ImageView location_imageview;
    @BindView(R.id.mytopo_recyclerview)
    RecyclerView mytopo_recyclerview;
    @BindView(R.id.mytopo_textview)
    TextView mytopo_textview;
    @BindView(R.id.saved_textview)
    TextView saved_textview;
    @BindView(R.id.noresults)
    TextView noresults;
    MyTopoListAdapter myTopoListAdapter;
    String TAG = SavedTopoFragment.class.getSimpleName();
    APIInterface apiInterface;

    public static SavedTopoFragment newInstance() {
        return new SavedTopoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.savedtopo_fragment, container, false);

        ButterKnife.bind(this, view);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        initView();

        return view;

    }

    void initView() {
        Utility.showProgressDlg(getActivity(), null);
        getRequestSavedToposMethoed();

        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(location_imageview);
        Glide.with(this).load(R.raw.location).into(imageViewTarget);

        savedtopo_recyclerview.setHasFixedSize(true);
        savedtopo_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mytopo_recyclerview.setHasFixedSize(true);
        mytopo_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

    }


    @OnClick(R.id.location_imageview)
    void callingInstanttopo() {

        Intent i = new Intent(getActivity(), InstatntTopoActivity.class);
        startActivity(i);

    }

    @OnClick(R.id.search_imageview)
    void onSearchClicked() {
        Intent i = new Intent(getActivity(), SearchActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.search_edittext)
    void onSearchEdittextClicked() {
        Intent i = new Intent(getActivity(), SearchActivity.class);
        startActivity(i);
    }


    MyTopoListAdapter.OnItemSelectedListener selectedListener = new MyTopoListAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(Topos topos) {
            Intent i = new Intent(getActivity(), ViewTopoActivity.class);
            i.putExtra("topoName", topos.getTopo_name());
            i.putExtra("from", "notfinish");
            startActivity(i);
        }
    };


    void getRequestSavedToposMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doSavedTopos("app", StoreDetails.getTopoUserId(getActivity()), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(getActivity()), "", "", StoreDetails.getLoginkey(getActivity()), utility.getDeviceIpAdress(getActivity()), utility.getCurrentDate(), "home");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();

                if (data.getResult().toLowerCase().equals("failed")) {
                    errorDialog(getActivity(), data.getMsg());
                } else {
                    int check = 0;
                    if (data.getTopos() != null && data.getTopos().size() > 0) {
                        check = 1;
                        myTopoListAdapter = new MyTopoListAdapter(selectedListener, data.getTopos());
                        mytopo_recyclerview.setAdapter(myTopoListAdapter);
                    }else {
                        mytopo_textview.setVisibility(View.GONE);
                    }
                    if (data.getSavedToposList() != null && data.getSavedToposList().size() > 0) {
                        check = 1;
                        myTopoListAdapter = new MyTopoListAdapter(selectedListener, data.getSavedToposList());
                        savedtopo_recyclerview.setAdapter(myTopoListAdapter);
                    }else
                    {
                        saved_textview.setVisibility(View.GONE);
                    }
                    if(check==0)
                    {
                        noresults.setVisibility(View.VISIBLE);
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

    public void errorDialog(Context con, String error) {
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
