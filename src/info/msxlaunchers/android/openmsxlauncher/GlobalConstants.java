package info.msxlaunchers.android.openmsxlauncher;

/**
 * @author Sam Elsharif
 * @since
 *
 */
public interface GlobalConstants
{
	final int ADD_PROFILE_REQUEST_CODE = 0;
	final int EDIT_PROFILE_REQUEST_CODE = 1;

	final int FILE_CHOOSER_ROM_A_REQUEST_CODE = 2;
	final int FILE_CHOOSER_ROM_B_REQUEST_CODE = 3;
	final int FILE_CHOOSER_DISK_A_REQUEST_CODE = 4;
	final int FILE_CHOOSER_DISK_B_REQUEST_CODE = 5;
	final int FILE_CHOOSER_TAPE_REQUEST_CODE = 6;
	final int FILE_CHOOSER_HARDDISK_REQUEST_CODE = 7;
	final int FILE_CHOOSER_LASERDISC_REQUEST_CODE = 8;
	final int FILE_CHOOSER_SCRIPT_REQUEST_CODE = 9;

	enum INTENT_KEYS
	{
		GAME,
		OLD_GAME,
		NEW_GAME,
		CURRENT_DATABASE,
		EDIT_MODE,
		CHOSEN_FILE,
		EXTENSIONS
	}
}
