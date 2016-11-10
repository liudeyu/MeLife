package com.gzmelife.app.hhd;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HHD on 2016/11/4.
 *
 */

public class StepNode implements Serializable{

    /*TODO:20161104*/
    /** 食材名称 */
    private ArrayList mlistMore;
    /** 食材UID */
    private ArrayList mlisetMoreID;
    /** 步骤节点 */
    private TimeNode timeNode;
    /** 节点开始时间 */
    private int startTime;
    /** 节点结束时间 */
    private int endTime;
    /** 节点描述 */
    private String step;

    private boolean state;
    /** 节点文件路径 */
    private String filePath;

    public ArrayList getMlisetMoreID() {
        return mlisetMoreID;
    }

    public void setMlisetMoreID(ArrayList mlisetMoreID) {
        this.mlisetMoreID = mlisetMoreID;
    }

    public ArrayList getMlistMore() {
        return mlistMore;
    }

    public void setMlistMore(ArrayList mlistMore) {
        this.mlistMore = mlistMore;
    }

    public TimeNode getTimeNode() {
        return timeNode;
    }

    public void setTimeNode(TimeNode timeNode) {
        this.timeNode = timeNode;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
	/*TODO:20161104*/
}
