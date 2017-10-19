package wf.database;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class Pinyin {
    /**
     * 将汉字转换为全拼
     *
     * @param src
     * @return String
     */
    public static String getPinYin(String src) {
        char[] t1 = null;
        t1 = src.toCharArray();
        // System.out.println(t1.length);
        String[] t2 = new String[t1.length];
        // System.out.println(t2.length);
        // 设置汉字拼音输出的格式
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断是否为汉字字符
                // System.out.println(t1[i]);
                if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
                    t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
                } else {
                    // 如果不是汉字字符，直接取出字符并连接到字符串t4后
                    t4 += Character.toString(t1[i]);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return t4;
    }

    public static String getNamePinyin(String nameString){
        String sexPart = nameString.substring(0,1);
        System.out.println("sexpart:"+sexPart+" name part:"+nameString.substring(1,nameString.length()));
        String firstNamepy = Pinyin.getPinYin(nameString.substring(0,1));
        firstNamepy = Character.toUpperCase(firstNamepy.charAt(0)) + firstNamepy.substring(1);

        String lastNamepy = Pinyin.getPinYin(nameString.substring(1,nameString.length()));
        lastNamepy = Character.toUpperCase(lastNamepy.charAt(0)) + lastNamepy.substring(1);

        String pinyin = lastNamepy+" "+firstNamepy;
        return pinyin;
    }

    /**
     * 提取每个汉字的首字母
     *
     * @param str
     * @return String
     */
    public static String getPinYinHeadChar(String str) {
        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            // 提取汉字的首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }

    /**
     * 将字符串转换成ASCII码
     *
     * @param cnStr
     * @return String
     */
    public static String getCnASCII(String cnStr) {
        StringBuffer strBuf = new StringBuffer();
        // 将字符串转换成字节序列
        byte[] bGBK = cnStr.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            // System.out.println(Integer.toHexString(bGBK[i] & 0xff));
            // 将每个字符转换成ASCII码
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff)+" ");
        }
        return strBuf.toString();
    }

    public static void main(String[] args) {
        String cnStr = "中华人民共和国";
        System.out.println(getPinYin(cnStr));
        System.out.println(getPinYinHeadChar(cnStr));
//      System.out.println(getCnASCII(cnStr));
    }
    
    public static String getAttendMeetingNames(String meetings){
//        String[] results = meetings.split(",");
//        StringBuilder sb = new StringBuilder();
//        for (String index : results) {
//            if (index.equals("1")) {
//                sb.append("精分大会&&");
//            } else if (index.equals("2")) {
//                sb.append("女性&&");
//            } else if (index.equals("3")) {
//                sb.append("工作坊");
//            }
//        }
//        return sb.toString();
        return "";
    }

}