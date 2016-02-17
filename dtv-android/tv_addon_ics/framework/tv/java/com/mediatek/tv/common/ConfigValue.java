package com.mediatek.tv.common;

import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * for the configuration value
 *
 */
public class ConfigValue implements Parcelable {
	private int intValue;	
	private int[] intArrayValue;	
	private byte[] byteArrayValue;
	private boolean booleanValue;
	private String stringValue;


	private int minValue;
	private int maxValue;
	
	private int gpioID;
	private int gpioMask;
	private int gpioValue;
	
	
	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	
	/**
	 * @return the integer value of the specified operation
	 */
	public int getIntValue() {
		return intValue;
	}

	/**
	 * @param intValue
	 * 			set intValue for specified operation
	 */
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	
	/**
	 * @return the integer array of the specified operation
	 */
	public int[] getIntArrayValue() {
		return intArrayValue;
	}

	/**
	 * @param arrayValue
	 * 			set the array value for the specified operation
	 */
	public void setIntArrayValue(int[] intArrayValue) {
		this.intArrayValue = intArrayValue;
	}
	
	/**
	 * @return the integer array of the specified operation
	 */
	public byte[] getByteArrayValue() {
		return byteArrayValue;
	}

	/**
	 * @param arrayValue
	 * 			set the array value for the specified operation
	 */
	public void setByteArrayValue(byte[] byteArrayValue) {
		this.byteArrayValue = byteArrayValue;
	}

	/**
	 * @return the boolean value of the specified operation
	 */
	public boolean isBoolVal() {
		return booleanValue;
	}

	/**
	 * @param cfg_bool_val
	 * 			set the boolean value for the specified operation
	 */
	public void setBoolValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	
	/**
	 * @param minValue
	 * 			get minValue for specified operation
	 */
	public int getMinValue() {
		return minValue;
	}
	/**
	 * @param minValue
	 * 			set minValue for specified operation
	 */
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
	/**
	 * @return the maxValue of the specified operation
	 */
	public int getMaxValue() {
		return maxValue;
	}
	/**
	 * @param maxValue
	 * 			set maxValue for specified operation
	 */
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
	
	
	/**
	 * @return the gpio id value
	 */
	public int getGpioID() {
		return gpioID;
	}

	/**
	 * @param gpio_id
	 * 			set the gpioID value
	 */
	public void setGpioID(int gpioID) {
		this.gpioID = gpioID;
	}

	/**
	 * @return the gpio mask value
	 */
	public int getGpioMask() {
		return gpioMask;
	}

	/**
	 * @param gpio_mask
	 * 			set the gpioMask value
	 */
	public void setGpioMask(int gpioMask) {
		this.gpioMask = gpioMask;
	}

	/**
	 * @return the gpio value
	 */
	public int getGpioValue() {
		return gpioValue;
	}

	/**
	 * @param gpioValue
	 * 			set the gpio value
	 */
	public void setGpioValue(int gpioValue) {
		this.gpioValue = gpioValue;
	}
	
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public static final Creator<ConfigValue> CREATOR = new Parcelable.Creator<ConfigValue>() {
        public ConfigValue createFromParcel(Parcel source) {
            return new ConfigValue(source);
        }

        public ConfigValue[] newArray(int size) {
            return new ConfigValue[size];
        }
    };

    private ConfigValue(Parcel source) {
        readFromParcel(source);
    }

    public ConfigValue() {
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(intValue);

        if (intArrayValue != null) {
            out.writeInt(intArrayValue.length);
            out.writeIntArray(intArrayValue);
        } else {
            out.writeInt(0);
        }
        if (byteArrayValue != null) {
            out.writeInt(byteArrayValue.length);
            out.writeByteArray(byteArrayValue);
        } else {
            out.writeInt(0);
        }

        out.writeInt(booleanValue ? 1 : 0);// convert to int
        out.writeInt(gpioID);
        out.writeInt(gpioMask);
        out.writeInt(gpioValue);
        out.writeInt(minValue);
        out.writeInt(maxValue);
    }

    public void readFromParcel(Parcel in) {
        intValue = in.readInt();
        int intArrayValueLen = in.readInt();
        if (intArrayValueLen > 0) {
            intArrayValue = new int[intArrayValueLen];
            in.readIntArray(intArrayValue);
        }
        int byteArrayValueLen = in.readInt();
        if (byteArrayValueLen > 0) {
            byteArrayValue = new byte[byteArrayValueLen];
            in.readByteArray(byteArrayValue);
        }
        booleanValue = (in.readInt() == 1) ? true : false;
        gpioID = in.readInt();
        gpioMask = in.readInt();
        gpioValue = in.readInt();
minValue = in.readInt();
maxValue = in.readInt();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConfigValue [intValue=");
        builder.append(intValue);
        builder.append(", intArrayValue=");
        builder.append(Arrays.toString(intArrayValue));
        builder.append(", byteArrayValue=");
        builder.append(Arrays.toString(byteArrayValue));
        builder.append(", booleanValue=");
        builder.append(booleanValue);
        builder.append(", gpioID=");
        builder.append(gpioID);
        builder.append(", gpioMask=");
        builder.append(gpioMask);
        builder.append(", gpioValue=");
        builder.append(gpioValue);
        builder.append("]");
        return builder.toString();
    }
	
}
