package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.fragments.SettingsFragment;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.utils.CircleTransform;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Manoj on 11/2/2017.
 */

public class EditProfileActivity extends Activity {


    @BindView(R.id.name_edittext)
    EditText name_edittext;
    @BindView(R.id.signup_email_edittext)
    EditText signup_email_edittext;
    @BindView(R.id.mobile_edittext)
    EditText mobile_edittext;
    @BindView(R.id.profile_image)
    ImageView profile_image;
    @BindView(R.id.image_layout)
    RelativeLayout image_layout;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    APIInterface apiInterface;
    File destination;
    int signupcall = 0;
    String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    TransferUtility transferUtility;
    AmazonS3 s3Client;
    String filename = "";
    String bucket = "edispatch/DhiviExpress/DeliveryDocs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_editprofile);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Utility.showProgressDlg(EditProfileActivity.this, null);
        getProfileMethoed();
        s3credentialsProvider();
        setTransferUtility();

    }

    @OnClick(R.id.back_layout)
    void onBack() {
        finish();
    }

    @OnClick(R.id.edit_image)
    void onEditImage() {
        image_layout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.cross_layout)
    void hideimageLayout() {
        image_layout.setVisibility(View.GONE);
    }

    @OnClick(R.id.changeprofilepic_button)
    void changeProfilePic() {
        image_layout.setVisibility(View.GONE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @OnClick(R.id.removeprofilepic_button)
    void removeProfilePicMethoed() {
        Utility.showProgressDlg(EditProfileActivity.this, null);
        getImageremoveMethoed();
    }


    @OnClick(R.id.signup_password_layout)
    void changePwd() {
        Intent i = new Intent(EditProfileActivity.this, ChangePasswordActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.signup_password_edittext)
    void changeeditPwd() {
        Intent i = new Intent(EditProfileActivity.this, ChangePasswordActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.updateprofile_button)
    void updateProfile() {
        if (Utility.isEmpty(signup_email_edittext)) {
            Utility.errorDialog(EditProfileActivity.this, "Please Enter Email");
        } else if (!validate(signup_email_edittext.getText().toString())) {
            Utility.errorDialog(EditProfileActivity.this, "Please Enter Valid Email");
        } else if (Utility.isEmpty(name_edittext)) {
            Utility.errorDialog(EditProfileActivity.this, "Please Enter Email");
        } else if (Utility.isEmpty(mobile_edittext)) {
            Utility.errorDialog(EditProfileActivity.this, "Please Enter Mobile Number");
        } else {
            if (signupcall == 0) {
                signupcall = 1;
                Utility.showProgressDlg(EditProfileActivity.this, null);
                updateProfileMethoed();
            }

        }
    }

    public boolean validate(final String hex) {

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    void getProfileMethoed() {
        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doProfileView("app", StoreDetails.getTopoUserId(EditProfileActivity.this), "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(EditProfileActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(EditProfileActivity.this), utility.getDeviceIpAdress(EditProfileActivity.this), utility.getCurrentDate(), "getprofile");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i("", "update Response::" + data.getMsg());
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(EditProfileActivity.this, data.getMsg());
                    } else {
                        if (data.getProfileView() != null) {
                            signup_email_edittext.setText(data.getProfileView().getTopo_user_email());
                            name_edittext.setText(data.getProfileView().getTopo_user_name());
                            mobile_edittext.setText(data.getProfileView().getTopo_user_mobile());
                            if (data.getProfileView().getTopo_image_url() != null && !data.getProfileView().getTopo_image_url().equals("")) {
                                filename = data.getProfileView().getTopo_image_url();
                                Picasso.with(EditProfileActivity.this).load(getString(R.string.profile_image) + data.getProfileView().getTopo_image_url()).error(R.drawable.defalut_profile).transform(new CircleTransform()).into(profile_image);
                            } else {
                                Picasso.with(EditProfileActivity.this).load(R.drawable.defalut_profile).into(profile_image);
                            }

                        }
                    }
                } else {
                    Utility.errorDialog(EditProfileActivity.this, "Unable to handle request");
                }

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                call.cancel();
            }
        });
    }

    void updateProfileMethoed() {
        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doProfileUpdate("app", StoreDetails.getTopoUserId(EditProfileActivity.this), mobile_edittext.getText().toString(), signup_email_edittext.getText().toString(), "gender", filename, name_edittext.getText().toString(), "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(EditProfileActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(EditProfileActivity.this), utility.getDeviceIpAdress(EditProfileActivity.this), utility.getCurrentDate(), "editprofile");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                signupcall = 0;
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i("", "update Response::" + data.getMsg());
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(EditProfileActivity.this, data.getMsg());
                    } else {
                        Utility.errorDialog(EditProfileActivity.this, data.getMsg());
                    }
                } else {
                    Utility.errorDialog(EditProfileActivity.this, "Unable to handle request");
                }

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                signupcall = 0;
                call.cancel();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        String currenttime = "" + System.currentTimeMillis();
        sendingSizes(thumbnail, 0, currenttime);
        sendingSizes(thumbnail, 50, currenttime);
        sendingSizes(thumbnail, 100, currenttime);
        sendingSizes(thumbnail, 150, currenttime);
        sendingSizes(thumbnail, 200, currenttime);
    }

    private void sendingSizes(Bitmap thumbnail, int THUMBSIZE, String currenttime) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (THUMBSIZE != 0)
            thumbnail = Bitmap.createScaledBitmap(thumbnail, THUMBSIZE, THUMBSIZE, false);
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            if (THUMBSIZE == 0) {
                filename = StoreDetails.getTopoUserId(EditProfileActivity.this) + "_" + currenttime + ".jpg";
                finalUplaod(destination, StoreDetails.getTopoUserId(EditProfileActivity.this) + "_" + currenttime + ".jpg");
            } else
                finalUplaod(destination, THUMBSIZE + "_" + StoreDetails.getTopoUserId(EditProfileActivity.this) + "_" + currenttime + ".jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(EditProfileActivity.this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void s3credentialsProvider() {

        CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider =
                new CognitoCachingCredentialsProvider(
                        EditProfileActivity.this.getApplicationContext(),
                        "ap-south-1:f320b760-c78a-4eb1-a212-9fda24a89ce0", // Identity Pool ID
                        Regions.AP_SOUTH_1 // Region
                );
        createAmazonS3Client(cognitoCachingCredentialsProvider);
    }


    public void createAmazonS3Client(CognitoCachingCredentialsProvider
                                             credentialsProvider) {

        s3Client = new AmazonS3Client(credentialsProvider);

        s3Client.setS3ClientOptions(
                S3ClientOptions.builder()
                        .setPathStyleAccess(true)
                        .disableChunkedEncoding().build());
        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
    }

    public void setTransferUtility() {

        transferUtility = new TransferUtility(s3Client, EditProfileActivity.this.getApplicationContext());
    }

    public void finalUplaod(File file, String fileName) {
        if (file.exists()) {
            TransferObserver transferObserver = transferUtility.upload(
                    bucket,     /* The bucket to upload to */
                    fileName,    /* The key for the uploaded object */
                    file,       /* The file where the data to upload exists */
                    CannedAccessControlList.PublicRead
            );

            transferObserverListener(transferObserver);
        } else {
            Log.i("", "no file");
        }
    }

    public void transferObserverListener(TransferObserver transferObserver) {

        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Toast.makeText(EditProfileActivity.this.getApplicationContext(), "Uplaoding profile picture"
                        , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent / bytesTotal * 100);
                Toast.makeText(EditProfileActivity.this.getApplicationContext(), "Progress in %"
                        + percentage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                Log.e("error", "error");
            }

        });
    }

    void getImageremoveMethoed() {
        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doRemoveImage("app", StoreDetails.getTopoUserId(EditProfileActivity.this), "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(EditProfileActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(EditProfileActivity.this), utility.getDeviceIpAdress(EditProfileActivity.this), utility.getCurrentDate(), "getprofile");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i("", "update Response::" + data.getMsg());
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(EditProfileActivity.this, data.getMsg());
                    } else {
                        image_layout.setVisibility(View.GONE);
                    }
                } else {
                    Utility.errorDialog(EditProfileActivity.this, "Unable to handle request");
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
