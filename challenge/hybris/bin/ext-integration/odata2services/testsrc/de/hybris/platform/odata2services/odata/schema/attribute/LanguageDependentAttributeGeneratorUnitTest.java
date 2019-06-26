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


import static de.hybris.platform.integrationservices.model.BaseMockAttributeDescriptorModelBuilder.attributeDescriptor;
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder;
import static de.hybris.platform.odata2services.odata.schema.attribute.AttributeGeneratorUnitTestHelper.assertAttribute;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.junit.Test;

@UnitTest
public class LanguageDependentAttributeGeneratorUnitTest
{
	private static final String IS_LANGUAGE_DEPENDENT = "s:IsLanguageDependent";
	private final LanguageDependentAttributeGenerator generator = new LanguageDependentAttributeGenerator();

	@Test
	public void testIsApplicableNull()
	{
		assertThat(generator.isApplicable(null))
				.isFalse();
	}

	@Test
	public void testIsApplicableNotLanguageDependent()
	{
		assertThat(generator.isApplicable(attributeWithLocalized(false)))
				.isFalse();
	}

	@Test
	public void testIsApplicableNullLocalizedValue()
	{
		assertThat(generator.isApplicable(attributeWithLocalized(null)))
				.isFalse();
	}

	@Test
	public void testIsApplicableLanguageDependentIsTrue()
	{
		assertThat(generator.isApplicable(attributeWithLocalized(true)))
				.isTrue();
	}

	@Test
	public void testGenerateLanguageDependent()
	{
		final AnnotationAttribute annotation = generator.generate(attributeWithLocalized(true));

		assertAttribute(annotation, IS_LANGUAGE_DEPENDENT, "true");
	}

	private IntegrationObjectItemAttributeModel attributeWithLocalized(final Boolean isLocalized)
	{
		return simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptor().withLocalized(isLocalized))
				.build();
	}
}