package jrdcom.com.wificonnectclient;

/**
 * Created by longcheng on 2017/5/13.
 */

public class ChartModel {
    private String chartContent;
    private int chartType;
    private String chartName;

    public ChartModel(String content, int type){
        chartContent = content;
        chartType = type;
    }
    public void setChartContent(String chartContent) {
        this.chartContent = chartContent;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public void setChartType(int chartType) {
        this.chartType = chartType;
    }

    public int getChartType() {
        return chartType;
    }

    public String getChartContent() {
        return chartContent;
    }

    public String getChartName() {
        return chartName;
    }
}
