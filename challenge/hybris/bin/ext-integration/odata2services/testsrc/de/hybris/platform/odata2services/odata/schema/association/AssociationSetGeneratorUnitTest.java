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

import static de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils.toFullQualifiedName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator;

import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationEnd;
import org.apache.olingo.odata2.api.edm.provider.AssociationSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AssociationSetGeneratorUnitTest
{
	@Mock
	private EntitySetNameGenerator nameGenerator;
	@InjectMocks
	private AssociationSetGenerator generator;

	@Test
	public void testGenerate()
	{
		doReturn("Units").when(nameGenerator).generate("Unit");
		doReturn("Products").when(nameGenerator).generate("Product");

		final Association association = new Association()
				.setName("Product_unit_Unit_code")
				.setEnd1(new AssociationEnd().setType(toFullQualifiedName("Product")).setRole("product"))
				.setEnd2(new AssociationEnd().setType(toFullQualifiedName("Unit")).setRole("unit_of_measurement"));

		final AssociationSet associationSet = generator.generate(association);

		assertThat(associationSet)
				.isNotNull()
				.hasFieldOrPropertyWithValue("name", "Product_Units")
				.hasFieldOrPropertyWithValue("association", toFullQualifiedName("Product_unit_Unit_code"));
		assertThat(associationSet.getEnd1())
				.isNotNull()
				.hasFieldOrPropertyWithValue("entitySet", "Products")
				.hasFieldOrPropertyWithValue("role", "product");
		assertThat(associationSet.getEnd2())
				.isNotNull()
				.hasFieldOrPropertyWithValue("entitySet", "Units")
				.hasFieldOrPropertyWithValue("role", "unit_of_measurement");
	}

	@Test
	public void testGenerateNullAssosiation()
	{
		assertThatThrownBy(() -> generator.generate(null))
				.isInstanceOf(IllegalArgumentException.class);
	}
}