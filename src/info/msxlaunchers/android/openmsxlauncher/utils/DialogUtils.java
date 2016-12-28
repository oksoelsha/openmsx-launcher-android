package info.msxlaunchers.android.openmsxlauncher.utils;

import info.msxlaunchers.android.openmsxlauncher.LauncherException;
import info.msxlaunchers.android.openmsxlauncher.R;
import info.msxlaunchers.android.openmsxlauncher.R.id;
import info.msxlaunchers.android.openmsxlauncher.R.layout;
import info.msxlaunchers.android.openmsxlauncher.R.string;
import info.msxlaunchers.android.openmsxlauncher.action.DialogAction;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DialogUtils
{
	public static void showTextUserInputDialog( final Context context, final DialogAction<String> dialogAction )
	{
    	LayoutInflater li = LayoutInflater.from( context );
		View promptsView = li.inflate( R.layout.user_input, null );

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( context );

		alertDialogBuilder.setView( promptsView );

		final EditText userInput = (EditText)promptsView.findViewById( R.id.editTextDialogUserInput );

		alertDialogBuilder
			.setCancelable( false )
			.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
			    public void onClick( DialogInterface dialog, int id ) {
			    	try
			    	{
						dialogAction.execute( userInput.getText().toString() );
					}
			    	catch( LauncherException le )
					{
			    		dialog.cancel();
			    		showErrorMessage( context, le );
					}
			    }
			})
			.setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick( DialogInterface dialog, int id ) {
			    	dialog.cancel();
				}
			});

		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}

	public static void showConfirmationDialog( final Context context, final DialogAction<Object> dialogAction )
	{
    	LayoutInflater li = LayoutInflater.from( context );
		View promptsView = li.inflate( R.layout.confirmation, null );

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( context );

		alertDialogBuilder.setView( promptsView );

		alertDialogBuilder
			.setCancelable( false )
			.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener() {
			    public void onClick( DialogInterface dialog, int id ) {
			    	try
			    	{
						dialogAction.execute();
					}
			    	catch( LauncherException le )
					{
			    		dialog.cancel();
			    		showErrorMessage( context, le );
					}
			    }
			})
			.setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick( DialogInterface dialog, int id ) {
			    	dialog.cancel();
				}
			});

		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}

	public static void showErrorMessage( final Context context, LauncherException le )
	{
    	LayoutInflater li = LayoutInflater.from( context );
		View errorView = li.inflate( R.layout.error, null );

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( context );

		alertDialogBuilder.setView( errorView );

		final TextView errorMessage = (TextView)errorView.findViewById( R.id.errorMessage );

		StringBuilder buffer = new StringBuilder( context.getString( context.getResources().
				getIdentifier( le.getCodeAsString().toString(), "string", context.getPackageName() ) ) );

		String additionalString = le.getAdditionalString();
		if(additionalString != null)
		{
			buffer.append( ": " ).append( additionalString );
		}

		errorMessage.setText( buffer.toString() );

		alertDialogBuilder
			.setCancelable( false )
			.setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick( DialogInterface dialog, int id ) {
			    	dialog.cancel();
				}
			});

		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}
}
