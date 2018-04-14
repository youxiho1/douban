package com.example.yang.douban;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register2Activity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton ib_back;
    private EditText et_code;
    private CountDownTimerButton btn_code;
    private Button btn_finish;
    private ImageView iv_code;
    //private CheckBox checkBox;
    String user = null, password = null, nickname = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        btn_code = (CountDownTimerButton) findViewById(R.id.btn_code);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        ib_back = (ImageButton) findViewById(R.id.ib_register_back);
        iv_code = (ImageView) findViewById(R.id.iv_delcode);
        et_code = (EditText) findViewById(R.id.et_code);
        et_code.setOnFocusChangeListener(new userOnFocusChanageListener());
        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        password = intent.getStringExtra("password");
        nickname = intent.getStringExtra("nickname");
        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str1 =  et_code.getText().toString();
                if(str1.length()>0){
                    iv_code.setVisibility(View.VISIBLE);
                } else {
                    iv_code.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ib_back) {
            finish();
        }
        else if(v.getId() == R.id.btn_code) {
            sendCode();
        }
        else if(v.getId() == R.id. btn_finish) {
            String code = null;
            code = et_code.getText().toString();
            if(code == null || code.length() <= 0) {
                showToast("您未填入验证码");
                return;
            }
            FlowerHttp flowerHttp = new FlowerHttp("http://118.25.40.220/api/registe/");
            Map<String, Object> map = new HashMap<>();
            map.put("type", "email");
            map.put("text", user);
            map.put("username", nickname);
            map.put("code", code);
            map.put("pwd", password);
            String response = flowerHttp.firstPost(map);
            int result = -10;
            try {
                result = new JSONObject(response).getInt("rsNum");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(result == 0) {
                showToast("未知错误");
                return;
            }
            else if(result == -2) {
                showToast("邮箱验证码错误");
                return;
            }
            else if(result == -1) {
                showToast("该邮箱已被注册");
                return;
            }
            else if(result == -10){
                showToast("服务器未响应");
                return;
            }
            else {
                showToast("注册成功，请登录");
                Intent intent1 = new Intent(Register2Activity.this, LoginActivity.class);
                startActivity(intent1);
            }
        }
    }
    private class userOnFocusChanageListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if ((v.getId() == et_code.getId()) & (et_code.getText().length() > 0)) {
                iv_code.setVisibility(View.VISIBLE);
            } else iv_code.setVisibility(View.INVISIBLE);
        }
    }
    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void sendCode() {
        FlowerHttp flowerHttp = new FlowerHttp("http://118.25.40.220/api/getCode/");
        Map<String, Object> map = new HashMap<>();
        map.put("type", "email");
        map.put("getType", "registe");
        map.put("text", user);
        String response = flowerHttp.firstPost(map);
        int result = -10;
        try {
            result = new JSONObject(response).getInt("rsNum");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(result == 0) {
            showToast("未知错误");
            return;
        }
        else if(result == 1) {
            showToast("已向邮箱发送验证码");
            return;
        }
        else if(result == -1) {
            showToast("邮箱已注册");
            return;
        }
        else if(result == -10){
            showToast("服务器未响应");
            return;
        }
    }
}
