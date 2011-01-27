package com.socialcomputing.wps.server.swatchs.loader;

import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;

import com.socialcomputing.wps.server.swatchs.XSwatch;

public class BeanSwatchLoader implements SwatchLoader {

	public String      m_SwatchDefinition = null;
	public String      m_Name = null;

	private XSwatch      m_Swatch = null; // Speeder
	
	public BeanSwatchLoader(String name , String swatchDefinition) {
		super();
		m_SwatchDefinition = swatchDefinition;
		m_Name = name;
	}

	public XSwatch getSwatch() throws RemoteException
	{
		try {
			if( m_Swatch == null)
			{
				org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder( false);
				org.jdom.Document doc = builder.build( new StringReader( m_SwatchDefinition));
				org.jdom.Element root = doc.getRootElement();
				m_Swatch = XSwatch.readObject( root);
			}
		}
		catch( Exception e)
		{
			throw new RemoteException ( "getDictionary failed : " + e.getMessage());
		}
		return m_Swatch;
	}

	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return m_Name;
	}

	public String getSwatchDefinition() throws RemoteException {
		return m_SwatchDefinition;
	}

	public void setSwatch(String definition) throws RemoteException {
		//m_Modified = true;
		m_SwatchDefinition = definition;
		m_Swatch = null;
	}
}
