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

import static de.hybris.platform.odata2services.odata.schema.attribute.AttributeGeneratorUnitTestHelper.assertAttribute;
import static de.hybris.platform.odata2services.odata.schema.attribute.AttributeGeneratorUnitTestHelper.givenIsUniqueReturns;
import static de.hybris.platform.odata2services.odata.schema.attribute.UniqueAttributeGenerator.IS_UNIQUE;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.junit.Test;

@UnitTest
public class UniqueAttributeGeneratorUnitTest
{
	private final UniqueAttributeGenerator generator = new UniqueAttributeGenerator();

	@Test
	public void testIsApplicableNullModel()
	{
		assertThat(generator.isApplicable(null))
				.isFalse();
	}

	@Test
	public void testIsApplicableWhenUniqueIsTrue()
	{
		assertThat(generator.isApplicable(givenIsUniqueReturns(true)))
				.isTrue();
	}

	@Test
	public void testIsApplicableWhenIsUniqueIsFalse()
	{
		assertThat(generator.isApplicable(givenIsUniqueReturns(false)))
				.isFalse();
	}

	@Test
	public void testGenerateWhenIsUniqueIsTrueCreatesTrueUniqueAttribute()
	{
		final AnnotationAttribute isUniqueAnnotation = generator.generate(givenIsUniqueReturns(true));
		assertAttribute(isUniqueAnnotation, IS_UNIQUE, "true");
	}
}