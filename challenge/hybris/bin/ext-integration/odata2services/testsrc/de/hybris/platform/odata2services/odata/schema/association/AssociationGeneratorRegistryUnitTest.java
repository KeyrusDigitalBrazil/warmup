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
package de.hybris.platform.odata2services.odata.schema.association;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AssociationGeneratorRegistryUnitTest
{
	@InjectMocks
	private AssociationGeneratorRegistry registry;
	@Mock
	private OneToOneAssociationGenerator oneToOneAssociationGenerator;
	@Mock
	private AssociationGenerator complexAssociationGenerator;
	@Mock
	private IntegrationObjectItemAttributeModel attribute;

	@Before
	public void setup()
	{
		when(complexAssociationGenerator.isApplicable(attribute)).thenReturn(false);
	}

	@Test
	public void testGetAssociationGeneratorFound()
	{
		when(oneToOneAssociationGenerator.isApplicable(attribute)).thenReturn(true);
		registry.setAssociationGenerators(Arrays.asList(oneToOneAssociationGenerator, complexAssociationGenerator));

		final Optional<AssociationGenerator> associationGeneratorOptional = registry.getAssociationGenerator(attribute);

		assertThat(associationGeneratorOptional).isPresent();
		assertThat(associationGeneratorOptional.get()).isEqualTo(oneToOneAssociationGenerator);
	}

	@Test
	public void testGetAssociationGeneratorNotFound()
	{
		when(oneToOneAssociationGenerator.isApplicable(attribute)).thenReturn(false);
		registry.setAssociationGenerators(Arrays.asList(oneToOneAssociationGenerator, complexAssociationGenerator));

		final Optional<AssociationGenerator> associationGeneratorOptional = registry.getAssociationGenerator(attribute);

		assertThat(associationGeneratorOptional).isNotPresent();
	}

	@Test
	public void testGetAssociationGeneratorNullAttribute()
	{
		assertThatThrownBy(() -> registry.getAssociationGenerator(null)).isInstanceOf(IllegalArgumentException.class);
	}
}
