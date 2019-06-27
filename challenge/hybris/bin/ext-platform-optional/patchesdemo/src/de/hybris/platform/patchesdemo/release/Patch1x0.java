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

import de.hybris.platform.patches.organisation.ImportLanguage;
import de.hybris.platform.patchesdemo.structure.CountryOrganisation;
import de.hybris.platform.patchesdemo.structure.Release;
import de.hybris.platform.patchesdemo.structure.ShopOrganisation;
import de.hybris.platform.patchesdemo.structure.StructureState;

import java.util.Set;


/**
 * Example patch which imports example shop and country data.
 */
public class Patch1x0 extends AbstractDemoPatch
{
	public Patch1x0()
	{
		super("1_0", "01_00", Release.R1, StructureState.V1);
	}

	@Override
	public void createGlobalData(final Set<ImportLanguage> languages, final boolean updateLanguagesOnly)
	{
		importGlobalData("r01_00_010-globalDataExample.impex", languages, updateLanguagesOnly);
	}

	@Override
	public void createShopData(final ShopOrganisation unit, final Set<ImportLanguage> languages, final boolean updateLanguages)
	{
		importShopSpecificData("r01_00_010-shopDataExample.impex", languages, unit, updateLanguages);
		importShopSpecificData("r01_00_020-shopDataExample.impex", languages, unit, updateLanguages);
		importShopSpecificData("r01_00_030-shopDataExample.impex", languages, unit, updateLanguages);
		importShopSpecificData("r01_00_040-shopDataExample.impex", languages, unit, updateLanguages);
		importShopSpecificData("r01_00_050-shopDataExample.impex", languages, unit, updateLanguages);
		importShopCatalogVersionSpecificData("r01_00_060-shopDataExample.impex", languages, unit, updateLanguages);
	}

	@Override
	public void createCountryData(final CountryOrganisation country)
	{
		importCountryData("r01_00_010-countryDataExample.impex", country);
		importCountryData("r01_00_020-countryDataExample.impex", country);
		importCountryData("r01_00_030-countryDataExample.impex", country);
		importCountryData("r01_00_040-countryDataExample.impex", country);
	}

}
