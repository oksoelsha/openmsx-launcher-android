package info.msxlaunchers.android.openmsxlauncher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FileChooser extends Activity
{
	private static final File ROOT_FOLDER = Environment.getExternalStorageDirectory();
	private static final File TOP_RELATIVE_FOLDER = new File( "/" );

	private enum INTENT_KEYS { CURRENT_FOLDER; };

	FileChooserArrayAdapter adapter = null;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.file_chooser );
		getActionBar().setDisplayHomeAsUpEnabled( false );


    	if( savedInstanceState == null )
    	{
    		showFolderContents( TOP_RELATIVE_FOLDER );
    	}
    	else
    	{
    		showFolderContents( new File( savedInstanceState.getString( INTENT_KEYS.CURRENT_FOLDER.toString() ) ) );
    	}
	}

	@Override
	protected void onSaveInstanceState( Bundle outState )
	{
	    super.onSaveInstanceState( outState );
	    outState.putString( INTENT_KEYS.CURRENT_FOLDER.toString(), adapter.getCurrentFolder() );
	}

	private void showFolderContents( final File currentFolder )
	{
		ListView listView = (ListView)findViewById( R.id.fileListView );
    	listView.setChoiceMode( ListView.CHOICE_MODE_SINGLE );

    	TextView currentFolderView = (TextView)findViewById( R.id.fileChooserCurrentFolderView );
		currentFolderView.setText( currentFolder.toString() );

    	adapter = getAdapter( currentFolder );
		listView.setAdapter( adapter );

    	listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
    		@Override
    		public void onItemClick( AdapterView<?> parent, View view, int position, long id )
    		{
    			File selectedFileOrFolder = adapter.getSelectedRealFileOrFolder( position );
    			if( selectedFileOrFolder == null )
    			{
    				//this is the 'Up' case
    				showFolderContents( currentFolder.getParentFile() );
    			}
    			else if( selectedFileOrFolder.isFile() )
    			{
	    			Intent returnIntent = new Intent();
	    			returnIntent.putExtra( GlobalConstants.INTENT_KEYS.CHOSEN_FILE.toString(),
	    					adapter.getSelectedRelativeFileOrFolder( position ).getAbsolutePath() );
	    			setResult( RESULT_OK, returnIntent );

	    			finish();
    			}
    			else
    			{
    				//this is the folder case
    				showFolderContents( adapter.getSelectedRelativeFileOrFolder( position ) );
    			}
    		}
    	});
	}

	private FileChooserArrayAdapter getAdapter( File currentFolder )
	{
    	File[] files = getRealFolder( currentFolder ).listFiles();

    	List<String> fileNamesList = new ArrayList<>( files.length + 1 );

    	//add a place holder for the 'Up' element
    	if( !currentFolder.equals( TOP_RELATIVE_FOLDER ) )
    	{
    		fileNamesList.add( null );
    	}

    	for( int index = 0; index < files.length; index++ )
    	{
    		fileNamesList.add( files[index].getName() );
    	}

    	//get which extensions to show from the caller
    	Intent intent = getIntent();

    	ArrayList<String> extensionsToFilter = intent.getStringArrayListExtra( GlobalConstants.INTENT_KEYS.EXTENSIONS.toString() );

    	return new FileChooserArrayAdapter( this, R.layout.game_list, R.id.game_name, fileNamesList, currentFolder,
    			files, extensionsToFilter );
	}

	private File getRealFolder( File folder )
	{
		return new File( ROOT_FOLDER, folder.toString() );
	}

	private class FileChooserArrayAdapter extends ArrayAdapter<String>
	{
		private final List<String> fileNames;
		private final File currentFolder;
		private final File realCurrentFolder;

	    FileChooserArrayAdapter( Context context, int resource, int textViewResourceId,
	    		List<String> fileNames, File currentFolder, File[] files, ArrayList<String> extensionsToFilter )
	    {
	    	super( context, resource, textViewResourceId, fileNames );

	    	int fileNamesListSize = fileNames.size();

	    	if( fileNamesListSize == 1 && fileNames.get( 0 ) == null )
	    	{
	    		//this is the case where the list contains only the Up element - no sorting necessary
		    	this.fileNames = fileNames;
	    	}
	    	else
	    	{
	    		//sort the folders and the files in two separate lists then combine them
	    		SortedSet<String> sortedFolderNamesSet = new TreeSet<String>( String.CASE_INSENSITIVE_ORDER );
	    		SortedSet<String> sortedFileNamesSet = new TreeSet<String>( String.CASE_INSENSITIVE_ORDER );

	    		for( int index = 0; index < files.length; index++ )
	    		{
	    			String filename = files[index].getName();
	    			String extension = filename.substring( filename.lastIndexOf( '.' ) + 1 ).toLowerCase();

	    			if( files[index].isFile() )
	    			{
		    			if( extensionsToFilter == null || extensionsToFilter.contains( extension ) )
		    			{
		    				//extensionsToFilter is optional and therefore can be null - if so then show all files
		    				sortedFileNamesSet.add( filename );
		    			}
	    			}
	    			else
	    			{
	    				sortedFolderNamesSet.add( filename );
	    			}
	    		}

		    	this.fileNames = new ArrayList<>( fileNamesListSize );

		    	if( fileNames.get( 0 ) == null )
		    	{
		    		this.fileNames.add( null );
		    	}

		    	this.fileNames.addAll( sortedFolderNamesSet );
		    	this.fileNames.addAll( sortedFileNamesSet );

		    	//update the list in the parent class
		    	fileNames.clear();
		    	fileNames.addAll( this.fileNames );
	    	}

	    	this.currentFolder = currentFolder;
	    	this.realCurrentFolder = getRealFolder( currentFolder );
	    }

	    @Override
	    public View getView( int position, View convertView, ViewGroup parent )
	    {
	    	LayoutInflater inflater = (LayoutInflater)getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	    	View rowView = inflater.inflate( R.layout.game_list, parent, false );

	    	ImageView imageView = (ImageView)rowView.findViewById( R.id.game_type_icon );
	    	TextView textViewName = (TextView)rowView.findViewById( R.id.game_name );
	    	TextView textViewSize = (TextView)rowView.findViewById( R.id.game_info );

	    	if( position == 0 && !currentFolder.equals( TOP_RELATIVE_FOLDER ) )
	    	{
	    		textViewName.setText( ".." );
	    		textViewSize.setText( R.string.parent_folder );
	    		imageView.setImageResource( R.drawable.up );
	    	}
	    	else
	    	{
		    	String text = fileNames.get( position );
		    	File file = new File( realCurrentFolder, fileNames.get( position ) );

		    	textViewName.setText( text );

		    	if( file.isFile() )
		    	{
			    	textViewSize.setText( (file.length() >> 10) + " KB" );
		    		imageView.setImageResource( R.drawable.file );
		    	}
		    	else
		    	{
			    	textViewSize.setText( R.string.folder );
		    		imageView.setImageResource( R.drawable.folder );
		    	}
	    	}

	    	return rowView;
	    }

	    String getCurrentFolder()
	    {
	    	return currentFolder.toString();
	    }

	    /**
	     * @return Selected real file or folder - or null if the selection is the Up element in the list
	     */
	    File getSelectedRealFileOrFolder( int position )
	    {
	    	if( fileNames.get( position ) == null )
	    	{
	    		return null;
	    	}
	    	else
	    	{
	    		return new File( realCurrentFolder, fileNames.get( position ) );
	    	}
	    }

	    File getSelectedRelativeFileOrFolder( int position )
	    {
	    	return new File( currentFolder, fileNames.get( position ) );
	    }
	}
}
