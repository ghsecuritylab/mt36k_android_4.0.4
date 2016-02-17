package com.mediatekk.tvcm;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.ConfigType;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.InputExchange;
import com.mediatek.tv.service.InputService;
import com.mediatek.tv.service.InputService.InputServiceListener;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;

import com.mediatekk.tvcm.TVComponent;

public class TVInputManager extends TVComponent {

	private TVManager tvMngr = null;
	InputService inpSrv = null;

	public final static String INPUT_TYPE_TV = InputService.INPUT_TYPE_TV;
	public final static String INPUT_TYPE_AV = InputService.INPUT_TYPE_AV;
	public final static String INPUT_TYPE_SVIDEO = InputService.INPUT_TYPE_SVIDEO;
	public final static String INPUT_TYPE_COMPONENT = InputService.INPUT_TYPE_COMPONENT;
	public final static String INPUT_TYPE_COMPOSITE = InputService.INPUT_TYPE_COMPOSITE;
	public final static String INPUT_TYPE_HDMI = InputService.INPUT_TYPE_HDMI;
	public final static String INPUT_TYPE_VGA = InputService.INPUT_TYPE_VGA;

	public static final String inputTypes[] = {INPUT_TYPE_TV, INPUT_TYPE_AV,
			INPUT_TYPE_SVIDEO, INPUT_TYPE_COMPONENT, INPUT_TYPE_COMPOSITE,
			INPUT_TYPE_HDMI, INPUT_TYPE_VGA};

	protected static final int cfgGrpForType[] = {TVConfigurer.CFG_GRP_ATV,
			TVConfigurer.CFG_GRP_AV, TVConfigurer.CFG_GRP_AV,
			TVConfigurer.CFG_GRP_COMPONENT, TVConfigurer.CFG_GRP_AV,
			TVConfigurer.CFG_GRP_HDMI, TVConfigurer.CFG_GRP_VGA};

	public static final String OUTPUT_NAME_MAIN = "main";
	public static final String OUTPUT_NAME_SUB = "sub";

	// private TVOutput[] allOutputs = null;
	// private TVInput[] allInputs = null;

	private ArrayList<TVOutput> outputSet = new ArrayList<TVOutput>();
	private ArrayList<TVInput> inputSet = new ArrayList<TVInput>();

	public TVOutput[] getOutputs() {
		return outputSet.toArray(new TVOutput[outputSet.size()]);
	}

	public TVInput[] getInputs() {
		return inputSet.toArray(new TVInput[inputSet.size()]);
	}

	public TVInput getInput(String name) {
		for (TVInput each : inputSet) {
			if (each.getName().equals(name)) {
				return each;
			}
		}
		return null;
	}

	public TVOutput getOutput(String name) {
		for (TVOutput each : outputSet) {
			if (each.getName().equals(name)) {
				return each;
			}
		}
		return null;
	}

	class InputListenerDelegate implements InputServiceListener {

		public void notifyInputGotSignal(String arg0) {
			// notifyInputSignal(arg0, true);
		}

		public void notifyOutputOperatorDone(String arg0) {

		}

		public void notifyOutputSignalSatus(String arg0,
				InputListenerSignalSatus arg1) {
		}

		public void notifyInputSignalStatus(String arg0, InputSignalStatus arg1) {
			// TODO Auto-generated method stub
			if (arg1 == InputSignalStatus.SignalStatusLocked) {
				notifyInputSignal(arg0, true);
			} else {
				notifyInputSignal(arg0, false);
			}	 

		}

		public void notifyOutputSignalStatus(String arg0, InputSignalStatus arg1) {
			// TODO Auto-generated method stub
			if (arg1 == InputSignalStatus.SignalStatusLocked) {
				notifyOutputSignalChange(arg0, true);
			} else {
				notifyOutputSignalChange(arg0, false);
			}

		}

