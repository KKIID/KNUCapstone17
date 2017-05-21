package kr.ac.bist.ars_iot.Engine;

/**
 * Created by Bist on 2017-05-21.
 */

public class ARSEngine {
    private int step = 0;
    private SpeakerEngine engine = null;
    private int selected;

    public void nextStep() {
        switch(step) {
            case 0:
                engine = new SpeakerEngine();
                engine.execute("비밀번호를 입력해주세요");
                break;
            case 1:
                engine = new SpeakerEngine();
                engine.execute("사용하실 제품을 선택해주세요." + getDevices());
                break;
            case 2:
                engine = new SpeakerEngine();
                engine.execute(getDeviceStatus(selected));
                break;
            case 3:
                engine = new SpeakerEngine();
                engine.execute(String.format("정상 처리되었습니다. 계속하시려면 샵버튼을 눌러주세요"));
                break;
        }
    }
    public String getDeviceStatus(int selected) {
        String name = DeviceEngine.getDevices().get(selected-1).getName();
        String status = (DeviceEngine.getDevices().get(selected-1).getStatus())?"켜진":"꺼진";
        String newStatus = (DeviceEngine.getDevices().get(selected-1).getStatus())?"끄":"켜";
        return String.format("%d번, %s는 현재 %s 상태입니다. 전원을 %s려면 비밀번호와 샵버튼을 입력하세요",selected,name,status,newStatus);
    }
    public void doStep(String string) {
        switch (step) {
            case 0:
                if(passwordVerify(string)) {
                    step++;
                    nextStep();
                }
                break;
            case 1:
                if((selected = menuSelected(string))!=-1) {
                    step++;
                    nextStep();
                }
                break;
            case 2:
                if(doControl(string)) {
                    step++;
                    nextStep();
                }
                break;
            case 3:
                step = 1;
                nextStep();
                break;
        }
    }
    private boolean passwordVerify(String string) {
        engine = new SpeakerEngine();
        if (string.equals("1523")) {
            return true;
        } else {
            engine.execute("잘못 입력하셨습니다. 다시 입력해주세요.");
            return false;
        }
    }
    private int menuSelected(String string) {
        engine = new SpeakerEngine();
        if(DeviceEngine.isInList(Integer.parseInt(string))) {
            return Integer.parseInt(string);
        } else {
            engine.execute("등록되지 않은 번호입니다. 다시 입력해주세요");
            return -1;
        }
    }
    private boolean doControl(String string) {
        if(passwordVerify(string)) {
            DeviceEngine.controlDevices(selected);
            return true;
        } else {
            return false;
        }
    }
    private String getDevices() {
        DeviceEngine.refreshDevices();
        return DeviceEngine.deviceList();
    }
    public void muteEngine() {
        engine.stop_speaker();
    }
}
