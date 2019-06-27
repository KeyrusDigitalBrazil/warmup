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
package de.hybris.platform.odata2services.odata.schema.attribute;

import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder;
import static de.hybris.platform.integrationservices.model.MockAttributeDescriptorModelBuilder.attributeDescriptorModelBuilder;
import static de.hybris.platform.odata2services.odata.schema.attribute.AttributeGeneratorUnitTestHelper.assertAttribute;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.junit.Test;

@UnitTest
public class NullableAttributeGeneratorUnitTest
{
	private final NullableAttributeGenerator generator = new NullableAttributeGenerator();

	@Test
	public void testIsApplicableNullModel()
	{
		assertThat(generator.isApplicable(null))
				.isFalse();
	}

	@Test
	public void testIsApplicableNullDescriptor()
	{
		assertThat(generator.isApplicable(simpleAttributeBuilder().withAttributeDescriptor(null).build()))
				.isFalse();
	}

	@Test
	public void testIsApplicableNotNull()
	{
		assertThat(generator.isApplicable(simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptorModelBuilder()
					.withDefaultValue("val"))
				.build()))
				.isTrue();
	}

	@Test
	public void testGenerateWhenTrueUniqueFalseOptionalAndNoDefaultValueSet()
	{
		testGenerateSetsToFalse(simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptorModelBuilder()
						.withOptional(false)
						.withDefaultValue(null)
						.withUnique(true))
				.build());
	}

	@Test
	public void testGenerateWhenTrueUniqueFalseOptionalAndHasDefaultValueSet()
	{
		testGenerateSetsToTrue(simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptorModelBuilder()
						.withOptional(false)
						.withDefaultValue("some value")
						.withUnique(true))
				.build());
	}

	@Test
	public void testGenerateWhenFalseUniqueFalseOptionalAndNoDefaultValueSet()
	{
		testGenerateSetsToFalse(simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptorModelBuilder()
						.withOptional(false)
						.withDefaultValue(null)
						.withUnique(false))
				.build());
	}


	@Test
	public void testGenerateWhenFalseUniqueFalseOptionalAndHasDefaultValueSet()
	{
		testGenerateSetsToTrue(simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptorModelBuilder()
						.withOptional(false)
						.withDefaultValue("some value")
						.withUnique(false))
				.build());
	}

	@Test
	public void testGenerateWhenFalseUniqueTrueOptionalHasDefaultValueSet()
	{
		testGenerateSetsToTrue(simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptorModelBuilder()
						.withOptional(true)
						.withDefaultValue(Integer.MAX_VALUE)
						.withUnique(false))
				.build());
	}

	@Test
	public void testGenerateWhenFalseUniqueTrueOptionalNoDefaultValueSet()
	{
		testGenerateSetsToTrue(simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptorModelBuilder()
						.withOptional(true)
						.withDefaultValue(null)
						.withUnique(false))
				.build());
	}

	@Test
	public void testGenerateWhenTrueUniqueNullOptional()
	{
		testGenerateSetsToTrue(simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptorModelBuilder()
						.withOptional(null)
						.withUnique(true))
				.build());
	}

	@Test
	public void testGenerateWhenFalseUniqueNullOptional()
	{
		testGenerateSetsToTrue(simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptorModelBuilder()
						.withOptional(null)
						.withUnique(false))
				.build());
	}

	private void testGenerateSetsToTrue(final IntegrationObjectItemAttributeModel model)
	{
		final AnnotationAttribute attribute = generator.generate(model);
		assertAttribute(attribute, "Nullable", "true");
	}

	private void testGenerateSetsToFalse(final IntegrationObjectItemAttributeModel model)
	{
		final AnnotationAttribute attribute = generator.generate(model);
		assertAttribute(attribute, "Nullable", "false");
	}
}
