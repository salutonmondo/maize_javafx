/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wf;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import wf.bean.Reflection;
import wf.database.Pinyin;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wadeshu
 */
public class RegisterInfo extends HBox {

    Wf wf;
    List<String> typeList, typeList1;
    @FXML
    Label labelName;
    @FXML
    TextField tfValue;
    @FXML
    ImageView asterisk;
    String name;
    @FXML
    HBox sexHbox;
    @FXML
    ToggleGroup groupSex;

    @FXML
    RadioButton sexMale;
    @FXML
    RadioButton sexFemale;
    @FXML
    ChoiceBox choiceBoxType;

    @FXML
    HBox meetingWrapper;

    @FXML
    CheckBox meeting1;

    @FXML
    CheckBox meeting2;

    @FXML
    CheckBox meeting3;

    public boolean necessary;

    int mode;
    public static String[] typs = new String[]{"普通代表", "免费"};
    public static String[] typs1 = new String[]{"现场新增"};
    Reflection anno;
    public RegisterInfo(String name, boolean necessary, HashMap<String, RegisterInfo> registerInfoMap, int currentMode, Wf wf,Reflection anno) {
        System.out.println("____"+name);
        this.anno = anno;
        this.wf = wf;
        this.necessary = necessary;
        this.mode = currentMode;
        String[] identityType = new String[]{"身份证", "军官证", "护照", "MTPS/台胞证"};
        String[] jobTitleType = new String[]{"", "无职称", "心理咨询师", "医师", "主治医师", "副主任医师", "主任医师", "护士", "护师", "主管护师", "副主任护师", "主任护师"};
        String[] bacholerType = new String[]{"", "大专", "本科", "硕士", "博士"};
        typeList = Arrays.<String>asList(typs);
        typeList1 = Arrays.asList(typs1);
        this.name = name;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("itemInfo.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        labelName.setText(name);
        if ("性别".equals(name)) {
            tfValue.setVisible(false);
            sexHbox.setVisible(true);
        } else if ("报名类型".equals(name)) {
            choiceBoxType.setVisible(true);
            choiceBoxType.getSelectionModel().selectFirst();
            tfValue.setVisible(false);
//            if(currentMode==0){
//                choiceBoxType.getItems().addAll(typeList1);
//                choiceBoxType.getSelectionModel().select("现场新增");
//            }else{
//                choiceBoxType.getItems().addAll(typeList);
//                choiceBoxType.getSelectionModel().select("");
//            }
        } else if ("证件类型".equals(name)) {
            choiceBoxType.setVisible(true);
            choiceBoxType.getItems().addAll(identityType);
            choiceBoxType.getSelectionModel().selectFirst();
            tfValue.setVisible(false);
        } else if ("职称".equals(name)) {
            choiceBoxType.setVisible(true);
            choiceBoxType.getItems().addAll(jobTitleType);
            choiceBoxType.getSelectionModel().selectFirst();
            tfValue.setVisible(false);
        } else if ("学历".equals(name)) {
            choiceBoxType.setVisible(true);
            choiceBoxType.getItems().addAll(bacholerType);
            choiceBoxType.getSelectionModel().selectFirst();
            tfValue.setVisible(false);
        } else if ("是否付费".equals(name)) {
            sexMale.setText("是");
            sexFemale.setText("否");
            tfValue.setVisible(false);
            sexHbox.setVisible(true);
            sexFemale.setSelected(true);
            RegisterInfo registerInfo = registerInfoMap.get("报名类型");
            registerInfo.choiceBoxType.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("报名类型："+newValue);
                if(newValue.intValue()==1){
                    sexMale.setSelected(true);
                }
            });
        } else if ("全拼或英文名".equals(name)) {
            TextField nameTf = registerInfoMap.get("姓名").tfValue;
            nameTf.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    String nameString = nameTf.getText();
                    System.out.println("ppppppp"+nameString);
                    if("".equals(nameString)){
                          tfValue.setText("");
                          return;
                    }
                    String pinyin = Pinyin.getNamePinyin(nameString);
                    tfValue.setText(pinyin);
                }
            });
        } else if ("注册费用".equals(name)) {
            tfValue.setText("1800");
            if (currentMode == 0) {
                tfValue.setEditable(false);
            }
            RegisterInfo registerInfo = registerInfoMap.get("报名类型");
            RegisterInfo registerMeeting = registerInfoMap.get("会议报名");

            registerInfo.choiceBoxType.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                tfValue.setText(caculateMoney(registerInfo.choiceBoxType.getItems().size() == 1, newValue, null));
            });
