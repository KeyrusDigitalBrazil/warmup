/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.datahub.core.services.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class ImpexMacroFragmentUnitTest extends AbstractScriptFragmentTest<ImpexMacroFragment>
{
	@Before
	public void setup()
	{
		fragment = new ImpexMacroFragment();
	}

	@Test
	public void testScriptFragmentIsEmptyBeforeAnyLineWasAdded()
	{
		assertThat(fragment.getContent()).isEmpty();
	}

	@Test
	public void testMacroCannotBeAdded() throws IOException
	{
		final String aMacro = "$catalogVersion=catalogversion(catalog(id[default=apparelProductCatalog]),version[default='Staged'])";

		final boolean wasAdded = fragment.addLine(aMacro, new ArrayList<>());

		assertLineIsInTheFragment(wasAdded, aMacro);
	}
}
