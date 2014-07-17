package net.imartin.flickr;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.imartin.flickr.xml.FlickrResponseParser;
import net.imartin.flickr.xml.FlickrResponseParser.HandlerType;


public class FlickrPanel extends JFrame implements Observer
{

	class CustomCellRenderer extends DefaultListCellRenderer
	{
		private static final long	serialVersionUID	= 2124023877274957790L;

		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected,
		        boolean cellHasFocus )
		{
			super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
			if( value instanceof FlickrImageInfo )
			{
				FlickrImageInfo fii = (FlickrImageInfo)value;
				this.setIcon( fii.getImage() );
				this.setForeground( Color.GRAY );
				this.setText( ( fii.getTitle().length() > 10 ) ? fii.getTitle().substring( 0, 8 ) + "..." : fii
				        .getTitle() );
			}
			return this;
		}
	}

	private class ImageFetcher extends SwingWorker<Void, FlickrImageInfo>
	{
		private DefaultListModel	listModel	= null;

		ImageFetcher( DefaultListModel model )
		{
			this.listModel = model;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		protected Void doInBackground() throws Exception
		{
			FlickrResponseParser parser = null;
			List<FlickrImageInfo> flickrInfoList = null;
			try
			{
				String location = String.format(
				                "http://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=%s&extras=owner_name,date_taken&per_page=%s&page=1",
				                FlickrPanel.this.labels.getString( "flickr_api_key" ), getImageCountCombo().getSelectedItem() );
				URL url = new URL( location );
				// System.out.println("Requesting URL: " + location);

				BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );
				parser = new FlickrResponseParser( in, HandlerType.FlickrImageInfo );
				in.close();
			}
			catch( MalformedURLException urle )
			{}
			catch( IOException ioe )
			{}
			if( parser != null )
				flickrInfoList = (List<FlickrImageInfo>)parser.getResultList();
			else
				flickrInfoList = new ArrayList<FlickrImageInfo>();

			int processed = 0;
			setProgress( processed );
			for( FlickrImageInfo fii : flickrInfoList )
			{
				try
				{
					fii.setImage( new ImageIcon( fii.getURL() ) );
					publish( fii );
					setProgress( ( ++processed * 100 ) / flickrInfoList.size() );
				}
				catch( Exception e )
				{
					e.printStackTrace();
					this.cancel( true );
					break;
				}
			}
			return null;
		}

		@Override
		protected void process( List<FlickrImageInfo> chunks )
		{
			for( FlickrImageInfo icon : chunks )
			{
				listModel.addElement( icon );
				// System.out.println("Added image " + listModel.getSize());
			}
		}

	}

	private static final long	serialVersionUID	= -6625931845544961374L;

	/**
	 * @param args
	 */
	public static void main( String[] args )
	{
      System.out.println("Starting " + Package.getPackage("net.imartin.flickr").getImplementationTitle() + ", version " +
         Package.getPackage("net.imartin.flickr").getImplementationVersion());
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				new FlickrPanel();
			}
		} );
	}

	private JButton	               fetchButton	     = null;
	private ResourceBundle	       labels	         = null;
	private JScrollPane	           sideScroll	     = null;
	private JList	               sideList	         = null;
	private JProgressBar	       progBar	         = null;
	private JComboBox	           imageCountCombo	 = null;
	private JMenuBar	           menuBar	         = null;
	private EditMenuActionListener	editMenuListener	= new EditMenuActionListener();
	private Preferences	           prefs	         = null;

	public FlickrPanel()
	{
		prefs = Preferences.userNodeForPackage( getClass() );
		String token = prefs.get( FlickrTestConstants.PREF_FLICKRTOKEN, null );
		Localizer.getLocalizer();
		labels = ResourceBundle.getBundle( "LabelsBundle" );
		
		init();
		this.setPreferredSize( new Dimension( 800, 600 ) );
		this.pack();
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setVisible( true );
		if( token == null && !FlickrAuthorize.checkToken( token ) )
			JOptionPane.showMessageDialog( this, "Exising token is no longer valid.\nYou must re-authorize before uploading.", "Token no longer valid", JOptionPane.INFORMATION_MESSAGE );
		
	}

	private JButton getFetchButton()
	{
		if( fetchButton == null )
		{
			String txt = labels.getString( "fetch" );
			fetchButton = new JButton( txt );
			fetchButton.setPreferredSize( new Dimension( 100, 25 ) );
			fetchButton.addActionListener( new ActionListener()
			{

				@Override
				public void actionPerformed( ActionEvent e )
				{
					ImageFetcher fetcher = new ImageFetcher( (DefaultListModel)getSideList().getModel() );
					fetcher.addPropertyChangeListener( new PropertyChangeListener()
					{
						@Override
						public void propertyChange( PropertyChangeEvent evt )
						{
							if( "state".equals( evt.getPropertyName() )
							        && SwingWorker.StateValue.DONE == evt.getNewValue() )
							{
								fetchButton.setEnabled( true );
								fetchButton.setText( labels.getString( "fetch" ) );
								getProgBar().setValue( getProgBar().getMinimum() );
								getProgBar().setStringPainted( false );
								getProgBar().setEnabled( false );
							}
							if( "progress".equals( evt.getPropertyName() ) )
							{
								JProgressBar pbar = getProgBar();
								int progress = (Integer)evt.getNewValue();

								if( progress < 1 ) pbar.setString( labels.getString( "pbar_images" ) );
								if( pbar.isIndeterminate() ) pbar.setIndeterminate( false );
								pbar.setString( null );
								pbar.setValue( progress );
							}
						}
					} );

					fetchButton.setEnabled( false );
					fetchButton.setText( labels.getString( "fetching" ) );
					getProgBar().setEnabled( true );
					getProgBar().setIndeterminate( true );
					getProgBar().setStringPainted( true );
					getProgBar().setString( labels.getString( "pbar_fetching" ) );
					fetcher.execute();
				}
			} );
		}
		return fetchButton;
	}

	private JComboBox getImageCountCombo()
	{
		if( imageCountCombo == null )
		{
			imageCountCombo = new JComboBox( new Integer[]{5, 10, 25, 50, 75, 100, 200} );
			imageCountCombo.setEditable( false );
			imageCountCombo.setPrototypeDisplayValue( "MMMM" );
		}
		return imageCountCombo;
	}

	private JMenuBar getMenu()
	{
		//TODO move this to its own "MenuBar" class. That way it can handle locale updates on its own
		if( menuBar == null )
		{
			JMenu tempMenu;
			JMenuItem tempItem;
			menuBar = new JMenuBar();

			tempMenu = new JMenu( labels.getString( "menu_file" ) );
			tempMenu.setMnemonic( KeyEvent.VK_F );
			menuBar.add( tempMenu );

			tempItem = new JMenuItem( labels.getString( "menu_file_new" ) );
			tempMenu.add( tempItem );

			tempMenu = new JMenu( labels.getString( "menu_edit" ) );
			tempMenu.setMnemonic( KeyEvent.VK_E );
			menuBar.add( tempMenu );

			tempItem = new JMenu( labels.getString( "menu_edit_lang" ) );
			ButtonGroup group = new ButtonGroup();
			JRadioButtonMenuItem tempRadioButton = new JRadioButtonMenuItem( labels.getString( "lang_en" ), true );
			tempRadioButton.setActionCommand( "LANG_en" );
			tempRadioButton.addActionListener( editMenuListener );
			group.add( tempRadioButton );
			tempItem.add( tempRadioButton );

			tempRadioButton = new JRadioButtonMenuItem( labels.getString( "lang_fr" ) );
			tempRadioButton.setActionCommand( "LANG_fr" );
			tempRadioButton.addActionListener( editMenuListener );
			group.add( tempRadioButton );
			tempItem.add( tempRadioButton );

			tempRadioButton = new JRadioButtonMenuItem( labels.getString( "lang_de" ) );
			tempRadioButton.setActionCommand( "LANG_de" );
			tempRadioButton.addActionListener( editMenuListener );
			group.add( tempRadioButton );
			tempItem.add( tempRadioButton );

			tempRadioButton = new JRadioButtonMenuItem( labels.getString( "lang_pt" ) );
			tempRadioButton.setActionCommand( "LANG_pt" );
			tempRadioButton.addActionListener( editMenuListener );
			group.add( tempRadioButton );
			tempItem.add( tempRadioButton );

			tempRadioButton = new JRadioButtonMenuItem( labels.getString( "lang_ar" ) );
			tempRadioButton.setActionCommand( "LANG_ar" );
			tempRadioButton.addActionListener( editMenuListener );
			group.add( tempRadioButton );
			tempItem.add( tempRadioButton );
			tempMenu.add( tempItem );

			String authStatus = prefs.get( FlickrTestConstants.PREF_FLICKRUNAME, null );
			if( authStatus == null )
				tempItem = new JMenuItem( labels.getString( "menu_edit_auth" ) );
			else
				tempItem = new JMenuItem( labels.getString( "menu_edit_deauth" ) + " " + authStatus);
			tempItem.setActionCommand( "AUTH" );
			tempItem.addActionListener( editMenuListener );
			tempMenu.add( tempItem );

		}
		return menuBar;
	}

	private JProgressBar getProgBar()
	{
		if( progBar == null )
		{
			progBar = new JProgressBar();
			progBar.setEnabled( false );
			progBar.setVisible( true );
			progBar.setStringPainted( false );
		}
		return progBar;
	}

	private JList getSideList()
	{
		if( sideList == null )
		{
			sideList = new JList( new DefaultListModel() );
			sideList.setCellRenderer( new CustomCellRenderer() );
		}
		return sideList;
	}

	private JScrollPane getSideScroll()
	{
		if( sideScroll == null )
		{
			sideScroll = new JScrollPane( getSideList() );
			sideScroll.setPreferredSize( new Dimension( 200, 300 ) );
		}
		return sideScroll;
	}

	private void init()
	{
		this.setJMenuBar( getMenu() );
		java.awt.Container cp = this.getContentPane();
		cp.setLayout( new GridBagLayout() );
		GridBagConstraints defaultGBC = new GridBagConstraints( 0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
		        GridBagConstraints.NONE, new Insets( 2, 2, 2, 2 ), 0, 0 );
		GridBagConstraints constraints = (GridBagConstraints)defaultGBC.clone();

		constraints.weighty = 1.0;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.VERTICAL;
		constraints.gridheight = GridBagConstraints.RELATIVE;
		cp.add( getSideScroll(), constraints );

		constraints = (GridBagConstraints)defaultGBC.clone();
		constraints.gridx = 10;
		constraints.gridy = 9;
		cp.add( getImageCountCombo(), constraints );

		constraints.gridy = 10;
		cp.add( getFetchButton(), constraints );

		constraints.gridx = 0;
		constraints.gridy = 20;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weighty = 1.0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		cp.add( getProgBar(), constraints );
	}

	private class EditMenuActionListener implements ActionListener
	{

		@Override
		public void actionPerformed( ActionEvent e )
		{
			if( e.getActionCommand().length() >= 7 && "LANG_".equals( e.getActionCommand().substring( 0, 5 ) ) )
			{
				String newLocCode = e.getActionCommand().substring( 5 );
				Localizer.getLocalizer().setLocale( newLocCode );
			}
			else if( e.getActionCommand().equals( "AUTH" ) )
			{
				if( ((JMenuItem)e.getSource()).getText().equals( labels.getObject("menu_edit_auth") ) )
				{
					FlickrAuthenticationDialog d = new FlickrAuthenticationDialog();
					d.setVisible( true );
				}
				else
				{
					prefs.remove( FlickrTestConstants.PREF_FLICKRTOKEN );
					prefs.remove( FlickrTestConstants.PREF_FLICKRNSID );
					prefs.remove( FlickrTestConstants.PREF_FLICKRPERMS );
					prefs.remove( FlickrTestConstants.PREF_FLICKRUNAME );
					prefs.remove( FlickrTestConstants.PREF_FLICKRFNAME );
				}
			}
		}

	}
	
	public Preferences getPrefs()
	{
		return prefs;
	}
	
	public static Preferences getPreferences()
	{
		return Preferences.userNodeForPackage( FlickrPanel.class );
	}

	@Override
    public void update( Observable o, Object arg )
    {
	    if( o instanceof Localizer && arg instanceof Locale )
	    {
	    	//we got a locale change notification
	    	labels = ResourceBundle.getBundle( "LabelsBundle" );
			getFetchButton().setText( labels.getString( "fetch" ) );
			this.setTitle( labels.getString( "title" ) );
	    }
    }

	private class MenuLocalizer
	{
		private Vector<AbstractButton> obs = new Vector<AbstractButton>();
		
        public void notifyObservers( Object arg )
        {
	        /*
	         * a temporary array buffer, used as a snapshot of the state of
	         * current Observers.
	         */
	        AbstractButton[] arrLocal;

	        synchronized (this) {
	            /* We don't want the Observer doing callbacks into
	             * arbitrary code while holding its own Monitor.
	             * The code where we extract each Observable from
	             * the Vector and store the state of the Observer
	             * needs synchronization, but notifying observers
	             * does not (should not).  The worst result of any
	             * potential race-condition here is that:
	             * 1) a newly-added Observer will miss a
	             *   notification in progress
	             * 2) a recently unregistered Observer will be
	             *   wrongly notified when it doesn't care
	             */
	            arrLocal = obs.toArray(new AbstractButton[] {});
	        }

	        for (int i = arrLocal.length-1; i>=0; i--)
	            arrLocal[i].setText( "test");
        }

        public synchronized void addObserver( AbstractButton o )
        {
	        if (o == null)
	            throw new NullPointerException();
			//only add AbstractButton inheritants. This allows calling setText()
	        if( o instanceof AbstractButton && !obs.contains( o ))
	        	obs.addElement(o);
        }

        public synchronized void deleteObserver( Observer o )
        {
	        // TODO Auto-generated method stub
        }

        public synchronized void deleteObservers()
        {
	        // TODO Auto-generated method stub
        }

        public void notifyObservers()
        {
	        // TODO Auto-generated method stub
        }
		
	}
}
