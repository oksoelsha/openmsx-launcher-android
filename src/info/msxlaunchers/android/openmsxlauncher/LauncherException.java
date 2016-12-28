package info.msxlaunchers.android.openmsxlauncher;

@SuppressWarnings("serial")
public class LauncherException extends Exception
{
	private final LauncherExceptionCode code;
	private final String additionalString;

	public LauncherException( LauncherExceptionCode code )
	{
		this( code, null );
	}

	public LauncherException( LauncherExceptionCode code, String additionalString )
	{
		this.code = code;
		this.additionalString = additionalString;
	}

	public LauncherExceptionCode getCode()
	{
		return code;
	}

	public String getCodeAsString()
	{
		return code.toString();
	}

	public String getAdditionalString()
	{
		return additionalString;
	}
}
