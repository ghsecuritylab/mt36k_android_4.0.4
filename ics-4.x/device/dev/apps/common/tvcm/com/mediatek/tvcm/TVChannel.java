package com.mediatek.tvcm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mediatek.tv.common.ChannelCommon;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.AnalogChannelInfo;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.DvbChannelInfo;
import com.mediatek.tv.service.ChannelService;
import com.mediatek.tv.service.ChannelService.ChannelOperator;

/**
 * TVChannel .<br>
 * There will be no sub class for different 'type' of channel such as
 * AnalogChannel or Digtal channel. Because we don't assume that some operations
 * could be separated by this 'type'.
 * 
 * @author mtk40063
 * 
 */
public class TVChannel {

	public final static String STANDARD_DVB = "dvb";
	public final static String STANDARD_PAL = "pal";

	private TVChannelManager cm;
	private TVChannelSelector cs;

	private ChannelInfo rawInfo;
	private String standard;

	private short channelNum;
	private boolean isNew;
	private int originalFreq;

	final int CH_MASK_STORED = 1 << 0;
	final int CH_MASK_SKIP = 1 << 1;
	final int CH_MASK_FAVR = 1 << 2;
	final int CH_MASK_BLOCKED = 1 << 3;
	// final int CH_MASK_DELETED = 1 << 4;
	private int mask;

	private ChannelService rawSrv;

	private String dummyName;

	boolean valid;

	boolean usrUnblocked;

	/**
	 * Channel TV System option
	 * 
	 * @author mtk40063
	 * 
	 */
	public static class ChTvSystemOption extends TvAudioSystemOption {
		private TVChannel ch;

		public ChTvSystemOption(TVChannel ch) {
			super();
			this.ch = ch;
		}

		// Don't return AUTO
		public Integer getMin() {
			return TV_BG;
		}

		public Integer get() {
			if (ch.rawInfo == null) {
				return super.get();
			}

			AnalogChannelInfo info = (AnalogChannelInfo) ch.rawInfo;
			int good = -1;
			int better = -1;
			for (int i = 0; i < tv_sys_tbl.length; i++) {
				// Work around: TV Manager will give a mask with first garbage
				// bit.

				if (tv_sys_tbl[i] == (info.getTvSys() & ~(0x1 << 17))) {
					if (good < 0) {
						good = (i == 9 ? (i - 1) : i);
					}

					if (aud_sys_tbl[i] == info.getAudioSys()) {
						better = (i == 9 ? (i - 1) : i);
						break;
					}
				}
			}

			if (better > 0) {
				return better;
			}

			if (good > 0) {
				return good;
			}
			// SHould not??
			TVComponent.TvLog("Channel info found failed " + info.getTvSys()
					+ "," + info.getAudioSys());
			return this.TV_BG;
		}

		@Override
		public boolean set(Integer val) {
			int i = val.intValue();
			if (ch.rawInfo == null) {
				return super.set(val);
			}
			// TVChannelList cm = ch.getChannelManager();
			// cm.beginEditChannel();
			if (ch.rawInfo != null) {
				AnalogChannelInfo info = (AnalogChannelInfo) ch.rawInfo;
				info.setTvSys(tv_sys_tbl[i]);
				info.setAudioSys(aud_sys_tbl[i]);
				ch.flush();
			}
			// cm.endEditChannel();
			return true;
		}
	}

	ChTvSystemOption tvSysOption;

	/**
	 * Get TV system option
	 * 
	 * @return
	 */
	public ChTvSystemOption getTvSystemOption() {
		return tvSysOption;
	}

	public static class ChColorSystemOption extends ColorSystemOption {
		private TVChannel ch;

		public ChColorSystemOption(TVChannel ch) {
			super();
			this.ch = ch;
		}

		@Override
		public Integer get() {
			AnalogChannelInfo info = (AnalogChannelInfo) ch.rawInfo;
			if (info == null) {
				return super.get();
			}
			int cs = info.getColorSys();
			for (int i = 0; i < color_sys_tbl.length; i++) {
				if (color_sys_tbl[i] == info.getColorSys()) {
					return i;
				}
			}
			TVComponent
					.TvLog("Channel info found failed " + info.getColorSys());
			return this.COLOR_SYS_PAL;
		}

		@Override
		public boolean set(Integer val) {
			if (ch.rawInfo == null) {
				return super.set(val);
			}
			int i = val.intValue();
			// TVChannelList cm = ch.getChannelManager();
			// cm.beginEditChannel();
			if (ch.rawInfo != null) {
				AnalogChannelInfo info = (AnalogChannelInfo) ch.rawInfo;
				info.setColorSys(color_sys_tbl[i]);
				ch.flush();
			}
			// cm.endEditChannel();
			return true;
		}
	}

