package com.starlight.locale.exception;

import com.starlight.locale.ResourceKey;


/**
 * Version of {@link Exception} localized with {@link com.starlight.locale.ResourceKey}.
 */
public class LocalizedException extends Exception {
	static final long serialVersionUID = -2541994687677476286L;
	
	private final ResourceKey<String> message;


	public LocalizedException() {
		super();
		this.message = null;
	}


	public LocalizedException( ResourceKey<String> message ) {
		super( message == null ? null : message.getValue() );
		this.message = message;
	}

	public LocalizedException( ResourceKey<String> message, Throwable cause ) {
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
