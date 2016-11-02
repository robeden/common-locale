/*
 * Copyright(c) 2002-2010, Rob Eden
 * All rights reserved.
 */
package com.starlight.locale;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Locale;


/**
 *
 */
public class IconResourceKey extends ResourceKey<Icon> {
	static final long serialVersionUID = 0L;
	
	private String default_icon_path;
	private boolean relative;

	private ImageIcon cached_icon;
	private String cached_path;


	/**
	 * FOR EXTERNALIZATION ONLY!!
	 */
	public IconResourceKey() {
		super( true );
	}

	public IconResourceKey( String default_icon_path ) {
		this( default_icon_path, true );
	}

	public IconResourceKey( String default_icon_path, boolean relative ) {
		super( false );

		if ( !relative ) default_icon_path = "/" + default_icon_path;
		
		this.default_icon_path = default_icon_path;
	}


	public Image getValueAsImage() {
		return ( ( ImageIcon ) getValue() ).getImage();
	}

	public Image getValueAsImage( Locale locale ) {
		return ( ( ImageIcon ) getValue( locale ) ).getImage();
	}

	public Image getValueAsImage( Locale locale, ClassLoader cl ) {
		return ( ( ImageIcon ) getValue( locale, cl ) ).getImage();
	}


	protected ImageIcon convertToValue( String string, Locale locale ) {
		return loadIcon( string );
	}

	ImageIcon getDefaultValue() {
		return convertToValue( default_icon_path, null );
	}


	private synchronized ImageIcon loadIcon( String path ) {
		if ( cached_path != null && cached_path.equals( path ) ) return cached_icon;

		Class resource_class = getResourceListClass();
		if ( resource_class == null ) {
			return null;
		}

		URL duke_of = resource_class.getResource( path );
		if ( duke_of == null ) {
			System.err.println( "Icon not found for path \"" + path + "\" relative to " +
				resource_class.getName() );
			return null;
		}

		cached_icon = new ImageIcon( duke_of );
		cached_path = path;

		return cached_icon;
	}
}