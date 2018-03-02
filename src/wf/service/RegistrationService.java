/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wf.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import wf.RegisterInfo;
import wf.Wf;
import wf.bean.RegistrationInformation;
import wf.database.DataBaseUtility;
import wf.database.Pinyin;

/**
 *
 * @author wadeshu
 */
public class RegistrationService {

    Wf wf;

    public RegistrationService(Wf wf) {
        this.wf = wf;
    }

    public RegistrationService() {
    }

    public <T> boolean saveRegisterInfos(Class<T> clazz, HashMap<String, RegisterInfo> registerInfoMap, List<HashMap<String, String>> importList) {
        try {
            Connection conn = DataBaseUtility.getConnection();
            if (importList == null) {
                insertSingleRgistration(clazz, registerInfoMap, null, conn);
            } else {
                for (HashMap<String, String> poiMap : importList) {
                    insertSingleRgistration(clazz, null, poiMap, conn);
                }
            }
            conn.commit();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private <T> void insertSingleRgistration(Class<T> clazz, HashMap<String, RegisterInfo> registerInfoMap, HashMap<String, String> poiMap, Connection conn) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into registration(id,");
        for (String name : RegistrationInformation.arr) {
            sb.append(name + ",");
        };
        sb.append("pinyin)values(null ");
        String userName = "default";
        for (String name : RegistrationInformation.arr) {
            String value;
            if (registerInfoMap != null) {
                RegisterInfo regInfo = registerInfoMap.get(name);
                value = regInfo.getValue();
                userName = registerInfoMap.get("姓名").getValue();
            } else {
                value = poiMap.get(name);
                userName = poiMap.get("姓名");
                if ("全拼或英文名".equals(name)) {
                    if(!"abc".equals(poiMap.get("全拼或英文名"))){
                         value = Pinyin.getNamePinyin(userName);
                    }else{
                        value = "";
                    }
                }
            }
            if (userName == null) {
                return;
            }

            sb.append(",'").append(value).append("'");
        }
        sb.append(",'").append(Pinyin.getPinYinHeadChar(userName)).append("'");
        sb.append(")");
        Statement stm = conn.createStatement();

        System.out.println(sb.toString());
        stm.executeUpdate(sb.toString());
    }

    public static void delete(String id) {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from registration where id = '").append(id).append("'");
        try {
            Connection conn = DataBaseUtility.getConnection();
            Statement stm = conn.createStatement();
            stm.executeUpdate(sb.toString());
            conn.commit();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateRegistration(String id, HashMap<String, RegisterInfo> registerInfoMap) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("update registration set ");
            Connection conn = DataBaseUtility.getConnection();
            String userName = "default";
            boolean first = true;
            for (String name : RegistrationInformation.arr) {
                String value;
                RegisterInfo regInfo = registerInfoMap.get(name);
                value = regInfo.getValue();
                userName = registerInfoMap.get("姓名").getValue();
                sb.append(first ? name + "='" : "," + name + "='").append(value).append("'");
                first = false;
            }
            sb.append(",pinyin='").append(Pinyin.getPinYinHeadChar(userName)).append("'");
            sb.append(" where id='").append(id).append("'");
            Statement stm = conn.createStatement();
            System.out.println(sb.toString());
            stm.executeUpdate(sb.toString());
            conn.commit();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean updatePay(String id) {
        String sql = "update registration set 是否付费 = '是' where id='" + id + "'";
        return executeSql(sql);
    }

    public static boolean executeSql(String sql) {
        try {
            Connection conn = DataBaseUtility.getConnection();
            Statement stm = conn.createStatement();
            System.out.println(sql);
            stm.executeUpdate(sql);
            conn.commit();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean dinnerCardGiveSuccess(String id){
        String sql = "update registration set 发票抬头 = '晚宴-已发' where id='" + id + "'";
        return executeSql(sql);
    }

    public static boolean scanSuccess(String id) {
        String sql = "update registration set 已领资料 = true where id='" + id + "'";
        return executeSql(sql);
    }
    
    public static boolean printSuccess(String id){
        String sql = "update registration set 已打印胸牌 = true where id='" + id + "'";
        return executeSql(sql);
    }
    
        public static String getAttendMeetings(String id){
        String sql = "select 会议报名 from registration  where id='" + id + "'";
        try {
            Connection conn = DataBaseUtility.getConnection();
            Statement stm = conn.createStatement();
            System.out.println(sql);
            ResultSet rs = stm.executeQuery(sql);
            String s = null;
            if(rs.next()){
                 s = rs.getString("会议报名");
            }
            conn.commit();
            conn.close();
             return Pinyin.getAttendMeetingNames(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
   
       
}
