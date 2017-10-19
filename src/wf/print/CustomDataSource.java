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
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import wf.bean.RegistrationInformation;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: CustomDataSource.java 5876 2013-01-07 19:05:05Z teodord $
 */
public class CustomDataSource implements JRDataSource {

    RegistrationInformation regInfo;   
    private int index = -1;
    /**
     *
     */
    public CustomDataSource(RegistrationInformation regInfo) {
        this.regInfo  = regInfo;
    }

    /**
     *
     */
    @Override
    public boolean next() throws JRException {
        System.out.println("abc!!!!");
        index++;
        if(index==0){
            return true;
        }else{
            return false;
        }
    }

    /**
     *
     */
    @Override
    public Object getFieldValue(JRField field) throws JRException {
        Object value = null;
        String fieldName = field.getName();
        if ("姓名".equals(fieldName)) {
            value = regInfo.get姓名();
//        } else if ("quantity".equals(fieldName)) {
//            value = data[1];
//        } else if ("money".equals(fieldName)) {
//            value = data[2];
        } else if ("pinyin".equals(fieldName)) {
            value = "pinyin";
        }
        System.out.println("++++++++++" + value);
        return value;
    }

}