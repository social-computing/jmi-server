package com.socialcomputing.wps.server.utils;

/**
  * describes object with an id (String) and a float value
  *
  * **********************************************************
  * Java Class Name : NumAndFloat
  * ---------------------------------------------------------
  * Filetype: (SOURCE)
  * Filepath: C:\Dvpt\src\com\voyezvous\wps\server\\utils\NumAndFloat.java
  *
  *
  * GDPro Properties
  * ---------------------------------------------------
  *  - GD Symbol Type    : CLD_Class
  *  - GD Method         : UML ( 5.0 )
  *  - GD System Name    : WPS
  *  - GD Diagram Type   : Class Diagram
  *  - GD Diagram Name   : Utils
  * ---------------------------------------------------
  *  Author         : espinat
  *  Creation Date  : Tues - Jan 23, 2001
  *
  *  Change Log     :
  *
  * ********************************************************** */

public class StringAndFloat extends AndFloat {
    
    public String m_Id = null;

    public StringAndFloat(String id, float value ) {
        super(value);
        m_Id = id;
    }

    @Override
    public String toString() {
        return "" + m_Id + "/" + super.toString();
   }
}
