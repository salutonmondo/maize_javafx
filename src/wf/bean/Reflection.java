/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wf.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author wadeshu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) //can use in method only.
public @interface Reflection {
    public int type() default 0;
    public String dbFieldName() default "";
    public boolean isNecessary() default false;
    public boolean existsInDb() default true;
    public boolean onlyInTable() default false;
    public boolean customNotAloowed() default false;
    public int columWidth() default 0;
    public boolean dependent() default false;
    public boolean display() default false;
    public String displayName() default "";
    public String customName() default "";
}
