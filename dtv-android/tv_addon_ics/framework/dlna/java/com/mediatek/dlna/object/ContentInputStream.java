package com.mediatek.dlna.object;

import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This Class is the Content object streaming, use to download the content resource
 * @see com.mediatek.dlna.object.Content#getInputStream()
 */

public class ContentInputStream extends InputStream {
	private static final int DMS_BUSY = -2;
	private static final int RETRY_COUNT = 5;
    private Content content;
    private int handle;
    private long current;

    /**
     *
     * @param content  The content object
     */
    public ContentInputStream(Content content) {
        this.content = content;
        handle = nativeOpen(content.getResUri(), content.getMimeType(), content.getDtcpInfo(), content.getSize(), content.getFlag(), content.getMediaType(), content.getDrmType(), content.getSessionId());
        current = 0;
    }
    
    private boolean retry() {
        if (handle != 0) {
            nativeClose(handle);
        }
      handle = 0;
      handle = nativeOpen(content.getResUri(), content.getMimeType(), content.getDtcpInfo(), content.getSize(), content.getFlag(), content.getMediaType(), content.getDrmType(), content.getSessionId());
      if (handle == 0) {
            return false;
        }
      if(current != 0){
        	long s =  nativeSkip(handle, current);
          if(s != current) {
              return false;        
           }
       }
       return true;
    }

    @Override
    public int read() throws IOException {
        throw new IOException("Not Supported");
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (handle == 0) {
            throw new FileNotFoundException();
        }
        int ret = nativeRead(handle, b, 0, b.length);
        if(ret > 0) {
            current += ret;
        }else if(ret == DMS_BUSY) {
        	for(int i = 0; i < RETRY_COUNT; i++) {
        	    if(retry()) {
        	        ret = nativeRead(handle, b, 0, b.length);
                  if(ret > 0) {
                      current += ret;
                      break;
                  }	
        	    } 
        	    
        	    try {
        	    	Thread.sleep(200*(i+1));
        	    }catch(InterruptedException e) {
					Log.e("ContentInputStreaming", "sleep error!");
        	    }        	                
        	}
        }
        return ret;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (handle == 0) {
            throw new FileNotFoundException();
        }
        int ret = nativeRead(handle, b, off, len);
        if(ret > 0) {
            current += ret;
        }
        else if(ret == DMS_BUSY) {
        	for(int i = 0; i < RETRY_COUNT; i++) {
        	    if(retry()) {
        	        ret = nativeRead(handle, b, off, len);
                  if(ret > 0) {
                      current += ret;
                      break;
                  }	
        	    } 
        	    try {
                    Thread.sleep(200*(i+1));
        	    }catch(InterruptedException e) {
					Log.e("ContentInputStreaming", "sleep error!");
        	    }             
        	}
        }
        return ret;
    }

    @Override
    public long skip(long n) throws IOException {
        if (handle == 0) {
            throw new FileNotFoundException();
        }
        long s =  nativeSkip(handle, n);
        if(s > 0) {
            long skip = s - current;
            current = s;
            return skip;
        }
        return s;
    }

    @Override
    public int available() throws IOException {
        if(content.getSize() != 0) {
            return (int)(content.getSize() - current);
        }
        return 0;
    }

    @Override
    public void close() throws IOException {
        if (handle == 0) {
            throw new FileNotFoundException();
        }
        nativeClose(handle);
        handle = 0;
    }

    @Override
    public void reset() throws IOException {
        if (handle != 0) {
            nativeClose(handle);
            handle = 0;
        }
        handle = nativeOpen(content.getResUri(), content.getMimeType(), content.getDtcpInfo(), content.getSize(), content.getFlag(), content.getMediaType(), content.getDrmType(), content.getSessionId());
        current = 0;
        if(handle == 0) {
            throw  new IOException("Not Supported");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if(handle != 0) {
            nativeClose(handle);
            handle = 0;
        }
        super.finalize();
    }

    @Override
    public void mark(int readlimit) {
        super.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return super.markSupported();
    }

    /**
     *
     * @return  The content object size
     * @see   com.mediatek.dlna.object.Content#getSize()
     */
    public long size() {
        return content.getSize();
    }

    private native int nativeOpen(String resUri, String mimeType, String dtcpInfo, long size, int flag, int mediaType, int drmType, byte[] sessionId);

    private native int nativeRead(int handle, byte[] b, int off, int len);

    private native long nativeSkip(int handle, long n);

    private native int nativeClose(int handle);
}
