/*
 * Copyright(c) 2002-2010, Rob Eden
 * All rights reserved.
 */
package com.starlight.locale;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class must be extended by classes containing sets of
 * {@link ResourceKey ResourceKeys}. Other than extending the
 * class, no additional work is necessary.
 */
public abstract class ResourceList {
	private static final Map<Class, Object[][]> BUNDLE_MAP =
		new ConcurrentHashMap<Class, Object[][]>();


	/**
	 * Static method to create a {@link TextResourceKey}.
	 */
	protected static TextResourceKey text( String default_text ) {
		return new TextResourceKey( default_text );
	}

	/**
	 * Static method to create an {@link IconResourceKey}.
	 */
	protected static IconResourceKey icon( String icon_path ) {
		return new IconResourceKey( icon_path );
	}

	/**
	 * Static method to create an {@link IconResourceKey}.
	 */
	protected static IconResourceKey icon( String icon_path, boolean relative ) {
		return new IconResourceKey( icon_path, relative );
	}



	private final InnerBundle INNER_BUNDLE;


	public ResourceList() {
		Class clazz = getClass();
		INNER_BUNDLE = new InnerBundle( clazz );

		if ( !BUNDLE_MAP.containsKey( clazz ) ) {
			INNER_BUNDLE.getContents();
		}
	}


	private class InnerBundle extends ListResourceBundle {
		private final Class resource_list_class;

		InnerBundle( Class resource_list_class ) {
			this.resource_list_class = resource_list_class;
		}

		@Override
		protected Object[][] getContents() {
			Object[][] contents = BUNDLE_MAP.get( resource_list_class );
			if ( contents != null ) return contents;

			Map<String, Object> content_map = buildContentMap();

			contents = new Object[content_map.size()][];
			Iterator<Map.Entry<String, Object>> it = content_map.entrySet().iterator();
			for ( int i = 0; it.hasNext(); i++ ) {
				Map.Entry<String, Object> entry = it.next();

				contents[ i ] = new Object[2];
				contents[ i ][ 0 ] = entry.getKey();
				contents[ i ][ 1 ] = entry.getValue();
			}

			BUNDLE_MAP.put( resource_list_class, contents );

			return contents;
		}


		private Map<String, Object> buildContentMap() {
			Field[] fields = resource_list_class.getDeclaredFields();
			Map<String, Object> field_map = new HashMap<String, Object>();

			for ( Field field : fields ) {
				final int modifiers = field.getModifiers();

				// Only care about ResourceKey's
				if ( !ResourceKey.class.isAssignableFrom( field.getType() ) ) continue;

				// Make sure it's static final
				if ( !( Modifier.isStatic( modifiers ) &&
					Modifier.isFinal( modifiers ) ) ) {
					System.err
						.println( "WARNING: " + resource_list_class.getName() + "." +
							field.getName() +
							" is not static final. It will not be properly" +
							" initialized." );
					continue;
				}

				field.setAccessible( true );

				try {
					final ResourceKey key = ( ResourceKey ) field.get( field );

					if ( key == null ) {
						System.err.println(
							"A problem has occurred with a ResourceList field: \"" +
								field + "\". It will not be properly localized." );
						continue;
					}

					// Set the field name so the key knows how to reference itself
					key.setKeyFieldName( field.getName(), resource_list_class );

					field_map.put( field.getName(),
						( ( ResourceKey ) field.get( field ) ).getDefaultValue() );
				}
				catch ( IllegalAccessException ex ) {
					ex.printStackTrace();
				}
			}

			return field_map;
		}
	}
}