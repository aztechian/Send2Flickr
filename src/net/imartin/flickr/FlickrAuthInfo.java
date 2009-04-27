package net.imartin.flickr;

public class FlickrAuthInfo
{
	private String	token, perms, nsid, username, fullname = "";
	private boolean error = true;
	private String errMsg = "";
	private int errCode = 0;

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

	public boolean isError()
    {
    	return error;
    }

	public void setError( boolean error )
    {
    	this.error = error;
    }

	public String getErrMsg()
    {
    	return errMsg;
    }

	public void setErrMsg( String errMsg )
    {
    	this.errMsg = errMsg;
    }

	public int getErrCode()
    {
    	return errCode;
    }

	public void setErrCode( int errCode )
    {
    	this.errCode = errCode;
    }
}