	ChColorSystemOption tvColorOption;

	/**
	 * Get Color system option
	 * 
	 * @return
	 */
	public ChColorSystemOption getColorSystemOption() {
		return tvColorOption;
	}

	TVChannel(TVChannelManager cm, ChannelInfo rawInfo) {
		this.rawInfo = rawInfo;
		this.cm = cm;
		this.valid = true;
		this.usrUnblocked = false;
		/* TVChannelList list, */
		rawSrv = cm.rawChSrv;
		this.cs = cm.getContent().getChannelSelector();
		// Build option table
		// Channels of different standard may have differenct options.
		if (rawInfo instanceof AnalogChannelInfo) {
			tvSysOption = new ChTvSystemOption(this);
			tvColorOption = new ChColorSystemOption(this);
			frequence = new ChFrequence(this);
			autoFineTuneOption = new ChAutoFineTuneOption(this);
			standard = STANDARD_PAL;
			if (rawInfo.getServiceName() == null) {
				rawInfo.setServiceName("");
			}
			originalFreq = ((AnalogChannelInfo) rawInfo).getFrequency();
		} else if (rawInfo instanceof DvbChannelInfo) {
			// Fix me , we need to do more about this.
			// TODO: Add options for DVBC.
			standard = STANDARD_DVB;
			originalFreq = ((DvbChannelInfo) rawInfo).getFrequency();
		} else {
			// ...Maybe dummy .
			standard = STANDARD_PAL;
		}
		loadPrivate();
		if ((mask & CH_MASK_STORED) == 0) {
			this.mask = CH_MASK_STORED;
			this.isNew = true;
			storePrivate();
			flush();
		} else {
			isNew = false;
		}
	}

	/**
	 * Create a empty channel,
	 * 
	 * @param cm
	 */
	TVChannel() {
		this.rawInfo = null;
		this.rawSrv = null;
		this.cm = null;
		this.cs = null;
		this.valid = true;

		standard = STANDARD_PAL;
		isNew = false;

	}

	public boolean isRadio() {
		if (rawInfo != null) {
			return rawInfo.isRadioService();
		}
		return false;
	}

	public boolean isAnalog() {
		if (rawInfo != null) {
			return rawInfo.isAnalogService();
		} else {
			//If rawInfo is not usable, determine this by standard			
			if (standard.equals(STANDARD_PAL)) {
				return true;
			}
		}
		return false;
	}
	public boolean isDTV() {
		if (rawInfo != null) {
			return rawInfo.isTvService();
		} else {
			//If rawInfo is not usable, determine this by standard
			if (standard.equals(STANDARD_DVB)) {
				return true;
			}
		}
		return false;
	}

	public boolean isVisible() {
		if (rawInfo != null) {
			return rawInfo.isVisible();
		}
		return true;
	}

	public boolean isVisibleInEPG() {
		if (rawInfo != null) {
			return rawInfo.isEpgVisible();
		}
		return false;
	}

	public String getStandard() {
		return standard;
	}

	synchronized void invalid() {
		valid = false;
	}

	public synchronized boolean isValid() {
		return valid;
	}

	synchronized void checkValid() {
		if (!valid)
			throw new TVChannelInvalidException();
	}

	// This is for temporarily
	private int userData = 0;
	public void setUser(int val) {
		userData = val;
	}

	public int getUser() {
		return userData;
	}

