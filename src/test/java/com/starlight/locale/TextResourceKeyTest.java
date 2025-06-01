/*
 * Copyright(c) 2007, StarLight Systems
 * All rights reserved.
 */
package com.starlight.locale;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 *
 */
public class TextResourceKeyTest {
	@Test
	public void testDefault() {
		assertEquals( "One", TestResourceList.ONE.toString() );
		assertEquals( "Two", TestResourceList.TWO.toString() );
	}


	@Test
	public void testSpanish() {
		Locale spanish = new Locale( "es" );
		assertEquals( "Uno", TestResourceList.ONE.getValue( spanish ) );
		assertEquals( "Dos", TestResourceList.TWO.getValue( spanish ) );
	}


	@Test
	public void testUnknownLocale() {
		Locale foo = new Locale( "foo" );

		assertEquals( "One", TestResourceList.ONE.getValue( foo ) );
		assertEquals( "Two", TestResourceList.TWO.getValue( foo ) );
	}
}
