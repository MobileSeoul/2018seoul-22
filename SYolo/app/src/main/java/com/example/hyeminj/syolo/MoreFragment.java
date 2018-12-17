package com.example.hyeminj.syolo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.example.hyeminj.syolo.LoginActivity.mOAuthLoginModule;

public class MoreFragment extends Fragment implements View.OnClickListener {
    SharedPreferences pref;
    String my_img, nickname, email;

    TextView profile_nm, profile_login;
    ImageView login_chk;
    CircleImageView profile_img;
    Bitmap bitmap;
    String login;
    Context ncontext;
    ImageButton seoul_map;
    LinearLayout myReview,logout,QnA,FaQ;


    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_more, container, false);

        ncontext = getActivity();
        pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        my_img=pref.getString("url","");
        nickname=pref.getString("name","");
        email=pref.getString("email","");
        login=pref.getString("login","");
        seoul_map = rootView.findViewById(R.id.img_seoul);
        profile_img = (CircleImageView)rootView.findViewById(R.id.profile_img);
        profile_nm = (TextView)rootView.findViewById(R.id.profile_nm);
        login_chk = (ImageView)rootView.findViewById(R.id.profile_login);
        logout = rootView.findViewById(R.id.logout);
        myReview =  rootView.findViewById(R.id.myReview);
        QnA =  rootView.findViewById(R.id.QnA);
        FaQ = rootView.findViewById(R.id.FaQ);
        seoul_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),seoul_chk.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(this);
        myReview.setOnClickListener(this);
        QnA.setOnClickListener(this);
        FaQ.setOnClickListener(this);
        profile_nm.setText(nickname);
        switch(login){
            case "kakao":
                 login_chk.setImageResource(R.drawable.kakao_chk);
                 break;
            case "facebook":
                login_chk.setImageResource(R.drawable.facebook_chk);
                break;
            case "naver":
                login_chk.setImageResource(R.drawable.naver_chk);
                break;
        }

        Thread mThread = new Thread(){
            public void run(){
                try {
                    URL url = new URL(my_img);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
        try {
            mThread.join();
            profile_img.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void onClickLogout() {
        if(login.equals("kakao")) {
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();
                    redirectLoginActivity();
                }
            });
        }
        else if(login.equals("naver")){
            boolean isSuccessDeleteToken = mOAuthLoginModule.logoutAndDeleteToken(ncontext);
            if (!isSuccessDeleteToken) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                Log.d("errorCode", "errorCode:" + mOAuthLoginModule.getLastErrorCode(ncontext));
                Log.d("errorCode", "errorDesc:" + mOAuthLoginModule.getLastErrorDesc(ncontext));
            }
            pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            redirectLoginActivity();
        }else if(login.equals("facebook")){

                  /*  pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.commit();*/
            LoginManager.getInstance().logOut();

            redirectLoginActivity();


        }
    }
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.myReview :
                i = new Intent(this.getActivity(), MyReview.class);
                startActivity(i);
                break;
            case R.id.QnA :
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                // email setting 배열로 해놔서 복수 발송 가능
                String[] address = {"wlalsdns@gmail.com","201602084@cs-cnu.org","201602090@cs-cnu.org"};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                email.putExtra(Intent.EXTRA_SUBJECT,"[SYOLO 문의]");
                startActivity(email);
                break;
            case R.id.FaQ:
                i = new Intent(this.getActivity(),FAQ.class);
                startActivity(i);
                break;
            case R.id.logout:
                onClickLogout();
                break;
            default:
                break;
        }
    }

    protected void redirectLoginActivity() {
        final Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        getActivity().finish();
    }

}