		 public void notifyOutputEvent(String arg0, InputListenerEventNotify arg1) {
		 // TODO Auto-generated method stub
		 if (arg1 == InputListenerEventNotify.InputEventUnknown) {
			 notifyOutputEventChange(arg0,
					 InputSourceEventListener.InputEventUnknown);
		 } else if (arg1 == InputListenerEventNotify.InputEventNoSignal) {
			 notifyOutputEventChange(arg0,
					 InputSourceEventListener.InputEventNoSignal);
		 } else if (arg1 == InputListenerEventNotify.InputEventWithSignal) {
			 notifyOutputEventChange(arg0,
					 InputSourceEventListener.InputEventWithSignal);
		 } else if (arg1 == InputListenerEventNotify.InputEventVideoUpdate) {
			 TvInfo("====== InputEventNotify.InputEventVideoUpdate ======");
			 notifyOutputEventChange(arg0,
					 InputSourceEventListener.InputEventVideoUpdate);
		 }
			
	 }
	

	}

	int getCfgGrpIdxForType(String type) {
		if (type == null) {
			return 0;
		}
		for (int i = 0; i < inputTypes.length; i++) {
			if (type.equals(inputTypes[i])) {
				return cfgGrpForType[i];
			}
		}
		return 0;
	}

	TVInputManager(Context context) {
		super(context);
		if (!dummyMode) {
			tvMngr = getTVMngr();
			inpSrv = (InputService) tvMngr
					.getService(InputService.InputServiceName);

			if (inpSrv != null) {
				int inputNum;
				String inputNames[];
				for (int i = 0; i < inputTypes.length; i++) {
					inputNum = inpSrv.getDesignatedTypeInputsNum(inputTypes[i]);
					inputNames = inpSrv
							.getDesignatedTypeInputsString(inputTypes[i]);
					for (int j = 0; j < inputNum; j++) {
						inputSet.add(new TVInput(this, inputTypes[i],
								inputNames[j]));
					}
				}
				int outputNum = inpSrv.getScreenOutputsNum();
				String[] outputNames = inpSrv.getScreenOutputs();
				for (int i = 0; i < outputNum; i++) {
					outputSet.add(new TVOutput(this, outputNames[i]));
				}
				inpSrv.setInputListener(new InputListenerDelegate());
			} else {
				System.out
						.printf("====== Error: inputService is null. ======\n");
			}
		} else {

			String tmpInputs[] = {"tv", "av", "svideo", "component",
					"composite", "hdmi", "vga"};

			Vector<TVInput> inputs = new Vector<TVInput>();

			for (int i = 0; i < tmpInputs.length; i++) {
				inputSet.add(new TVInput(this, inputTypes[i], tmpInputs[i]));
			}
			outputSet.add(new TVOutput(this, "main"));
			outputSet.add(new TVOutput(this, "sub"));
		}
		setDefaultOutput("main");
		enterOutputMode(OUTPUT_MODE_NORMAL);
	}

	void init() {

		TVInput[] inputs = getInputs();
		for (TVInput eachInp : inputs) {
			if (eachInp.getType().equals(TVInputManager.INPUT_TYPE_TV)) {
				deleteInput(eachInp);

				registerInput(new TVInput(this, TVInputManager.INPUT_TYPE_TV,
						"dtv", new TVInputContextBase(getContent(),
								TVChannelManager.LIST_DVB_NAME, eachInp
										.getHWName(), TVConfigurer.CFG_GRP_DTV)));

				registerInput(new TVInput(this, TVInputManager.INPUT_TYPE_TV,
						"atv", new TVInputContextBase(getContent(),
								TVChannelManager.LIST_PAL_NAME, eachInp
										.getHWName(), TVConfigurer.CFG_GRP_ATV)));

			}

		}

		load();

	}

	void load() {
		for (TVOutput each : outputSet) {
			each.loadConnect();
		}
	}

	private TVOutput defaultOutput;

	/**
	 * Set output to default j
	 * 
	 * @param output
	 */
	public void setDefaultOutput(TVOutput output) {
		if (output != null) {
			if (output.setDefault()) {
				defaultOutput = output;
			}
		}
	}

	public void setDefaultOutput(String name) {
		setDefaultOutput(getOutput(name));
	}

	public TVOutput getDefaultOutput() {
		return defaultOutput;
	}

	/**
	 * Get current input source of the specified output path.
	 * 
	 * @param
	 */
	public String getCurrInputSource(String outputName) {
		TVOutput output = getOutput(outputName);
		if (output != null && output.getInput() != null) {
			return output.getInput().getName();
		}
		return null;
	}

	/**
	 * Get current input source of the specified output path.
	 * 
	 * @param
	 */
	public String getCurrInputSource() {
		return getCurrInputSource(defaultOutput.name);
	}

