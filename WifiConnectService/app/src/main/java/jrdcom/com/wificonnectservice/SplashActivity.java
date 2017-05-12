package jrdcom.com.wificonnectservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by longcheng on 2017/5/12.
 * 显示当前的IP并添加进入按钮
 */

public class SplashActivity extends AppCompatActivity {
    private Button mButton;
    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private void initView(){
        mButton = (Button)findViewById(R.id.btn_go);
        mTextView = (TextView)findViewById(R.id.text);

        String ip = getIp();
        if(ip != null){
            mTextView.setText(ip);
        }else{
            mTextView.setText(getResources().getText(""R.string.no_ip));
        }

        mButton.setOnClickListener(onClickListener);
    }

    private String getIp(){
        String ipString = null;
        int type = NetWorkUtil.getNetworkType(this);

        switch (type){
            case NetWorkUtil.NETWORK_ERROR:
                Toast.makeText(this,"network error", Toast.LENGTH_LONG).show();
                break;
            case NetWorkUtil.WIFI_CONNECT:
                ipString = NetWorkUtil.getlocalip(this);
                break;
            case NetWorkUtil.GPRS_CONNECT:
                ipString = NetWorkUtil.getLocalIpAddress();
                break;
        }

        return ipString;
    }

    /*进入主页面*/

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_go:
                    //进入Main
                    MainActivity.startThis(SplashActivity.this);
                    break;
            }
        }
    };
}
