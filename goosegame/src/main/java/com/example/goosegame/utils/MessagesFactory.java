package com.example.goosegame.utils;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

// Factory for management of the messages
// (messages are read from messages.properties file, with key/value mechanism)
@Service
public class MessagesFactory {

	@Autowired
	private MessageSource messageSource;

	private MessageSourceAccessor accessor;

	@PostConstruct
	private void init() {
		accessor = new MessageSourceAccessor(messageSource, Locale.ENGLISH);
	}

	// Method that retrieves messages without placeholders
	// "Code" parameter must be the key in messages.properties file
	public String get(String code) {
		return accessor.getMessage(code);
	}

	// Method that retrieves messages with placeholders
	// "Code" parameter must be the key in messages.properties file
	public String get(String code, Object[] args) {
		return accessor.getMessage(code, args);
	}

}
