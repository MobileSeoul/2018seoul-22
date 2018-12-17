package com.example.hyeminj.syolo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class LoginActivity extends AppCompatActivity {
    private ImageButton btn_kakao_login;
    private ImageButton btn_face_login;
    private ImageButton btn_naver_login;
    private CallbackManager mCallbackManager;
    private LoginCallback mLoginCallback;

    private SessionCallback callback;

    private Context context;

    private TextView naverlogout;

    //naver
    public static OAuthLogin mOAuthLoginModule;
    private URL url_face;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    public CallbackManager callbackmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getHas();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        context = this;
        mCallbackManager = CallbackManager.Factory.create();
        mLoginCallback = new LoginCallback();
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        btn_kakao_login = (ImageButton)findViewById(R.id.btn_kakao_login);
        btn_kakao_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session session = Session.getCurrentSession();
                callback = new SessionCallback();
                session.addCallback((ISessionCallback) callback);
                session.open(AuthType.KAKAO_ACCOUNT, LoginActivity.this);

            }
        });
        btn_face_login = (ImageButton)findViewById(R.id.btn_facebook_login);
        btn_face_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FacebookSdk.sdkInitialize(getApplicationContext());
                callbackmanager = CallbackManager.Factory.create();
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
                LoginManager.getInstance().registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        // App code
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        if(response.getError() != null){

                                        }else{
                                            Log.d("TAG","user: "+ object.toString());
                                            Log.d("TAG","AccessToken"+loginResult.getAccessToken().getToken());
                                            Profile profile = Profile.getCurrentProfile();
                                            final String link = profile.getProfilePictureUri(200, 200).toString();
                                            setResult(RESULT_OK);
                                            SharedPreferences pref= getSharedPreferences("pref", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = pref.edit();

                                            try {
                                                url_face = new URL(link);
//                                                InputStream is =url_face.openStream();

                                                //                          bitmap = BitmapFactory.decodeStream(is);
                                                editor.putString("login","facebook");
                                                editor.putString("name", object.get("name").toString());
                                                editor.putString("id", object.get("id").toString());
                                                editor.putString("url", String.valueOf(url_face));
                                                //editor.putString("email",response.getJSONObject().getString("email").toString());

                                                final String id = pref.getString("id", object.get("id").toString());
                                                final String name = pref.getString("name",object.get("name").toString());
                                                final boolean[] check = new boolean[1];
                                                check[0]=false;

                                                final DatabaseReference member = databaseReference.child("member").child("facebook");
                                                member.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                            if (snapshot.getChildren()!=null &&snapshot.child("id").getValue(String.class).equals(id)) {
                                                                check[0] = true;
                                                            }

                                                        }
                                                        if (check[0] != true) {
                                                            databaseReference.child("member").child("facebook").child(id).child("id").setValue(id); //
                                                            databaseReference.child("member").child("facebook").child(id).child("name").setValue(name);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            //     editor.putString("email", email);
                                            editor.commit();
                                            Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                        // Application code
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                        //**// 성공시 반환되는 값을 웹뷰로....**
                    }

                    @Override
                    public void onCancel() {
                        // not called
                        Log.d("fb_login_sdk", "callback cancel");
                    }

                    @Override
                    public void onError(FacebookException e) {
                        // not called
                        Log.d("fb_login_sdk", "callback onError");
                    }
                });
            }

        });

        setNaver();
    }
    private void getHas() {
        try {
            PackageInfo Info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : Info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
   private void redirectLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
       /* final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        this.finish();
*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            redirectSignupActivity();  // 세션 연결성공 시 redirectSignupActivity() 호출
        }
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
            redirectLoginActivity();
            //setContentView(R.layout.activity_login); // 세션 연결이 실패했을때
        }                                            // 로그인화면을 다시 불러옴
    }

    protected void redirectSignupActivity() {       //세션 연결 성공 시 SignupActivity로 넘김
        final Intent intent = new Intent(this, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void setNaver() {
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(this, "gACXXnwBfyo7uc2UomKt", "ywYHcu7NVP", "SYolo");



        btn_naver_login=findViewById(R.id.btn_naver_login);
        btn_naver_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mOAuthLoginModule.startOauthLoginActivity(LoginActivity.this,mOAuthLoginHandler);
            }
        });
//        mOAuthLoginModule.startOauthLoginActivity(this, mOAuthLoginHandler);
    }

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginModule.getAccessToken(context);
                String refreshToken = mOAuthLoginModule.getRefreshToken(context);
                long expiresAt = mOAuthLoginModule.getExpiresAt(context);
                String tokenType = mOAuthLoginModule.getTokenType(context);
                Rerequest re = new Rerequest();
                re.execute(accessToken);
                ProfileTask task = new ProfileTask();
                task.execute(accessToken);
            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(context).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(context);
            }
        };
    };

    class Rerequest extends AsyncTask<String, Void, String> { //거부한 요청 다시 받기
        String result;

        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];// 네이버 로그인 접근 토큰;
            String header = "Bearer " + token; // Bearer 다음에 공백 추가
            try {
                String apiURL = "https://nid.naver.com/oauth2.0/authorize";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", header);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                result = response.toString();
                br.close();
                System.out.println(response.toString());
            } catch (Exception e) {
                System.out.println(e);
            }
            //result 값은 JSONObject 형태로 넘어옵니다.
            return result;
        }
    }

    class ProfileTask extends AsyncTask<String, Void, String> {
        String result;
        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];// 네이버 로그인 접근 토큰;
            String header = "Bearer " + token; // Bearer 다음에 공백 추가
            try {
                String apiURL = "https://openapi.naver.com/v1/nid/me";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", header);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                result = response.toString();
                br.close();
                System.out.println(response.toString());
            } catch (Exception e) {
                System.out.println(e);
            }
            //result 값은 JSONObject 형태로 넘어옵니다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //넘어온 result 값을 JSONObject 로 변환해주고, 값을 가져오면 되는데요.
                // result 를 Log에 찍어보면 어떻게 가져와야할 지 감이 오실거에요.
                JSONObject object = new JSONObject(result);
                if(object.getString("resultcode").equals("00")) {
                    JSONObject jsonObject = new JSONObject(object.getString("response"));
                    Log.d("jsonObject", jsonObject.toString());

                    final SharedPreferences pref= getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("email", jsonObject.getString("email"));
                    editor.putString("id", jsonObject.getString("email").split("@")[0]);
                    editor.putString("name", jsonObject.getString("name"));
                    editor.putString("url", jsonObject.getString("profile_image"));
                    editor.putString("login", "naver");
                    editor.putString("context", String.valueOf(context));
                    editor.commit();

                    final String id = pref.getString("id","");
                    final String name = pref.getString("name","");
                    final boolean[] check = new boolean[1];
                    check[0]=false;

                    final DatabaseReference member = databaseReference.child("member").child("naver");
                    member.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.child("id").getValue(String.class).equals(id)) {
                                    check[0] = true;
                                }
                            }
                            if (check[0] != true) {
                                databaseReference.child("member").child("naver").child(id).child("id").setValue(id); //
                                databaseReference.child("member").child("naver").child(id).child("name").setValue(name);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}