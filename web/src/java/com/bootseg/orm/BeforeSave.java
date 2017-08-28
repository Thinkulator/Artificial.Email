/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bootseg.orm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**  Used to notate a function that should be called to prepare the object for being saved to the database
 * 
 * Things such as updating timestamps, clearing data, etc.
 *
 * @author hack
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeSave {
}
