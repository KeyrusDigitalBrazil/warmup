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
package de.hybris.platform.marketplaceservices.strategies.hooks;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.marketplaceservices.dao.MarketplaceCartEntryDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class MarketplaceCartValidationHookTest
{
	private MarketplaceCartValidationHook marketplaceCartValidationHook;

	public static final String PRODUCT_CODE_1 = "00001101";
	public static final String PRODUCT_CODE_2 = "00001102";

	@Mock
	private ModelService modelService;

	@Mock
	private MarketplaceCartEntryDao marketplaceCartEntryDao;

	@Mock
	private CommerceCartParameter cartParameter;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		marketplaceCartValidationHook = new MarketplaceCartValidationHook();
		marketplaceCartValidationHook.setModelService(modelService);
		marketplaceCartValidationHook.setCartEntryDao(marketplaceCartEntryDao);
	}

	@Test
	public void testAfterValidateCart()
	{
		final CartModel cart = new CartModel();

		final CartEntryModel cartEnty1 = new CartEntryModel();
		final ProductModel product1 = new ProductModel();
		product1.setCode(PRODUCT_CODE_1);
		product1.setSaleable(Boolean.TRUE);
		cartEnty1.setProduct(product1);

		final CartEntryModel cartEnty2 = new CartEntryModel();
		final ProductModel product2 = new ProductModel();
		product2.setCode(PRODUCT_CODE_2);
		product2.setSaleable(Boolean.FALSE);
		cartEnty2.setProduct(product2);

		cart.setEntries(Arrays.asList(cartEnty1, cartEnty2));

		given(cartParameter.getCart()).willReturn(cart);
		given(marketplaceCartEntryDao.findUnSaleableCartEntries(cart)).willReturn(Arrays.asList(cartEnty2));

		Mockito.doNothing().when(modelService).remove(Matchers.any());
		Mockito.doNothing().when(modelService).refresh(Matchers.any());

		final List<CommerceCartModification> modifications = new ArrayList<>();

		final CommerceCartModification modification1 = new CommerceCartModification();
		modification1.setEntry(cartEnty1);
		modification1.setProduct(cartEnty1.getProduct());
		modification1.setStatusCode(CommerceCartModificationStatus.SUCCESS);

		final CommerceCartModification modification2 = new CommerceCartModification();
		modification2.setEntry(cartEnty2);
		modification2.setProduct(cartEnty2.getProduct());
		modification2.setStatusCode(CommerceCartModificationStatus.SUCCESS);

		modifications.add(modification1);
		modifications.add(modification2);

		marketplaceCartValidationHook.afterValidateCart(cartParameter, modifications);

		Mockito.verify(modelService, Mockito.times(1)).removeAll(Arrays.asList(cartEnty2));

		modifications.sort(Comparator.comparing(CommerceCartModification::getStatusCode));

		assertEquals(CommerceCartModificationStatus.SUCCESS, modifications.get(0).getStatusCode());
		assertEquals(CommerceCartModificationStatus.UNAVAILABLE, modifications.get(1).getStatusCode());

	}

}
