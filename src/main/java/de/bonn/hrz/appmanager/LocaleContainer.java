package de.bonn.hrz.appmanager;

import java.util.Locale;

public class LocaleContainer {
	private Locale locale;
	private String countryCode;
	private String languageName;

	public LocaleContainer(Locale locale) {
		this.locale = locale;
		this.countryCode = locale.getCountry().toLowerCase();
		this.languageName = locale.getDisplayLanguage(locale);
	}

	public Locale getLocale() {
		return locale;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getLanguageName() {
		return languageName;
	}
}