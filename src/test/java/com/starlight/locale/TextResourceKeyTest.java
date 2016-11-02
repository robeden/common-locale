/*
 * Copyright(c) 2007, StarLight Systems
 * All rights reserved.
 */
package com.starlight.locale;

import junit.framework.TestCase;

import java.util.Locale;


/**
 *
 */
public class TextResourceKeyTest extends TestCase {
	public void testDefault() {
		assertEquals( "One", TestResourceList.ONE.toString() );
		assertEquals( "Two", TestResourceList.TWO.toString() );
	}


	public void testSpanish() {
		Locale spanish = new Locale( "es" );
		assertEquals( "Uno", TestResourceList.ONE.getValue( spanish ) );
		assertEquals( "Dos", TestResourceList.TWO.getValue( spanish ) );
	}


	public void testUnknownLocale() {
		Locale foo = new Locale( "foo" );

		assertEquals( "One", TestResourceList.ONE.getValue( foo ) );
		assertEquals( "Two", TestResourceList.TWO.getValue( foo ) );
	}
}
