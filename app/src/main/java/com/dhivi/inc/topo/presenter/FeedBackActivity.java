package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dhivi.inc.topo.Manifest;
import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.plugin.holder.FeedbackList;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.PermissionUtils;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Manoj on 11/3/2017.
 */

public class FeedBackActivity extends Activity {

    @BindView(R.id.comments_edittext)
    EditText comments_edittext;
    @BindView(R.id.opinion_layout)
    RelativeLayout opinion_layout;
    @BindView(R.id.opinion_text)
    TextView opinion_text;
    int feedbackcall = 0;
    APIInterface apiInterface;
    String rating = "";
    ArrayList<FeedbackList> feedback = new ArrayList<>();
    ArrayList<String> feedbacklist = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Utility.showProgressDlg(FeedBackActivity.this, null);
        getOpinionMethoed();


    }

    @OnClick(R.id.back_layout)
    void onBack() {
        finish();
    }

    @OnClick(R.id.submit_button)
    void updateComments() {
        if (Utility.isEmpty(comments_edittext)) {
            Utility.errorDialog(FeedBackActivity.this, "Please Enter Your Comments");
        } else {

            if (feedbackcall == 0) {
                Utility.showProgressDlg(FeedBackActivity.this, null);
                feedbackcall = 1;
                updatehelpMethoed();
            }

        }
    }

    @OnClick(R.id.opinion_layout)
    void estimateLayout() {
        final Dialog dialog = new Dialog(FeedBackActivity.this);
        dialog.setContentView(R.layout.business_dialog);
        TextView text = (TextView) dialog.findViewById(R.id.header_titie);
        text.setText("Your opinion");
        ListView busness_listview = (ListView) dialog.findViewById(R.id.busness_listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.business_list_item, feedbacklist);
        busness_listview.setAdapter(adapter);
        busness_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                opinion_text.setText(feedbacklist.get(position));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void updatehelpMethoed() {
        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doFeedback("app", StoreDetails.getTopoUserId(FeedBackActivity.this), comments_edittext.getText().toString(), opinion_text.getText().toString(), "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(FeedBackActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(FeedBackActivity.this), utility.getDeviceIpAdress(FeedBackActivity.this), utility.getCurrentDate(), "feedback");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                feedbackcall = 0;
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i("", "update Response::" + data.getMsg());
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(FeedBackActivity.this, data.getMsg());
                    } else {
                        Utility.errorDialog(FeedBackActivity.this, data.getMsg(), FeedBackActivity.this);
                    }
                } else {
                    Utility.errorDialog(FeedBackActivity.this, "Unable to handle request");
                }

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                feedbackcall = 0;
                call.cancel();
            }
        });
    }


    void getOpinionMethoed() {
        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doFeedbackList("app", StoreDetails.getTopoUserId(FeedBackActivity.this), "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(FeedBackActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(FeedBackActivity.this), utility.getDeviceIpAdress(FeedBackActivity.this), utility.getCurrentDate(), "getprofile");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i("", "update Response::" + data.getMsg());
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(FeedBackActivity.this, data.getMsg());
                    } else {
                        feedback = data.getFeedbackList();
                        if (feedback != null && feedback.size() > 0) {
                            for (int i = 0; i < feedback.size(); i++) {
                                feedbacklist.add(feedback.get(i).getFeedback_question_question());
                            }
                        }
                    }
                } else {
                    Utility.errorDialog(FeedBackActivity.this, "Unable to handle request");
                }

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                call.cancel();
            }
        });
    }


}
