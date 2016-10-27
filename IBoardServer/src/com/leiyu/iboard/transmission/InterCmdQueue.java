package com.leiyu.iboard.transmission;

import java.util.ArrayList;

/**
 * Created by leiyu on 2016/10/25.
 */

public class InterCmdQueue {
    private ArrayList<Object> cmdIn = new ArrayList<>();
    private ArrayList<String> cmdOut = new ArrayList<>();

    private final Integer cmdInSyn = 0;
    private final Integer cmdOutSyn = 1;

    public void addCmdIn(Object cmd) {
        synchronized (cmdInSyn) {
            cmdIn.add(cmd);
        }
    }

    public Object getCmdIn() {
        Object rtnStr = null;
        synchronized (cmdInSyn) {
            if (cmdIn.size() > 0) {
                rtnStr = cmdIn.remove(0);
            }
        }

        return rtnStr;
    }

    public void addCmdOut(String cmd) {
        synchronized (cmdOutSyn) {
            cmdOut.add(cmd);
        }
    }

    public String getCmdOut() {
        String rtnStr = null;
        synchronized (cmdOutSyn) {
            if (cmdOut.size() > 0) {
                rtnStr = cmdOut.remove(0);
            }
        }

        return rtnStr;
    }

    public void clear() {
        synchronized (cmdInSyn) {
            cmdIn.clear();
        }
        synchronized (cmdOutSyn) {
            cmdOut.clear();
        }
    }
}
