package info.msxlaunchers.android.openmsxlauncher.action;

import info.msxlaunchers.android.openmsxlauncher.LauncherException;

public interface DialogAction<E>
{
	/**
	 * @param something
	 * @throws LauncherException
	 */
	void execute( E something ) throws LauncherException;

	/**
	 * @throws LauncherException
	 */
	void execute() throws LauncherException;
}
