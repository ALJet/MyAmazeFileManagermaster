package indi.aljet.myamazefilemanager_master.utils.SmbStreamer;

import android.webkit.MimeTypeMap;

import java.io.IOException;
import java.io.InputStream;

import jcifs.smb.SmbFile;

/**
 * Created by PC-LJL on 2018/1/24.
 */

public class StreamSource {

    protected String mime;
    protected  long fp;
    protected long len;
    protected String name;
    protected SmbFile file;
    InputStream input;
    protected int bufferSize;

    public StreamSource() {
    }


    public StreamSource(SmbFile file,long len) {
        fp = 0;
        this.len = len;
        this.file = file;
        mime = MimeTypeMap.getFileExtensionFromUrl(file
        .getName());
        name = file.getName();
        bufferSize = 1024 * 60;
    }


    public void open()throws IOException{
        try{
            input = file.getInputStream();
            if(fp > 0){
                input.skip(fp);
            }
        }catch (Exception e){
            throw new IOException(e);
        }
    }


    public int read(byte[] buff) throws IOException{
        return read(buff,0,buff.length);
    }

    public int read(byte[] bytes,int start,int offs)
    throws IOException{
        int read = input.read(bytes,start,offs);
        fp += read;
        return read;
    }


    public long moveTo(long position) throws
            IOException{
        fp = position;
        return fp;
    }

    public void close(){
        try{
            input.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public String getMimeType(){
        return mime;
    }
    public long length(){
        return len;
    }
    public String getName(){
        return name;
    }
    public long available(){
        return len - fp;
    }

    public void reset(){
        fp = 0;
    }

    public SmbFile getFile(){
        return file;
    }

    public int getBufferSize(){
        return bufferSize;
    }



}
