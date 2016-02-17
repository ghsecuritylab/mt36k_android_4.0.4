package com.mediatek.common.capture;

public class MtkCaptureDataInfo{
	public final int length;
	private byte data[];
	
	public MtkCaptureDataInfo(int length, byte[] Data)
	{
		super();
		this.length = length;
		this.data = Data;
	}
    public int getLength() {
        return length;
    }
    public byte[] getData() {
        return data;
    }
}