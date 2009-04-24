package net.imartin.flickr;

public class FlickrAuthInfo
{
	private String	token;
	private String	perms;
	private String	nsid;
	private String	username;
	private String	fullname;

	public String getToken()
	{
		return token;
	}

	public void setToken( String token )
	{
		this.token = token;
	}

	public String getPerms()
	{
		return perms;
	}

	public void setPerms( String perms )
	{
		this.perms = perms;
	}

	public String getNsid()
	{
		return nsid;
	}

	public void setNsid( String nsid )
	{
		this.nsid = nsid;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername( String username )
	{
		this.username = username;
	}

	public String getFullname()
	{
		return fullname;
	}

	public void setFullname( String fullname )
	{
		this.fullname = fullname;
	}

	public String toString()
	{
		return "Token: " + getToken() + "\nPerms: " + getPerms() + "\nNSID: " + getNsid() + "\nUsername: "
				+ getUsername() + "\nFullname: " + getFullname();
	}
}
