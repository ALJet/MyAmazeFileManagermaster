package indi.aljet.myamazefilemanager_master.utils;

import java.util.ArrayList;
import java.util.regex.Pattern;

import indi.aljet.myamazefilemanager_master.exceptions.ShellNotRunningException;
import indi.aljet.myamazefilemanager_master.filesystem.RootHelper;

/**
 * Created by PC-LJL on 2018/1/18.
 */

public class RootUtils {

    public static final int CHMOD_READ = 4,
    CHMOD_WRITE = 2,CHMOD_EXECUTE = 1;

    public static final String DATA_APP_DIR = "/data/app";

    public static final String CHMOD_COMMAND = "chmod %s " +
            " %o \"%s\"";

    private static final String LS = "ls -lAnH \"%\" --color=never";
    private static final String LSDIR = "ls -land \"%\" --color=never";
    public static final String SYSTEM_APP_DIR = "/system/app";
    private static final Pattern mLsPattern;

    static {
        mLsPattern = Pattern.compile(".[rwxsStT-]{9}\\s+.*");
    }


    private static String mountFileSystemRW(String path)
    throws ShellNotRunningException{
        String command = "mount";
        ArrayList<String> output = RootHelper
                .runShellCommand(command);
        String mountPoint = "",types = null;
        for(String line : output){
            String[] words = line.split(" ");
            if(path.contains(words[2])){
                if(words[2].length() > mountPoint
                        .length()){
                    mountPoint = words[2];
                    types = words[5];
                }
            }
        }
        if(!mountPoint.equals("") &&
                types != null){
            if(types.contains("rw")){
                return null;
            }
        }else if(types.contains("ro")){
            String mountCommand = "mount -o rw,remount " + mountPoint;
            ArrayList<String> mountOutput =
                    RootHelper.runShellCommand(mountCommand);
            if(mountOutput.size() != 0){
                return null;
            }else{
                return mountCommand;
            }
        }
        return null;
    }

    private static void mountFileSystemRO(String path)
    throws ShellNotRunningException{
        String command = "umount -r \"" + path
                + "\"";
        RootHelper.runShellCommand(command);
    }

    public static void copy(String source,
                       String destination)
        throws ShellNotRunningException{
        String mountPoint = mountFileSystemRW(destination);
        RootHelper.runShellCommand("cp \"" + source + "\" \"" + destination + "\"");
        if(mountPoint != null){
            mountFileSystemRO(mountPoint);
        }
    }


    public static void mkDir(String path,
                             String name)
        throws ShellNotRunningException{
        String mountPoint = mountFileSystemRW(path);
        RootHelper.runShellCommand("mkdir \"" + path + "/" + name + "\"");
        if(mountPoint != null){
            mountFileSystemRO(mountPoint);
        }
    }


    public static void mkFile(String path)
        throws ShellNotRunningException{
        String mountPoint = mountFileSystemRW(path);
        RootHelper.runShellCommand("touch \"" + path + "\"");
        if (mountPoint != null) {
            // we mounted the filesystem as rw, let's mount it back to ro
            mountFileSystemRO(mountPoint);
        }
    }


    private static int getFilePermissions(String path)
    throws ShellNotRunningException{
        String line = RootHelper.
                runShellCommand
                        ("stat -c  %a \"" + path + "\"").get(0);
        return Integer.valueOf(line);
    }


    public static boolean delete(String path)
        throws ShellNotRunningException{
        String mountPoint = mountFileSystemRW(path);
        ArrayList<String> result = RootHelper.
        runShellCommand("rm -rf \"" + path + "\"");

        if (mountPoint != null) {
            // we mounted the filesystem as rw, let's mount it back to ro
            mountFileSystemRO(mountPoint);
        }

        return result.size() != 0;
    }


    /**
     * Moves file using root
     */
    public static void move(String path, String destination) throws ShellNotRunningException {
        // remounting destination as rw
        String mountPoint = mountFileSystemRW(destination);

        //mountOwnerRW(mountPath);
        RootHelper.runShellCommand("mv \"" + path + "\" \"" + destination + "\"");

        if (mountPoint != null) {
            // we mounted the filesystem as rw, let's mount it back to ro
            mountFileSystemRO(mountPoint);
        }
    }



    /**
     * Renames file using root
     *
     * @param oldPath path to file before rename
     * @param newPath path to file after rename
     * @return if rename was successful or not
     */
    public static boolean rename(String oldPath, String newPath) throws ShellNotRunningException {
        String mountPoint = mountFileSystemRW(oldPath);
        ArrayList<String> output = RootHelper.runShellCommand("mv \"" + oldPath + "\" \"" + newPath + "\"");

        if (mountPoint != null) {
            // we mounted the filesystem as rw, let's mount it back to ro
            mountFileSystemRO(mountPoint);
        }

        return output.size() == 0;
    }

    public static void cat(String sourcePath, String destinationPath)
            throws ShellNotRunningException {

        String mountPoint = mountFileSystemRW(destinationPath);

        RootHelper.runShellCommand("cat \"" + sourcePath + "\" > \"" + destinationPath + "\"");
        if (mountPoint != null) {
            // we mounted the filesystem as rw, let's mount it back to ro
            mountFileSystemRO(mountPoint);
        }
    }


    /**
     * This converts from a set of booleans to OCTAL permissions notations.
     * For use with {@link }
     * (true, false, false,  true, true, false,  false, false, true) => 0461
     */
    public static int permissionsToOctalString(boolean ur, boolean uw, boolean ux,
                                               boolean gr, boolean gw, boolean gx,
                                               boolean or, boolean ow, boolean ox) {
        int u = ((ur?CHMOD_READ:0) | (uw?CHMOD_WRITE:0) | (ux?CHMOD_EXECUTE:0)) << 6;
        int g = ((gr?CHMOD_READ:0) | (gw?CHMOD_WRITE:0) | (gx?CHMOD_EXECUTE:0)) << 3;
        int o = (or?CHMOD_READ:0) | (ow?CHMOD_WRITE:0) | (ox?CHMOD_EXECUTE:0);
        return u | g | o;
    }

}
