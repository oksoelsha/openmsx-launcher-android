package info.msxlaunchers.android.openmsxlauncher.action;

import info.msxlaunchers.android.openmsxlauncher.GameListArrayAdapter;
import info.msxlaunchers.android.openmsxlauncher.LauncherException;
import info.msxlaunchers.android.openmsxlauncher.LauncherExceptionCode;
import info.msxlaunchers.openmsx.launcher.data.game.Game;
import info.msxlaunchers.openmsx.launcher.persistence.game.DatabaseNotFoundException;
import info.msxlaunchers.openmsx.launcher.persistence.game.GameNotFoundException;
import info.msxlaunchers.openmsx.launcher.persistence.game.GamePersister;

import java.io.IOException;
import java.util.Map;

import android.widget.ArrayAdapter;

public class RemoveSelectedGameDialogAction implements DialogAction<Object>
{
	private final GamePersister gamePersister;
	private final String gameName;
	private final String databaseName;
	private final GameListArrayAdapter adapter;

	public RemoveSelectedGameDialogAction( GamePersister gamePersister, String gameName, String databaseName, GameListArrayAdapter adapter )
	{
		this.gamePersister = gamePersister;
		this.gameName = gameName;
		this.databaseName = databaseName;
		this.adapter = adapter;
	}

	@Override
	public void execute() throws LauncherException
	{
    	try
    	{
    		Game game = adapter.getGamesMap().get( gameName );
    		gamePersister.deleteGame( game, databaseName );

    		//clean up
    		adapter.remove( game.getName() );
    		adapter.notifyDataSetChanged();
    	}
    	catch( GameNotFoundException | DatabaseNotFoundException e )
    	{
    		//Shouldn't happen in normal circumstances - just ignore and proceed
		}
    	catch( IOException ioe )
    	{
    		throw new LauncherException( LauncherExceptionCode.ERR_IO );
    	}
	}

	@Override
	public void execute( Object obj ) throws LauncherException
	{
		//unused
	}
}
