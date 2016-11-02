/*
 * Copyright(c) 2002-2010, Rob Eden
 * All rights reserved.
 */
package com.starlight.locale;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Locale;


/**
 * This class allows using the ResourceKey API with a text value that is not localizable
 * (for example, user input).
 */
public class UnlocalizableTextResourceKey extends TextResourceKey {
	static final long serialVersionUID = 0L;
	
	private String text;

	/**
	 * FOR EXTERNALIZATION ONLY!!!
	 */
	public UnlocalizableTextResourceKey() {
		super( "unused", true );
	}

	public UnlocalizableTextResourceKey( String text ) {
		super( text, true );

		if ( text == null ) throw new IllegalArgumentException( "Text cannot be null" );

		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	protected String convertToValue( String string, Locale locale ) {
		return text;
	}

	@Override
	String getDefaultValue() {
		return text;
	}

	@Override
	public String getValue() {
		return text;
	}

	@Override
	public String getValue( Locale locale ) {
		return getValue();
	}

	@Override
	public String getValue( Locale locale, ClassLoader class_loader ) {
		return getValue();
	}


	public boolean equals( Object o ) {
		if ( this == o ) return true;
		if ( o == null || getClass() != o.getClass() ) return false;
		if ( !super.equals( o ) ) return false;

		UnlocalizableTextResourceKey that = ( UnlocalizableTextResourceKey ) o;

		if ( !text.equals( that.text ) ) return false;

		return true;
	}

	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + text.hashCode();
		return result;
	}


	@Override
	void setKeyFieldName( String field_name,
		Class<? extends ResourceList> resource_list_class ) {
	}


	@Override
	public void writeExternal( ObjectOutput out ) throws IOException {
		super.writeExternal( out );

		// VERSION
		out.writeByte( 0 );

		// TEXT
		writeString( out, text );
	}

	@Override
	public void readExternal( ObjectInput in )
		throws IOException, ClassNotFoundException {

		super.readExternal( in );

		// VERSION
		in.readByte();

		// TEXT
		text = readString( in );
	}
}