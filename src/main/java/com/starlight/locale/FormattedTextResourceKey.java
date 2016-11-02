/*
 * Copyright(c) 2002-2010, Rob Eden
 * All rights reserved.
 */

package com.starlight.locale;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;


/**
 *
 */
public class FormattedTextResourceKey extends ResourceKey<String> {
	static final long serialVersionUID = 0L;
	
	private ResourceKey<String> message;
	private Object[] args;


	/**
	 * FOR EXTERNALIZATION ONLY!!!
	 */
	public FormattedTextResourceKey() {
		super( true );
	}

	public FormattedTextResourceKey( ResourceKey<String> message,
		Object... args ) {

		this( false, message, args );
	}

	protected FormattedTextResourceKey( boolean ignore_resource_list_class,
		ResourceKey<String> message, Object... args ) {

		super( ignore_resource_list_class );

		if ( message == null ) {
			throw new IllegalArgumentException( "Default text cannot be null" );
		}

		this.message = message;
		this.args = args;
	}


	@Override
	protected void initFieldNames() {
		// don't do anything
	}

	@Override
	public String toString() {
		return getValue();
	}


	@Override
	public boolean equals( Object o ) {
		if ( this == o ) return true;
		if ( o == null || getClass() != o.getClass() ) return false;
		if ( !super.equals( o ) ) return false;

		FormattedTextResourceKey that = ( FormattedTextResourceKey ) o;

		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		if ( !Arrays.equals( args, that.args ) ) return false;
		if ( message != null ? !message.equals( that.message ) : that.message != null )
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + ( message != null ? message.hashCode() : 0 );
		result = 31 * result + ( args != null ? Arrays.hashCode( args ) : 0 );
		return result;
	}


	@Override
	protected String convertToValue( String string, Locale locale ) {
		if ( locale == null ) locale = Locale.getDefault();

		return new MessageFormat( string, locale ).format( args );
	}

	String getDefaultValue() {
		return convertToValue( message.getDefaultValue(), null );
	}


	public void writeExternal( ObjectOutput out ) throws IOException {
		super.writeExternal( out );

		// VERSION
		out.writeByte( 0 );

		// MESSAGE
		out.writeObject( message );

		// ARGS
		out.writeInt( args.length );
		for( Object arg : args ) {
			out.writeObject( arg );
		}

	}

	public void readExternal( ObjectInput in )
		throws IOException, ClassNotFoundException {

		super.readExternal( in );

		// VERSION
		in.readByte();

		// MESSAGE
		message = ( ResourceKey<String> ) in.readObject();

		// ARGS
		int length = in.readInt();
		args = new Object[ length ];
		for( int i = 0; i < length; i++ ) {
			args[ i ] = in.readObject();
		}
	}
}
