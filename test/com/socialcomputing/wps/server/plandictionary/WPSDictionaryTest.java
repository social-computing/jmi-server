package com.socialcomputing.wps.server.plandictionary;

import java.io.File;

import org.junit.Test;


public class WPSDictionaryTest {

	// Starter helper
	public static WPSDictionary CreateTestInstance( String name)
	{
		WPSDictionary dico = null;

		try
		{
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder( true);
			org.jdom.Document doc = null;

			if( name.equalsIgnoreCase( "BooSol"))
				doc = builder.build( new File( "..\\plandictionary\\mapstan_net.xml"));
			if( name.equalsIgnoreCase( "SEngine"))
				doc = builder.build( new File( "..\\plandictionary\\mapstan_search.xml"));
			if( name.equalsIgnoreCase( "Test"))
				doc = builder.build( new File( "..\\plandictionary\\test.xml"));

			if( doc != null)
			{
				org.jdom.Element root = doc.getRootElement();
				dico = WPSDictionary.readObject( root);
				if( dico != null)
					System.out.println( dico.m_Name + " created");
				else
					System.out.println( "Dico failed");

			}
			else
				System.out.println( "Unknown dico failed");
		}
		catch (org.jdom.JDOMException se)
		{
			System.err.println(se.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return dico;
	}

	@Test
	public void createDictionnaries()
	{
		//WPSDictionary dico = null;
		WPSDictionaryTest.CreateTestInstance( "BooSol");
		WPSDictionaryTest.CreateTestInstance( "SEngine");
	}
	
}
