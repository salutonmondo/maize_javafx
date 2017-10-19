/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wf.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 *
 * @author wadeshu
 */
public class ParamsUtil {

    public static HashMap<String, String> loadParams() {
        HashMap<String, String> prams = new HashMap<>();
        Properties props = new Properties();
        InputStream is = null;

        // First try loading from the current directory
        try {
            File f = new File("wf_config_file");
            is = new FileInputStream(f);
        } catch (Exception e) {
            is = null;
        }
        try {
            if (is == null) {
                // Try loading from classpath
                is = ParamsUtil.class.getResourceAsStream("wf_config_file");
            }
            props.load(is);
        } catch (Exception e) {
        }
        prams.put("ServerAddress", props.getProperty("ServerAddress", "192.168.0.1"));
        return prams;
    }

    public static void saveParamChanges(HashMap<String, String> params) {
        try {
            Properties props = new Properties();
            props.setProperty("ServerAddress", params.get("ServerAddress"));
            File file = new File("wf_config_file");
            if (!file.exists()) {
                file.createNewFile();
            }
            System.out.println("path:" + file.getAbsolutePath());
            OutputStream out = new FileOutputStream("wf_config_file");
            props.store(out, "服务器地址");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
