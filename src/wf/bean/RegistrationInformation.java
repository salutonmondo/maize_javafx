/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wf.bean;

/**
 * @author wadeshu
 */
public class RegistrationInformation {

    /**
     * 必填 发票抬头，类型默认空白,姓名,手机号（加验证）,邮箱(加验证)，注册费用(自动生成),工作单位
     */
    @Reflection(existsInDb = false)
    public static String[] arr = new String[]{"报名类型", "姓名", "性别","工作单位","全拼或英文名", "发票抬头","职务","手机号","邮箱","是否需要学分","是否付费","注册费用"};

    @Reflection(existsInDb = false)
    public static String[] identityType = new String[]{"身份证", "军官证", "护照", "MTPS/台胞证"};
    @Reflection(columWidth = 80, isNecessary = true,display = true,customNotAloowed = true)
    String 报名类型;
    @Reflection(columWidth = 60, isNecessary = true,display = true,customName = "Name:")
    String 姓名;
    @Reflection(columWidth = 100,display = false)
    String 全拼或英文名;
    @Reflection(columWidth = 50, display = false)
    String 性别;
    @Reflection(columWidth = 100, display = false,isNecessary = false)
    String 手机号;
    @Reflection(columWidth = 120, isNecessary = true,display = true,customName = "Email:")
    String 邮箱;
//    @Reflection(columWidth = 30)
//    String 证件类型;
//
//    @Reflection(columWidth = 30)
//    String 证件号码;

    @Reflection(columWidth = 60, isNecessary = false)
    String 会议报名;
    @Reflection(columWidth = 60,display = true,customNotAloowed = true )
    String 注册费用;

    @Reflection(columWidth = 60, customNotAloowed = true,display = true)
    String 是否付费;

    @Reflection(columWidth = 30, onlyInTable = true)
    String 导入ID;

    @Reflection(existsInDb = true, columWidth = 30, onlyInTable = true)
    String id;

    @Reflection(columWidth = 60, displayName = "职称",display = false)
    String 发票抬头;
    @Reflection(columWidth = 120, isNecessary = true,display = true,customName = "Organization:")
    String 工作单位;
    @Reflection(columWidth = 60,display=false,isNecessary = false)
    String 是否需要学分;
    @Reflection(columWidth = 30, dependent = true,display = false)
    String 职称;
    @Reflection(columWidth = 30, dependent = true, isNecessary = true,display = false)
    String 学历;
    @Reflection(columWidth = 30, dependent = true, display = false)
    String 职务;
    @Reflection(columWidth = 100,display = false)
    String 邮寄地址;
    @Reflection(columWidth = 50, onlyInTable = true)
    String 已领资料;//最后流程
    @Reflection(columWidth = 30, customNotAloowed = true, onlyInTable = true)
    String 已打印胸牌;//第二次打印

    public static String[] getArr() {
        return arr;
    }

    public static void setArr(String[] arr) {
        RegistrationInformation.arr = arr;
    }

    public static String[] getIdentityType() {
        return identityType;
    }

    public static void setIdentityType(String[] identityType) {
        RegistrationInformation.identityType = identityType;
    }

    public String get全拼或英文名() {
        return 全拼或英文名;
    }

    public void set全拼或英文名(String 全拼或英文名) {
        this.全拼或英文名 = 全拼或英文名;
    }

//    public String get证件类型() {
//        return 证件类型;
//    }
//
//    public void set证件类型(String 证件类型) {
//        this.证件类型 = 证件类型;
//    }
//
//    public String get证件号码() {
//        return 证件号码;
//    }
//
//    public void set证件号码(String 证件号码) {
//        this.证件号码 = 证件号码;
//    }

    public String get注册费用() {
        return 注册费用;
    }

    public void set注册费用(String 注册费用) {
        this.注册费用 = 注册费用;
    }

    public String get是否付费() {
        return 是否付费;
    }

    public void set是否付费(String 是否付费) {
        this.是否付费 = 是否付费;
    }

    public String get导入ID() {
        return 导入ID;
    }

    public void set导入ID(String 导入ID) {
        this.导入ID = 导入ID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get报名类型() {
        return 报名类型;
    }

    public void set报名类型(String 报名类型) {
        this.报名类型 = 报名类型;
    }

    public String get手机号() {
        return 手机号;
    }

    public void set手机号(String 手机号) {
        this.手机号 = 手机号;
    }

    public String get姓名() {
        return 姓名;
    }

    public void set姓名(String 姓名) {
        this.姓名 = 姓名;
    }

    public String get性别() {
        return 性别;
    }

    public void set性别(String 性别) {
        this.性别 = 性别;
    }


    public String get邮箱() {
        return 邮箱;
    }

    public void set邮箱(String 邮箱) {
        this.邮箱 = 邮箱;
    }

    public String get工作单位() {
        return 工作单位;
    }

    public void set工作单位(String 工作单位) {
        this.工作单位 = 工作单位;
    }

    public String get邮寄地址() {
        return 邮寄地址;
    }

    public void set邮寄地址(String 邮寄地址) {
        this.邮寄地址 = 邮寄地址;
    }

    public String get发票抬头() {
        return 发票抬头;
    }

    public void set发票抬头(String 发票抬头) {
        this.发票抬头 = 发票抬头;
    }

    public String get已领资料() {
        return 已领资料;
    }

    public void set已领资料(String 已领资料) {
        this.已领资料 = 已领资料;
    }

    public String get已打印胸牌() {
        return 已打印胸牌;
    }

    public void set已打印胸牌(String 已打印胸牌) {
        this.已打印胸牌 = 已打印胸牌;
    }

    public String get会议报名() {
        return 会议报名;
    }

    public void set会议报名(String 会议报名) {
        this.会议报名 = 会议报名;
    }

    public String get是否需要学分() {
        return 是否需要学分;
    }

    public void set是否需要学分(String 是否需要学分) {
        this.是否需要学分 = 是否需要学分;
    }

    public String get职称() {
        return 职称;
    }

    public void set职称(String 职称) {
        this.职称 = 职称;
    }

    public String get学历() {
        return 学历;
    }

    public void set学历(String 学历) {
        this.学历 = 学历;
    }

    public String get职务() {
        return 职务;
    }

    public void set职务(String 职务) {
        this.职务 = 职务;
    }
}
