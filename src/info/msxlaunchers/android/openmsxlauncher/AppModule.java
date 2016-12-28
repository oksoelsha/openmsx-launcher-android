/*
 * Copyright 2014 Sam Elsharif
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.msxlaunchers.android.openmsxlauncher;

import java.io.File;

import android.os.Environment;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * @since v1.0-Android
 * @author Sam Elsharif
 *
 */
public class AppModule extends AbstractModule
{
	private final static String GENERATION_MSX_URL = "http://www.generation-msx.nl/msxdb/softwareinfo/";
	private final static String USER_DATA_DIRECTORY;
	private final static String LAUNCHER_DATA_DIRECTORY;

	static
	{
		File externalStorage = Environment.getExternalStorageDirectory();
		LAUNCHER_DATA_DIRECTORY = new File( externalStorage, "openMSX Launcher" ).toString();

		USER_DATA_DIRECTORY = LAUNCHER_DATA_DIRECTORY;
	}

	@Override
	protected void configure()
	{
		bind( String.class ).annotatedWith( Names.named( "UserDataDirectory" ) ).toInstance( USER_DATA_DIRECTORY );
		bind( String.class ).annotatedWith( Names.named( "LauncherDataDirectory" ) ).toInstance( LAUNCHER_DATA_DIRECTORY );
		bind( String.class ).annotatedWith( Names.named( "GenerationMSXURL" ) ).toInstance( GENERATION_MSX_URL );
	}
}