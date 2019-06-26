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
package de.hybris.platform.commerceservices.order.strategies.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.hook.CommerceCartMetadataUpdateMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartMetadataParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCommerceCartMetadataUpdateStrategyTest
{
	@InjectMocks
	private final DefaultCommerceCartMetadataUpdateStrategy defaultCommerceCartMetadataUpdateStrategy = new DefaultCommerceCartMetadataUpdateStrategy();

	@Mock
	private CommerceCartMetadataUpdateMethodHook commerceCartMetadataUpdateMethodHook1;

	@Mock
	private CommerceCartMetadataUpdateMethodHook commerceCartMetadataUpdateMethodHook2;

	@Mock
	private ModelService modelService;

	@Mock
	private CommerceCartMetadataParameter metadataParameter;

	@Mock
	private CartModel cartModel;

	@Before
	public void setup()
	{
		defaultCommerceCartMetadataUpdateStrategy.setCommerceCartMetadataUpdateMethodHooks(
				Arrays.asList(commerceCartMetadataUpdateMethodHook1, commerceCartMetadataUpdateMethodHook2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNullParameter()
	{
		defaultCommerceCartMetadataUpdateStrategy.updateCartMetadata(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateNullCart()
	{
		given(metadataParameter.getCart()).willReturn(null);

		defaultCommerceCartMetadataUpdateStrategy.updateCartMetadata(metadataParameter);
	}

	@Test
	public void shouldHaveNoModification()
	{
		given(metadataParameter.getCart()).willReturn(cartModel);
		given(metadataParameter.getName()).willReturn(Optional.empty());
		given(metadataParameter.getDescription()).willReturn(Optional.empty());
		given(metadataParameter.getExpirationTime()).willReturn(Optional.empty());
		given(Boolean.valueOf(metadataParameter.isRemoveExpirationTime())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(metadataParameter.isEnableHooks())).willReturn(Boolean.TRUE);

		defaultCommerceCartMetadataUpdateStrategy.updateCartMetadata(metadataParameter);

		verify(modelService, never()).save(cartModel);
	}

	@Test
	public void shouldModifyName()
	{
		given(metadataParameter.getCart()).willReturn(cartModel);
		given(metadataParameter.getName()).willReturn(Optional.of("myQuoteName"));
		given(metadataParameter.getDescription()).willReturn(Optional.empty());
		given(metadataParameter.getExpirationTime()).willReturn(Optional.empty());
		given(Boolean.valueOf(metadataParameter.isRemoveExpirationTime())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(metadataParameter.isEnableHooks())).willReturn(Boolean.TRUE);

		defaultCommerceCartMetadataUpdateStrategy.updateCartMetadata(metadataParameter);

		verify(cartModel).setName("myQuoteName");
		verify(modelService).save(cartModel);
	}

	@Test
	public void shouldModifyDescription()
	{
		given(metadataParameter.getCart()).willReturn(cartModel);
		given(metadataParameter.getName()).willReturn(Optional.empty());
		given(metadataParameter.getDescription()).willReturn(Optional.of("myQuoteDescription"));
		given(metadataParameter.getExpirationTime()).willReturn(Optional.empty());
		given(Boolean.valueOf(metadataParameter.isRemoveExpirationTime())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(metadataParameter.isEnableHooks())).willReturn(Boolean.TRUE);

		defaultCommerceCartMetadataUpdateStrategy.updateCartMetadata(metadataParameter);

		verify(cartModel).setDescription("myQuoteDescription");
		verify(modelService).save(cartModel);
	}

	@Test
	public void shouldModifyExpirationTime()
	{
		final Date currentDate = new Date();
		given(metadataParameter.getCart()).willReturn(cartModel);
		given(metadataParameter.getName()).willReturn(Optional.empty());
		given(metadataParameter.getDescription()).willReturn(Optional.empty());
		given(metadataParameter.getExpirationTime()).willReturn(Optional.of(currentDate));
		given(Boolean.valueOf(metadataParameter.isRemoveExpirationTime())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(metadataParameter.isEnableHooks())).willReturn(Boolean.TRUE);

		defaultCommerceCartMetadataUpdateStrategy.updateCartMetadata(metadataParameter);

		verify(cartModel).setExpirationTime(currentDate);
		verify(modelService).save(cartModel);
	}

	@Test
	public void shouldModifyRemoveExpirationTime()
	{
		given(metadataParameter.getCart()).willReturn(cartModel);
		given(metadataParameter.getName()).willReturn(Optional.empty());
		given(metadataParameter.getDescription()).willReturn(Optional.empty());
		given(metadataParameter.getExpirationTime()).willReturn(Optional.empty());
		given(Boolean.valueOf(metadataParameter.isRemoveExpirationTime())).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(metadataParameter.isEnableHooks())).willReturn(Boolean.TRUE);

		defaultCommerceCartMetadataUpdateStrategy.updateCartMetadata(metadataParameter);

		verify(cartModel).setExpirationTime(null);
		verify(modelService).save(cartModel);
	}
}
