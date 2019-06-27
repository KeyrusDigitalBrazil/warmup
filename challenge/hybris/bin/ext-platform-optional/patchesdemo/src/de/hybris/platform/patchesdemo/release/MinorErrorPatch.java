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
package de.hybris.platform.patchesdemo.release;

import de.hybris.platform.patches.Rerunnable;
import de.hybris.platform.patches.organisation.ImportLanguage;
import de.hybris.platform.patchesdemo.structure.CountryOrganisation;
import de.hybris.platform.patchesdemo.structure.Release;
import de.hybris.platform.patchesdemo.structure.ShopOrganisation;
import de.hybris.platform.patchesdemo.structure.StructureState;

import java.util.Set;


/**
 * Example patch which is switched off by default as it is containing broken actions which cause errors. Should be
 * switched on to demonstrate error handling or tracking of problems.
 */
public class MinorErrorPatch extends AbstractDemoPatch implements Rerunnable
{
	public MinorErrorPatch()
	{
		super("minor_error_patch", "patch_with_minor_error", Release.E1, StructureState.LAST);
	}

	@Override
	public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly)
	{
		// Add actions which are broken on purpose to show error handling:
		// test for importing impex file with wrong suffix
		importGlobalData("r03_00_010-globalDataExample.impex_", languages, updateLanguagesOnly);
		// test for not existing file
		importGlobalData("r03_00_020-notExistingFileExample.impex", languages, updateLanguagesOnly);
		// test for proper SQL command - it will pass if accelerator is used with electronics shop
		executeUpdateOnDB("A proper SQL command if electronics shop is present",
				"select lang.p_isocode from basestore bs, languages lang "
						+ "where bs.p_uid like '%electronics%' and bs.p_defaultlanguage = lang.PK");
		// test for improper SQL command - not existing table
		executeUpdateOnDB("Not existing table modification",
				"update incorrecttable set p_uid = 'electronics3' where p_uid like '%electronics%'");
		// test for importing impex file with wrong syntax
		// patchExecutionUnit error status and errorLog should be set
		importGlobalData("r03_00_030-globalDataWrongSyntaxExample.impex", languages, updateLanguagesOnly);
	}

	@Override
	public void createShopData(final ShopOrganisation unit, final Set<ImportLanguage> languages, final boolean updateLanguages)
	{
		// Add actions which are broken on purpose to show error handling
	}

	@Override
	public void createCountryData(final CountryOrganisation country)
	{
		// Add actions which are broken on purpose to show error handling
	}
}
