package jrdcom.com.wificonnectclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by longcheng on 2017/5/12.
 */

public class SplashActivity extends AppCompatActivity {
    private Button btn_Gointo;
    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_spalsh);
        initView();
    }

    private void initView(){
        //findViewId
        btn_Gointo = (Button)findViewById(R.id.btn_go);
        editText = (EditText)findViewById(R.id.edit_text);

        //Add Listener
        btn_Gointo.setOnClickListener(onClickListener);
    }

    public boolean isIP(String addr)
    {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();
        //============对之前的ip判断的bug在进行判断
        if (ipAddress==true){
            String ips[] = addr.split("\\.");
            if(ips.length==4){
                try{
                    for(String ip : ips){
                        if(Integer.parseInt(ip)<0||Integer.parseInt(ip)>255){
                            return false;
                        }
                    }
                }catch (Exception e){
                    return false;
                }
                return true;
            }else{
                return false;
            }
        }
        return ipAddress;
    }



    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_go:
                    if(false == isIP(editText.getText().toString())){
                        Toast.makeText(SplashActivity.this ,"Your Ip is invalid, your can check it from your server",Toast.LENGTH_LONG).show();
                    }else if(NetWorkUtil.NETWORK_ERROR == NetWorkUtil.getNetworkType(SplashActivity.this)){
                        Toast.makeText(SplashActivity.this ,"Your network is error, pls connect same wifi with service",Toast.LENGTH_LONG).show();
                    }else{
                        /*Send the ipString to MainActivity*/
                        Intent intent = new Intent(SplashActivity.this, ContactActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("ip", editText.getText().toString());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        //MainActivity.startThis(SplashActivity.this);
                    }
                    break;
            }
        }
    };
}
