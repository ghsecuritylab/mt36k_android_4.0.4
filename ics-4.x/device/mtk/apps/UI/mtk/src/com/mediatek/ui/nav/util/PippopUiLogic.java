package com.mediatek.ui.nav.util;

import java.util.Vector;

import android.content.Context;
import android.graphics.RectF;

import com.mediatek.tvcm.TVContent;
import com.mediatek.tvcm.TVInput;
import com.mediatek.tvcm.TVInputManager;
import com.mediatek.tvcm.TVOutput;

/**
 * this class use to abstract logic from UI.
 * 
 * @author MTK40530
 * 
 */
public class PippopUiLogic {
	private static PippopUiLogic instance;
	private TVContent mTvContent;
	private TVInputManager mTvInputManager;
	private TVOutput mTvOutput;

	public static final int MODE_NORMAL = 0;
	public static final int MODE_PIP = 1;
	public static final int MODE_POP = 2;

	public static final int PIP_POS_ZERO = 0;
	public static final int PIP_POS_ONE = 1;
	public static final int PIP_POS_TWO = 2;
	public static final int PIP_POS_THREE = 3;
	public static final int PIP_POS_FOUR = 4;
	public static int currentpos = PIP_POS_ZERO;

	public static final int PIP_SIZE_SMALL = 0;
	public static final int PIP_SIZE_MIDDLE = 1;
	public static final int PIP_SIZE_LARGE = 2;
	public static int currentsize = PIP_SIZE_SMALL;

	private float x, y, w, h;

	private PippopUiLogic(Context context) {
		mTvContent = TVContent.getInstance(context);
		mTvInputManager = mTvContent.getInputManager();
	}

	public static synchronized PippopUiLogic getInstance(Context context) {
		if (instance == null) {
			instance = new PippopUiLogic(context);
		}
		return instance;
	}

	/**
	 * get current mode of PIP/POP, MODE_NORMAL = 0, MODE_PIP = 1, MODE_POP = 2.
	 * 
	 * @return 0/1/2
	 */
	public int getCurrentMode() {
		return mTvInputManager.getOuputMode();
	}

	/**
	 * get output(main/sub) that got focus.
	 * 
	 * @return
	 */
	public TVOutput getCurrentFocusOutput() {
		return mTvInputManager.getDefaultOutput();
	}

	/**
	 * get name of output(main/sub) that got focus.
	 * 
	 * @return
	 */
	public String getCurrentFocusOutputName() {
		return getCurrentFocusOutput().getName();
	}

	public int getCurrentPipPosition() {
		return currentpos;
	}

	public int getCurrentPipSize() {
		return currentsize;
	}

	/**
	 * set focus on the other output
	 */
	public void setOtherOutputFocus(OutputChangeListener mListener) {
		if (mTvInputManager.getDefaultOutput().getName().equals("main")) {
			mTvInputManager.setDefaultOutput("sub");
		} else {
			mTvInputManager.setDefaultOutput("main");
		}

		if (mListener != null) {
			mTvOutput = mTvInputManager.getDefaultOutput();
			if (mTvOutput != null) {
				mListener.changedInfo(mTvOutput.getName(), getCurrentMode(),
						mTvOutput.getScreenRectangle());
			}
		}
	}

	/**
	 * change next output mode. MODE_NORMAL = 0, MODE_PIP = 1, MODE_POP = 2.
	 */
	public void changeOutputMode(OutputChangeListener mListener) {
		int currentMode = mTvInputManager.getOuputMode();

		currentMode++;
		if (currentMode > 2) {
			currentMode = 0;
		}

		mTvInputManager.enterOutputMode(currentMode);

		if (mListener != null) {
			mTvOutput = mTvInputManager.getDefaultOutput();
			if (mTvOutput != null) {
				mListener.changedInfo(mTvOutput.getName(), currentMode,
						mTvOutput.getScreenRectangle());
			}
		}
	}

	/**
	 * swap input source between main output and sub output.
	 */
	/*public void swapInputSource(OutputChangeListener mListener) {
		mTvInputManager.swapInputSource();

		if (mListener != null) {
			mTvOutput = mTvInputManager.getDefaultOutput();
			if (mTvOutput != null) {
				mListener.changedInfo(mTvOutput.getName(), getCurrentMode(),
						mTvOutput.getScreenRectangle());
			}
		}
	}*/

	/**
	 * get the position of output screen which has focus
	 * 
	 * @return
	 */
	public RectF getPositonOfOutput() {
		mTvOutput = mTvInputManager.getDefaultOutput();
		if (mTvOutput != null) {
			return mTvOutput.getScreenRectangle();
		} else {
			return null;
		}
	}

	public String[] getInputSourceList() {
		return mTvInputManager.getInputSourceArray();
	}

