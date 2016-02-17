package com.mediatek.tv.service;

import android.graphics.Rect;

import com.mediatek.tv.TVManager;
import com.mediatek.tv.common.TVMException;
import com.mediatek.tv.model.InputExchange;
import com.mediatek.tv.model.InputExchangeOutputMute;
//import com.mediatek.tv.model.InputListener;

public class InputServiceTest {
    @SuppressWarnings("unused")
	private static final String TAG = "[J]InputServiceTest";
    
//    public class AppInputListener implements InputListener {
//        public void notifyInputGotSignal(String input) {
//            Logger.i(TAG, "notifyInputGotSignal");
//        }
//
//        public void notifyOutputOperatorDone(String output) {
//            Logger.i(TAG, "notifyOutputOperatorDone");
//        }
//
//        public void notifyOutputSignalStatus(String output, InputListenerSignalStatus signalStatus ) {
//            Logger.i(TAG, "notifyOutputLossSignal");
//        }
//    }

    private InputService inputService;

    public InputServiceTest() {
        TVManager tvManager = TVManager.getInstance(null);
        this.inputService = (InputService) tvManager.getService(InputService.InputServiceName);

//        AppInputListener appInputListener = new AppInputListener();
//        this.inputService.setInputListener(appInputListener);
    }

    public void testInputOutputList() {
        System.out.println("------------------testInputOutputList------------------");
        {
            int designatedTypeInputsNum;
            String designatedTypeInputs[];
            int i = 0;
            int j = 0;
            int k = 0;
            int inputTypesNum = 7;
            String inputTypesString[] = new String[inputTypesNum];

            inputTypesString[i++] = InputService.INPUT_TYPE_TV;
            inputTypesString[i++] = InputService.INPUT_TYPE_AV;
            inputTypesString[i++] = InputService.INPUT_TYPE_VGA;
            inputTypesString[i++] = InputService.INPUT_TYPE_SVIDEO;
            inputTypesString[i++] = InputService.INPUT_TYPE_COMPONENT;
            inputTypesString[i++] = InputService.INPUT_TYPE_COMPOSITE;
            inputTypesString[i++] = InputService.INPUT_TYPE_HDMI;

            System.out.println("");
            i = 0;
            while (i < inputTypesNum) {
                designatedTypeInputsNum = this.inputService.getDesignatedTypeInputsNum(inputTypesString[i]);
                designatedTypeInputs = this.inputService.getDesignatedTypeInputsString(inputTypesString[i]);

                for (j = 0; j < designatedTypeInputsNum; j++) {
                    String inputName = designatedTypeInputs[j];
                    String inputType = inputService.getTypeFromInputString(designatedTypeInputs[j]);
                    int ID = 0;

                    try {
                        ID = this.inputService.getInputIdByString(designatedTypeInputs[j]);
                    } catch (TVMException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    String inputName2 = this.inputService.getInputStringById(ID);

                    if (!inputName.equals(inputName2)) {
                        System.out.println("name ERROR");
                    }

                    System.out.println(inputType + "--- name: " + inputName2 + ", id: " + ID);
                }

                {
                    int conflictTypeNum = this.inputService.getConflictTypeNum(inputTypesString[i]);
                    String conflictTypes[] = this.inputService.getConflictType(inputTypesString[i]);

                    for (k = 0; k < conflictTypeNum; k++) {
                        System.out.println(inputTypesString[i] + " conflict with " + conflictTypes[k]);
                    }
                }

                System.out.println("");

                i++;
            }
            System.out.println("");
        }

        {
            int outputsNum = this.inputService.getScreenOutputsNum();
            String outputs[] = this.inputService.getScreenOutputs();
            int i;

            System.out.println("");

            for (i = 0; i < outputsNum; i++) {
                System.out.println("output idx: " + i + ",  " + outputs[i]);
            }

            System.out.println("");
        }

        System.out.println("------------------testInputOutputList END------------------\n\n");
    }

    public void testBind() {
        System.out.println("------------------testBind------------------");
        String designatedTypeInputs[] = this.inputService.getDesignatedTypeInputsString(InputService.INPUT_TYPE_VGA);
        String outputs[] = this.inputService.getScreenOutputs();

        this.inputService.bind(outputs[0], designatedTypeInputs[0]);

        String lastInput = this.inputService.getLastInputOfOutput(outputs[0]);
        System.out.println("after bind(VGA), lastInput is : " + lastInput);
        System.out.println("------------------testBind END------------------\n\n");
    }

    public void testRect() {
        System.out.println("------------------testMute------------------");
        String outputs[] = this.inputService.getScreenOutputs();

        Rect r = new Rect();
        r.left = 5000;
        r.right = 10000;
        r.top = 5000;
        r.bottom = 10000;

        this.inputService.setScreenOutputRect(outputs[0], r);
        System.out.println("------------------testMute END------------------\n\n");
    }

    public void testMute( boolean mute ) {
        System.out.println("------------------testMute------------------");
        String outputs[] = this.inputService.getScreenOutputs();
        InputExchangeOutputMute inputExchangeOutputMute = new InputExchangeOutputMute();

        inputExchangeOutputMute.setDoMute(mute);

        this.inputService.setOutputProperty(outputs[0], InputExchange.INPUT_SET_TYPE_OUTPUT_MUTE,
                inputExchangeOutputMute);
        System.out.println("------------------testMute END------------------\n\n");
    }

    public static void testGetInputOutputList() {
        InputServiceTest inputServiceTest = new InputServiceTest();
        inputServiceTest.testInputOutputList();
        inputServiceTest.testBind();
        System.out.println("-------testGetInputOutputList1--------\n\n");
//        inputServiceTest.testMute( true );
        inputServiceTest.testRect();
        inputServiceTest.testMute( false );
        System.out.println("-------testGetInputOutputList2--------\n\n");
    }

    public static void main(String[] args) {
        InputServiceTest.testGetInputOutputList();
    }
}
