/**
 * 
 */
package net.imartin.flickr;

import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author S364398
 * 
 */
public class FlickrAuthenticationDialog extends JDialog
{
	private static final long	serialVersionUID	= 45367344249356505L;
	ResourceBundle				labels				= null;
	FlickrAuthorize				auth				= null;
	JPanel						cards				= null;

	/**
	 * 
	 */
	public FlickrAuthenticationDialog()
	{
		labels = ResourceBundle.getBundle( "LabelsBundle" );

		cards = new JPanel( new CardLayout() );
		cards.add( getAuth1Panel(), "Auth1" );
		cards.add( getAuth2Panel(), "Auth2" );
		this.getContentPane().add( cards );
		this.setTitle( labels.getString( "auth_title" ) );
		( (CardLayout)cards.getLayout() ).first( cards );
		this.setPreferredSize( new Dimension( 400, 300 ) );
		this.setModal( true );
		pack();
	}

	/**
	 * @param owner
	 */
	public FlickrAuthenticationDialog( Frame owner )
	{
		super( owner );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 */
	public FlickrAuthenticationDialog( Dialog owner )
	{
		super( owner );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 */
	public FlickrAuthenticationDialog( Window owner )
	{
		super( owner );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param modal
	 */
	public FlickrAuthenticationDialog( Frame owner, boolean modal )
	{
		super( owner, modal );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 */
	public FlickrAuthenticationDialog( Frame owner, String title )
	{
		super( owner, title );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param modal
	 */
	public FlickrAuthenticationDialog( Dialog owner, boolean modal )
	{
		super( owner, modal );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 */
	public FlickrAuthenticationDialog( Dialog owner, String title )
	{
		super( owner, title );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param modalityType
	 */
	public FlickrAuthenticationDialog( Window owner, ModalityType modalityType )
	{
		super( owner, modalityType );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 */
	public FlickrAuthenticationDialog( Window owner, String title )
	{
		super( owner, title );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public FlickrAuthenticationDialog( Frame owner, String title, boolean modal )
	{
		super( owner, title, modal );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public FlickrAuthenticationDialog( Dialog owner, String title, boolean modal )
	{
		super( owner, title, modal );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 * @param modalityType
	 */
	public FlickrAuthenticationDialog( Window owner, String title, ModalityType modalityType )
	{
		super( owner, title, modalityType );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public FlickrAuthenticationDialog( Frame owner, String title, boolean modal, GraphicsConfiguration gc )
	{
		super( owner, title, modal, gc );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public FlickrAuthenticationDialog( Dialog owner, String title, boolean modal, GraphicsConfiguration gc )
	{
		super( owner, title, modal, gc );
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param owner
	 * @param title
	 * @param modalityType
	 * @param gc
	 */
	public FlickrAuthenticationDialog( Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc )
	{
		super( owner, title, modalityType, gc );
		// TODO Auto-generated constructor stub
	}

	private JPanel getAuth1Panel()
	{
		JPanel basePanel = new JPanel();
		basePanel.setBorder( BorderFactory.createEmptyBorder( 4, 10, 10, 10 ) );
		basePanel.setLayout( new BoxLayout( basePanel, BoxLayout.PAGE_AXIS ) );

		JLabel auth1Label = new JLabel( labels.getString( "auth_1" ) );
		auth1Label.setAlignmentX( CENTER_ALIGNMENT );
		basePanel.add( auth1Label );
		basePanel.add( Box.createVerticalStrut( 20 ) );
		JLabel auth2Label = new JLabel( labels.getString( "auth_2" ) );
		auth2Label.setAlignmentX( CENTER_ALIGNMENT );
		basePanel.add( auth2Label );
		basePanel.add( Box.createVerticalGlue() );
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.LINE_AXIS ) );
		buttonPanel.add( Box.createHorizontalGlue() );
		JButton authButton = new JButton( labels.getString( "auth_btn1" ) );
		authButton.addActionListener( new AuthActionListener() );
		JButton cancelButton = new JButton( labels.getString( "cancel_btn" ) );
		cancelButton.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent e )
			{
				setVisible( false );
			}
		} );
		buttonPanel.add( authButton );
		buttonPanel.add( Box.createHorizontalStrut( 4 ) );
		buttonPanel.add( cancelButton );
		basePanel.add( buttonPanel );
		JLabel auth3Label = new JLabel( labels.getString( "auth_3" ) );
		auth3Label.setAlignmentX( CENTER_ALIGNMENT );
		basePanel.add( auth3Label );
		return basePanel;
	}

	private JPanel getAuth2Panel()
	{
		JPanel basePanel = new JPanel();
		basePanel.setBorder( BorderFactory.createEmptyBorder( 4, 10, 10, 10 ) );
		basePanel.setLayout( new BoxLayout( basePanel, BoxLayout.PAGE_AXIS ) );

		JLabel auth1Label = new JLabel( labels.getString( "auth_4" ) );
		auth1Label.setAlignmentX( CENTER_ALIGNMENT );
		basePanel.add( auth1Label );
		basePanel.add( Box.createVerticalStrut( 20 ) );
		JLabel auth2Label = new JLabel( labels.getString( "auth_5" ) );
		auth2Label.setAlignmentX( CENTER_ALIGNMENT );
		basePanel.add( auth2Label );
		basePanel.add( Box.createVerticalGlue() );
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.LINE_AXIS ) );
		buttonPanel.add( Box.createHorizontalGlue() );
		JButton authButton = new JButton( labels.getString( "auth_btn2" ) );
		authButton.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				auth.getToken();
				FlickrAuthInfo authInfo = auth.getAuthInfo();
				Preferences prefs = Preferences.userNodeForPackage( getClass() );
				prefs.put( FlickrTestConstants.PREF_FLICKRTOKEN, authInfo.getToken() );
				prefs.put( FlickrTestConstants.PREF_FLICKRNSID, authInfo.getNsid() );
				prefs.put( FlickrTestConstants.PREF_FLICKRPERMS, authInfo.getPerms() );
				prefs.put( FlickrTestConstants.PREF_FLICKRUNAME, authInfo.getUsername() );
				prefs.put( FlickrTestConstants.PREF_FLICKRFNAME, authInfo.getFullname() );
				try
				{
					prefs.flush();
				}
				catch( BackingStoreException e1 )
				{
					System.out.println("Error storing preferences!");
					e1.printStackTrace();
				}
			}
		} );
		JButton cancelButton = new JButton( labels.getString( "cancel_btn" ) );
		cancelButton.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent e )
			{
				setVisible( false );
			}
		} );
		buttonPanel.add( authButton );
		buttonPanel.add( Box.createHorizontalStrut( 4 ) );
		buttonPanel.add( cancelButton );
		basePanel.add( buttonPanel );
		JLabel auth3Label = new JLabel( labels.getString( "auth_6" ) );
		auth3Label.setAlignmentX( CENTER_ALIGNMENT );
		basePanel.add( auth3Label );
		return basePanel;
	}

	private class AuthActionListener implements ActionListener, Observer
	{
		@Override
		public void actionPerformed( ActionEvent e )
		{
			//TODO verify return values
			auth = new FlickrAuthorize();
			auth.addObserver( this );
			auth.getFrob();
			auth.launchLogin( auth.getLoginLink() );
		}
		
		@Override
		public void update( Observable o, Object arg )
		{
			((CardLayout)cards.getLayout()).show( cards, "Auth2" );
		}
	}
}
