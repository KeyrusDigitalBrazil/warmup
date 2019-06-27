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

package de.hybris.platform.odata2services.odata.schema.entity;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;

@UnitTest
public class PluralizingEntitySetNameGeneratorUnitTest
{
	private final PluralizingEntitySetNameGenerator generator = new PluralizingEntitySetNameGenerator();

	@Test
	public void testGenerate()
	{
		assertThat(generator.generate("Product")).isEqualTo("Products");
		assertThat(generator.generate("Category")).isEqualTo("Categories");
		assertThat(generator.generate("Mouse")).isEqualTo("Mice");
	}
}