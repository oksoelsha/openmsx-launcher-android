package info.msxlaunchers.android.openmsxlauncher;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.google.inject.Guice;
import com.google.inject.Injector;

import info.msxlaunchers.openmsx.game.repository.RepositoryData;
import info.msxlaunchers.openmsx.game.repository.RepositoryDataModule;
import info.msxlaunchers.openmsx.game.scan.Scanner;
import info.msxlaunchers.openmsx.game.scan.ScannerModule;
import info.msxlaunchers.openmsx.launcher.builder.GameBuilderModule;
import info.msxlaunchers.openmsx.launcher.data.game.Game;
import info.msxlaunchers.openmsx.launcher.data.repository.RepositoryGame;
import info.msxlaunchers.openmsx.launcher.extra.ExtraDataModule;
import info.msxlaunchers.openmsx.launcher.persistence.game.DatabaseAlreadyExistsException;
import info.msxlaunchers.openmsx.launcher.persistence.game.DatabaseMaxBackupReachedException;
import info.msxlaunchers.openmsx.launcher.persistence.game.DatabaseNotFoundException;
import info.msxlaunchers.openmsx.launcher.persistence.game.DatabaseNullNameException;
import info.msxlaunchers.openmsx.launcher.persistence.game.GamePersister;
import info.msxlaunchers.openmsx.launcher.persistence.game.GamePersisterModule;
import info.msxlaunchers.openmsx.launcher.persistence.game.GameWithNullNameException;
import info.msxlaunchers.openmsx.launcher.persistence.settings.SettingsPersisterModule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayMessageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    // Get the message from the intent

		Intent intent = getIntent();
//	    String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

	    // Create the text view
	    TextView textView = new TextView(this);
	    textView.setTextSize(40);
  //  	textView.setText("done! " + message);

	    scan();

	    // Set the text view as the activity layout
	    setContentView(textView);

//	    File path = Environment.getExternalStorageDirectory();

	    //start openMSX
//	    Intent openMSXIntent = getPackageManager().getLaunchIntentForPackage("org.openmsx.android.openmsx");
//	    openMSXIntent.putExtra( "-carta", "/storage/emulated/0/openMSX/share/software/MG2-UK.ROM" );
	    
//	    startActivity(openMSXIntent);
	}

	private void scan()
	{
		Injector injector = Guice.createInjector(new AppModule(),
				new SettingsPersisterModule(),
				new ScannerModule(),
				new GamePersisterModule(),
				new RepositoryDataModule(),
				new ExtraDataModule(),
				new GameBuilderModule());

		GamePersister gamePersister = injector.getInstance( GamePersister.class );
		try
		{
			gamePersister.deleteDatabase( "test" );
		}
		catch (DatabaseNotFoundException | IOException e)
		{
			Log.d("error-delete", e.toString());
		}

		Scanner scanner = injector.getInstance( Scanner.class );

		try
		{
			scanner.scan( new String[] {"/storage/emulated/0/openMSX/share/software"},
					false,
					"test",
					true,
					false,
					"MSXturboR",
					true,
					false,
					true,
					false,
					true,
					false);
		}
		catch( GameWithNullNameException | DatabaseMaxBackupReachedException | DatabaseAlreadyExistsException |
				DatabaseNullNameException | DatabaseNotFoundException | IOException e )
		{
			Log.d("error-scan", e.toString());
		}

		try
		{
			Set<Game> games = gamePersister.getGames( "test" );
			for(Game game:games )
			{
				Log.d( "game-name", game.getName() + " - " + game.getMsxGenID() + " - " + game.getGenre1() );
			}
		}
		catch( DatabaseNotFoundException | IOException e )
		{
			Log.d("error-get", e.toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
