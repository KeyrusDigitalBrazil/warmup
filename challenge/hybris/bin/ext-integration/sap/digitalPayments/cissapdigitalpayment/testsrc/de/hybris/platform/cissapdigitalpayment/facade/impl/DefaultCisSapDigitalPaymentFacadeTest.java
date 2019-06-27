/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cissapdigitalpayment.facade.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentPollRegisteredCardResult;
import de.hybris.platform.cissapdigitalpayment.facade.impl.DefaultCisSapDigitalPaymentFacade;
import de.hybris.platform.cissapdigitalpayment.service.CisSapDigitalPaymentService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Default implementation of {@link DefaultCisSapDigitalPaymentFacadeTest}
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCisSapDigitalPaymentFacadeTest
{

	@Mock
	private CisSapDigitalPaymentService cisSapDigitalPaymentService;

	@Mock
	private SessionService sessionService;
	@Mock
	private Converter<CisSapDigitalPaymentPollRegisteredCardResult, CCPaymentInfoData> cisSapDigitalPaymentPaymentInfoConverter;

	@Mock
	private CartService cartService;

	@Mock
	private Converter<AddressData, AddressModel> paymentAddressReverseConverter;

	@Mock
	private Converter<AddressModel, AddressData> paymentAddressConverter;

	@Mock
	private ModelService modelService;

	@Mock
	private UserService userService;

	@Mock
	private CalculationService calculationService;

	@InjectMocks
	private DefaultCisSapDigitalPaymentFacade defaultCisSapDigitalPaymentFacade;

	@Before
	public void setup()
	{
		//Initialization method
	}

	@Test
	public void checkIfPaymentAddressAddedToCart() throws CalculationException
	{
		final CartModel cart = new CartModel();
		final AddressModel address = new AddressModel();
		when(cartService.getSessionCart()).thenReturn(cart);
		when(userService.getCurrentUser()).thenReturn(getDummyUser());
		when(paymentAddressReverseConverter.convert(any(AddressData.class))).thenReturn(address);
		when(modelService.isNew(any(Object.class))).thenReturn(true);
		doNothing().when(modelService).save(any(CartModel.class));
		doNothing().when(calculationService).recalculate(any(CartModel.class));
		defaultCisSapDigitalPaymentFacade.addPaymentAddressToCart(new AddressData());

		assertNotNull(cart.getPaymentAddress());
		assertTrue("Dummy user".equals(((CustomerModel) address.getOwner()).getName()));
		assertTrue(!address.getVisibleInAddressBook());

	}


	/**
	 *
	 */
	private UserModel getDummyUser()
	{
		final UserModel user = new CustomerModel();
		user.setName("Dummy user");
		return user;
	}






}
