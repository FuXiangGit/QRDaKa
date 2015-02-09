package qiandao.qr.com.qrattendance;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import qiandao.qr.com.domain.SharedPreferenceStorage;
import qiandao.qr.com.domain.User;
import qiandao.qr.com.tools.Constant;


public class LoginActivity extends ActionBarActivity {

    @ViewInject(R.id.edit_user)
    EditText username;
    @ViewInject(R.id.edit_pwd)
    EditText userpsw;

    String loginName,loginPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
    }

    @OnClick({R.id.login,R.id.txt_register})
    public void LoginClick(View v){
        switch (v.getId()){
            case R.id.login:
                loginHttp();
                break;
            case R.id.txt_register:
                Intent regintent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(regintent);
        }
    }

    private void loginHttp() {
        final String url = Constant.path + "oasys/userinfoAction!userLogin.action";
        loginName = username.getText().toString();
        loginPass = userpsw.getText().toString();
        if (StringUtils.isEmpty(loginName)) {
            Toast.makeText(LoginActivity.this, "手机号码不能为空！", Toast.LENGTH_LONG).show();
        } else if (StringUtils.isEmpty(loginPass)) {
            Toast.makeText(LoginActivity.this, "不能为空！", Toast.LENGTH_LONG).show();
        }else{
            RequestParams params = new RequestParams();
            params.addBodyParameter("tel", loginName);
            params.addBodyParameter("pass", loginPass);
            HttpUtils http = new HttpUtils();
            http.send(HttpRequest.HttpMethod.POST, url, params,new RequestCallBack<Object>() {
                @Override
                public void onStart() {
                    Log.d("jack", "开始上传"+url);
                }
                @Override
                public void onSuccess(ResponseInfo<Object> objectResponseInfo) {
                    Log.d("jack", "上传成功");
                    String res = objectResponseInfo.result.toString();
                    Log.d("jack", res);
                    JSONObject jsonObject = null;
                    JSONObject userJson = null;
                    try {
                        jsonObject = new JSONObject(res);
                        int isSuccess = jsonObject.optInt("retcode");
                        if (isSuccess == 1) {
                            Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_LONG).show();
                            userJson = jsonObject.getJSONObject("obj");
                            String uID = userJson.optString("id");
                            String uName = userJson.optString("name");
                            String uPhone = userJson.optString("tel");
                            String uGongHao = userJson.optString("attr5");
                            User user = new User(uID,uName,uPhone,uGongHao,loginPass,"1");
                            Log.d("jack", user.toString());
                            try {
                                SharedPreferenceStorage.saveLoginUserInfo(
                                        LoginActivity.this, user);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "登陆失败！", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(LoginActivity.this, "链接失败！", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
