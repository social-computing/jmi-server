package com.socialcomputing.wps.server.affinityengine.scheduler;

import java.util.Collection;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

//import javax.naming.Context;
//import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import com.socialcomputing.wps.server.plandictionary.loader.DictionaryLoader;
import com.socialcomputing.wps.server.plandictionary.loader.DictionnaryLoaderDao;
//import com.socialcomputing.wps.server.plandictionary.loader.DictionaryLoaderHome;
import com.socialcomputing.wps.server.plandictionary.loader.DictionaryManager;

/**
 * Title:        Affinity Scheduler
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public class PlanAffinityMain extends TimerTask
{
	static public boolean s_Debug = false;

	public PlanAffinityMain()
	{
	}

	public void run()
	{
		try {
			DictionaryManager manager =  new DictionaryManager();

			Collection col = manager.findAll(); //m_DictionaryLoaderHome.findAll();
			Iterator it = col.iterator();
			while( it.hasNext())
			{
				DictionaryLoader dicLoader = (DictionaryLoader) PortableRemoteObject.narrow( it.next(), DictionaryLoader.class);
				java.util.Date plannifiedDate = dicLoader.getNextFilteringDate();
				String name = dicLoader.getName();
				if( plannifiedDate != null)
				{
					if( plannifiedDate.compareTo( new java.util.Date()) < 0)
					{
						if( s_Debug)
							System.out.println( "Affinity Scheduler : launching " + name + " Thread");
						new PlanAffinityThread( name).run();
						dicLoader.computeNextFilteringDate();
//						FIX ME ajouter un update sur le dao dictionnary
					}
					else
						if( s_Debug)
							System.out.println( "Affinity Scheduler : " + name + " planned later");
				}
				else
					if( s_Debug)
						System.out.println( "Affinity Scheduler : " + name + " not planned");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			cancel();
		}
	}

	public static void main(String[] args)
	{
		System.out.println( (new java.util.Date()).toString() +  " Launching Affinity Scheduler");
		try {
			Timer maintimer = new Timer( !s_Debug);
			maintimer.schedule( new PlanAffinityMain(), 1000, 60000);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}




