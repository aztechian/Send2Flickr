/**
 * 
 */
package net.imartin.flickr.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import net.imartin.flickr.FlickrAuthInfo;
import net.imartin.flickr.FlickrImageInfo;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author S364398
 * 
 */
public class FlickrResponseParser
{
	public enum HandlerType
	{
		FlickrImageInfo, SimpleString, FlickrAuthInfo
	};

	private static final long	serialVersionUID	= 5412977113523745110L;
	private BufferedReader		input				= null;
	private List<?>				resultList			= null;
	private HandlerType			handlerType			= HandlerType.SimpleString;

	/**
	 * 
	 */
	public FlickrResponseParser( HandlerType type )
	{
		handlerType = type;
		this.input = new BufferedReader( new InputStreamReader( System.in ) );
		parse();
	}

	public FlickrResponseParser( BufferedReader input, HandlerType type )
	{
		if( input == null )
			this.input = new BufferedReader( new InputStreamReader( System.in ) );
		else
			this.input = input;
		handlerType = type;
		parse();
	}

	public List<?> getResultList()
	{
		return this.resultList;
	}

	private void parse()
	{
		XMLReader reader = null;
		FlickrTestXMLHandler handler = null;
		try
		{
			reader = XMLReaderFactory.createXMLReader();
			if( HandlerType.FlickrImageInfo == handlerType )
				handler = new FlickrImageInfoHandler();
			else if( HandlerType.SimpleString == handlerType ) 
				handler = new SimpleStringHandler();
			else if( HandlerType.FlickrAuthInfo == handlerType )
				handler = new FlickrAuthHandler();

			reader.setContentHandler( handler );
			reader.parse( new InputSource( this.input ) );
		}
		catch( SAXException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if( reader != null ) resultList = handler.getResults();
		}
	}

	public class FlickrImageInfoHandler extends FlickrTestXMLHandler
	{
		private List<FlickrImageInfo>	results	= new ArrayList<FlickrImageInfo>();

		public void startElement( String uri, String localName, String name, Attributes attributes )
				throws SAXException
		{
			if( name.equalsIgnoreCase( "PHOTO" ) )
			{
				String pid = attributes.getValue( uri + "id" );
				String secret = attributes.getValue( uri + "secret" );
				String farm = attributes.getValue( uri + "farm" );
				String server = attributes.getValue( uri + "server" );
				String title = attributes.getValue( uri + "title" );
				String datetaken = attributes.getValue( uri + "datetaken" );

				results.add( new FlickrImageInfo( pid, null, secret, server, farm, title, null, datetaken ) );
			}
		}

		@Override
		public List<FlickrImageInfo> getResults()
		{
			return results;
		}
	}

	public class FlickrAuthHandler extends FlickrTestXMLHandler
	{
		boolean status = true;
		StringBuffer charData = null;
		FlickrAuthInfo fai = null;
		private List<FlickrAuthInfo>	results	= new ArrayList<FlickrAuthInfo>();

		public void startElement( String uri, String localName, String name, Attributes attributes )
				throws SAXException
		{
			if( "rsp".equals( name ) && "fail".equals( attributes.getValue( 0 ) ) ) status = false;
			if( !status )
			{
				if( "err".equals( name ) )
					throw new SAXException( "Error from flickr (" + attributes.getValue( "code" ) + "). "
							+ attributes.getValue( "msg" ) );
			}
			else
			{
				if( "auth".equals( name ) )
					fai = new FlickrAuthInfo();
				else if( "token".equals( name ) )
					charData = new StringBuffer();
				else if( "perms".equals( name ) )
					charData = new StringBuffer();
				else if( "user".equals( name ))
				{
					fai.setNsid( attributes.getValue( uri+"nsid" ) );
					fai.setFullname( attributes.getValue( uri+"fullname;" ) );
					fai.setUsername( attributes.getValue( uri+"username" ) );
				}
			}
		}

		@Override
		public void characters( char[] ch, int start, int length ) throws SAXException
		{
			if( charData != null )
				charData.append( ch, start, length );
		}

		@Override
		public void endElement( String uri, String localName, String name ) throws SAXException
		{
			if( charData != null )
			{
				if( "token".equals( name ) )
					fai.setToken( charData.toString() );
				if( "perms".equals( name ) )
					fai.setPerms( charData.toString() );
			}
			if( "auth".equals( name ) )
			{
				results.add( fai );
			}
				
		}

		@Override
		public List<FlickrAuthInfo> getResults()
		{
			return results;
		}

	}
	
	public class SimpleStringHandler extends FlickrTestXMLHandler
	{
		boolean					status		= true;
		StringBuffer			charData	= null;
		private List<String>	results		= new ArrayList<String>();

		@Override
		public void characters( char[] ch, int start, int length ) throws SAXException
		{
			if( charData != null )
				charData.append(ch, start, length);
		}

		@Override
		public void startElement( String uri, String localName, String name, Attributes attributes )
				throws SAXException
		{
			if( "rsp".equals( name ) && "fail".equals( attributes.getValue( 0 ) ) ) status = false;
			if( !status )
				if( "err".equals( name ) )
					throw new SAXException( "Error from flickr (" + attributes.getValue( "code" ) + "). "
							+ attributes.getValue( "msg" ) );
			charData = new StringBuffer();
		}

		@Override
		public void endElement( String uri, String localName, String name ) throws SAXException
		{
			if( charData != null )
				results.add( charData.toString() );
		}

		@Override
		public List<String> getResults()
		{
			return results;
		}
	}
}
