package jrdcom.com.wificonnectclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by longcheng on 2017/5/13.
 */

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChartModel> chartList;
    private Context mContext;
    private LayoutInflater mInflater;
    public ContactAdapter(Context context, List<ChartModel> charts){
        mContext = context;
        chartList = charts;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return chartList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;
        if(viewType ==Common.CHART_SEND){
            view = mInflater.inflate(R.layout.layout_right, parent, false);
            viewHolder = new ChartSendViewHolder(view);
        }else{
            view = mInflater.inflate(R.layout.layout_left,parent,false);
            viewHolder = new ChartSendViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChartModel model = chartList.get(position);
        int type = model.getChartType();
        //头像不用管
        ((ChartSendViewHolder)holder).mImageView.setImageResource(R.mipmap.ic_launcher_round);
        ((ChartSendViewHolder)holder).mTextView.setText(model.getChartContent());
        /*if(type == Common.CHART_SEND){

        }else{

        }*/
    }

    @Override
    public int getItemViewType(int position) {
        return chartList.get(position).getChartType();//super.getItemViewType(position);
    }

    private class ChartSendViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView;
        public ChartSendViewHolder(View view){
            super(view);
            mImageView = (ImageView)view.findViewById(R.id.image_view);
            mTextView = (TextView)view.findViewById(R.id.text_view);

        }
    }
    private class ChartReceiverViewHolder extends RecyclerView.ViewHolder{

        public ChartReceiverViewHolder(View view){
            super(view);
        }
    }

    public void addItem(ChartModel chartModel){
        chartList.add(chartModel);
        notifyItemInserted(chartList.size() - 1);
    }
}
