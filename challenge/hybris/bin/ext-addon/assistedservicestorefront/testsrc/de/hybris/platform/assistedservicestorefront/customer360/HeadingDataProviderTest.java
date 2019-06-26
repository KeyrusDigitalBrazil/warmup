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
package de.hybris.platform.assistedservicestorefront.customer360;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer360.CustomerViewHeadingData;
import de.hybris.platform.assistedservicestorefront.customer360.provider.HeadingDataProvider;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticket.service.TicketService;

import java.util.Collections;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class HeadingDataProviderTest
{
	@Mock
	private OrderFacade orderFacade;
	@Mock
	private TicketService ticketService;
	@Mock
	private TicketBusinessService ticketBusinessService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private UserService userService;
	@Mock
	private CartFacade cartFacade;

	@InjectMocks
	private final HeadingDataProvider dataProvider = new HeadingDataProvider();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testDataProviding()
	{
		final UserModel currentCustomer = Mockito.mock(UserModel.class);
		final CartData sessionCart = Mockito.mock(CartData.class);
		final CsTicketModel ticketModel = Mockito.mock(CsTicketModel.class);

		Mockito.when(userService.getCurrentUser()).thenReturn(currentCustomer);
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(null);
		Mockito.when(cartFacade.getMiniCart()).thenReturn(sessionCart);

		final SearchPageData<CsTicketModel> pageData = new SearchPageData<>();
		pageData.setResults(Collections.singletonList(ticketModel));
		Mockito.when(
				ticketService.getTicketsForCustomerOrderByModifiedTime(Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject()))
				.thenReturn(pageData);
		final SearchPageData<OrderHistoryData> orderData = new SearchPageData<>();
		Mockito.when(orderFacade.getPagedOrderHistoryForStatuses(Mockito.anyObject())).thenReturn(orderData);

		final String name = "name";
		final String tId = "123124";
		final String cardId = "cartId";
		final Integer cartSize = Integer.valueOf(2);
		Mockito.when(sessionCart.getEntries()).thenReturn(null);
		Mockito.when(sessionCart.getTotalUnitCount()).thenReturn(cartSize);
		Mockito.when(sessionCart.getCode()).thenReturn(cardId);
		Mockito.when(currentCustomer.getName()).thenReturn(name);
		Mockito.when(currentCustomer.getProfilePicture()).thenReturn(null);
		Mockito.when(ticketModel.getTicketID()).thenReturn(tId);
		final CustomerViewHeadingData customerViewHeadingData = dataProvider.getModel(new HashMap<>());

		Assert.assertEquals(cardId, customerViewHeadingData.getCartCode());
		Assert.assertEquals(name, customerViewHeadingData.getName());
		Assert.assertEquals(tId, customerViewHeadingData.getLatestOpenedTicketId());
		Assert.assertEquals(cartSize, customerViewHeadingData.getCartSize());
	}
}
