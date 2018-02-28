package indi.aljet.myamazefilemanager_master.utils.cloud;

import java.io.IOException;
import java.io.InputStream;

import indi.aljet.myamazefilemanager_master.utils.SmbStreamer.StreamSource;

/**
 * Created by PC-LJL on 2018/1/25.
 */

public class CloudStreamSource extends StreamSource {

    protected String mime;
    protected long fp;
    protected long len;
    protected String name;
    protected  int bufferSize;

    private InputStream inputStream;


    public CloudStreamSource(String fileName, long length, InputStream inputStream) {

        fp = 0;
        len = length;
        this.name = fileName;
        this.inputStream = inputStream;
        bufferSize = 1024*60;
    }


    public void open()throws IOException{
        try{
            if(fp > 0){
                inputStream.skip(fp);
            }
        }catch (Exception e){
            throw new IOException(e);
        }
    }


    public int read(byte[] buff)throws IOException{
        return read(buff,0,buff.length);
    }


    public int read(byte[] bytes,int start,int offs)
    throws IOException{
        int read = inputStream.read(bytes,start,offs);
        fp += read;
        return read;
    }

    public long moveTo(long position)throws IOException{
        fp = position;
        return fp;
    }

    public void close(){
        try{
            inputStream.close();
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

    public int getBufferSize(){
        return bufferSize;
    }




}
