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
package de.hybris.platform.odata2services.odata.schema.property;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.odata2services.odata.schema.attribute.AnnotationGenerator;
import de.hybris.platform.odata2services.odata.schema.attribute.AttributeAnnotationListGenerator;

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AttributeAnnotationListGeneratorUnitTest
{
	private static final IntegrationObjectItemAttributeModel ATTRIBUTE = new IntegrationObjectItemAttributeModel();

	private final AttributeAnnotationListGenerator attributeAnnotationListGenerator = new AttributeAnnotationListGenerator();
	@Mock
	private AnnotationGenerator uniqueAttributeGenerator;
	@Mock
	private AnnotationGenerator nullableAttributeGenerator;
	@Mock
	private AnnotationGenerator partOfAttributeGenerator;


	@Before
	public void setUp()
	{
		attributeAnnotationListGenerator.setAnnotationGenerators(
				Arrays.asList(uniqueAttributeGenerator, nullableAttributeGenerator, partOfAttributeGenerator));
	}

	@Test
	public void testGenerateForUnique()
	{
		final AnnotationAttribute nullableFalse = givenNullableAttribute(false);
		final AnnotationAttribute uniqueAttribute = givenUniqueAttribute();
		final AnnotationAttribute partOfTrue = givenPartOfAttribute();
		final List<AnnotationAttribute> attributes = attributeAnnotationListGenerator.generate(ATTRIBUTE);
		assertThat(attributes).containsExactlyInAnyOrder(nullableFalse, uniqueAttribute, partOfTrue);
	}

	@Test
	public void testGenerateForNotUnique()
	{
		final AnnotationAttribute nullableTrue = givenNullableAttribute(true);
		givenNonUniqueAttribute();
		givenPartOfAttributeIsFalse();

		final List<AnnotationAttribute> attributes = attributeAnnotationListGenerator.generate(ATTRIBUTE);
		assertThat(attributes).containsExactly(nullableTrue);
	}

	private AnnotationAttribute givenNullableAttribute(final boolean nullable)
	{
		final AnnotationAttribute nullableAttribute = new AnnotationAttribute().setName("nullable").setText(Boolean.toString(nullable));
		when(nullableAttributeGenerator.isApplicable(ATTRIBUTE)).thenReturn(true);
		when(nullableAttributeGenerator.generate(ATTRIBUTE)).thenReturn(nullableAttribute);
		return nullableAttribute;
	}

	private AnnotationAttribute givenUniqueAttribute()
	{
		final AnnotationAttribute uniqueAttribute = new AnnotationAttribute().setName("isUnique").setText("true");
		when(uniqueAttributeGenerator.isApplicable(ATTRIBUTE)).thenReturn(true);
		when(uniqueAttributeGenerator.generate(ATTRIBUTE)).thenReturn(uniqueAttribute);
		return uniqueAttribute;
	}

	private void givenNonUniqueAttribute()
	{
		when(uniqueAttributeGenerator.isApplicable(ATTRIBUTE)).thenReturn(false);
	}

	private AnnotationAttribute givenPartOfAttribute()
	{
		final AnnotationAttribute partOfAttribute = new AnnotationAttribute().setName("PartOf").setText("true");
		when(partOfAttributeGenerator.isApplicable(ATTRIBUTE)).thenReturn(true);
		when(partOfAttributeGenerator.generate(ATTRIBUTE)).thenReturn(partOfAttribute);
		return partOfAttribute;
	}

	private void givenPartOfAttributeIsFalse()
	{
		when(partOfAttributeGenerator.generate(ATTRIBUTE)).thenReturn(null);
	}
}
