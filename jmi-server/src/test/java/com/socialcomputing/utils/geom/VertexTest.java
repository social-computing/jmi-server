package com.socialcomputing.utils.geom;

import static org.junit.Assert.*;

import org.junit.Test;

public class VertexTest {

	@Test
	public void testVertex() 
	{
		final   int ITER_CNT = 5000000;
		int     i;
		Vertex  v = new Vertex();

		for ( i = 0; i < ITER_CNT; i ++ )
		{
			Vertex.randomize( 10.f );
			v.randomizeThis( 10.f );
			v.sqrRandThis( 10.f );
		}
	}
}
