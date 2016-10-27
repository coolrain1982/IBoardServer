package com.leiyu.iboard.transmission;

/**
 * Created by leiyu on 2016/10/25.
 */

public class ConnectToServerException extends Exception {

    public ConnectToServerException(String msg, String server, String port) {
        super(String.format("Connect to server[%s:%s] failed: %s", server, port, msg));
    }
}
