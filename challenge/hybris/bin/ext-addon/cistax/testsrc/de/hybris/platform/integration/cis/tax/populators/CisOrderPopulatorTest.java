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
package de.hybris.platform.integration.cis.tax.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.integration.cis.tax.strategies.CisShippingAddressStrategy;
import de.hybris.platform.integration.commons.OndemandDiscountedOrderEntry;
import de.hybris.platform.integration.commons.services.OndemandPromotionService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisLineItem;
import com.hybris.cis.client.shared.models.CisOrder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.BDDMockito.given;


@UnitTest
public class CisOrderPopulatorTest
{
	private static final String ORDER_CODE = "test-order";
	private static final String CURRENCY_CODE = "USD";
	private Date orderDate = new Date();

	private CisOrderPopulator cisOrderPopulator;

	@Mock
	private CisShippingAddressStrategy cisShippingAddressStrategy;
	@Mock
	private Converter<AbstractOrderModel, CisLineItem> deliveryCisLineItemConverter;
	@Mock
	private OndemandPromotionService ondemandPromotionService;
	@Mock
	private Converter<OndemandDiscountedOrderEntry, CisLineItem> cisLineItemConverter;
	@Mock
	private AbstractOrderModel abstractOrder;
	@Mock
	private CisLineItem cisLineItem;
	@Mock
	private CisLineItem deliveryCisLineItem;
	@Mock
	private CisAddress address;
	@Mock
	private CurrencyModel currencyModel;
	@Mock
	private DeliveryModeModel deliveryMode;

	private List<CisAddress> addresses = new ArrayList<>();
	private List<CisLineItem> lineItems = new ArrayList<>();
	private List<OndemandDiscountedOrderEntry> ondemandDiscountedOrderEntries = new ArrayList<>();

	@Mock
	private OndemandDiscountedOrderEntry ondemandDiscountedOrderEntry;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		cisOrderPopulator = new CisOrderPopulator();
		cisOrderPopulator.setCisShippingAddressStrategy(cisShippingAddressStrategy);
		cisOrderPopulator.setDeliveryCisLineItemConverter(deliveryCisLineItemConverter);
		cisOrderPopulator.setOndemandPromotionService(ondemandPromotionService);
		cisOrderPopulator.setCisLineItemConverter(cisLineItemConverter);

		addresses.add(address);

		lineItems.add(cisLineItem);
		lineItems.add(deliveryCisLineItem);

		ondemandDiscountedOrderEntries.add(ondemandDiscountedOrderEntry);
	}

	@Test
	public void shouldPopulate()
	{
		// Given
		final CisOrder cisOrder = new CisOrder();
		given(abstractOrder.getDeliveryMode()).willReturn(deliveryMode);
		given(currencyModel.getIsocode()).willReturn(CURRENCY_CODE);
		given(cisShippingAddressStrategy.getAddresses(abstractOrder)).willReturn(addresses);
		given(ondemandPromotionService.calculateProportionalDiscountForEntries(abstractOrder)).willReturn(ondemandDiscountedOrderEntries);
		given(cisLineItemConverter.convert(ondemandDiscountedOrderEntry)).willReturn(cisLineItem);
		given(deliveryCisLineItemConverter.convert(abstractOrder)).willReturn(deliveryCisLineItem);
		given(abstractOrder.getCode()).willReturn(ORDER_CODE);
		given(abstractOrder.getCurrency()).willReturn(currencyModel);
		given(abstractOrder.getDate()).willReturn(orderDate);

		// When
		cisOrderPopulator.populate(abstractOrder, cisOrder);

		// Then
		assertEquals(addresses, cisOrder.getAddresses());
		assertEquals(lineItems.size(), cisOrder.getLineItems().size());
		assertTrue(cisOrder.getLineItems().contains(cisLineItem));
		assertTrue(cisOrder.getLineItems().contains(deliveryCisLineItem));
		assertEquals(ORDER_CODE, cisOrder.getId());
		assertEquals(CURRENCY_CODE, cisOrder.getCurrency());
		assertEquals(orderDate, cisOrder.getDate());

	}
}