//            ChangeListener<Boolean> lis = (ov, old_val, new_val) -> tfValue.setText(
//                    caculateMoney(registerInfo.choiceBoxType.getItems().size() == 1, registerInfo.choiceBoxType.getSelectionModel().getSelectedIndex(), registerMeeting.getValue()));
//            registerMeeting.meeting1.selectedProperty().addListener(lis);
//            registerMeeting.meeting2.selectedProperty().addListener(lis);
//            registerMeeting.meeting3.selectedProperty().addListener(lis);
        } else if ("会议报名".equals(name)) {
            tfValue.setVisible(false);
            meetingWrapper.setVisible(true);
        } else if ("是否需要学分".equals(name)) {
            sexMale.setText("是");
            sexFemale.setText("否");
            tfValue.setVisible(false);
            sexHbox.setVisible(true);
            if (wf.needPoint) {
                sexMale.setSelected(true);
            } else {
                sexFemale.setSelected(true);
            }
            ChangeListener<Boolean> lis = (ov, old_val, new_val) -> hideFields(new_val, registerInfoMap);
            sexMale.selectedProperty().addListener(lis);
        }
        if (!necessary) {
            asterisk.setVisible(false);
        }
        labelName.setText("".equals(anno.displayName())?name:anno.displayName());
    }

    private void hideFields(boolean need, HashMap<String, RegisterInfo> registerInfoMap) {
        wf.showHideFields(need);
        wf.needPoint = need;
//        wf.gpInfo.getChildren().clear();
//        wf.loadRegistrationInfo(false);
    }

    private String caculateMoney(boolean mode0, Number registerType, String registeredMeetings) {
        System.out.println("mmmmm:" + registerType.intValue());
        if (0==registerType.intValue()) {
            return "1800";
        } else {
            return "0";
        }
    }

    public String[] getFieldAndValue() {
        String[] fv = new String[2];
        fv[0] = name;
        fv[1] = tfValue.getText();
        return fv;
    }

    public String getValue() {
        String value = "null";
        if ("性别".equals(name)) {
            value = ((RadioButton) groupSex.getSelectedToggle()).getText();
        } else if ("报名类型".equals(name) || "证件类型".equals(name) || "职称".equals(name) || "学历".equals(name)) {
            Object obj = choiceBoxType.getSelectionModel().getSelectedItem();
            if (obj == null) {
                obj = new String("");
            }
            value = obj.toString();
        } else if ("是否付费".equals(name) || "是否需要学分".equals(name)) {
            value = ((RadioButton) groupSex.getSelectedToggle()).getText();
        } else if ("会议报名".equals(name)) {
            StringBuilder sb = new StringBuilder();
            if (meeting1.isSelected()) {
                sb.append(",1");
            }
            if (meeting2.isSelected()) {
                sb.append(",2");
            }
            if (meeting3.isSelected()) {
                sb.append(",3");
            }
            return sb.toString();
        } else {
            value = tfValue.getText();
        }
        return value;
    }

    public void clearInfo() {
        if ("性别".equals(name)) {
            groupSex.selectToggle(sexMale);
        } else if ("报名类型".equals(name) || "证件类型".equals(name) || "职称".equals(name) || "学历".equals(name)) {
            choiceBoxType.getSelectionModel().select(0);
            choiceBoxType.getSelectionModel().selectFirst();
        } else if ("是否付费".equals(name) || "是否需要学分".equals(name)) {
            sexFemale.setSelected(true);
        } else if ("会议报名".equals(name)) {
            meeting1.setSelected(false);
            meeting2.setSelected(false);
            meeting3.setSelected(false);
        } else {
            tfValue.setText("");
        }
    }

    public void setValue(String value) {
        if ("性别".equals(name)) {
            if ("男".equals(value)) {
                groupSex.selectToggle(sexMale);
            } else {
                groupSex.selectToggle(sexFemale);
            }
        } else if ("报名类型".equals(name) || "证件类型".equals(name) || "职称".equals(name) || "学历".equals(name)) {
            if (value == null) {
                value = "";
            }
            if ("报名类型".equals(name)) {

            }
            choiceBoxType.getSelectionModel().select(value);
//            choiceBoxType.setValue(value);
        } else if ("会议报名".equals(name)) {
            String[] results = value.split(",");
            meeting1.setSelected(false);
            meeting2.setSelected(false);
            meeting3.setSelected(false);
            for (String index : results) {
                if (index.equals("1")) {
                    meeting1.setSelected(true);
                } else if (index.equals("2")) {
                    meeting2.setSelected(true);
                } else if (index.equals("3")) {
                    meeting3.setSelected(true);
                }
            }
        } else if ("是否付费".equals(name) || "是否需要学分".equals(name)) {
            if ("是".equals(value)) {
                groupSex.selectToggle(sexMale);
            } else {
                groupSex.selectToggle(sexFemale);
            }
        } else {
            tfValue.setText(value);
        }
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public boolean isChinaPhoneLegal(String str) {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,4,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

}
