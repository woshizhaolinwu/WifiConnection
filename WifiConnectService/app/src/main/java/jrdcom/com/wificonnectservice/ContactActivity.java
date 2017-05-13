package jrdcom.com.wificonnectservice;

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
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        initView();
    }

    private void initView(){
        //findView
        findViewId();
        initRecycler();
        addListener();
        receiveSocketMessage();
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
                    //发送数据给客户端

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
                        /*返回数据*/
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


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
        if (msg.what==0x11) { //receive msg
            Bundle bundle = msg.getData();
            //在这里构建
            String message = bundle.getString("msg");
            ChartModel chartModel = new ChartModel(message, Common.CHART_RECEIVER);
            updateList(chartModel);

        }
    };
    };

    private void updateList(ChartModel chartModel){
        //list.add(chartModel);
        contactAdapter.addItem(chartModel);
    }
}
