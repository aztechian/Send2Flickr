package net.imartin.flickr;

import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;

public class FlickrImageInfo
{
	private static final String	base_url	= "http://farm%s.static.flickr.com/%s/%s_%s_%s.jpg";
	private static final String	size		= "s";
	private String				id, owner, secret, server, farm, title, ownername, datetaken;
	private URL					url;
	private ImageIcon				image;

	public FlickrImageInfo( String id, String owner, String secret, String server, String farm, String title,
			String ownername, String datetaken )
	{
		setId( id );
		setOwner( owner );
		setSecret( secret );
		setServer( server );
		setFarm( farm );
		setTitle( title );
		setOwnername( ownername );
		setDatetaken( datetaken );
		setURL( generateURL() );
	}

	public FlickrImageInfo( String id, String secret, String server, String farm )
	{
		setId( id );
		setSecret( secret );
		setServer( server );
		setFarm( farm );
		setURL( generateURL() );
	}

	public String generateURL()
	{
		return String.format( base_url, getFarm(), getServer(), getId(), getSecret(), size );
	}

	public String getId()
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner( String owner )
	{
		this.owner = owner;
	}

	public String getSecret()
	{
		return secret;
	}

	public void setSecret( String secret )
	{
		this.secret = secret;
	}

	public String getServer()
	{
		return server;
	}

	public URL getURL()
	{
		return this.url;
	}

	public void setServer( String server )
	{
		this.server = server;
	}

	public String getFarm()
	{
		return farm;
	}

	public void setFarm( String farm )
	{
		this.farm = farm;
	}

	public void setImage( ImageIcon image )
	{
		this.image = image;
	}

	public ImageIcon getImage()
	{
		return image;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle( String title )
	{
		this.title = title;
	}

	public String getOwnername()
	{
		return ownername;
	}

	public void setOwnername( String ownername )
	{
		this.ownername = ownername;
	}

	public String getDatetaken()
	{
		return datetaken;
	}

	public void setDatetaken( String datetaken )
	{
		this.datetaken = datetaken;
	}

	public void setURL( String url )
	{
		try
		{
			this.url = new URL( url );
		}
		catch( MalformedURLException e )
		{
			System.out.println( "Error during image URL setting:\n" + e.getMessage() );
		}
	}

}
