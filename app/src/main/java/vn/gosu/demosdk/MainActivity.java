package vn.gosu.demosdk;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.appsflyer.AppsFlyerLib;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gosu.sdk.DialogLoginID.OnLoginListener;
import com.gosu.sdk.DialogLoginID.OnLogoutListener;
import com.gosu.sdk.Gosu;
import com.gosu.sdk.utils.GosuSDKConstant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import java.util.Calendar;


import org.json.JSONObject;

public class MainActivity extends Activity {
    private Gosu mGosu = null;
    private ScrollView layoutMain = null;
    private RelativeLayout layoutIAP = null;
    private Button btnFloating = null;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init GOSU SDK
        Gosu.getSharedInstance().initialize(this, "m319.LqmSIpuS5UqkbUPc");
        mGosu = Gosu.getSharedInstance();

        getDeviceToken();

        // init for activity
        final TextView tv_UID = (TextView) this.findViewById(R.id.txt_uID);
        tv_UID.setText("UserName: ");
        layoutMain = (ScrollView) findViewById(R.id.scrollView);
        layoutMain.setVisibility(View.GONE);

        layoutIAP = (RelativeLayout) findViewById(R.id.layout_iap);
        layoutIAP.setVisibility(View.GONE);

        // for login SDK
        final Button btnVaoGame = (Button) findViewById(R.id.btnVaoGame);
        btnVaoGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mGosu.showLogin(new OnLoginListener() {

                    @Override
                    public void onLoginSuccessful(String UserId, String UserName, String accesstoken) {
                        btnVaoGame.setVisibility(View.GONE);
                        layoutMain.setVisibility(View.VISIBLE);
                        tv_UID.setText("UseName: " + UserName);
                    }

                    @Override
                    public void onLoginFailed() {
                    }
                });
            }
        });

        //for Tracking Character
        findViewById(R.id.btn_gosutracking).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String serverID = "123";
                String roleID = "909484455";
                String roleName = "BigSDK";
                mGosu.gosuTrackingAppOpen(serverID, roleID, roleName);
            }
        });

        // for payment IAP
        findViewById(R.id.btn_payment_iap).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layoutIAP.setVisibility(View.VISIBLE);
            }
        });

        // for appsflyer tracking custom
        findViewById(R.id.btn_appsflyer_tracking_custom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    JSONObject jsonContent = new JSONObject();
                    jsonContent.put("event", "name_event");

                    JSONObject jsonRole = new JSONObject();

                    jsonRole.put("name", "roleName");
                    jsonRole.put("server", "ServerID");

                    jsonContent.put("params", jsonRole);

                    Log.d("LOG_JSON", jsonContent.toString());
                    mGosu.trackingStartTrialEventCustomAF(MainActivity.this, jsonContent.toString());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // for share image
        findViewById(R.id.btn_facebook_share).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        // for share link
        findViewById(R.id.btn_share_link).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mGosu.shareLinkToFacebook("", new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });
            }
        });



        // for logout
        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mGosu.logOut(new OnLogoutListener() {
                    public void onLogoutSuccessful() {
                        btnVaoGame.setVisibility(View.VISIBLE);
                        layoutMain.setVisibility(View.GONE);
                    }

                    public void onLogoutFailed() {
                    }
                });
            }
        });

        //////////// FOR IAP /////////////
        findViewById(R.id.btn_cancel_iap).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layoutIAP.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.btn_pk1).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                layoutIAP.setVisibility(View.GONE);
                callIAP(1);
            }
        });

        findViewById(R.id.btn_pk2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                layoutIAP.setVisibility(View.GONE);
                callIAP(2);
            }
        });

        findViewById(R.id.btn_pk3).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                layoutIAP.setVisibility(View.GONE);
                callIAP(3);
            }
        });
    }

    private void getDeviceToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("SDK_TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log
                        Log.d("SDK_TAG", "TOKEN FIREBASE:"+token);
                    }
                });
    }

    ///////////// CAL IAP ////////////////
    /**
     *
     * sku insert to com.phlmobile.thanlong.properties like above, GOSU SDK will read file and
     * then split via , and return a vector
     *
     * @param type
     */
    private void callIAP(int type) {
        try {
            String orderid = String.valueOf(Calendar.getInstance().getTimeInMillis()) + "_10095276_2_13_11";
            String orderInfo = "";
            String amount = "0";//VNĐ
            String sku = "";
            switch (type) {
                case 1:
                    sku = GosuSDKConstant.iap_product_ids.get(0);// com.com.phlmobile.thanlong.sdkdemo.p4
                    amount = "22000";
                    orderInfo = "Get 99 KNB";
                    break;
                case 2:
                    sku = GosuSDKConstant.iap_product_ids.get(1);// com.com.phlmobile.thanlong.sdkdemo.p5
                    amount = "44000";
                    orderInfo = "Get 199 KNB";
                    break;
                case 3:
                    sku = GosuSDKConstant.iap_product_ids.get(2);// com.com.phlmobile.thanlong.sdkdemo.p6
                    amount = "66000";
                    orderInfo = "Get 299 KNB";
                    break;
            }
            //Log.e("SKU==", sku);

            orderInfo = URLEncoder.encode(orderInfo, "UTF-8");
            String extraInfo = "YourContent";
            String character = "YourCharacter";

            mGosu.payment_iap(this, GosuSDKConstant.username, sku, "s1", orderid, orderInfo, amount, character, extraInfo,
                    new Gosu.OnPaymentIAPListener(){
                        public void onPaymentBegin() {
                            // show iap loading
                            // ...
                            mGosu.showLoadingDialog(MainActivity.this, true);
                        }

                        public void onPaymentSuccessful(String msg) {
                            // stop iap loading
                            // ....
                            mGosu.showLoadingDialog(MainActivity.this, false);
                            mGosu.showConfirmDialog("Payment Success!", msg);
                        }

                        public void onPaymentFailed(String msg, int errCode, String iapToken) {
                            // stop iap loading
                            // ....
                            mGosu.showLoadingDialog(MainActivity.this, false);
                            mGosu.showConfirmDialog("Payment Fail!", msg + " (error_code: " + errCode + ")");
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mGosu.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }

}
