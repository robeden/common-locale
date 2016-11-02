/*
 * Copyright(c) 2002-2010, Rob Eden
 * All rights reserved.
 */
package com.starlight.locale;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 *
 */
public abstract class ResourceKey<K> implements Externalizable {
	static final long serialVersionUID = 0L;
	
	private String resource_list_class_name;
	private Class<? extends ResourceList> resource_list_class = null;

	private volatile boolean inited = false;

	private String key_field_name;

	private transient Locale cached_locale;
	private transient K cached_value;

	protected ResourceKey( boolean ignore_resource_list_class ) {
		if ( !ignore_resource_list_class ) {
			final String key_instance_class_name = getClass().getName();

			// Find the first element that is PAST the instance class
			//noinspection ThrowableInstanceNeverThrown
			StackTraceElement[] stack = new Throwable().getStackTrace();
			boolean found_instance_class = false;
			String resource_list_class_name = null;
			for ( StackTraceElement element : stack ) {
				if ( element.getClassName().equals( key_instance_class_name ) ) {
					found_instance_class = true;
				}
				else if ( found_instance_class ) {
					// Special case handling of ResourceList to ignore its constructors
					if ( element.getClassName().equals( ResourceList.class.getName() ) ) {
						continue;
					}

					resource_list_class_name = element.getClassName();
					break;
				}
			}

			if ( resource_list_class_name == null ) throwKeyDefinitionError( null );

			this.resource_list_class_name = resource_list_class_name;
		}
		else inited = true;
	}


	/**
	 * See {@link ResourceBundle#getBundle(String)}.
	 */
	public K getValue() {
		return getValue( null );
	}

	/**
	 * See {@link ResourceBundle#getBundle(String, Locale)}.
	 */
	public K getValue( Locale locale ) {
		return getValue( locale, ResourceKey.class.getClassLoader() );
	}

	/**
	 * See
	 * {@link ResourceBundle#getBundle(String, Locale, ClassLoader)}.
	 */
	public K getValue( Locale locale, ClassLoader class_loader ) {
		if ( locale == null ) locale = Locale.getDefault();

		// Make sure keys know their field names in the ResourceLists
		if ( key_field_name == null ) initFieldNames();

		if ( locale == cached_locale ) return cached_value;

		// If the locale is english, just use the default value
		if ( locale != null && locale.getLanguage().equals( "en" ) ) {
			return cacheValue( locale, getDefaultValue() );
		}

		// Find the correct bundle
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(
				resource_list_class_name, locale, class_loader );

			if ( key_field_name == null ) {
				throw new IllegalStateException( "Key (" + key_field_name +
					") has not been initialized correctly." );
			}

			try {
				return cacheValue( locale,
					convertToValue( bundle.getString( key_field_name ), locale ) );
			}
			catch ( NullPointerException ex ) {	// thrown by ListResourceBundle
				return cacheValue( locale, getDefaultValue() );
			}
		}
		catch ( MissingResourceException ex ) {
			System.err.println( "Resource lookup failed for bundle \"" +
				resource_list_class_name + "\", locale=" + locale );

			return cacheValue( locale, getDefaultValue() );
		}
	}


	public boolean equals( Object o ) {
		if ( this == o ) return true;
		if ( o == null || getClass() != o.getClass() ) return false;

		ResourceKey that = ( ResourceKey ) o;

		if ( key_field_name != null ? !key_field_name.equals( that.key_field_name ) :
			that.key_field_name != null ) {
			return false;
		}
		if ( resource_list_class_name != null ?
			!resource_list_class_name.equals( that.resource_list_class_name ) :
			that.resource_list_class_name != null ) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int result;
		result =
			( resource_list_class_name != null ? resource_list_class_name.hashCode() :
				0 );
		result = 31 * result + ( key_field_name != null ? key_field_name.hashCode() : 0 );
		return result;
	}


	protected abstract K convertToValue( String string, Locale locale );


	protected final Class<? extends ResourceList> getResourceListClass() {
		if ( !inited ) initFieldNames();

		return resource_list_class;
	}

	protected void initFieldNames() {
		if ( inited ) return;

		// Make sure the resource list is initialized
		try {
			Class resource_list_class = Class.forName( resource_list_class_name );

			if ( !ResourceList.class.isAssignableFrom( resource_list_class ) ) {
				throwKeyDefinitionError( null );
			}

			//noinspection unchecked
			this.resource_list_class =
				( Class<? extends ResourceList> ) resource_list_class;

			// Instantiate the ResourceList. This causes the keys to be initialized.
			// This should only be done once per list.
			Constructor cons = resource_list_class.getDeclaredConstructor();
			cons.setAccessible( true );
			cons.newInstance();

			inited = true;
		}
		catch ( Exception ex ) {
			throwKeyDefinitionError( ex );
		}
	}


	abstract K getDefaultValue();


	void setKeyFieldName( String field_name,
		Class<? extends ResourceList> resource_list_class ) {

		// Handle setting twice
		if ( field_name.equals( key_field_name ) ) return;

		assert key_field_name == null : "key_field_name=" + key_field_name +
			" field_name=" + field_name;

		key_field_name = field_name;
		this.resource_list_class = resource_list_class;

		assert resource_list_class.getName().equals( resource_list_class_name ) :
			resource_list_class.getName() + " != " + resource_list_class_name;

		inited = true;
	}


	private void throwKeyDefinitionError( Exception cause ) {
		RuntimeException ex = new RuntimeException(
			"ResourceKeys must be defined as " +
				"static final fields in a class that extends ResourceList" );
		if ( cause != null ) ex.initCause( cause );

		throw ex;
	}


	private K cacheValue( Locale locale, K value ) {
		cached_locale = locale;
		cached_value = value;
		return value;
	}


	public void writeExternal( ObjectOutput out ) throws IOException {
		// Make sure keys know their field names in the ResourceLists
		if ( key_field_name == null ) initFieldNames();

		// VERSION
		out.writeByte( 0 );

		// RESOURCE LIST CLASS NAME
		writeString( out, resource_list_class_name );

		// KEY FIELD NAME
		writeString( out, key_field_name );
	}

	public void readExternal( ObjectInput in )
		throws IOException, ClassNotFoundException {

		// VERSION
		in.readByte();

		// RESOURCE LIST CLASS NAME
		resource_list_class_name = readString( in );

		// KEY FIELD NAME
		key_field_name = readString( in );

		inited = true;
		cached_locale = null;
		cached_value = null;
	}



	static void writeString( DataOutput out, String string )
		throws IOException {

		if ( string == null )
			out.writeBoolean( false );
		else {
			out.writeBoolean( true );
			out.writeUTF( string );
		}
	}

	static String readString( DataInput in ) throws IOException {
		if ( in.readBoolean() ) {
			return in.readUTF();
		}
		else
			return null;
	}
}