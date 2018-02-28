package indi.aljet.myamazefilemanager_master.exceptions;

/**
 * Created by PC-LJL on 2018/1/9.
 */

public class ShellNotRunningException extends Exception {
    public ShellNotRunningException(){
        super("Shell stopped running!");
    }
}
