/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wf.poi;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ddf.EscherColorRef;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wf.bean.RegistrationInformation;
import wf.database.DataBaseUtility;
import wf.database.Pinyin;
import wf.service.RegistrationService;

/**
 *
 * @author wadeshu
 */
public class PoiUtil {

    public static void importExcel(File file) {
        try {
            OPCPackage pkg = OPCPackage.open(file);
            XSSFWorkbook wb = new XSSFWorkbook(pkg);
            boolean first = true;
            List<HashMap<String,String>> list = new ArrayList<>();
            ArrayList<String> headers = new ArrayList<>();
            for (Sheet sheet : wb) {
                for (Row row : sheet) {
                    if (first) {
                        first = false;
                        for (Cell cell : row) {
                            headers.add(cell.getStringCellValue());
                        }
                        continue;
                    }
                    int index = 0;
                     HashMap<String,String> regMap = new HashMap<>();
                    for (Cell cell : row) {
                        CellType type = cell.getCellTypeEnum();
                        if(type==CellType.STRING){
                            regMap.put(headers.get(index), cell.getStringCellValue());
                            System.out.println("   "+cell.getStringCellValue());
                        }else if(type==CellType.NUMERIC){
                            regMap.put(headers.get(index), cell.getNumericCellValue()+"");
                        }else {
                            regMap.put(headers.get(index), "");
                        }
                        index++;
                    }
                    list.add(regMap);
                    System.out.println("");
                }
            }
            System.out.println("xxxxx:"+list.size());
            new RegistrationService().saveRegisterInfos(RegistrationInformation.class, null, list);
            pkg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
     public static String[] arr = new String[]{
        "报名类型", "姓名", "全拼或英文名",
        "手机号", "邮箱",
        "是否付费",  "发票抬头", "工作单位",
        "邮寄地址","会议报名","注册费用","职称","学历","职务","是否需要学分","已打印胸牌","已领资料"
    };
    
    public static void exportExcel(File file){
        try{
            XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("精神分析大会数据");
        int rowIndex = 0;
        int columnIndex = 0;
        Row row = sheet.createRow(rowIndex);
        
        for(String header:arr){
            Cell cell = row.createCell(columnIndex);
            cell.setCellValue(header);
            columnIndex++;
        }
        rowIndex++;
        Connection conn = DataBaseUtility.getConnection();
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery("select * from registration ");
        while(rs.next()){
             Row row1 = sheet.createRow(rowIndex);
             int columnIndex1 = 0;
              for(String header:arr){
                  Cell cell = row1.createCell(columnIndex1);
                  if("会议报名".equals(header)){
                      cell.setCellValue(Pinyin.getAttendMeetingNames(rs.getString(header)));
                  }
                  else if("已打印胸牌".equals(header)||"已领资料".equals(header)){
                      cell.setCellValue(rs.getBoolean(header)?"是":"否");
                  }
                  else{
                      cell.setCellValue(rs.getString(header));
                  }
                  columnIndex1++;
              }
              rowIndex++;
        }
        FileOutputStream os = new FileOutputStream(file);
        workbook.write(os);
        workbook.close();
        rs.close();
        conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
    }
}
