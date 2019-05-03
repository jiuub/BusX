package com.xaau.bs.busx;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xaau.bs.busx.util.HttpUtils;
import com.xaau.bs.busx.util.SharedPreferenceUtil;

import java.util.HashMap;
import java.util.Map;

public class AccountCenterActivity extends AppCompatActivity {
    private EditText mOldPwd, mNewPwd, mRePwd;
    private CheckPassword checkPassword=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_center);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account_center, menu);
        MenuItem signOutMenuItem=menu.findItem(R.id.action_signOut);
        signOutMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AccountCenterActivity.this);
                builder.setTitle(SharedPreferenceUtil.getEmail(AccountCenterActivity.this))
                        .setMessage("确定注销登录?")
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferenceUtil.saveUserMessage(AccountCenterActivity.this,"",false);
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void init(){
        TextView mAccount=(TextView)findViewById(R.id.text_account);
        mAccount.setText(SharedPreferenceUtil.getEmail(AccountCenterActivity.this));

        mOldPwd=(EditText)findViewById(R.id.edit_old_password) ;

        mNewPwd=(EditText)findViewById(R.id.edit_new_password) ;

        mRePwd=(EditText)findViewById(R.id.edit_re_password) ;

        Button mChangePwd=(Button)findViewById(R.id.btn_change_password);
        mChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard(v);
                changePassword();
            }
        });
    }

    private void changePassword(){
        if (checkPassword!=null){
            return;
        }
        mOldPwd.setError(null);
        mNewPwd.setError(null);
        mRePwd.setError(null);
        String oldPassword=mOldPwd.getText().toString();
        String newPassword=mNewPwd.getText().toString();
        String rePassword=mRePwd.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (rePassword.isEmpty()){
            mRePwd.setError(getString(R.string.error_field_required));
            focusView=mRePwd;
            cancel=true;
        }else if (!TextUtils.isEmpty(rePassword)
                && ! isPasswordValid(rePassword)){
            mRePwd.setError(getString(R.string.error_invalid_password));
            focusView=mRePwd;
            cancel=true;
        }else if (!TextUtils.isEmpty(rePassword)
                && isPasswordValid(rePassword)
                && !isRe_passwordValid(newPassword,rePassword )){
            mRePwd.setError(getString(R.string.error_invalid_re_password));
            focusView=mRePwd;
            cancel=true;
        }

        if (newPassword.isEmpty()){
            mNewPwd.setError(getString(R.string.error_field_required));
            focusView=mNewPwd;
            cancel=true;
        }else if (!TextUtils.isEmpty(newPassword) && !isPasswordValid(newPassword)) {
            mNewPwd.setError(getString(R.string.error_invalid_password));
            focusView = mNewPwd;
            cancel = true;
        }else if (!TextUtils.isEmpty(newPassword) && isRe_passwordValid(oldPassword,newPassword)){
            mNewPwd.setError(getString(R.string.error_invalid_new_password));
            focusView = mNewPwd;
            cancel = true;
        }

        if (oldPassword.isEmpty()){
            mOldPwd.setError(getString(R.string.error_field_required));
            focusView=mOldPwd;
            cancel=true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            checkPassword = new CheckPassword(SharedPreferenceUtil.getEmail(AccountCenterActivity.this),oldPassword,newPassword);
            checkPassword.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    private boolean isRe_passwordValid(String password, String re_password){
        return re_password.equals(password);
    }

    private void showToast(final String toast){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AccountCenterActivity.this,toast,Toast.LENGTH_LONG).show();
            }
        });
    }

    //收起键盘
    private void hideKeyBoard(View view){
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(view,InputMethodManager.SHOW_FORCED);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    private class CheckPassword extends AsyncTask<Void,Void,Boolean>{

        private final String mEmail;
        private final String mPassword;
        private final String mNewPassword;

        private CheckPassword(String mEmail, String mPassword, String mNewPassword) {
            this.mEmail = mEmail;
            this.mPassword = mPassword;
            this.mNewPassword=mNewPassword;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Map<String,String> loginParams=new HashMap<String, String>();
            loginParams.put("username",mEmail);
            loginParams.put("password",mPassword);
            loginParams.put("sign","change");
            loginParams.put("npassword",mNewPassword);
            String s= HttpUtils.sendPostMessage(loginParams);
            Log.e("this",s);
            // Simulate network access.
            //Thread.sleep(2000);
            if (s == null || s.isEmpty()) {
                showToast("连接服务器失败");
            }else if (s.equals("change_password_failed")){
                showToast("密码不正确");
                initError();
            }else {return s.equals("change_password_success");}
            return false;
        }

        private void initError(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mOldPwd.setText(null);
                    mNewPwd.setText(null);
                    mRePwd.setText(null);
                }
            });
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            checkPassword=null;
            super.onPostExecute(aBoolean);
            if (aBoolean){
                showToast("密码修改成功");
                SharedPreferenceUtil.saveUserMessage(AccountCenterActivity.this,"",false);
                finish();
            }
        }
    }
}