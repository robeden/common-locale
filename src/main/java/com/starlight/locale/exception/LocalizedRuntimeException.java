package com.starlight.locale.exception;

import com.starlight.locale.ResourceKey;


/**
 * Version of {@link RuntimeException} localized with
 * {@link com.starlight.locale.ResourceKey}.
 */
public class LocalizedRuntimeException extends RuntimeException {
	static final long serialVersionUID = 3715038979463330632L;
	
	private final ResourceKey<String> message;


	public LocalizedRuntimeException() {
		super();
		this.message = null;
	}


	public LocalizedRuntimeException( ResourceKey<String> message ) {
		super( message == null ? null : message.getValue() );
		this.message = message;
	}

	public LocalizedRuntimeException( ResourceKey<String> message, Throwable cause ) {
		super( message == null ? null : message.getValue(), cause );
		this.message = message;
	}


	@Override
	public String getLocalizedMessage() {
		return message == null ? null : message.getValue();
	}


	public ResourceKey<String> getMessageResourceKey() {
		return message;
	}
}
