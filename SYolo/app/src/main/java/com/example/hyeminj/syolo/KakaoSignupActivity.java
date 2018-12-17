package com.example.hyeminj.syolo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

public class KakaoSignupActivity extends Activity{

    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        requestMe();
    }
    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void requestMe() { //유저의 정보를 받아오는 함수
        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }
            @Override
            public void onNotSignedUp() {} // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                final String kakaoID = String.valueOf(userProfile.getId()); // userProfile에서 ID값을 가져옴
                final String kakaoName = userProfile.getNickname();     // Nickname 값을 가져옴
                String url = String.valueOf(userProfile.getProfileImagePath());
                String kakaoEmail = String.valueOf(userProfile.getEmail());

                Logger.d("UserProfile : " + userProfile);
                Log.d("kakao", "==========================");
                Log.d("kakao", ""+userProfile);
                Log.d("kakao", kakaoID);
                Log.d("kakao", kakaoName);
                Log.d("kakao", kakaoEmail);
                Log.d("kakao", "==========================");

                final boolean[] check = new boolean[1];
                check[0]=false;
                final DatabaseReference member = databaseReference.child("member").child("kakao");

                member.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d(snapshot.child("id").getValue(String.class),kakaoID);
                            if (snapshot.child("id").getValue(String.class).equals(kakaoID)) {
                                check[0] = true;
                            }
                        }
                        if (check[0] != true) {
                            databaseReference.child("member").child("kakao").child(kakaoID).child("id").setValue(kakaoID);
                            databaseReference.child("member").child("kakao").child(kakaoID).child("name").setValue(kakaoName);//setValue를 닉네임으로
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                redirectMainActivity(url, kakaoName, kakaoEmail, kakaoID); // 로그인 성공시 MainActivity로

            }
        });
    }

    private void redirectMainActivity(String url, String name, String email, String id) {
        Intent intent = new Intent(KakaoSignupActivity.this, MainActivity.class);
        //intent.putExtra("url", url);
        //intent.putExtra("nickname", nickname);
        SharedPreferences pref= getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("url",url);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("login", "kakao");
        editor.putString("id", id);
        editor.commit();

        startActivity(intent);
        finish();
    }

    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}