	/**
	 * Get the number of all output path, it always equal to 2.
	 * 
	 * @param
	 */
	public int getOutputNum() {
		return outputSet.size();
	}

	/**
	 * Get all output path list, it always equal to {"main", "sub"}.
	 * 
	 * @param
	 */
	public String[] getOutputArray() {
		Vector<String> outputNames = new Vector<String>();
		// for (int i = 0; i < allOutputs.length; i++) {
		// outputNames.add(allOutputs[i].name);
		// }
		for (TVOutput each : outputSet) {
			outputNames.add(each.getName());
		}
		return (String[]) outputNames.toArray(new String[outputNames.size()]);
	}

	/**
	 * Get the number of all input source.
	 * 
	 * @param
	 */
	public int getInputSourceNum() {
		return inputSet.size();
	}

	/**
	 * Get all input source list.
	 * 
	 * @param
	 */
	public String[] getInputSourceArray() {
		Vector<String> inputNames = new Vector<String>();
		// for (int i = 0; i < allInputs.length; i++) {
		// inputNames.add(allInputs[i].name);
		// }
		for (TVInput each : inputSet) {
			inputNames.add(each.getName());
		}
		return (String[]) inputNames.toArray(new String[inputNames.size()]);
		// return allInputsIs;
	}

	public void registerInput(TVInput input) {
		inputSet.add(0, input);
	}

	public void registerInput(int idx, TVInput input) {
		inputSet.add(idx, input);
	}

	public void deleteInput(TVInput input) {
		inputSet.remove(input);
	}

	/**
	 * Get input type of the input source.
	 * 
	 * @param the
	 *            input source
	 */
	public String getTypeFromInputSource(String inputSourceName) {
		if (!dummyMode) {
			// if (inpSrv != null) {
			// // InputService can handle this...
			//				
			// return inpSrv.getTypeFromInputString(inputSourceName);
			// }
			TVInput input = getInput(inputSourceName);
			if (input != null) {
				return input.getType();
			}
			return null;
		} else {

			return inputSourceName;
		}
	}

	/**
	 * Change the specified output path to the specified input source. *
	 * 
	 * @param the
	 *            output path and the input source
	 */
	public void changeInputSource(String outputName, String inputName) {
		TvLog("Change input from " + outputName + " inp " + inputName);
		TVOutput output = getOutput(outputName);
		TVInput input = getInput(inputName);
		if (input != null) {
			input.connect(output);
		} else {
			output.connect(null);
			TvLog("Cannot find input");
		}
	}

	public void changeInputSource(String inputName) {
		changeInputSource(defaultOutput.getName(), inputName);
	}

	/**
	 * Change the specified output to the next input source.
	 * 
	 * @deprecated
	 * @param the
	 *            PIP/POP is on/of
	 */
	public void changeNextInputSource(String outputName, boolean isPIPorPOP) {
		changeNextInputSource(outputName);

	}

	/**
	 * Change the specified output to the next input source.
	 */
	public void changeNextInputSource(String outputName) {

		TVOutput output = getOutput(outputName);
		TVInput input = output.getInput();
		TVInput nextInput = null;

		int i, j;
		for (i = 0; i < inputSet.size(); i++) {
			if (inputSet.get(i) == input) {
				for (j = i + 1; j < inputSet.size(); j++) {
					nextInput = inputSet.get(j);
					if (output.canConnect(nextInput)) {
						nextInput.connect(output);
						return;
					}
				}
				// Round
				for (j = 0; j < i; j++) {
					nextInput = inputSet.get(j);
					if (output.canConnect(nextInput)) {
						nextInput.connect(output);
						return;
					}
				}
			}
		}
	}

	public void changeNextInputSource() {
		changeNextInputSource(defaultOutput.getName());
	}

	/**
	 * Swap the main output and the sub output.
	 * 
	 * @param
	 */
	public void swapInputSource() {
		if (!dummyMode) {
			if (inpSrv != null) {
				if (outputSet.size() >= 2 && outputSet.get(0) != null
						&& outputSet.get(1) != null) {
					outputSet.get(0).swap(outputSet.get(1));
				}

			}
		} else {
			TVInput tmp = outputSet.get(0).input;
			outputSet.get(0).input = outputSet.get(1).input;
			outputSet.get(1).input = tmp;
		}
	}

