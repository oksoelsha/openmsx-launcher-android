package info.msxlaunchers.android.openmsxlauncher;

import info.msxlaunchers.openmsx.common.Utils;
import info.msxlaunchers.openmsx.launcher.data.game.Game;
import info.msxlaunchers.openmsx.launcher.data.repository.RepositoryGame;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GameListArrayAdapter extends ArrayAdapter<String>
{
	private final List<String> gameNames;
	private final Map<String,Game> gamesMap;
	private final Map<String,RepositoryGame> repositoryInfoMap;

    public GameListArrayAdapter( Context context, int resource, int textViewResourceId, List<String> gameNames,
    		Map<String,Game> gamesMap, Map<String,RepositoryGame> repositoryInfoMap )
    {
    	super( context, resource, textViewResourceId, gameNames );
    	this.gameNames = gameNames;
    	this.gamesMap = gamesMap;
    	this.repositoryInfoMap = repositoryInfoMap;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
    	LayoutInflater inflater = (LayoutInflater)getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    	View rowView = inflater.inflate( R.layout.game_list, parent, false );
    	ImageView imageView = (ImageView)rowView.findViewById( R.id.game_type_icon );
    	TextView textViewName = (TextView)rowView.findViewById( R.id.game_name );
    	TextView textViewInfoAndSize = (TextView)rowView.findViewById( R.id.game_info );

    	String text = gameNames.get( position );
    	textViewName.setText( text );

    	textViewInfoAndSize.setText( getGameInfoAndSize( gamesMap.get( text ) ) );

    	if( gamesMap.get( text ).isROM() )
    	{
    		imageView.setImageResource( R.drawable.rom );
    	}
    	else if( gamesMap.get( text ).isDisk() )
    	{
    		imageView.setImageResource( R.drawable.disk );
    	}
    	else if( gamesMap.get( text ).isTape() )
    	{
    		imageView.setImageResource( R.drawable.tape );
    	}
    	else if( gamesMap.get( text ).isLaserdisc() )
    	{
    		imageView.setImageResource( R.drawable.laserdisc );
    	}
    	else if( gamesMap.get( text ).isScript() )
    	{
    		imageView.setImageResource( R.drawable.script );
    	}

    	return rowView;
    }

    @Override
    public void remove( String name )
    {
    	super.remove( name );
    	gamesMap.remove( name );
    }

    public void update( Game oldGame, Game newGame )
    {
    	//update the map
    	gamesMap.remove( oldGame.getName() );
    	gamesMap.put( newGame.getName(), newGame );

    	//update the array list
    	if( !oldGame.getName().equals( newGame.getName() ) )
    	{
    		gameNames.remove( oldGame.getName() );
    		gameNames.add( newGame.getName() );
    		Collections.sort( gameNames, String.CASE_INSENSITIVE_ORDER );
    	}
    }

    public Map<String,Game> getGamesMap()
    {
    	return gamesMap;
    }

    public Game getGame( int position )
    {
    	return gamesMap.get( gameNames.get( position ) );
    }

    private String getGameInfoAndSize( Game game )
    {
    	RepositoryGame repositoryData = null;
    	String company = null;
    	String year = null;

    	if( repositoryInfoMap != null )
    	{
    		repositoryData = repositoryInfoMap.get( game.getSha1Code() );
    		if( repositoryData != null )
    		{
    			company = repositoryData.getCompany();
    			year = repositoryData.getYear();
    		}
    	}

    	//divide size by 1024
    	long size = game.getSize() >> 10;

    	String gameInfoAndSize;
		if( Utils.isEmpty( company ) && Utils.isEmpty( year ) )
		{
			if( size == 0.0 )
			{
				//size 0 is for files with real size of 0, files that don't exist and scripts
				gameInfoAndSize = "";
			}
			else
			{
				gameInfoAndSize = size + " KB";
			}
		}
		else
		{
			gameInfoAndSize = company + " " + year + " - " + size + " KB";
		}

		return gameInfoAndSize;
    }
}
