package net.imartin.flickr;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import net.imartin.flickr.xml.FlickrResponseParser;
import net.imartin.flickr.xml.FlickrResponseParser.HandlerType;


public class FlickrAuthorize extends Observable
{

	private String	       api_key, frob;
	private FlickrAuthInfo	authInfo;

	public FlickrAuthorize()
	{
		ResourceBundle labels = ResourceBundle.getBundle( "LabelsBundle" );
		this.api_key = labels.getString( "flickr_api_key" );
	}

	public static String generateSig( String cipher, String data )
	{
		String secret = ResourceBundle.getBundle( "LabelsBundle" ).getString( "flickr_secret" );
		data = secret + data;
		MessageDigest md5 = null;
		try
		{
			md5 = MessageDigest.getInstance( cipher );
		}
		catch( NoSuchAlgorithmException e )
		{
			e.printStackTrace();
			return "";
		}
		md5.reset();
		byte[] hashb = md5.digest( data.getBytes() );

		StringBuffer strbuf = new StringBuffer();
		for( int x = 0; x < hashb.length; ++x )
		{
			String hex = Integer.toHexString( 0xFF & hashb[x] );
			// we need to pad to 32 char MD5 sum in this case
			strbuf.append( ( hex.length() < 2 ) ? "0" + hex : hex );
		}
		return strbuf.toString();
	}

	public static String generateSig( String data )
	{
		return generateSig( "MD5", data );
	}

