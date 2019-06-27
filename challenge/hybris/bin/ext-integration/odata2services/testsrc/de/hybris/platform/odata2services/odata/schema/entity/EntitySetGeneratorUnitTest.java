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

import static de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils.toFullQualifiedName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.olingo.odata2.api.edm.provider.EntitySet;
import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EntitySetGeneratorUnitTest
{
	@Mock
	private PluralizingEntitySetNameGenerator nameGenerator;
	@InjectMocks
	private EntitySetGenerator entitySetGenerator;

	@SuppressWarnings("SameParameterValue")
	private static EntityType entityType(final String typeName)
	{
		final EntityType type = new EntityType();
		type.setName(typeName);
		return type;
	}

	@Test
	public void testGenerate()
	{
		doReturn("Products").when(nameGenerator).generate("Product");

		final EntitySet entitySet = entitySetGenerator.generate(entityType("Product"));

		assertThat(entitySet)
				.isNotNull()
				.hasFieldOrPropertyWithValue("name", "Products")
				.hasFieldOrPropertyWithValue("entityType", toFullQualifiedName("Product"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGenerateFailsPrecondition()
	{
		entitySetGenerator.generate(null);
	}

	@Test
	public void testGenerateNullEntityType()
	{
		assertThatThrownBy(() -> entitySetGenerator.generate(entityType(null)))
				.isInstanceOf(IllegalArgumentException.class);
	}
}