	void loadPrivate() {
		if (rawInfo == null) {
			return;
		}
		checkValid();
		byte[] storage = rawInfo.getPrivateData();

		DataInputStream st = new DataInputStream(new ByteArrayInputStream(
				storage));
		try {
			mask = st.readInt();
			channelNum = st.readShort();
			userData = st.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void storePrivate() {
		if (rawInfo == null) {
			return;
		}
		checkValid();
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		DataOutputStream st = new DataOutputStream(bs);
		try {
			st.writeInt(mask);
			st.writeShort(channelNum);
			st.writeInt(userData);
			rawInfo.setPrivateData(bs.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: Write back data to rawInfo's private.
	}

	/**
	 * TBD: Should this be called by user? Flush modification to DB,
	 */
	protected void flush() {
		checkValid();

		if (rawSrv != null) {
			List<ChannelInfo> list = new ArrayList<ChannelInfo>();
			list.add(getRawInfo());
			try {
				rawSrv.setChannelList(ChannelOperator.UPDATE,
						ChannelCommon.DB_ANALOG, list);
			} catch (TVMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected ChannelInfo getRawInfo() {
		return rawInfo;
	}

	/**
	 * Set channel number
	 * 
	 * @param num
	 */
	public void setChannelNum(short num) {
		if (rawInfo != null) {
			cm.preEdit();
			rawInfo.setChannelNumber(num);
			rawInfo.setChannelNumberEdited(true);
			flush();
			cm.endEdit(this);
		} else {
			this.channelNum = num;
		}
		// TODO:Notify?
		// chList.setChannelNumber(this, num);
	}

	/**
	 * Get channel number
	 * 
	 * @return
	 */
	public synchronized short getChannelNum() {
		if (rawInfo != null) {
			return (short) rawInfo.getChannelNumber();
		} else {
			return channelNum;
		}
	}

	synchronized boolean isNew() {
		return isNew;
	}

	/**
	 * Set channel name
	 * 
	 * @param name
	 */
	public synchronized void setChannelName(String name) {
		checkValid();
		if (rawInfo != null) {
			cm.preEdit();
			rawInfo.setServiceName(name);
			rawInfo.setChannelNameEdited(true);
			flush();
			cm.endEdit(this);
		} else {
			dummyName = name;
		}
	}

	/**
	 * Get channel name
	 * 
	 * @return
	 */
	public synchronized String getChannelName() {
		if (rawInfo != null) {
			return rawInfo.getServiceName();
		} else {
			return dummyName;
		}
	}

	public synchronized void setMask(int mask, boolean set) {
		checkValid();
		cm.preEdit();
		if (set) {
			this.mask |= mask;
		} else {
			this.mask &= ~mask;
		}
		storePrivate();
		flush();
		cm.endEdit(this);
	}

	/**
	 * Get channel is skipped?
	 * 
	 * @return true:is skipped false: is not skipped
	 */
	public boolean isMaskSet(int mask) {
		if ((this.mask & mask) == 0) {
			return false;
		}
		return true;
	}

	/**
	 * Set skip
	 * 
	 * @param skip
	 * @return
	 */
	public synchronized void setSkip(boolean skip) {
		setMask(CH_MASK_SKIP, skip);
	}

	/**
	 * Get channel is skipped?
	 * 
	 * @return true:is skipped false: is not skipped
	 */
	public boolean isSkip() {
		return isMaskSet(CH_MASK_SKIP);
	}

	/**
	 * Set favorite
	 * 
	 * @param favr
	 * @return
	 */
	public synchronized void setFavorite(boolean favr) {
		setMask(CH_MASK_FAVR, favr);
	}

	/**
	 * Get is favorite?
	 * 
	 * @return true:is favorite false: is not favorite
	 */
	public synchronized boolean isFavorite() {
		return isMaskSet(CH_MASK_FAVR);
	}

	/**
	 * Set blocked
	 * 
	 * @param favr
	 * @return
	 */
	public synchronized void setBlocked(boolean block) {
		setMask(CH_MASK_BLOCKED, block);

		if (this.equals(cs.getCurrentChannel())) {
			cm.getContent().sendUpdate();
		}
	}

	/**
	 * Get is blocked?
	 * 
	 * @return true:is favorite false: is not favorite
	 */
	public synchronized boolean isBlocked() {
		return isMaskSet(CH_MASK_BLOCKED);
	}

	/**
	 * User unblocked.
	 */
	public synchronized void usrUnblock() {
//		if (isBlocked()) {
			usrUnblocked = true;
//		}
	}
	
	public synchronized boolean isUsrUnblocked() {
		return usrUnblocked;
	}

	/**
	 * Set skip
	 * 
	 * @param skip
	 * @return
	 */
	// public synchronized void setDeleted(boolean delete) {
	// setMask(CH_MASK_DELETED, delete);
	// }

	/**
	 * Get channel is skipped?
	 * 
	 * @return true:is skipped false: is not skipped
	 */
	// public boolean isDeleted() {
	// return isMaskSet(CH_MASK_DELETED);
	// }

	public static class ChFrequence extends TVOptionRange<Integer> {
		protected TVChannel ch;
		ChFrequence(TVChannel ch) {
			this.ch = ch;
		}
		public Integer getMax() {
			return 1000000000;// TODO:::.????
		}
		public Integer getMin() {
			return 0;
		}

		public Integer get() {
			ChannelInfo rawInfo = ch.getRawInfo();
			if (rawInfo instanceof AnalogChannelInfo) {
				AnalogChannelInfo info = (AnalogChannelInfo) rawInfo;
				return info.getFrequency();
			}
			return null;
		}

		@Override
		public boolean set(Integer val) {
			ChannelInfo rawInfo = ch.getRawInfo();
			if (rawInfo instanceof AnalogChannelInfo) {
				AnalogChannelInfo info = (AnalogChannelInfo) rawInfo;
				info.setFrequency(val);
				return true;
			}
			return false;
		}

	}

	ChFrequence frequence;

	/**
	 * @deprecated
	 * @return
	 */
	public ChFrequence getFreqence() {
		return frequence;
	}

	int dummyFreq = (int) (Math.random() * 1000000000);

	/**
	 * 
	 * @param freq
	 */
	public synchronized void setFreq(int freq) {
		checkValid();
		if (rawInfo != null) {
			cm.preEdit();
			if (rawInfo instanceof AnalogChannelInfo) {
				((AnalogChannelInfo) rawInfo).setFrequency(freq);
				rawInfo.setFrequencyEdited(true);
				flush();
			} else if (rawInfo instanceof DvbChannelInfo) {
				((DvbChannelInfo) rawInfo).setFrequency(freq);
				rawInfo.setFrequencyEdited(true);
				flush();
			}
			cm.endEdit(this);
			dummyFreq = freq;
		} else {
			dummyFreq = freq;
		}
	}

	/**
	 * Get frequency
	 * 
	 * @return
	 */
	public synchronized int getFreq() {
		if (rawInfo != null) {
			if (rawInfo instanceof AnalogChannelInfo) {
				return ((AnalogChannelInfo) rawInfo).getFrequency();
			} else if (rawInfo instanceof DvbChannelInfo) {
				return ((DvbChannelInfo) rawInfo).getFrequency();
			} else {
				return dummyFreq;
			}
		} else {
			return dummyFreq;
		}
	}

	public int getOriginalFreq(){
		return this.originalFreq;
	}

	/**
	 * Get frequency in Mhz
	 * 
	 * @return
	 */
	public float getMFreq() {
		return getFreq() / 1000000.0f;
	}

	/**
	 * Set frequency in Mhz
	 * 
	 * @return
	 */
	public void setMFreq(float freq) {
		setFreq((int) (freq * 1000000.0f));
	}

	boolean dummyAutoFineTune = true;

	public static class ChAutoFineTuneOption extends TVOption<Boolean> {
		protected TVChannel ch;
		ChAutoFineTuneOption(TVChannel ch) {
			this.ch = ch;
		}

		boolean canSet(Boolean val) {
			return true;
		}

		// Maybe use this for Digtal?
		boolean enabled() {
			return true;
		}

		public Boolean get() {
			ChannelInfo rawInfo = ch.getRawInfo();
			if (rawInfo instanceof AnalogChannelInfo) {
				AnalogChannelInfo info = (AnalogChannelInfo) rawInfo;
				// Where is get the finetune
				return !info.isNoAutoFineTune();
			}
			return false;
		}

		@Override
		public boolean set(Boolean val) {
			ChannelInfo rawInfo = ch.getRawInfo();
			if (rawInfo instanceof AnalogChannelInfo) {
				AnalogChannelInfo info = (AnalogChannelInfo) rawInfo;
				info.setNoAutoFineTune(!val);
				ch.flush();
			}
			return false;

		}

	}
	ChAutoFineTuneOption autoFineTuneOption = null;
	public ChAutoFineTuneOption getAutoFineTuneOption() {
		return autoFineTuneOption;
	}

	/**
	 * Set auto finetune.
	 * 
	 * @deprecated
	 * @param autoFineTune
	 */
	public synchronized void setAutoFineTune(boolean autoFineTune) {
		checkValid();
		if (rawInfo != null) {
			AnalogChannelInfo info = (AnalogChannelInfo) rawInfo;
			info.setNoAutoFineTune(!autoFineTune);
			flush();
		} else {
			dummyAutoFineTune = autoFineTune;
		}
	}

	/**
	 * Get frequency
	 * 
	 * @deprecated
	 * @return
	 */
	public synchronized boolean isAutoFineTune() {
		if (rawInfo != null) {
			if (rawInfo instanceof AnalogChannelInfo) {
				AnalogChannelInfo info = (AnalogChannelInfo) rawInfo;
				// Where is get the finetune
				return !info.isNoAutoFineTune();
			}
			return false;
		} else {
			return dummyAutoFineTune;
		}
	}

	// ChannelInfo getRawInfo() {
	// return rawInfo;
	// }

	public String toString() {
		return " CH " + getChannelNum() + " name:" + getChannelName() + "";
	}

	// public int compareTo(TVChannel another) {
	// return (new Short(this.getChannelNum()).compareTo(new Short(
	// another.channelNum)));
	// }

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof TVChannel))
			return false;
		TVChannel ch = (TVChannel) o;
		if (ch == this)
			return true;
		return (rawInfo == ch.rawInfo || (rawInfo != null && rawInfo
				.equals(ch.rawInfo)))
				&& valid == ch.valid
				&& standard == ch.standard
				&& isNew == ch.isNew;
	}

	@Override
	public int hashCode() {
		int result = 47;
		result = 31 * result + (valid ? 1 : 0);
		result = 31 * result + (isNew ? 1 : 0);
		result = 31 * result + (rawInfo != null ? rawInfo.hashCode() : 0);
		return result;
	}

}