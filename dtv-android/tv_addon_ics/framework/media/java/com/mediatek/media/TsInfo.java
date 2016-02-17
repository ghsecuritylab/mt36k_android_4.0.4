package com.mediatek.media;

import java.util.ArrayList;

public class TsInfo {
    
    public TsInfo(){
        
    }
    
    public static class StreamInfo {
        
        private short strm_pid;
        private short strm_type;
        
        public StreamInfo(short strm_pid, short strm_type) {
            super();
            this.strm_pid = strm_pid;
            this.strm_type = strm_type;
        }

        public short getStrm_pid() {
            return strm_pid;
        }

        public short getStrm_type() {
            return strm_type;
        }

    }
    
   
    public static class PmtInfo {
        
        private boolean fg_init;
        private short pmt_pid;
        private byte strm_num;
        private ArrayList<StreamInfo> streamInfo;
      //  private ArrayList<StreamInfo> streamInfo = new ArrayList<StreamInfo>();

        public PmtInfo(boolean fg_init, short pmt_pid, byte strm_num,
            ArrayList<StreamInfo> streamInfo) {
            super();
            this.fg_init = fg_init;
            this.pmt_pid = pmt_pid;
            this.strm_num = strm_num;
            this.streamInfo = streamInfo;
        }
        
        public boolean isFg_init() {
            return fg_init;
        }

        public short getPmt_pid() {
            return pmt_pid;
        }

        public byte getStrm_num() {
            return strm_num;
        }

        public ArrayList<StreamInfo> getStreamInfo() {
            return streamInfo;
        } 

    }

    private short packet_size;
    private short pat_pid;
    private byte pmt_num;
    private ArrayList<PmtInfo> pmtInfo;

    public TsInfo(short packet_size, short pat_pid, byte pmt_num,
            ArrayList<PmtInfo> pmtInfo) {
        super();
        this.packet_size = packet_size;
        this.pat_pid = pat_pid;
        this.pmt_num = pmt_num;
        this.pmtInfo = pmtInfo;
    }
    public short getPacket_size() {
        return packet_size;
    }
    public short getPat_pid() {
        return pat_pid;
    }
    public byte getPmt_num() {
        return pmt_num;
    }
    public ArrayList<PmtInfo> getPmtInfo() {
        return pmtInfo;
    }
    
}
