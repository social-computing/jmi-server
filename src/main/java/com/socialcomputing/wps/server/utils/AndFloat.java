package com.socialcomputing.wps.server.utils;

/**
  * describes object with a float value
  *
  * **********************************************************
  * Java Class Name : AndFloat
  * ---------------------------------------------------------
  * Filetype: (SOURCE)
  * Filepath: C:\Dvpt\src\com\voyezvous\wps\server\\utils\AndFloat.java
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

public class AndFloat implements Comparable<AndFloat> {

   public float m_value = 0;

   public int compareTo(AndFloat anotherAndFloat) {
       float val2 = anotherAndFloat.m_value;
       return (val2 < m_value ? -1: (val2 == m_value ? 0: 1));
   }

   public AndFloat(float value) {
       m_value = value;
   }

   @Override
   public String toString() {
       return Float.toString(m_value);
   }
}