	@SuppressWarnings( "unchecked" )
	public boolean getFrob()
	{
		// TODO add error checking from returned XML
		String api_sig = generateSig( "api_key" + this.api_key + "method" + "flickr.auth.getFrob" );
		String location = String.format( "http://api.flickr.com/services/rest/?method=%s&api_key=%s&api_sig=%s",
		        "flickr.auth.getFrob", api_key, api_sig );
		URL url;
		String flickrFrob = null;
		try
		{
			url = new URL( location );

			BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );
			FlickrResponseParser parser = new FlickrResponseParser( in, HandlerType.SimpleString );
			in.close();

			List<String> response = (List<String>)parser.getResultList();
			flickrFrob = response.get( 0 );
		}
		catch( MalformedURLException e )
		{
			System.err.println( "Invalid URL: " + location );
			e.printStackTrace();
		}
		catch( IOException e )
		{
			System.err.println( "Unable to get Flickr response." );
			e.printStackTrace();
		}
		this.frob = flickrFrob;
		return true;
	}

	public String getLoginLink()
	{
		final String perms = "read";
		String sigString = "api_key" + api_key + "frob" + frob + "perms" + perms;
		String api_sig = generateSig( sigString );
		return String.format( "http://flickr.com/services/auth/?api_key=%s&perms=%s&frob=%s&api_sig=%s", api_key,
		        perms, frob, api_sig );
	}

	public boolean launchLogin( String url )
	{
		try
		{
			URI u = new URI( url );
			if( !Desktop.isDesktopSupported() ) return false;
			Desktop.getDesktop().browse( u );
		}
		catch( URISyntaxException e )
		{
			System.err.println( "Invalid URL: " + url );
			e.printStackTrace();
			return false;
		}
		catch( IOException e )
		{
			System.err.println( "Unable to open browser." );
			e.printStackTrace();
			return false;
		}
		setChanged();
		notifyObservers();
		return true;
	}

	@SuppressWarnings( "unchecked" )
	public boolean getToken()
	{
		// TODO add error checking from returned XML
		String api_sig = generateSig( "api_key" + this.api_key + "frob" + frob + "method" + "flickr.auth.getToken" );
		String location = String.format(
		        "http://api.flickr.com/services/rest/?method=%s&api_key=%s&frob=%s&api_sig=%s", "flickr.auth.getToken",
		        api_key, frob, api_sig );
		URL url;
		FlickrAuthInfo fai = null;
		try
		{
			url = new URL( location );

			BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );
			FlickrResponseParser parser = new FlickrResponseParser( in, HandlerType.FlickrAuthInfo );
			in.close();

			List<FlickrAuthInfo> response = (List<FlickrAuthInfo>)parser.getResultList();
			fai = response.get( 0 );
			System.out.println( "Auth Info returned: \n" + fai.toString() );
		}
		catch( MalformedURLException e )
		{
			System.err.println( "Invalid URL: " + location );
			e.printStackTrace();
		}
		catch( IOException e )
		{
			System.err.println( "Unable to get Flickr response." );
			e.printStackTrace();
		}
		this.authInfo = fai;
		return true;
	}

	public static void main( String[] args )
	{
		FlickrAuthorize auth = new FlickrAuthorize();
		auth.getFrob();
		auth.launchLogin( auth.getLoginLink() );
		System.out.print( "Press enter when ready: " );
		try
		{
			System.in.read();
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		auth.getToken();
		auth.authInfo.toString();
	}

	public String getApi_key()
	{
		return api_key;
	}

	public void setApi_key( String api_key )
	{
		this.api_key = api_key;
	}

	public FlickrAuthInfo getAuthInfo()
	{
		return authInfo;
	}

	public void setAuthInfo( FlickrAuthInfo authInfo )
	{
		this.authInfo = authInfo;
	}

	public void setFrob( String frob )
	{
		this.frob = frob;
	}

	@SuppressWarnings( "unchecked" )
	public static boolean checkToken( String token )
	{
		if( token == null || token.isEmpty() ) return false;

		ResourceBundle labels = ResourceBundle.getBundle( "LabelsBundle" );
		String api_key = labels.getString( "flickr_api_key" );
		String api_sig = generateSig( "api_key" + api_key + "auth_token" + token + "method" + "flickr.auth.checkToken" );
		String location = String.format(
		        "http://api.flickr.com/services/rest/?method=%s&api_key=%s&auth_token=%s&api_sig=%s",
		        "flickr.auth.checkToken", api_key, token, api_sig );
		URL url;
		FlickrAuthInfo returnStatus = null;
		try
		{
			url = new URL( location );

			BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );
			FlickrResponseParser parser = new FlickrResponseParser( in, HandlerType.FlickrAuthInfo );
			in.close();

			List<FlickrAuthInfo> response = (List<FlickrAuthInfo>)parser.getResultList();
			returnStatus = response.get( 0 );
		}
		catch( MalformedURLException e )
		{
			System.err.println( "Invalid URL: " + location );
			e.printStackTrace();
		}
		catch( IOException e )
		{
			System.err.println( "Unable to get Flickr response." );
			e.printStackTrace();
		}
		try
		{
			if( returnStatus.isError() )
				throw new NullPointerException(); // hack to make the "catch" block execute
			else if( returnStatus.getToken().equals( token ) )
				return true; // if the given token matched, there's nothing else to do
			else
			{
				Preferences prefs = FlickrPanel.getPreferences();
				prefs.put( FlickrTestConstants.PREF_FLICKRTOKEN, returnStatus.getToken() );
				prefs.put( FlickrTestConstants.PREF_FLICKRNSID, returnStatus.getNsid() );
				prefs.put( FlickrTestConstants.PREF_FLICKRPERMS, returnStatus.getPerms() );
				prefs.put( FlickrTestConstants.PREF_FLICKRUNAME, returnStatus.getUsername() );
				prefs.put( FlickrTestConstants.PREF_FLICKRFNAME, returnStatus.getFullname() );
				return true;
			}
		}
		catch( NullPointerException npe )
		{
			Preferences prefs = FlickrPanel.getPreferences();
			prefs.remove( FlickrTestConstants.PREF_FLICKRTOKEN );
			prefs.remove( FlickrTestConstants.PREF_FLICKRNSID );
			prefs.remove( FlickrTestConstants.PREF_FLICKRPERMS );
			prefs.remove( FlickrTestConstants.PREF_FLICKRUNAME );
			prefs.remove( FlickrTestConstants.PREF_FLICKRFNAME );
			return false;
		}
	}
}