	public boolean isConflictWithTheInput(String input) {
//		TVInput mTvInput = null;
//		mTvInput = mTvInputManager.getInput(input);
//		mTvOutput = mTvInputManager.getDefaultOutput();

		return  true;//(!mTvOutput.canConnect(mTvInput));
	}

	/**
	 * @deprecated
	 * @return
	 */
	public String[] getConnectableInputsourceList() {
//		Vector<String> inputSouceName = new Vector<String>();
//		TVInput[] mTvInput = null;
//
//		mTvOutput = mTvInputManager.getDefaultOutput();
//		if(null != mTvOutput){
//		 mTvInput = mTvOutput.getConnectableInputs();
//		}
//		if(null != mTvInput){
//		for (int i = 0; i < mTvInput.length; i++) {
//			inputSouceName.add(mTvInput[i].getName());
//		 }
//		}
//		return (String[]) inputSouceName.toArray(new String[inputSouceName
//				.size()]);
		
		return null;
	}

	

	public void changeNextPipSize(OutputChangeListener mListener) {
		if (getCurrentMode() == MODE_PIP) {
			currentsize++;
			if (currentsize > PIP_SIZE_LARGE) {
				currentsize = PIP_SIZE_SMALL;
			}
			
			setScreenPos();
			mTvContent.setScreenPosition("sub", x, y, w, h);

			if (mListener != null) {
				mTvOutput = mTvInputManager.getDefaultOutput();
				if (mTvOutput != null) {
					mListener.changedInfo(mTvOutput.getName(),
							getCurrentMode(), mTvOutput.getScreenRectangle());
				}
			}
		}
	}


	public void changeNextPipPosition(OutputChangeListener mListener) {
		if (getCurrentMode() == MODE_PIP) {
			currentpos++;
			if (currentpos > PIP_POS_FOUR) {
				currentpos = PIP_POS_ZERO;
			}
			setScreenPos();
			mTvContent.setScreenPosition("sub", x, y, w, h);

			if (mListener != null) {
				mTvOutput = mTvInputManager.getDefaultOutput();
				if (mTvOutput != null) {
					mListener.changedInfo(mTvOutput.getName(),
							getCurrentMode(), mTvOutput.getScreenRectangle());
				}
			}
		}
	}

	public interface OutputChangeListener {
		public void changedInfo(String focusOutput, int mode, RectF mRecf);
	}
	
	private void setScreenPos() {
		switch (currentpos) {
		case PIP_POS_ZERO:
			if (currentsize == PIP_SIZE_SMALL) {
				x = 0.1f;
				y = 0.7f;
				w = 0.302f;
				h = 0.9f;
			} else if (currentsize == PIP_SIZE_MIDDLE) {
				x = 0.1f;
				y = 0.65f;
				w = 0.352f;
				h = 0.9f;
			} else {
				x = 0.1f;
				y = 0.5f;
				w = 0.502f;
				h = 0.9f;
			}
			break;

		case PIP_POS_ONE:
			if (currentsize == PIP_SIZE_SMALL) {
				x = 0.7f;
				y = 0.7f;
				w = 0.902f;
				h = 0.9f;
			} else if (currentsize == PIP_SIZE_MIDDLE) {
				x = 0.65f;
				y = 0.65f;
				w = 0.902f;
				h = 0.9f;
			} else {
				x = 0.5f;
				y = 0.5f;
				w = 0.902f;
				h = 0.9f;
			}
			break;

		case PIP_POS_TWO:
			if (currentsize == PIP_SIZE_SMALL) {
				x = 0.4f;
				y = 0.4f;
				w = 0.602f;
				h = 0.6f;
			} else if (currentsize == PIP_SIZE_MIDDLE) {
				x = 0.35f;
				y = 0.35f;
				w = 0.652f;
				h = 0.65f;
			} else {
				x = 0.3f;
				y = 0.3f;
				w = 0.702f;
				h = 0.7f;
			}
			break;

		case PIP_POS_THREE:
			if (currentsize == PIP_SIZE_SMALL) {
				x = 0.7f;
				y = 0.1f;
				w = 0.902f;
				h = 0.3f;
			} else if (currentsize == PIP_SIZE_MIDDLE) {
				x = 0.65f;
				y = 0.1f;
				w = 0.902f;
				h = 0.35f;
			} else {
				x = 0.5f;
				y = 0.1f;
				w = 0.902f;
				h = 0.5f;
			}
			break;

		case PIP_POS_FOUR:
			if (currentsize == PIP_SIZE_SMALL) {
				x = 0.1f;
				y = 0.1f;
				w = 0.302f;
				h = 0.3f;
			} else if (currentsize == PIP_SIZE_MIDDLE) {
				x = 0.1f;
				y = 0.1f;
				w = 0.352f;
				h = 0.35f;
			} else {
				x = 0.1f;
				y = 0.1f;
				w = 0.502f;
				h = 0.5f;
			}
			break;

		default:
			break;
		}
	}
}