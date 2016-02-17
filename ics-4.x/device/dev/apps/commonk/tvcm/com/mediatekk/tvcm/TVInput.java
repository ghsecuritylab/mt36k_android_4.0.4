/**
 * 
 */
package com.mediatekk.tvcm;

import java.util.Vector;

import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.service.InputService;
import com.mediatek.tv.service.InputService.InputServiceListener.InputSignalStatus;

public class TVInput {
	TVInputManager im;
	static String prfxInpBlock = "inp_block_";
	String type;
	String name;
	boolean blocked;

	private TVInputContext inpCtx;
		/**
	 * Create input with name equaling hwname, the context will be simply for hw
	 * input setting.
	 * 
	 * @param im
	 * @param type
	 * @param name
	 */
	TVInput(final TVInputManager im, String type, String name) {
		this(im, type, name, new TVInputContext() {
			public String getHWName(TVInput input) {
				return input.getName();
			}

			public void onConnected(TVInput input, TVOutput output) {
				// NULL should not happen.
			//	if (input != null) {
					TVConfigurer cfger = input.getInputManager().getContent()
							.getConfigurer();
					if (output.getHWName().equals(
							TVInputManager.OUTPUT_NAME_MAIN)) {
						cfger.setGroup(input.getInputManager()
								.getCfgGrpIdxForType(input.getType()));
					}
				//}
			}

			public void onDisconnected(TVInput input, TVOutput output) {

			}

			public int getCfgGrpIdx(TVInput input) {
				if (input != null) {
					return im.getCfgGrpIdxForType(input.getType());
				}
				return TVConfigurer.CFG_GRP_ATV;
			}
		});
	}
	/**
	 * 
	 * @param im
	 * @param type
	 * @param name
	 * @param inpCtx
	 */
	TVInput(TVInputManager im, String type, String name, TVInputContext inpCtx) {
		this.im = im;
		this.type = type;
		this.name = name;
		TVStorage st = im.getContent().getStorage();
		blocked = new Boolean(st.get(prfxInpBlock + name));
		// Use default as input context;
		this.inpCtx = inpCtx;
	}
	public String getName() {
		return name;
	}
	public String getHWName() {
		return inpCtx.getHWName(this);
	}

	public TVInputContext getInpCtx() {
		return inpCtx;
	}
	public String getType() {
		return type;
	}
	public boolean conflictWith(String inputType) {
		if (im.inpSrv != null) {
			// Don't kown whether this is dynamically.
			int len = im.inpSrv.getConflictTypeNum(inputType);
			String[] confcs = im.inpSrv.getConflictType(inputType);
			for (int i = 0; i < len; i++) {
				if (type.equals(confcs[i])) {
					return true;
				}
			}
			return false;
		}
		return false;
	}
	public boolean conflictWith(TVInput other) {
		return conflictWith(other.getType());
	}

	public TVInput[] getConflictInputs() {
		if (im.inpSrv != null) {
			// Don't kown whether this is dynamically.
			String[] confcs = im.inpSrv.getConflictType(type);
			if (confcs == null) {
				// This should not happen;
				return null;
			}
			Vector<TVInput> inputs = new Vector<TVInput>(3);
			TVInput[] allInputs = im.getInputs();
			for (int i = 0; i < allInputs.length; i++) {
				for (int j = 0; j < confcs.length; j++) {
					if (allInputs[i].getType().equals(confcs[j])) {
						inputs.add(allInputs[i]);
						break;
					}
				}
			}
			return inputs.toArray(new TVInput[inputs.size()]);
		} else {
			return null;
		}
	}

	public boolean hasSignal() {
		InputSignalStatus stat;
		try {
			stat = im.inpSrv.getInputSignalStatus(getHWName());
			if (stat == InputSignalStatus.SignalStatusLocked) {
				return true;
			} else {
				return false;
			}
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
		TVStorage st = im.getContent().getStorage();
		st.set(prfxInpBlock + name, new Boolean(blocked).toString());
		st.flush();
	}

	protected boolean usrUnblock = false;
	public void usrUnblock() {
//		if (blocked) {
			this.usrUnblock = true;
//		}
	}

	public boolean isUsrUnblocked() {
		return usrUnblock;
	}
	public void connect(TVOutput output) {
		if (output != null) {
			output.connect(this);
		} else {
			TVComponent.TvLog("Try to access NULL output ");
		}
	}

	public TVInputManager getInputManager() {
		return im;
	}

}