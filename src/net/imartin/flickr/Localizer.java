package net.imartin.flickr;

import java.util.Locale;
import java.util.Observable;

public class Localizer extends Observable
{
	private static Localizer localizer = null;
	private Locale currentLocale = null;
	
	private Localizer()
	{
		String userLang = FlickrPanel.getPreferences().get( FlickrTestConstants.PREF_LANG, "en" );
		System.out.println("got locale of '" + userLang + "' from prefs.");
		currentLocale = new Locale(userLang);
		Locale.setDefault( currentLocale );
	}
	
	public static Localizer getLocalizer()
	{
		if( localizer == null )
			localizer = new Localizer();
		return localizer;
	}

	public void setLocale(String lang)
	{
		currentLocale = new Locale(lang);
		System.out.println("Setting current locale to '" + lang + "' in prefs.");
		FlickrPanel.getPreferences().put( FlickrTestConstants.PREF_LANG, lang );
		Locale.setDefault( currentLocale );
		setChanged();
		notifyObservers( currentLocale );
	}
}
