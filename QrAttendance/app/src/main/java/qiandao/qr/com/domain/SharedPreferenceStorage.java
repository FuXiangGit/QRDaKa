package qiandao.qr.com.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by rc on 2015/2/2.
 */
public class SharedPreferenceStorage {
    /*
     * 存储用户名user_id
	 */
    public static void saveLoginUserInfo(Context context, User user) {
        SharedPreferences sp = context.getSharedPreferences("QRUser",
                context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putString("user_id", user.getId());
        et.putString("username", user.getUsername());
        et.putString("myphone", user.getMyphone());
        et.putString("gonghao", user.getGonghao());
        et.putString("userpassword", user.getUserpassword());
        et.putString("islogin", user.getUserpassword());
        et.commit();
    }

    /*
	 * 取用户名user_id
	 */
    public static User getLoginUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences("QRUser",
                context.MODE_PRIVATE);
        if (sp.getString("username", null) != null) {
            Log.d("way", sp.getString("myphone", null) + "电话");
            return new User(sp.getString("user_id", null), sp.getString(
                    "username", null), sp.getString("myphone", null),
                    sp.getString("gonghao", null),
                    sp.getString("userpassword", null), sp.getString("islogin", null));
        } else {
            return new User("0");
        }
        // return null;
    }
}
