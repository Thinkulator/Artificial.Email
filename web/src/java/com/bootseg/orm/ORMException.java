package com.bootseg.orm;

/**
 * Copyright 2008 Eric Kerin - Licensed under the LGPL
 * @author Eric Kerin <eric@bootseg.com>
 */
public class ORMException extends Exception{

    /** Creates a new instance of ORMException */
    public ORMException(String msg) {
        super(msg);
    }

    public ORMException(String msg,Throwable t) {
        super(msg,t);
    }

    public ORMException(Throwable t) {
        super(t);
    }

}
