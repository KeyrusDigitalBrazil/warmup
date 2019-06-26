/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.addonsupport.setup;

import de.hybris.platform.addonsupport.impex.AddonConfigDataImportType;
import de.hybris.platform.commerceservices.setup.data.ImpexMacroParameterData;


/**
 * Defines services for addon configuration data import
 */
public interface AddOnConfigDataImportService
{
	/**
	 * Imports data
	 *
	 * @param extensionName
	 *           the extension name
	 * @param importType
	 *           the data type to import
	 * @param macroParameters
	 *           the impex macro parameters
	 * @return whether the import was successful or not
	 */
	boolean executeImport(final String extensionName, AddonConfigDataImportType importType,
			ImpexMacroParameterData macroParameters);
}
