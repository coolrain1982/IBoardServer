package com.leiyu.iboard.transmission;

/**
 * Created by leiyu on 2016/10/25.
 */

public class Command {

    public static final int COMMAND_USERID = 1; //用户ID
    public static final int COMMAND_BOARD_START = 2; //画图开始
    public static final int COMMAND_BOARD_END = 3; //画图开始
    public static final int COMMAND_DRAWSHAPE_START = 4; //画图开始
    public static final int COMMAND_DRAWSHAPE_MOVE = 5; //移动画笔
    public static final int COMMAND_DRAWSHAPE_END = 6; //画图结束
    public static final int COMMAND_DRAWSHAPE_ALL = 7; //把画图轨迹全部重发一次
    public static final int COMMAND_HEARTBEAT = 999; //心跳

    private int commandId;
    private String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }
}
