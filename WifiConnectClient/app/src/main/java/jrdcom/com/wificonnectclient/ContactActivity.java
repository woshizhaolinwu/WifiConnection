package jrdcom.com.wificonnectclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by longcheng on 2017/5/13.
 */

public class ContactActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private Button mSendBtn;
    private EditText mEditText;
    private List<ChartModel> list;
    public static ServerSocket serverSocket = null;
    private ContactAdapter contactAdapter;
    String buffer = "";
    private Socket socket;
    String ipString;
    private MyThread myThread;
    private Handler myThreadHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        initView();
    }

    private void initView(){
        //获取Ip
        //获取IP
        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();
        ipString = bundle.getString("ip");
        //findView
        findViewId();
        initRecycler();
        addListener();

        //开启线程
        myThread = new MyThread("test");
        myThread.start();
    }


    private void findViewId(){
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mSendBtn = (Button)findViewById(R.id.send_btn);
        mEditText = (EditText)findViewById(R.id.edit_text);
    }

    private void initList(){
        //add fake data
        list = new ArrayList<>();
        ChartModel chartModel = new ChartModel("Hello",Common.CHART_SEND);
        list.add(chartModel);
        ChartModel chartModel1 = new ChartModel("hello ni hao", Common.CHART_RECEIVER);
        list.add(chartModel1);
    }
    private void initRecycler(){
        initList();
        //set Adapter
        contactAdapter = new ContactAdapter(this, list);
        mRecyclerView.setAdapter(contactAdapter);
        //set Lieanlayout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void addListener(){
        mSendBtn.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.send_btn:
                    String send = mEditText.getText().toString();
                    if(send.length() == 0){

                    }else{
                        //发送数据给客户端
                        Message msg = new Message();
                        msg.what = 0x12;
                        Bundle bundle = new Bundle();
                        bundle.putString("send_string",send);
                        myThreadHandler.sendMessage(msg);
                    }

                    break;
            }
        }
    };

    private void receiveSocketMessage(){
        new Thread() {
            public void run() {
            Looper.prepare();
            Bundle bundle = new Bundle();
            bundle.clear();
            OutputStream output;
            String str = "hello hehe";
            try {
                serverSocket = new ServerSocket(30000);
                while (true) {
                    Message msg = new Message();
                    msg.what = 0x11;
                    try {
                        Socket socket = serverSocket.accept();
                        output = socket.getOutputStream();
                        output.write(str.getBytes("gbk"));
                        output.flush();
                        socket.shutdownOutput();
                        BufferedReader bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String line = null;
                        buffer = "";
                        while ((line = bff.readLine())!=null) {
                            buffer = line + buffer;
                        }
                        bundle.putString("msg", buffer.toString());
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                        bff.close();
                        output.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            Looper.loop();
        };
    }.start();
    }


    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        if (msg.what==0x11) { //receive msg
            Bundle bundle = msg.getData();
            //在这里构建
            String message = bundle.getString("msg");
            ChartModel chartModel = new ChartModel(message, Common.CHART_RECEIVER);

        }
    };
    };

    private void updateList(ChartModel chartModel){
        list.add(chartModel);
        contactAdapter.addItem(chartModel);
    }


    /*这个线程是专门负责发送数据的*/
    class MyThread extends Thread {
        public String txt1;
        public MyThread(String str) {
            txt1 = str;
        }

        @Override
        public void run() {
            //定义消息
            Looper.prepare();
            myThreadHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case 0x12:
                            Bundle bundleGet = msg.getData();  //获取text信息
                            String sendString = bundleGet.getString("send_string");

                            Message message = new Message();
                            msg.what = 0x11;
                            Bundle bundle = new Bundle();
                            bundle.clear();
                            String result = sendMsgToService(sendString);  //通过socket发送
                            bundle.putString("msg",result);
                            msg.setData(bundle);
                            myHandler.sendMessage(message); //发送给UI线程
                            break;
                    }

                }
            };
            Looper.loop();
        }
    }


    //主线程的handler
    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x11) { //更新UI数据
                Bundle bundle = msg.getData();
                //txt1.append("server:"+bundle.getString("msg")+"\n");
            }
        }

    };


    private String sendMsgToService(String txt){
        String result= null;
        try {
            //连接服务器 并设置连接超时为1秒
            socket = new Socket();
            socket.connect(new InetSocketAddress(ipString, 30000), 1000); //端口号为30000

            //获取输入输出流
            OutputStream ou = socket.getOutputStream();
            BufferedReader bff = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            //读取发来服务器信息
            String line = null;
            buffer="";
            while ((line = bff.readLine()) != null) {
                buffer = line + buffer;
            }

            //向服务器发送信息
            ou.write(txt.getBytes("gbk"));
            ou.flush();
            result = buffer.toString();
            bff.close();
            ou.close();
            socket.close();
        } catch (SocketTimeoutException aa) {;
            result = "服务器连接失败！请检查网络是否打开";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
