package net.imartin.flickr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DBAccess
{
	private static String url = "jdbc:postgresql://atlantis.imartin.net/gallery2";
	Properties prop = new Properties();
	Connection conn = null;
	
	public DBAccess()
	{
		init();
	}
	
	public Connection getDBConn()
	{
		return conn;
	}
	
	public ResultSet getAlbums()
	{
		if( conn == null )
			init();
		
		ResultSet rs = null;
		try
        {
			PreparedStatement st = conn.prepareStatement( "SELECT * FROM g2_item WHERE g_cancontainchildren = 1", ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY );
	        rs = st.executeQuery();
        }
        catch( SQLException e )
        {
	        e.printStackTrace();
        }
		return rs;
	}
	
	private void init()
	{
		prop.setProperty( "username", "gallery" );
		prop.setProperty( "password", "secret" );
		prop.setProperty( "ssl", "true" );
		
		try
        {
	        Class.forName( "org.postgresql.Driver" );
	        conn = DriverManager.getConnection( url, prop );
	        
        }
        catch( ClassNotFoundException e )
        {
        	System.err.println("Postgresql Driver not found!");
	        e.printStackTrace();
        }
        catch( SQLException e )
        {
	        e.printStackTrace();
        }
	}
}
