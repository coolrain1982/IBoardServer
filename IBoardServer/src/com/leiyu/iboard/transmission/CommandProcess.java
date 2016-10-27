package com.leiyu.iboard.transmission;

/**
 * Created by leiyu on 2016/10/25.
 */

public class CommandProcess {

    private StringBuffer sb ;

    public CommandProcess(StringBuffer sb) {
        this.sb = sb;
    }

    public Command getCommand() {
        //消息头部不全，不处理
        if (sb.length() < 12) {
            return null;
        }
        //前四位是消息类型ID
        int cID = 0;
        try {
            cID = Integer.parseInt(sb.substring(0, 4));
        } catch (Exception e) {
            cID = 0;
        }
        //中间8位是消息长度
        int cLen = 0;
        try {
            cLen = Integer.parseInt(sb.substring(4, 12));
        } catch (Exception e) {
            cLen = 0;
        }

        if (cID == 0) {
            //把整个消息抛掉
            sb.delete(0, sb.length());
            return null;
        }

        if (cLen == 0) {
            //有问题，把前12个字符抛掉
            sb.delete(0, 12);
            return null;
        } if (cLen + 12 > sb.length()) {
            //没有收全，继续等
            return null;
        }

        //后面全部是消息内容
        Command command = new Command();
        command.setCommand(sb.substring(12, cLen + 4 + 8));
        command.setCommandId(cID);

        sb.delete(0, 12 + cLen);

        return command;
    }
}
