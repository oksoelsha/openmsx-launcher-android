package info.msxlaunchers.android.openmsxlauncher.action;

import info.msxlaunchers.android.openmsxlauncher.LauncherException;
import info.msxlaunchers.android.openmsxlauncher.LauncherExceptionCode;
import info.msxlaunchers.openmsx.launcher.persistence.game.DatabaseAlreadyExistsException;
import info.msxlaunchers.openmsx.launcher.persistence.game.DatabaseNullNameException;
import info.msxlaunchers.openmsx.launcher.persistence.game.GamePersister;

import java.io.IOException;

import android.app.ActionBar;
import android.widget.ArrayAdapter;

public class CreateEmptyDatabaseDialogAction implements DialogAction<String>
{
	private final GamePersister gamePersister;
	private final ArrayAdapter<String> adapter;
	private final ActionBar actionBar;

	public CreateEmptyDatabaseDialogAction( GamePersister gamePersister, ArrayAdapter<String> adapter, ActionBar actionBar )
	{
		this.gamePersister = gamePersister;
		this.adapter = adapter;
		this.actionBar = actionBar;
	}

	@Override
	public void execute( String name ) throws LauncherException
	{
    	try
    	{
    		gamePersister.createDatabase( name );

    		int insertionPoint = 0;
    		while( insertionPoint < adapter.getCount() && name.compareToIgnoreCase( adapter.getItem( insertionPoint ) ) > 0 )
    		{
    			insertionPoint++;
    		}
    		adapter.insert( name, insertionPoint );
    		adapter.notifyDataSetChanged();

    		//Set the selected database to the previously selected one.
    		//I had to do this because the database menu was set to the newly added database for some unknown reason.
    		int previousSelectionIndex = actionBar.getSelectedNavigationIndex();
    		actionBar.setSelectedNavigationItem( insertionPoint <= previousSelectionIndex ? (previousSelectionIndex + 1) : previousSelectionIndex );
    	}
    	catch( DatabaseAlreadyExistsException daee )
    	{
    		throw new LauncherException( LauncherExceptionCode.ERR_DATABASE_ALREADY_EXISTS, name );
    	}
    	catch( DatabaseNullNameException dnne )
    	{
    		throw new LauncherException( LauncherExceptionCode.ERR_DATABASE_NULL_NAME );
    	}    	
    	catch( IOException ioe )
    	{
    		throw new LauncherException( LauncherExceptionCode.ERR_IO );
    	}    	
	}

	@Override
	public void execute() throws LauncherException
	{
		//unused
	}
}