	/**
	 * Get the number of conflict type of the specified input type.
	 * 
	 * @deprecated
	 * @param the
	 *            specified input type
	 */
	public int getInputSourceConflictTypeNum(String inputType) {
		if (!dummyMode) {
			if (inpSrv != null) {
				return inpSrv.getConflictTypeNum(inputType);
			}
		}
		return 0;
	}

	/**
	 * Get the conflict type list of the specified input type.
	 * 
	 * @deprecated
	 * @param the
	 *            specified input type
	 */
	public String[] getInputSourceConflictTypeArray(String inputType) {
		if (!dummyMode) {
			if (inpSrv != null) {
				return inpSrv.getConflictType(inputType);
			}
		}
		return null;
	}

	/**
	 * Set some property when change the input source.
	 * 
	 * @deprecated
	 * @param
	 */
	public void setOutputProperty(String output, int setType,
			InputExchange inputExchange) {

	}

	/**
	 * Set main/sub output rectangle when PIP/POP on.
	 * 
	 * @deprecated
	 * @param
	 */
	public void setScreenOutputRect(String output, RectF rect) {
		if (!dummyMode) {
			if (inpSrv != null) {
				Rect r = new Rect((int) (rect.left * 10000.0f),
						(int) (rect.top * 10000.0f),
						(int) (rect.right * 10000.0f),
						(int) (rect.bottom * 10000.0f));
				inpSrv.setScreenOutputRect(output, r);
			}
		}
	}

	/**
	 * Freeze main or sub output.
	 * 
	 * @deprecated
	 * @param the
	 *            output path and the freeze on/off
	 */
	public void setFreeze(String outputName, boolean isFreeze) {
		TVOutput output = getOutput(outputName);
		if (output != null) {
			output.setFreeze(isFreeze);
		}
	}

	/**
	 * Freeze main or sub output.
	 * 
	 * @deprecated
	 * @param the
	 *            output path and the freeze on/off
	 */
	public void setFreeze(boolean isFreeze) {
		setFreeze(defaultOutput.getName(), isFreeze);
	}

	// String prfxInpBlock = "inp_block_";
	public boolean isBlock(String inputName) {
		TVInput input = getInput(inputName);
		if (input == null) {
			TvLog("Try to find input " + inputName);
			return false;
		}
		return input.isBlocked();
	}

	public void block(String inputName, boolean block) {
		TVInput input = getInput(inputName);
		if (input == null) {
			TvLog("Try to set input " + inputName);
			return;
		}
		input.setBlocked(block);
		if (getCurrInputSource().equals(inputName)) {
		getContent().sendUpdate();
	}
	}

	public static final int OUTPUT_MODE_PIP = 1;
	public static final int OUTPUT_MODE_POP = 2;
	public static final int OUTPUT_MODE_NORMAL = 0;

	// int curOutputMode = OUTPUT_MODE_NORMAL;

