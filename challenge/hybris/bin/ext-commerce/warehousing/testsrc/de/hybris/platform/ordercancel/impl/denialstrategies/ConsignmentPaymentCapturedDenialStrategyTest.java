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
 *
 */
package de.hybris.platform.ordercancel.impl.denialstrategies;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.DefaultOrderCancelDenialReason;
import de.hybris.platform.ordercancel.OrderCancelDenialReason;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static de.hybris.platform.warehousing.constants.WarehousingConstants.CAPTURE_PAYMENT_ON_CONSIGNMENT_PROPERTY_NAME;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConsignmentPaymentCapturedDenialStrategyTest
{
	protected static final ConsignmentStatus CANCELLABLE_CONSIGNMENT_STATUS = ConsignmentStatus.READY;
	protected static final ConsignmentStatus NOT_CANCELLABLE_CONSIGNMENT_STATUS = ConsignmentStatus.SHIPPED;

	@InjectMocks
	private ConsignmentPaymentCapturedDenialStrategy consignmentPaymentCapturedDenialStrategy;

	@Mock
	private OrderModel orderModel;
	@Mock
	private OrderEntryModel orderEntryModel1;
	@Mock
	private OrderEntryModel orderEntryModel2;
	@Mock
	private ConsignmentModel consignmentModel1;
	@Mock
	private ConsignmentModel consignmentModel2;
	@Mock
	private Collection<ConsignmentStatus> notCancellableConsignmentStatuses;
	@Mock
	ConfigurationService configurationService;
	@Mock
	Configuration configuration;

	private DefaultOrderCancelDenialReason orderCancelDenialReason = new DefaultOrderCancelDenialReason();

	@Before
	public void setup()
	{
		when(orderEntryModel1.getQuantityUnallocated()).thenReturn(0L);
		when(orderEntryModel2.getQuantityUnallocated()).thenReturn(0L);
		when(orderModel.getEntries()).thenReturn(asList(orderEntryModel1, orderEntryModel2));

		final Set<ConsignmentModel> consignmentModels = new HashSet<>();
		consignmentModels.add(consignmentModel1);
		consignmentModels.add(consignmentModel2);
		when(orderModel.getConsignments()).thenReturn(consignmentModels);

		when(notCancellableConsignmentStatuses.contains(NOT_CANCELLABLE_CONSIGNMENT_STATUS)).thenReturn(true);
		when(notCancellableConsignmentStatuses.contains(CANCELLABLE_CONSIGNMENT_STATUS)).thenReturn(false);
		consignmentPaymentCapturedDenialStrategy.setReason(orderCancelDenialReason);

		when(configuration.getBoolean(CAPTURE_PAYMENT_ON_CONSIGNMENT_PROPERTY_NAME, false)).thenReturn(true);
		when(configurationService.getConfiguration()).thenReturn(configuration);
	}


	@Test
	public void shouldReturnOkWhenOrderHasUnallocatedItems()
	{
		//given
		when(orderEntryModel1.getQuantityUnallocated()).thenReturn(1L);

		//when
		final OrderCancelDenialReason result = consignmentPaymentCapturedDenialStrategy
				.getCancelDenialReason(null, orderModel, null, true, true);

		//then
		assertNull(result);
	}

	@Test
	public void shouldReturnOkWhenOneConsignmentIsCancellable()
	{
		//given
		when(consignmentModel1.getStatus()).thenReturn(CANCELLABLE_CONSIGNMENT_STATUS);
		when(consignmentModel2.getStatus()).thenReturn(NOT_CANCELLABLE_CONSIGNMENT_STATUS);

		//when
		final OrderCancelDenialReason result = consignmentPaymentCapturedDenialStrategy
				.getCancelDenialReason(null, orderModel, null, true, true);

		//then
		assertNull(result);
	}

	@Test
	public void shouldReturnOkWhenAllConsignmentAreCancellable()
	{
		//given
		when(consignmentModel1.getStatus()).thenReturn(CANCELLABLE_CONSIGNMENT_STATUS);
		when(consignmentModel2.getStatus()).thenReturn(CANCELLABLE_CONSIGNMENT_STATUS);

		//when
		final OrderCancelDenialReason result = consignmentPaymentCapturedDenialStrategy
				.getCancelDenialReason(null, orderModel, null, true, true);

		//then
		assertNull(result);
	}

	@Test
	public void shouldReturnNotOkWhenAllConsignmentAreNotCancellable()
	{
		//given
		when(consignmentModel1.getStatus()).thenReturn(NOT_CANCELLABLE_CONSIGNMENT_STATUS);
		when(consignmentModel2.getStatus()).thenReturn(NOT_CANCELLABLE_CONSIGNMENT_STATUS);

		//when
		final OrderCancelDenialReason result = consignmentPaymentCapturedDenialStrategy
				.getCancelDenialReason(null, orderModel, null, true, true);

		//then
		assertNotNull(result);
	}

	@Test
	public void shouldReturnOkWhenCapturePaymentOnConsignmentIsNotEnabled()
	{
		//given
		when(configuration.getBoolean(CAPTURE_PAYMENT_ON_CONSIGNMENT_PROPERTY_NAME, false)).thenReturn(false);

		//when
		final OrderCancelDenialReason result = consignmentPaymentCapturedDenialStrategy
				.getCancelDenialReason(null, orderModel, null, true, true);

		//then
		assertNull(result);
		verify(orderModel, never()).getEntries();
		verify(orderModel, never()).getConsignments();
	}
}
