package com.bootseg.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Notes a variable that should be constructed and loaded using ORM from the current rowset, and set to the variable marked.
 *
 * In the example below. when instansiating Location using the ORM system, a GPSCoordinates object
 * would be constructed and use to set the variable _coordinates.
 *
 * <code>
 * class Location {
 *      @ORMChild private GPSCoordinates _coordinates;
 * }
 *
 *
 * class GPSCoordinates {
 *      @Column("latitude")  private BigDecimal _latitude;
 *      @Column("longitude") private BigDecimal _longitude;
 * }
 * </code>
 * Copyright 2008 Eric Kerin - Licensed under the LGPL
 * @author Eric Kerin <eric@bootseg.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ORMChild {
}
