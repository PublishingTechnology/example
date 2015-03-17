/*
 * Operator
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;

import com.ingenta.search.xml.XmlSerializable;

/**
 * An Enum defining the operators which may be used in searches.
 * 
 * @author Mike Bell
 */
public enum Operator implements XmlSerializable {
   EQUALS,
   NOT,
   AND,
   OR
}
