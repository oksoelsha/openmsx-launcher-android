package info.msxlaunchers.android.openmsxlauncher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import info.msxlaunchers.android.openmsxlauncher.utils.DialogUtils;
import info.msxlaunchers.openmsx.common.FileTypeUtils;
import info.msxlaunchers.openmsx.launcher.data.extra.ExtraData;
import info.msxlaunchers.openmsx.launcher.data.game.Game;
import info.msxlaunchers.openmsx.launcher.persistence.game.DatabaseNotFoundException;
import info.msxlaunchers.openmsx.launcher.persistence.game.GameAlreadyExistsException;
import info.msxlaunchers.openmsx.launcher.persistence.game.GameNotFoundException;
import info.msxlaunchers.openmsx.launcher.persistence.game.GameWithNullNameException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AddEditProfileActivity extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_add_edit_profile );
		getActionBar().setDisplayHomeAsUpEnabled( false );

		Intent intent = getIntent();

		if( intent.getBooleanExtra( GlobalConstants.INTENT_KEYS.EDIT_MODE.toString(), false ) )
		{
			getActionBar().setTitle( R.string.edit_profile );

			Game game = (Game)intent.getSerializableExtra( GlobalConstants.INTENT_KEYS.GAME.toString() );

	        TextView nameField = (TextView) findViewById( R.id.nameView );
	        nameField.setText( game.getName() );

	        TextView infoField = (TextView) findViewById( R.id.infoFileView );
	        infoField.setText( game.getInfo() );

	        TextView romAField = (TextView) findViewById( R.id.romAFileView );
	        romAField.setText( game.getRomA() );

	        TextView diskAField = (TextView) findViewById( R.id.diskAFileView );
	        diskAField.setText( game.getDiskA() );

	        TextView tapeField = (TextView) findViewById( R.id.tapeFileView );
	        tapeField.setText( game.getTape() );
		}
	}

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent intent )
    {
    	if( resultCode == RESULT_OK )
    	{
    		String selectedFile = intent.getStringExtra( GlobalConstants.INTENT_KEYS.CHOSEN_FILE.toString() );

	        switch( requestCode )
	        {
	            case GlobalConstants.FILE_CHOOSER_ROM_A_REQUEST_CODE:
	    	        TextView romAField = (TextView)findViewById( R.id.romAFileView );
	    	        romAField.setText( selectedFile );
	            	break;
	            case GlobalConstants.FILE_CHOOSER_ROM_B_REQUEST_CODE:
	    	        TextView romBField = (TextView)findViewById( R.id.romBFileView );
	    	        romBField.setText( selectedFile );
	            	break;
	            case GlobalConstants.FILE_CHOOSER_DISK_A_REQUEST_CODE:
	    	        TextView diskAField = (TextView)findViewById( R.id.diskAFileView );
	    	        diskAField.setText( selectedFile );
	            	break;
	            case GlobalConstants.FILE_CHOOSER_DISK_B_REQUEST_CODE:
	    	        TextView diskBField = (TextView)findViewById( R.id.diskBFileView );
	    	        diskBField.setText( selectedFile );
	            	break;
	            case GlobalConstants.FILE_CHOOSER_TAPE_REQUEST_CODE:
	    	        TextView tapeField = (TextView)findViewById( R.id.tapeFileView );
	    	        tapeField.setText( selectedFile );
	            	break;
	            case GlobalConstants.FILE_CHOOSER_HARDDISK_REQUEST_CODE:
	    	        TextView harddiskField = (TextView)findViewById( R.id.harddiskFileView );
	    	        harddiskField.setText( selectedFile );
	            	break;
	            case GlobalConstants.FILE_CHOOSER_LASERDISC_REQUEST_CODE:
	    	        TextView laserdiscField = (TextView)findViewById( R.id.laserdiscFileView );
	    	        laserdiscField.setText( selectedFile );
	            	break;
	            case GlobalConstants.FILE_CHOOSER_SCRIPT_REQUEST_CODE:
	    	        TextView scriptField = (TextView)findViewById( R.id.scriptFileView );
	    	        scriptField.setText( selectedFile );
	            	break;
	        }
    	}
    }

	public void handleEdit( View view )
	{
		try
		{
			Game oldGame = (Game)getIntent().getSerializableExtra( GlobalConstants.INTENT_KEYS.GAME.toString() );
			Game newGame = persistEditedGame();

			Toast.makeText( getApplicationContext(), R.string.profile_edited, Toast.LENGTH_SHORT ).show();

			finish( oldGame, newGame, RESULT_OK );
		}
		catch( LauncherException le )
		{
			DialogUtils.showErrorMessage( this, le );
		}
	}

	public void handleBrowseRomA( View view )
	{
		Intent intent = new Intent( getBaseContext(), FileChooser.class );

		intent.putCharSequenceArrayListExtra( GlobalConstants.INTENT_KEYS.EXTENSIONS.toString(),
				getExtenstionsToFilter( FileTypeUtils.getROMExtensions(), FileTypeUtils.getZIPExtensions() ) );

		startActivityForResult( intent, GlobalConstants.FILE_CHOOSER_ROM_A_REQUEST_CODE );
	}

	public void handleBrowseRomB( View view )
	{
		Intent intent = new Intent( getBaseContext(), FileChooser.class );

		intent.putCharSequenceArrayListExtra( GlobalConstants.INTENT_KEYS.EXTENSIONS.toString(),
				getExtenstionsToFilter( FileTypeUtils.getROMExtensions(), FileTypeUtils.getZIPExtensions() ) );

		startActivityForResult( intent, GlobalConstants.FILE_CHOOSER_ROM_B_REQUEST_CODE );
	}

	public void handleBrowseDiskA( View view )
	{
		Intent intent = new Intent( getBaseContext(), FileChooser.class );

		intent.putCharSequenceArrayListExtra( GlobalConstants.INTENT_KEYS.EXTENSIONS.toString(),
				getExtenstionsToFilter( FileTypeUtils.getDiskExtensions(), FileTypeUtils.getZIPExtensions() ) );

		startActivityForResult( intent, GlobalConstants.FILE_CHOOSER_DISK_A_REQUEST_CODE );
	}

	public void handleBrowseDiskB( View view )
	{
		Intent intent = new Intent( getBaseContext(), FileChooser.class );

		intent.putCharSequenceArrayListExtra( GlobalConstants.INTENT_KEYS.EXTENSIONS.toString(),
				getExtenstionsToFilter( FileTypeUtils.getDiskExtensions(), FileTypeUtils.getZIPExtensions() ) );

		startActivityForResult( intent, GlobalConstants.FILE_CHOOSER_DISK_B_REQUEST_CODE );
	}

	public void handleBrowseTape( View view )
	{
		Intent intent = new Intent( getBaseContext(), FileChooser.class );

		intent.putCharSequenceArrayListExtra( GlobalConstants.INTENT_KEYS.EXTENSIONS.toString(),
				getExtenstionsToFilter( FileTypeUtils.getTapeExtensions(), FileTypeUtils.getZIPExtensions() ) );

		startActivityForResult( intent, GlobalConstants.FILE_CHOOSER_TAPE_REQUEST_CODE );
	}

	public void handleBrowseHarddisk( View view )
	{
		Intent intent = new Intent( getBaseContext(), FileChooser.class );

		intent.putCharSequenceArrayListExtra( GlobalConstants.INTENT_KEYS.EXTENSIONS.toString(),
				getExtenstionsToFilter( FileTypeUtils.getDiskExtensions(), FileTypeUtils.getZIPExtensions() ) );

		startActivityForResult( intent, GlobalConstants.FILE_CHOOSER_HARDDISK_REQUEST_CODE );
	}

	public void handleBrowseLaserdisc( View view )
	{
		Intent intent = new Intent( getBaseContext(), FileChooser.class );

		intent.putCharSequenceArrayListExtra( GlobalConstants.INTENT_KEYS.EXTENSIONS.toString(),
				getExtenstionsToFilter( FileTypeUtils.getLaserdiscExtensions(), FileTypeUtils.getZIPExtensions() ) );

		startActivityForResult( intent, GlobalConstants.FILE_CHOOSER_LASERDISC_REQUEST_CODE );
	}

	public void handleBrowseScript( View view )
	{
		Intent intent = new Intent( getBaseContext(), FileChooser.class );

		startActivityForResult( intent, GlobalConstants.FILE_CHOOSER_SCRIPT_REQUEST_CODE );
	}

	public void handleCancel( View view )
	{
		finish( null, null, RESULT_CANCELED );
	}

	@SafeVarargs
	private final ArrayList<CharSequence> getExtenstionsToFilter( Set<String>... extensions )
	{
		ArrayList<CharSequence> arrayList = new ArrayList<>();

		for( Set<String> set:extensions )
		{
			arrayList.addAll( new ArrayList<String>( set ) );
		}
		arrayList.trimToSize();

		return arrayList;
	}

	private Game persistEditedGame() throws LauncherException
	{
		//Get old game
		Game oldGame = (Game)getIntent().getSerializableExtra( GlobalConstants.INTENT_KEYS.GAME.toString() );

		//Get current database
		String currentDatabase = getIntent().getCharSequenceExtra( GlobalConstants.INTENT_KEYS.CURRENT_DATABASE.toString() ).toString();

		//Create new Game object
		Map<String,ExtraData> extraDataMap = null;
		try
		{
			extraDataMap = MainActivity.extraDataGetter.getExtraData();
		}
		catch ( IOException ioe )
		{
			throw new LauncherException( LauncherExceptionCode.ERR_IO );
		}

		String name = ((TextView)findViewById( R.id.nameView )).getText().toString();
        String info = ((TextView)findViewById( R.id.infoFileView )).getText().toString();
        String machine = oldGame.getMachine();
        String romA = ((TextView)findViewById( R.id.romAFileView )).getText().toString();
        String romB = ((TextView)findViewById( R.id.romBFileView )).getText().toString();
        String diskA = ((TextView)findViewById( R.id.diskAFileView )).getText().toString();
        String diskB = ((TextView)findViewById( R.id.diskBFileView )).getText().toString();
        String tape = ((TextView)findViewById( R.id.tapeFileView )).getText().toString();
        String harddisk = ((TextView)findViewById( R.id.harddiskFileView )).getText().toString();
        String laserdisc = ((TextView)findViewById( R.id.laserdiscFileView )).getText().toString();
        String script = ((TextView)findViewById( R.id.scriptFileView )).getText().toString();

		Game newGame = null;
		try
		{
			newGame = MainActivity.gameBuilder.createGameObjectForDataEnteredByUser( name, info, machine, romA, romB, null,
					diskA, diskB, tape, harddisk, laserdisc, script, extraDataMap );
		}
		catch ( IllegalArgumentException iae )
		{
			throw new LauncherException( LauncherExceptionCode.ERR_EMPTY_GAME_FIELDS );
		}

		try
		{
			MainActivity.gamePersister.updateGame( oldGame, newGame, currentDatabase );
		}
		catch ( GameWithNullNameException gwnne )
		{
			throw new LauncherException( LauncherExceptionCode.ERR_GAME_WITH_NULL_NAME );
		}
		catch ( GameNotFoundException gnfe )
		{
			throw new LauncherException( LauncherExceptionCode.ERR_GAME_NOT_FOUND, oldGame.getName() );
		}
		catch ( GameAlreadyExistsException gaee )
		{
			throw new LauncherException( LauncherExceptionCode.ERR_GAME_ALREADY_EXISTS, newGame.getName() );
		}
		catch ( DatabaseNotFoundException dnfe )
		{
			throw new LauncherException( LauncherExceptionCode.ERR_DATABASE_NOT_FOUND, currentDatabase );
		}
		catch ( IOException ioe )
		{
			throw new LauncherException( LauncherExceptionCode.ERR_IO );
		}

		return newGame;
	}

	private void finish( Game oldGame, Game newGame, int resultCode )
	{
		Intent returnIntent = new Intent();

		if( oldGame != null && newGame != null )
		{
	    	Bundle bundle = new Bundle();

	    	bundle.putSerializable( GlobalConstants.INTENT_KEYS.OLD_GAME.toString(), oldGame ); 
	    	bundle.putSerializable( GlobalConstants.INTENT_KEYS.NEW_GAME.toString(), newGame ); 

	    	returnIntent.putExtras( bundle );
		}

		setResult( resultCode, returnIntent );

		finish();
	}
}
