/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.util.builder;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartMetadataParameter;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CommerceCartMetadataParameterBuilderTest
{
	private CommerceCartMetadataParameterBuilder commerceCartMetadataParameterBuilder = new CommerceCartMetadataParameterBuilder();

	@Test
	public void shouldBuildEmptyCommerceCartMetadataParameter()
	{
		final CommerceCartMetadataParameter cartMetadataParameter = commerceCartMetadataParameterBuilder.build();

		Assert.assertNotNull("Should return cart metadata parameter", cartMetadataParameter);
		Assert.assertEquals("Name should be empty optional", Optional.empty(), cartMetadataParameter.getName());
		Assert.assertEquals("Description should be empty optional", Optional.empty(), cartMetadataParameter.getDescription());
		Assert.assertEquals("Expiration time should be empty optional", Optional.empty(), cartMetadataParameter.getExpirationTime());
	}
}
