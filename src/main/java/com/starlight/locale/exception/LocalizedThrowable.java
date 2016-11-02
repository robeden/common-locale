package com.starlight.locale.exception;

import com.starlight.locale.ResourceKey;


/**
 * Version of {@link Throwable} localized with {@link com.starlight.locale.ResourceKey}.
 */
public class LocalizedThrowable extends Throwable {
	static final long serialVersionUID = 8993672879488315998L;

	private final ResourceKey<String> message;


	public LocalizedThrowable() {
		super();
		this.message = null;
	}

	public LocalizedThrowable( ResourceKey<String> message ) {
		super( message == null ? null : message.getValue() );
		this.message = message;
	}

	public LocalizedThrowable( ResourceKey<String> message, Throwable cause ) {
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
