package info.msxlaunchers.android.openmsxlauncher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import info.msxlaunchers.android.openmsxlauncher.action.CreateEmptyDatabaseDialogAction;
import info.msxlaunchers.android.openmsxlauncher.action.RemoveSelectedGameDialogAction;
import info.msxlaunchers.android.openmsxlauncher.utils.DialogUtils;
import info.msxlaunchers.openmsx.game.repository.RepositoryData;
import info.msxlaunchers.openmsx.game.repository.RepositoryDataModule;
import info.msxlaunchers.openmsx.game.scan.ScannerModule;
import info.msxlaunchers.openmsx.launcher.builder.GameBuilder;
import info.msxlaunchers.openmsx.launcher.builder.GameBuilderModule;
import info.msxlaunchers.openmsx.launcher.data.game.Game;
import info.msxlaunchers.openmsx.launcher.data.repository.RepositoryGame;
import info.msxlaunchers.openmsx.launcher.extra.ExtraDataGetter;
import info.msxlaunchers.openmsx.launcher.extra.ExtraDataModule;
import info.msxlaunchers.openmsx.launcher.persistence.game.DatabaseNotFoundException;
import info.msxlaunchers.openmsx.launcher.persistence.game.GamePersister;
import info.msxlaunchers.openmsx.launcher.persistence.game.GamePersisterModule;
import info.msxlaunchers.openmsx.launcher.persistence.settings.SettingsPersisterModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	public static final GamePersister gamePersister;
	public static final RepositoryData repositoryData;
	public static final ExtraDataGetter extraDataGetter;
	public static final GameBuilder gameBuilder;

	static
	{
    	Injector injector = Guice.createInjector(new AppModule(),
				new SettingsPersisterModule(),
				new ScannerModule(),
				new GamePersisterModule(),
				new RepositoryDataModule(),
				new ExtraDataModule(),
				new GameBuilderModule());

		gamePersister = injector.getInstance( GamePersister.class );
		repositoryData = injector.getInstance( RepositoryData.class );
		extraDataGetter = injector.getInstance( ExtraDataGetter.class );
		gameBuilder = injector.getInstance( GameBuilder.class );
	}

	private static Map<String,RepositoryGame> repositoryInfoMap;

	//current state
	private static String currentDatabaseName;

	//adapters
	private DatabaseListArrayAdapter databasesListAdapter;
	private GameListArrayAdapter gamesListAdapter;

	@Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        if( savedInstanceState == null )
        {
        	//This is first time initialization - show splash screen
        	new LoadViewTask().execute();
        }
        else
        {
			setupView();
        }
    }

	private class LoadViewTask extends AsyncTask<Void, Integer, Void>
    {
		@Override
		protected void onPreExecute()
		{
	        setContentView( R.layout.splash );
		}

		@Override
		protected Void doInBackground( Void... params )
		{
			try
			{
				repositoryInfoMap = repositoryData.getRepositoryInfo();
			}
			catch( IOException ioe )
			{
				//in this case reset it
				repositoryInfoMap = null;
			}

			//TODO Get the default database
			currentDatabaseName = "ROMs";

			return null;
		}

		@Override
		protected void onPostExecute( Void result )
		{
        	setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED );
			setupView();
		}
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if( id == R.id.action_settings )
        {
            return true;
        }
        else if( id == R.id.action_create_empty_database )
        {
        	getAndProcessUserInputForEmptyDatabase();

			return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override  
    public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo )
    {
         super.onCreateContextMenu( menu, v, menuInfo );

         AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
         int position = (int) info.id;
         menu.setHeaderTitle( gamesListAdapter.getItem( position ) );

         MenuInflater inflater  = getMenuInflater();
         inflater.inflate( R.menu.game_context_menu, menu );
    }

    @Override  
    public boolean onContextItemSelected( MenuItem item )
    {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();

    	switch( item.getItemId() )
    	{
    		case R.id.edit_item:
    			editSelectedProfile( (int)info.id );
    			return true;
   
    		case R.id.remove_item:
    			removeSelectedProfile( (int)info.id );
    			return true;
         }

         return super.onContextItemSelected(item);  
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent intent )
    {
    	if( resultCode == RESULT_OK )
    	{
	        switch( requestCode )
	        {
	            case GlobalConstants.EDIT_PROFILE_REQUEST_CODE:
	        		Game oldGame = (Game)intent.getSerializableExtra( GlobalConstants.INTENT_KEYS.OLD_GAME.toString() );
	        		Game newGame = (Game)intent.getSerializableExtra( GlobalConstants.INTENT_KEYS.NEW_GAME.toString() );
	
	        		gamesListAdapter.update( oldGame, newGame );
	        		gamesListAdapter.notifyDataSetChanged();
	            	break;
	        }
        }
    }

	private void setupView()
	{
		//initialize the "regular" View
		setContentView( R.layout.activity_main );

        fillDatabasesList();

        fillGameList();
	}

    private void fillDatabasesList()
    {
    	Set<String> databases = gamePersister.getDatabases();
    	final List<String> databasesList = new ArrayList<>( databases );
    	Collections.sort( databasesList, String.CASE_INSENSITIVE_ORDER );

	    databasesListAdapter = new DatabaseListArrayAdapter( this, R.layout.database_list, R.id.database_name, databasesList );

    	// Callback
    	OnNavigationListener callback = new OnNavigationListener() {
    	    @Override
    	    public boolean onNavigationItemSelected( int position, long id )
    	    {
    	    	String selectedDatabase = databasesListAdapter.getItem( position );
    	    	if( !selectedDatabase.equals( currentDatabaseName ) )
    	    	{
    	    		currentDatabaseName = selectedDatabase;
    	    		fillGameList();
    	    	}
    	        return true;
    	    }
    	};

    	// Action Bar
    	ActionBar actions = getActionBar();
    	actions.setNavigationMode( ActionBar.NAVIGATION_MODE_LIST );
    	actions.setDisplayShowTitleEnabled( false );
    	actions.setListNavigationCallbacks( databasesListAdapter, callback );
    	actions.setSelectedNavigationItem( getCurrentDatabasePositionInList() );
    }

    private void fillGameList()
    {
    	ListView gameListView = (ListView)findViewById( R.id.gameListView );
    	gameListView.setChoiceMode( ListView.CHOICE_MODE_SINGLE );
    	registerForContextMenu( gameListView );
 
		Set<Game> games = null;
		try
		{
			games = gamePersister.getGames( currentDatabaseName );
		}
		catch( DatabaseNotFoundException | IOException e )
		{
			//This should't happen - ignore
		}

    	if( games != null )
		{
			final List<String> gameNames = new ArrayList<>( games.size() );
			final Map<String,Game> gamesMap = new HashMap<>();

			for( Game game:games )
			{
				gameNames.add( game.getName() );
				gamesMap.put( game.getName(), game );
			}

			//sort the game names array
			Collections.sort( gameNames, String.CASE_INSENSITIVE_ORDER );

	    	gameListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
	    		@Override
	    		public void onItemClick( AdapterView<?> parent, View view, int position, long id )
	    		{
	    			Toast.makeText( getApplicationContext(), gamesListAdapter.getGame( position ).getRomA(), Toast.LENGTH_SHORT )
	  		      	.show();
	    		}
	    	});

			gamesListAdapter = new GameListArrayAdapter( this, R.layout.game_list, R.id.game_name, gameNames, gamesMap, repositoryInfoMap );
			gameListView.setAdapter( gamesListAdapter );
		}
    }

    private void getAndProcessUserInputForEmptyDatabase()
    {
    	DialogUtils.showTextUserInputDialog( this, new CreateEmptyDatabaseDialogAction( gamePersister, databasesListAdapter, getActionBar() ) );
    }

    private void editSelectedProfile( int position )
    {
    	Intent editProfileIntent = new Intent( this, AddEditProfileActivity.class );

    	Game game = gamesListAdapter.getGame( position );

    	editProfileIntent.putExtra( GlobalConstants.INTENT_KEYS.EDIT_MODE.toString(), true );
    	editProfileIntent.putExtra( GlobalConstants.INTENT_KEYS.CURRENT_DATABASE.toString(), currentDatabaseName );

    	Bundle bundle = new Bundle();
    	bundle.putSerializable( GlobalConstants.INTENT_KEYS.GAME.toString(), game ); 
    	editProfileIntent.putExtras( bundle );

    	startActivityForResult( editProfileIntent, GlobalConstants.EDIT_PROFILE_REQUEST_CODE );
    }

    private void removeSelectedProfile( int position )
    {
    	String gameName = gamesListAdapter.getItem( position );
    	DialogUtils.showConfirmationDialog( this,
    			new RemoveSelectedGameDialogAction( gamePersister, gameName, currentDatabaseName, gamesListAdapter ) );
    }

    private int getCurrentDatabasePositionInList()
    {
    	int position = 0;

    	if( currentDatabaseName != null )
    	{
        	int size = databasesListAdapter.getCount();
	    	for( int index = 0; index < size; index++ )
	    	{
	    		if( currentDatabaseName.equals( databasesListAdapter.getItem( index ) ) )
	    		{
	    			position = index;
	    			break;
	    		}
	    	}
    	}

    	return position;
    }

    private class DatabaseListArrayAdapter extends ArrayAdapter<String>
    {
    	private final List<String> databaseNames;

    	public DatabaseListArrayAdapter( Context context, int resource, int textViewResourceId, List<String> databaseNames )
        {
        	super( context, resource, textViewResourceId, databaseNames );
        	this.databaseNames = databaseNames;
        }

        @Override
        public View getView( int position, View convertView, ViewGroup parent )
        {
        	LayoutInflater inflater = (LayoutInflater)getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        	View rowView = inflater.inflate( R.layout.database_list, parent, false );
        	TextView textView = (TextView)rowView.findViewById( R.id.database_name );

        	String text = databaseNames.get( position );
        	textView.setText( text );

        	return rowView;
        }
    }
}
