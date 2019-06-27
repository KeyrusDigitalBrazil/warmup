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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.CsticData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


@UnitTest
public class ConfigUISupportTest
{

	@Test
	public void testHasRequiredCsticNullObject()
	{
		assertFalse(ConfigUISupport.hasRequiredCstic(null));
	}

	@Test
	public void testHasRequiredCsticEmptyList()
	{
		final List<CsticData> cstics = new ArrayList<>();

		assertFalse(ConfigUISupport.hasRequiredCstic(cstics));
	}

	@Test
	public void testHasRequiredCsticListWithoutRequiredCstic()
	{
		final List<CsticData> cstics = new ArrayList<>();
		cstics.add(createCstic("A", false));

		assertFalse(ConfigUISupport.hasRequiredCstic(cstics));
	}

	@Test
	public void testHasRequiredCsticListWithRequiredCstic()
	{
		final List<CsticData> cstics = new ArrayList<>();
		cstics.add(createCstic("A", true));

		assertTrue(ConfigUISupport.hasRequiredCstic(cstics));
	}

	@Test
	public void testHasRequiredCsticListWithManyCsticRequired()
	{
		final List<CsticData> cstics = new ArrayList<>();
		cstics.add(createCstic("A", false));
		cstics.add(createCstic("B", false));
		cstics.add(createCstic("C", false));
		cstics.add(createCstic("D", true));
		cstics.add(createCstic("E", false));

		assertTrue(ConfigUISupport.hasRequiredCstic(cstics));
	}


	private CsticData createCstic(final String name, final boolean required)
	{
		final CsticData cstic = new CsticData();

		cstic.setName(name);
		cstic.setRequired(required);

		return cstic;
	}
}
