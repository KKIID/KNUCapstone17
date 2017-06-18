package kr.ac.knu.bist.wheather_parse.Data;

/**
 * Created by BIST120 on 2017-05-28.
 */

public class moduleItem {
    private int moduleIcon;
    private String moudleName;
    public moduleItem(int moduleIcon, String moduleName){
        this.moduleIcon = moduleIcon;
        this.moudleName = moduleName;
    }

    public int getModuleIcon() {
        return moduleIcon;
    }
    public String getMoudleName() {
        return moudleName;
    }
    public void setModuleIcon(int moduleIcon) {
        this.moduleIcon = moduleIcon;
    }
    public void setMoudleName(String moudleName) {
        this.moudleName = moudleName;
    }

}