	public TVOutput enterOutputMode(int outputMode) {
		String outputName = "main";
		TvLog("Try to enter output mode " + outputMode);
		int mode = getOuputMode();
		try {
			if (!dummyMode) {
				// TODO :
				// TVManager requires that we must not enter a mode
				// which equals current mode.. However, currently
				// there is no method to get Current mode.
				if (mode != outputMode) {
					switch (outputMode) {
						case OUTPUT_MODE_PIP :
							outputName = inpSrv
									.enterPIPAndReturnCurrentFocusOutput();
							outputName = "sub";
							break;
						case OUTPUT_MODE_POP :
							outputName = inpSrv
									.enterPOPAndReturnCurrentFocusOutput();
							break;
						case OUTPUT_MODE_NORMAL :
							outputName = inpSrv
									.enterNormalAndReturnCurrentOutput();
							outputName = "main";
							break;
						default :
							throw new IllegalArgumentException();
					}
					TVOutput output = getOutput(outputName);
					if (output != null) {
						this.defaultOutput = output;
						TvLog("Success to enter output mode " + outputMode);
					} else {
						TvLog("TVManager returns bad output " + outputName);
					}
				}
			}

		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return defaultOutput;
	}

	public int getOuputMode() {
		if (!dummyMode) {
			InputService.InputState rawState = inpSrv.getCurrentState();
			switch (rawState) {
				case INPUT_STATE_PIP :
					return OUTPUT_MODE_PIP;
				case INPUT_STATE_POP :
					return OUTPUT_MODE_POP;
				case INPUT_STATE_NORMAL :
					return OUTPUT_MODE_NORMAL;
				default :
					// WHat happend?
					return OUTPUT_MODE_NORMAL;
			}
		} else {
			return OUTPUT_MODE_NORMAL;
		}
	}

	protected Vector<InputSourceListener> listeners = new Vector<InputSourceListener>();
	protected Vector<InputSourceEventListener> eventListeners = new Vector<InputSourceEventListener>();

	public static interface InputSourceListener {
		public void onSelected(String output, String input);

		public void onOutputSignalChange(String output, boolean hasSignal);

		public void onInputSignalChange(String input, boolean hasSignal);

		/**
		 * @deprecated
		 * @param input
		 */
		public void onInputGotSignal(String input);

		public void onBlocked(String input);
	}

	public static interface InputSourceEventListener {
		final static int STATE_UNKNOW = 0;
		final static int InputEventUnknown = 1;
		final static int InputEventNoSignal = 2;
		final static int InputEventWithSignal = 3;
		final static int InputEventVideoUpdate = 4;

		public void onOutputEventChange(String output, int eventType);
	}

	public synchronized void registerSourceListener(InputSourceListener listener) {
		listeners.add(listener);
	}

	public synchronized void removeSourceListener(InputSourceListener listener) {
		listeners.remove(listener);
	}
	
	public synchronized void registerSourceEventListener(InputSourceEventListener listener) {
		eventListeners.add(listener);
	}

	public synchronized void removeSourceEventListener(InputSourceEventListener listener) {
		eventListeners.remove(listener);
	}

	protected void notifyInputSignal(final String inputName,
			final boolean signal) {
		getHandler().post(new Runnable() {
			public void run() {
				Enumeration<InputSourceListener> e = listeners.elements();
				InputSourceListener item;
				while (e.hasMoreElements()) {
					item = e.nextElement();
					item.onInputSignalChange(inputName, signal);
				}
			}
		});
	}

	protected void notifyInputBlocked(final String inputName) {
		getHandler().post(new Runnable() {
			public void run() {
				Enumeration<InputSourceListener> e = listeners.elements();
				InputSourceListener item;
				while (e.hasMoreElements()) {
					item = e.nextElement();
					item.onBlocked(inputName);
				}
			}
		});
	}

	protected void notifyInputSelect(final String outputName, final String input) {
		getHandler().post(new Runnable() {
			public void run() {
				Enumeration<InputSourceListener> e = listeners.elements();
				InputSourceListener item;
				while (e.hasMoreElements()) {
					item = e.nextElement();
					item.onSelected(outputName, input);
				}
			}
		});
	}

	// Hashtable<String, Boolean> signalStat = new Hashtable<String, Boolean>();
	/**
	 * Record the output has signal
	 * 
	 * @param output
	 * @return
	 */
	public boolean outputHasSignal(String outputName) {
		TVOutput output = getOutput(outputName);
		return output.hasSignal();
	}

	/**
	 * Record the output has signal
	 * 
	 * @param output
	 * @return
	 */
	public boolean outputHasSignal() {
		return outputHasSignal(defaultOutput.getName());
	}

	protected void notifyOutputSignalChange(final String outputName,
			final boolean hasSignal) {
		getHandler().post(new Runnable() {
			public void run() {
				// signalStat.put(output, hasSignal);
				TVOutput output = getOutput(outputName);
				output.signal = hasSignal;
				Enumeration<InputSourceListener> e = listeners.elements();
				InputSourceListener item;
				while (e.hasMoreElements()) {
					item = e.nextElement();
					item.onOutputSignalChange(outputName, hasSignal);
				}
			}
		});
	}

	public void stopDesignateOutput(String outPutName, boolean needSync){
		try {
			if(inpSrv != null){
			inpSrv.stopDesignateOutput(outPutName, needSync);
				}
		} catch (TVMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void notifyOutputEventChange(final String outputName,
			final int eventType) {
		getHandler().post(new Runnable() {
			public void run() {
				Enumeration<InputSourceEventListener> e = eventListeners
						.elements();
				InputSourceEventListener item;
				while (e.hasMoreElements()) {
					item = e.nextElement();
					item.onOutputEventChange(outputName, eventType);
				}
			}
		});
	}

}
