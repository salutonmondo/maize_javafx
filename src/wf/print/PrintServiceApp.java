package wf.print;

/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2013 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRImageRenderer;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBasePrintImage;
import net.sf.jasperreports.engine.base.JRBasePrintLine;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import wf.Wf;
import wf.bean.RegistrationInformation;
import wf.database.DataBaseUtility;
import wf.database.DesUtils;
import wf.database.Pinyin;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: PrintServiceApp.java 7056 2014-04-28 09:30:15Z teodord $
 */
public class PrintServiceApp {

    RegistrationInformation regInfo;

    public void printOrder(RegistrationInformation regInfo) {
        this.regInfo = regInfo;
        try {
            fill();
            print();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("xxxxxx");
                                alert.setHeaderText(null);
                                alert.setContentText(e.getMessage());
                                Optional<ButtonType> result = alert.showAndWait();
            e.printStackTrace();
        }
    }

    public PrintServiceApp() {
    }

    public PrintServiceApp(RegistrationInformation regInfo) {
        this.regInfo = regInfo;
    }

    /**
     *
     */
    public void test() throws JRException {
    }

    /**
     *
     */
    JasperPrint jp;

    public void fill() throws Exception {
//        try {
            long startTime = System.currentTimeMillis();
//            File prFile = new File(DataBaseUtility.getRunTimePath() + "/wf.jasper");
            File prFile = new File("c:/wf.jasper");
            System.out.println("xxxxxx" + prFile.getAbsolutePath());
            if (prFile.exists()) {
                System.out.println("file existed" + prFile.getAbsolutePath());
            } else {
                System.out.println("recreate jasper file" + prFile.getAbsolutePath());
                InputStream in = Wf.class.getResourceAsStream("c:/wf.jrxml");
                FileOutputStream fos = new FileOutputStream(prFile);
//                JasperCompileManager.compileReportToStream(in, fos);
                JasperCompileManager.compileReportToFile("c:/wf.jrxml", "c:/wf.jasper");
                fos.flush();
                fos.close();
                long e1 = System.currentTimeMillis();
                System.out.println((e1 - startTime) / 1000 + " compile");
            }
            JasperReport jr = (JasperReport) JRLoader.loadObject(new File("c:/wf.jasper"));
            long e2 = System.currentTimeMillis();
            System.out.println((e2 - startTime) / 1000 + " load");
            Map<String, Object> parameters = new HashMap<String, Object>();
//		parameters.put("terminalNO", order.getTerminalno());
            Field[] fields = RegistrationInformation.class.getDeclaredFields();
//            try {
                for (Field field : fields) {
                    field.setAccessible(true);
                    String name = field.getName();
                    if (name.equals("arr") || "确认密码".equals(name) || "密码".equals(name)) {
                        continue;
                    }
                    if (field.get(regInfo) == null) {
                        continue;
                    }
                    System.out.println("----" + name + field.get(regInfo).toString());
                    String tmp = field.getName();

                    if (tmp.equals("姓名")) {
                        tmp = "name";
                        parameters.put(tmp, field.get(regInfo).toString());
                        parameters.put("fname", field.get(regInfo).toString());
                    } else if (tmp.equals("全拼或英文名")) {
                        tmp = "pinyin";
                        parameters.put(tmp, field.get(regInfo).toString());
                    } else if (tmp.equals("id")) {
                        tmp = "barcode";
                        DesUtils des = new DesUtils("hbue");//自定义密钥  
                        String encriptedId = des.encrypt("maizehuizhan" + field.get(regInfo).toString());
                        InputStream is = getQrCode(encriptedId);
                        parameters.put(tmp, is);
                    } else if (tmp.equals("会议报名")) {
                        String value = field.get(regInfo).toString();
                        tmp = "meetings";
                        parameters.put(tmp, Pinyin.getAttendMeetingNames(value));
                    } else if (tmp.equals("注册费用")) {
                        tmp = "money";
                        parameters.put(tmp, field.get(regInfo).toString());
                    } else if (tmp.equals("发票抬头")) {
                        tmp = "invoice";
                        parameters.put(tmp, field.get(regInfo).toString());
                    } else if (tmp.equals("是否付费")) {
                        tmp = "paid";
                        String string =  field.get(regInfo).toString().equals("否")?"":"1";
                        parameters.put(tmp,string);
                    }else if(tmp.equals("报名类型")){
                        tmp = "regType";
                        parameters.put(tmp, field.get(regInfo).toString());
                    }
                }
//            } 
//            catch (Exception e) {
//                e.printStackTrace();
//            }
            
            for(Map.Entry<String,Object> ent:parameters.entrySet()){
                System.out.println(ent.getKey()+"_____"+ent.getValue().toString());
            }
//		JasperPrint  jp = JasperFillManager.fillReport("58printer.jasper",parameters, new CustomDataSource(this.order));
            jp = JasperFillManager.fillReport(jr, parameters, new CustomDataSource(this.regInfo));
//            jp = JasperFillManager.fillReport(jr, parameters);
            JRSaver.saveObject(jp, "58printer.jrprint");
            System.err.println("fill time : " + (System.currentTimeMillis() - e2));
//        } 

//        catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private InputStream getQrCode(String code) {
        try {
            Map hintMap = new HashMap();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            createQRCode(code, "tmp.png", "utf-8", hintMap, 200, 200);
            return new FileInputStream(new File("tmp.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createQRCode(String qrCodeData, String filePath,
            String charset, Map hintMap, int qrCodeheight, int qrCodewidth)
            throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(qrCodeData.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
        MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath
                .lastIndexOf('.') + 1), new File(filePath));
    }

    /**
     *
     */
    public void print() throws JRException {
        long start = System.currentTimeMillis();
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
//        printRequestAttributeSet.add(MediaSizeName.ISO_A4);
        // printRequestAttributeSet.add(new MediaPrintableArea(5, 5, 58, 500,
        // MediaPrintableArea.MM));
        PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(new PrinterName("Argox CP-2140M PPLB", null));
        // printServiceAttributeSet.add(new
        // PrinterName("hp LaserJet 1320 PCL 6", null));
        // printServiceAttributeSet.add(new PrinterName("PDFCreator", null));
        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
//		exporter.setExporterInput(new SimpleExporterInput("58printer.jrprint"));
        SimpleExporterInput sei = new SimpleExporterInput(jp);
        exporter.setExporterInput(sei);
        SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
        configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
        configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
        configuration.setDisplayPageDialog(false);
        configuration.setDisplayPrintDialog(false);
        exporter.setConfiguration(configuration);
        exporter.exportReport();

        System.err.println("Printing time : " + (System.currentTimeMillis() - start));
    }

    /**
     *
     */
    private static JasperPrint getJasperPrint() throws JRException {
        // JasperPrint
        JasperPrint jasperPrint = new JasperPrint();
        jasperPrint.setName("NoReport");
        jasperPrint.setPageWidth(595);
        jasperPrint.setPageHeight(842);

        // Fonts
        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("Sans_Normal");
        normalStyle.setDefault(true);
        normalStyle.setFontName("DejaVu Sans");
        normalStyle.setFontSize(8f);
        normalStyle.setPdfFontName("Helvetica");
        normalStyle.setPdfEncoding("Cp1252");
        normalStyle.setPdfEmbedded(false);
        jasperPrint.addStyle(normalStyle);

        JRDesignStyle boldStyle = new JRDesignStyle();
        boldStyle.setName("Sans_Bold");
        boldStyle.setFontName("DejaVu Sans");
        boldStyle.setFontSize(8f);
        boldStyle.setBold(true);
        boldStyle.setPdfFontName("Helvetica-Bold");
        boldStyle.setPdfEncoding("Cp1252");
        boldStyle.setPdfEmbedded(false);
        jasperPrint.addStyle(boldStyle);

        JRDesignStyle italicStyle = new JRDesignStyle();
        italicStyle.setName("Sans_Italic");
        italicStyle.setFontName("DejaVu Sans");
        italicStyle.setFontSize(8f);
        italicStyle.setItalic(true);
        italicStyle.setPdfFontName("Helvetica-Oblique");
        italicStyle.setPdfEncoding("Cp1252");
        italicStyle.setPdfEmbedded(false);
        jasperPrint.addStyle(italicStyle);

        JRPrintPage page = new JRBasePrintPage();

        JRPrintLine line = new JRBasePrintLine(
                jasperPrint.getDefaultStyleProvider());
        line.setX(40);
        line.setY(50);
        line.setWidth(515);
        line.setHeight(0);
        page.addElement(line);

        JRPrintImage image = new JRBasePrintImage(
                jasperPrint.getDefaultStyleProvider());
        image.setX(45);
        image.setY(55);
        image.setWidth(165);
        image.setHeight(40);
        image.setScaleImage(ScaleImageEnum.CLIP);
        image.setRenderable(JRImageRenderer.getInstance(JRLoader
                .loadBytesFromResource("jasperreports.png")));
        page.addElement(image);

        JRPrintText text = new JRBasePrintText(
                jasperPrint.getDefaultStyleProvider());
        text.setX(210);
        text.setY(55);
        text.setWidth(345);
        text.setHeight(30);
        text.setTextHeight(text.getHeight());
        text.setHorizontalAlignment(HorizontalAlignEnum.RIGHT);
        text.setLineSpacingFactor(1.3133681f);
        text.setLeadingOffset(-4.955078f);
        text.setStyle(boldStyle);
        text.setFontSize(18f);
        text.setText("JasperReports Project Description");
        page.addElement(text);

        text = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
        text.setX(210);
        text.setY(85);
        text.setWidth(325);
        text.setHeight(15);
        text.setTextHeight(text.getHeight());
        text.setHorizontalAlignment(HorizontalAlignEnum.RIGHT);
        text.setLineSpacingFactor(1.329241f);
        text.setLeadingOffset(-4.076172f);
        text.setStyle(italicStyle);
        text.setFontSize(12f);
        text.setText((new SimpleDateFormat("EEE, MMM d, yyyy"))
                .format(new Date()));
        page.addElement(text);

        text = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
        text.setX(40);
        text.setY(150);
        text.setWidth(515);
        text.setHeight(200);
        text.setTextHeight(text.getHeight());
        text.setHorizontalAlignment(HorizontalAlignEnum.JUSTIFIED);
        text.setLineSpacingFactor(1.329241f);
        text.setLeadingOffset(-4.076172f);
        text.setStyle(normalStyle);
        text.setFontSize(14f);
        text.setText("JasperReports is a powerful report-generating tool that has the ability to deliver rich content onto the screen, to the printer or into PDF, HTML, XLS, CSV or XML files.\n\n"
                + "It is entirely written in Java and can be used in a variety of Java enabled applications, including J2EE or Web applications, to generate dynamic content.\n\n"
                + "Its main purpose is to help creating page oriented, ready to print documents in a simple and flexible manner.");
        page.addElement(text);

        jasperPrint.addPage(page);

        return jasperPrint;
    }

}
