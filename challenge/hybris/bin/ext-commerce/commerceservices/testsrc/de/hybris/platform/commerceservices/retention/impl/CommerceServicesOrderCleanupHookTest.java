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
package de.hybris.platform.commerceservices.retention.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.directpersistence.audit.dao.WriteAuditRecordsDAO;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CommerceServicesOrderCleanupHookTest
{
	private static final String ORDER_PROCESSES_QUERY = "SELECT {" + OrderProcessModel.PK + "} FROM {" + OrderProcessModel._TYPECODE + "} "
			+ "WHERE {" + OrderProcessModel.ORDER + "} = ?order";
	private static final String CONSIGNMENT_PROCESSES_QUERY = "SELECT {" + ConsignmentProcessModel.PK + "} FROM {"
			+ ConsignmentProcessModel._TYPECODE + "} " + "WHERE {" + ConsignmentProcessModel.CONSIGNMENT + "} IN (?consignments)";
	
	@InjectMocks
	private final CommerceServicesOrderCleanupHook orderCleanupHook = new CommerceServicesOrderCleanupHook();

	@Mock
	private ModelService modelService;
	@Mock
	private WriteAuditRecordsDAO writeAuditRecordsDAO;
	@Mock
	private TimeService timeService;
	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCleanupRelatedObjects()
	{
		final OrderModel orderModel = mock(OrderModel.class);
		final AddressModel paymentAddressModel = mock(AddressModel.class);
		given(orderModel.getPaymentAddress()).willReturn(paymentAddressModel);
		final PK paymentAddressPK = PK.parse("1111");
		given(paymentAddressModel.getPk()).willReturn(paymentAddressPK);

		final AddressModel deliveryAddressModel = mock(AddressModel.class);
		given(orderModel.getDeliveryAddress()).willReturn(deliveryAddressModel);
		final PK deliveryAddressPK = PK.parse("2222");
		given(deliveryAddressModel.getPk()).willReturn(deliveryAddressPK);

		final PaymentInfoModel paymentInfoModel = mock(PaymentInfoModel.class);
		given(orderModel.getPaymentInfo()).willReturn(paymentInfoModel);
		final PK paymentInfoPK = PK.parse("3333");
		given(paymentInfoModel.getPk()).willReturn(paymentInfoPK);

		final CommentModel commentModel = mock(CommentModel.class);
		final List<CommentModel> comments = Collections.singletonList(commentModel);
		given(orderModel.getComments()).willReturn(comments);
		final PK commentModelPK = PK.parse("4444");
		given(commentModel.getPk()).willReturn(commentModelPK);
		
		final ConsignmentEntryModel consignmentEntryModel = mock(ConsignmentEntryModel.class);
		final Set<ConsignmentEntryModel> consignmentEntries = Collections.singleton(consignmentEntryModel);
		final ConsignmentModel consignmentModel = mock(ConsignmentModel.class);
		final Set<ConsignmentModel> consignments = Collections.singleton(consignmentModel);
		given(consignmentModel.getConsignmentEntries()).willReturn(consignmentEntries);
		given(orderModel.getConsignments()).willReturn(consignments);
		final PK consignmentEntryModelPK = PK.parse("5555");
		given(consignmentEntryModel.getPk()).willReturn(consignmentEntryModelPK);
		final PK consignmentModelPK = PK.parse("6666");
		given(consignmentModel.getPk()).willReturn(consignmentModelPK);
		
		final FlexibleSearchQuery orderProcessesQuery = new FlexibleSearchQuery(ORDER_PROCESSES_QUERY);
		orderProcessesQuery.addQueryParameter("order", orderModel);
		final SearchResult orderProcessSearchResult = mock(SearchResult.class);
		given(flexibleSearchService.search(orderProcessesQuery)).willReturn(orderProcessSearchResult);
		
		final FlexibleSearchQuery consignmentProcessesQuery = new FlexibleSearchQuery(CONSIGNMENT_PROCESSES_QUERY);
		consignmentProcessesQuery.addQueryParameter("consignments", new ArrayList(consignments));
		final SearchResult consignmentProcessSearchResult = mock(SearchResult.class);
		given(flexibleSearchService.search(consignmentProcessesQuery)).willReturn(consignmentProcessSearchResult);
		
		final ConsignmentProcessModel consignmentProcessModel = mock(ConsignmentProcessModel.class);
		final List<ConsignmentProcessModel> consignmentProcesses = Collections.singletonList(consignmentProcessModel);
		given(consignmentProcessSearchResult.getResult()).willReturn(consignmentProcesses);
		
		final OrderProcessModel orderProcessModel = mock(OrderProcessModel.class);
		given(orderProcessModel.getConsignmentProcesses()).willReturn(consignmentProcesses);
		final PK consignmentProcessModelPK = PK.parse("7777");
		given(consignmentProcessModel.getPk()).willReturn(consignmentProcessModelPK);
		final PK orderProcessModelPK = PK.parse("8888");
		given(orderProcessModel.getPk()).willReturn(orderProcessModelPK);
		final List<OrderProcessModel> orderProcesses = Collections.singletonList(orderProcessModel);
		given(orderProcessSearchResult.getResult()).willReturn(orderProcesses);

		orderCleanupHook.cleanupRelatedObjects(orderModel);
		verify(modelService).remove(paymentAddressModel);
		verify(modelService).remove(deliveryAddressModel);
		verify(modelService).remove(paymentInfoModel);
		verify(modelService).remove(commentModel);
		verify(modelService).remove(consignmentEntryModel);
		verify(modelService).remove(consignmentModel);
		verify(modelService).remove(consignmentProcessModel);
		verify(modelService).remove(orderProcessModel);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(AddressModel._TYPECODE, paymentAddressPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(AddressModel._TYPECODE, deliveryAddressPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(PaymentInfoModel._TYPECODE, paymentInfoPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(CommentModel._TYPECODE, commentModelPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(ConsignmentEntryModel._TYPECODE, consignmentEntryModelPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(ConsignmentModel._TYPECODE, consignmentModelPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(ConsignmentProcessModel._TYPECODE, consignmentProcessModelPK);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(OrderProcessModel._TYPECODE, orderProcessModelPK);
	}

	@Test
	public void shouldDeactivateGuestCustomer()
	{
		final OrderModel orderModel = mock(OrderModel.class);
		given(orderModel.getPaymentAddress()).willReturn(null);
		given(orderModel.getDeliveryAddress()).willReturn(null);
		given(orderModel.getPaymentInfo()).willReturn(null);
		given(orderModel.getComments()).willReturn(Collections.EMPTY_LIST);

		final CustomerModel customerModel = new CustomerModel();
		customerModel.setType(CustomerType.GUEST);
		given(orderModel.getUser()).willReturn(customerModel);
		final Date deactivationDate = new Date();
		given(timeService.getCurrentTime()).willReturn(deactivationDate);
		final SearchResult orderProcessSearchResult = mock(SearchResult.class);
		given(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).willReturn(orderProcessSearchResult);

		orderCleanupHook.cleanupRelatedObjects(orderModel);

		assertEquals(deactivationDate, customerModel.getDeactivationDate());
		verify(modelService).save(customerModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCleanupRelatedObjectsIfInputIsNull()
	{
		orderCleanupHook.cleanupRelatedObjects(null);
	}
}
