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


public class RegisterActivity extends ActionBarActivity {

    @ViewInject(R.id.register_edit_gonghao)
    private EditText register_edit_gonghao;
    @ViewInject(R.id.register_edit_uname)
    private EditText register_edit_uname;
    @ViewInject(R.id.register_edit_uphone)
    private EditText register_edit_uphone;
    @ViewInject(R.id.register_edit_pwd)
    private EditText register_edit_pwd;
    @ViewInject(R.id.register_edit_pwd_sure)
    private EditText register_edit_pwd_sure;
    private String name, tel, gonghao, truepass, firpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ViewUtils.inject(this);
    }

    @OnClick(R.id.register)
    public void RegClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                regHttp();
                break;
        }
    }

    private void regHttp() {
        final String url = Constant.path + "oasys/userinfoAction!zhuce.action";
        name = register_edit_uname.getText().toString();
        tel = register_edit_uphone.getText().toString();
        gonghao = register_edit_gonghao.getText().toString();
        firpass = register_edit_pwd.getText().toString();
        truepass = register_edit_pwd_sure.getText().toString();
        if (StringUtils.isEmpty(gonghao)) {
            Toast.makeText(RegisterActivity.this, "工号不能为空！", Toast.LENGTH_LONG).show();
        } else if (StringUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "姓名不能为空！", Toast.LENGTH_LONG).show();
        } else if (StringUtils.isEmpty(tel)) {
            Toast.makeText(RegisterActivity.this, "手机号不能为空！", Toast.LENGTH_LONG).show();
        } else if (StringUtils.isEmpty(firpass)) {
            Toast.makeText(RegisterActivity.this, "密码不能为空！", Toast.LENGTH_LONG).show();
        } else if (StringUtils.isEmpty(truepass)) {
            Toast.makeText(RegisterActivity.this, "输入确认密码！", Toast.LENGTH_LONG).show();
        } else if (!truepass.equals(firpass)) {
            Toast.makeText(RegisterActivity.this, "请确保两次密码相同！", Toast.LENGTH_LONG).show();
        } else {
            RequestParams params = new RequestParams();
            params.addBodyParameter("name", name);
            params.addBodyParameter("tel", tel);
            params.addBodyParameter("gonghao", gonghao);
            params.addBodyParameter("pass", truepass);
            HttpUtils http = new HttpUtils();
            http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<Object>() {
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
                            Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_LONG).show();
                            userJson = jsonObject.getJSONObject("obj");
                            String uID = userJson.optString("id");
                            String uName = userJson.optString("name");
                            String uPhone = userJson.optString("tel");
                            String uGongHao = userJson.optString("attr5");
                            User user = new User(uID,uName,uPhone,uGongHao,truepass,"1");
                            Log.d("jack", user.toString());
                            SharedPreferenceStorage.saveLoginUserInfo(
                                    RegisterActivity.this, user);
                            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "注册失败！", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(RegisterActivity.this, "链接失败！", Toast.LENGTH_LONG).show();
                }
            });

        }

    }
}
