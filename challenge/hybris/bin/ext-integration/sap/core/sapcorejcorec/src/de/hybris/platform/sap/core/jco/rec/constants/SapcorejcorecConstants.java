/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.core.jco.rec.constants;

import java.io.File;
import java.io.IOException;

import de.hybris.platform.util.Utilities;
/**
 * Global class for all Sapcorejcorec constants. You can add global constants for your extension into this class.
 */
@SuppressWarnings("deprecation")
public final class SapcorejcorecConstants extends GeneratedSapcorejcorecConstants
{
	/**
	 * Name of the extension.
	 */
	public static final String EXTENSIONNAME = "sapcorejcorec";

	/**
	 * Constructor.
	 */
	private SapcorejcorecConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
	public static String getCanonicalPath()
	{
		try {
			return Utilities.getExtensionInfo("sapcorejcorec").getExtensionDirectory().getCanonicalPath() + File.separator;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
