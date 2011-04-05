package com.socialcomputing.wps.server.utils;

/**
  * describes object with a numerical (int) and a float value
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

public class NumAndFloat extends AndFloat {
    
    public int m_num = 0;

    public NumAndFloat(int num, float value) {
        super(value);
        m_num = num;
    }

    public int compareTo(NumAndFloat anotherNumAndFloat) {
        float val2 = anotherNumAndFloat.m_num;
        return (val2 < m_num ? -1: (val2 == m_num ? 0: 1));
    }
    
    @Override
    public  String toString() {
        return Integer.toString(m_num) + super.toString();
    }
}
