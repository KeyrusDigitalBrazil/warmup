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
package de.hybris.platform.ordercancel;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.OrderCancelEntryStatus;
import de.hybris.platform.basecommerce.enums.OrderModificationEntryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.ordercancel.OrderCancelResponse.ResponseStatus;
import de.hybris.platform.ordercancel.impl.DefaultOrderCancelService;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * Tests partial cancel execution in different scenarios.
 */
public class OrderCancelPartialTest extends AbstractOrderCancelTest
{
	private static final Logger LOG = Logger.getLogger(OrderCancelPartialTest.class);

	@Resource
	private DefaultOrderCancelService orderCancelService;

	@Resource
	private OrderService orderService;

	@Mock
	private OrderCancelNotificationServiceAdapter notificationServiceAdapter;

	@Mock
	private OrderCancelPaymentServiceAdapter paymentServiceAdapter;

	private PrincipalModel currentPrincipal;

	//-- requests
	private OrderCancelRequest partialRequest;
	private OrderCancelRequest partialEntryRequest;

	//-- responses
	private OrderCancelResponse partialResponseOK;
	private OrderCancelResponse partialEntryResponseOK;
	private OrderCancelResponse partialResponseDENIED;
	private OrderCancelResponse partialEntryResponseDENIED;


	private OrderCancelRecordEntryModel currentCancelRecord;

	@Before
	public void customSetUp() throws InvalidCartException
	{
		LOG.info("Setting up OrderCancelPartialTest");
		//order = placeTestOrder();
		currentPrincipal = REQUEST_ORIGIN_STRATEGY_CUSTOMER.getCallingPrincipal();
		injectStateMappingStrategy(STATE_STRATEGY_HOLDING_AREA_QUEUE_WAITING);

		final OrderCancelConfigModel configuration = getOrCreateOrderCancelConfig(3600);
		configuration.setPartialCancelAllowed(true);
		configuration.setPartialOrderEntryCancelAllowed(true);
		configuration.setOrderCancelAllowed(true);
		configuration.setCancelAfterWarehouseAllowed(true);
		configuration.setCompleteCancelAfterShippingStartedAllowed(true);

		initMocks(this);
		injectAdaptersDependencies(notificationServiceAdapter, paymentServiceAdapter, null, getOrderCancelService());

		/**
		 * Test Order structure: cartService.addToCart(cart, productService.getProduct("testProduct1"), 1, null);
		 * cartService.addToCart(cart, productService.getProduct("testProduct2"), 2, null); cartService.addToCart(cart,
		 * productService.getProduct("testProduct3"), 3, null);
		 */


		final List<OrderCancelEntry> requestEntries = new ArrayList<OrderCancelEntry>();
		//third order entry - fully
		final AbstractOrderEntryModel entry = orderService.getEntryForNumber(getOrder(), 2);

		Assert.assertEquals(getProduct3(), entry.getProduct());
		Assert.assertEquals(3, entry.getQuantity().longValue());

		requestEntries.add(new OrderCancelEntry(entry));


		partialRequest = new OrderCancelRequest(getOrder(), requestEntries);

		partialResponseOK = new OrderCancelResponse(getOrder(), requestEntries);
		partialResponseDENIED = new OrderCancelResponse(getOrder(), requestEntries, ResponseStatus.denied, "Already shipped");

		final List<OrderCancelEntry> requestPartialEntries = new ArrayList<OrderCancelEntry>();
		// third order entry - partially
		requestPartialEntries.add(new OrderCancelEntry(entry, 2));
		partialEntryRequest = new OrderCancelRequest(getOrder(), requestPartialEntries);

		partialEntryResponseOK = new OrderCancelResponse(getOrder(), requestPartialEntries);

		partialEntryResponseDENIED = new OrderCancelResponse(getOrder(), requestPartialEntries, ResponseStatus.denied,
				"Already shipped");

	}

	/**
	 * Given an Order that contains a set of order entries containing items (products) and Partial Order Cancellation is
	 * possible and the Order hasn't been sent to the warehouse yet (Order queue waiting time has not expired) then after
	 * Order Cancel Partial Request:
	 * <p>
	 * Order Cancel Entry should be created and set to "complete" state Canceled order entries should be removed. Order
	 * should be recalculated according to new set of Order Entries. State of the Order is unchanged.
	 *
	 * @throws Exception
	 */
	@Test
	public void testPartialCancelationBeforeWarehouse() throws Exception
	{

		requestPartialOrderCancelBeforeWarehouse(partialRequest);

		getModelService().refresh(getOrder());
		verify(notificationServiceAdapter, times(1)).sendCancelFinishedNotifications(any());

		Assert.assertEquals("Order cancel record entry should have 1 entry ", 1,
				currentCancelRecord.getOrderEntriesModificationEntries().size());

		assertPartialSuccessfulState(3, 3, 3);


	}



	/**
	 * Given an Order that contains a set of order entries containing items (products) and Partial Order Entry
	 * Cancellation is possible and the Order hasn't been sent to the warehouse yet (Order queue waiting time has not
	 * expired) then after Order Cancel Partial Request:
	 * <p>
	 * Order Cancel Entry should be created and set to "complete" state Quantity of order entries subject to cancelling
	 * should be reduced. Order should be recalculated according to new set of Order Entries. State of the Order is
	 * unchanged.
	 *
	 * @throws Exception
	 */
	@Test
	public void testPartialEntryCancelationBeforeWarehouse() throws Exception
	{
		requestPartialOrderCancelBeforeWarehouse(partialEntryRequest);

		final OrderEntryCancelRecordEntryModel orderEntryRecord = ((OrderEntryCancelRecordEntryModel) currentCancelRecord
				.getOrderEntriesModificationEntries().iterator().next());

		getModelService().refresh(getOrder());
		verify(notificationServiceAdapter, times(1)).sendCancelFinishedNotifications(any());

		Assert.assertEquals("Order cancel record entry should have 1 entry ", 1,
				currentCancelRecord.getOrderEntriesModificationEntries().size());

		Assert.assertFalse("Order cancel record should NOT be in progress ",
				currentCancelRecord.getModificationRecord().isInProgress());

		Assert.assertEquals("Order cancel record entry should be " + OrderModificationEntryStatus.SUCCESSFULL,
				OrderModificationEntryStatus.SUCCESSFULL, currentCancelRecord.getStatus());

		Assert.assertEquals("Order cancel record entry status should be " + OrderCancelEntryStatus.PARTIAL.getCode(),
				OrderCancelEntryStatus.PARTIAL, currentCancelRecord.getCancelResult());

		Assert.assertEquals("Order entry cancel record cancel request amount should be 2 ", 2,
				((OrderEntryCancelRecordEntryModel) currentCancelRecord.getOrderEntriesModificationEntries().iterator().next())
						.getCancelRequestQuantity().longValue());

		Assert.assertEquals("Order entry cancel record cancelled amount should be 2 ", 2,

				orderEntryRecord.getCancelledQuantity().longValue());

		Assert.assertEquals("Order should have all 3 entries", 3, getOrder().getEntries().size());

		Assert.assertEquals("Order should have all 3 entries", 1,
				getOrder().getEntries().get(orderEntryRecord.getOrderEntry().getEntryNumber().intValue()).getQuantity().longValue());
	}

	/**
	 * Given an Order that contains a set of order entries containing items (products) and Order Cancel Entry with
	 * "pending" state then after Cancel Successful Warehouse response
	 * <p>
	 * Order Cancel Entry should be updated with "completed" status. Canceled order entries should be removed. Order
	 * should be recalculated according to new set of Order Entries. State of the Order is restored to original value
	 * (before cancel request).
	 *
	 * @throws Exception
	 */
	@Test
	public void testWarehouseResponsePartialCancelOK() throws Exception
	{
		injectStateMappingStrategy(STATE_STRATEGY_SENT_TO_WAREHOUSE);
		requestPartialOrderCancelAfterWarehouse(partialRequest);

		assertTemporaryPendingState();

		orderCancelService.onOrderCancelResponse(partialResponseOK);

		getModelService().refresh(currentCancelRecord);
		//refresh OrderEntryModificationRecordEntries so that any references to (now-should-be-gone) OrderEntries are discarded.
		for (final OrderEntryModificationRecordEntryModel oemrem : currentCancelRecord.getOrderEntriesModificationEntries())
		{
			getModelService().refresh(oemrem);
		}


		assertPartialSuccessfulState(3, 3, 3);
	}





	/**
	 * Given an Order that contains a set of order entries containing items (products) and Order Cancel Entry with
	 * "pending" state then after Cancel Successful Warehouse response
	 * <p>
	 * Order Cancel Entry should be updated with "completed" status. Order entries subject to cancel should have their
	 * quantity reduced. Order should be recalculated according to new set of Order Entries. State of the Order is
	 * restored to original value (before cancel request).
	 *
	 * @throws Exception
	 */
	@Test
	public void testWarehouseResponsePartialEntryCancelOK() throws Exception
	{
		injectStateMappingStrategy(STATE_STRATEGY_SENT_TO_WAREHOUSE);
		requestPartialOrderCancelAfterWarehouse(partialEntryRequest);

		assertTemporaryPendingState();

		orderCancelService.onOrderCancelResponse(partialEntryResponseOK);

		getModelService().refresh(currentCancelRecord);
		//refresh OrderEntryModificationRecordEntries so that any references to (now-should-be-gone) OrderEntries are discarded.
		for (final OrderEntryModificationRecordEntryModel oemrem : currentCancelRecord.getOrderEntriesModificationEntries())
		{
			getModelService().refresh(oemrem);
		}

		assertPartialSuccessfulState(2, 2, 3);
	}

	/**
	 * Given an Order that contains a set of order entries containing items (products) and Order Cancel Entry with
	 * "pending" state then after Cancel Denied Warehouse response
	 * <p>
	 * Order Cancel Entry should be updated with "completed" status and reason code. State of the Order is restored to
	 * original value (before cancel request).
	 *
	 * @throws Exception
	 */
	@Test
	public void testWarehouseResponsePartialCancelDenied() throws Exception
	{
		injectStateMappingStrategy(STATE_STRATEGY_SENT_TO_WAREHOUSE);
		requestPartialOrderCancelAfterWarehouse(partialRequest);

		assertTemporaryPendingState();

		orderCancelService.onOrderCancelResponse(partialResponseDENIED);

		getModelService().refresh(currentCancelRecord);
		//refresh OrderEntryModificationRecordEntries so that any references to (now-should-be-gone) OrderEntries are discarded.
		for (final OrderEntryModificationRecordEntryModel oemrem : currentCancelRecord.getOrderEntriesModificationEntries())
		{
			getModelService().refresh(oemrem);
		}
		assertPartialDeniedState(3);

	}

	/**
	 * Given an Order that contains a set of order entries containing items (products) and Order Cancel Entry with
	 * "pending" state then after Cancel Denied Warehouse response
	 * <p>
	 * Order Cancel Entry should be updated with "completed" status and reason code. State of the Order is restored to
	 * original value (before cancel request).
	 *
	 * @throws Exception
	 */
	@Test
	public void testWarehouseResponsePartialEntryCancelDenied() throws Exception
	{
		injectStateMappingStrategy(STATE_STRATEGY_SENT_TO_WAREHOUSE);
		requestPartialOrderCancelAfterWarehouse(partialEntryRequest);

		assertTemporaryPendingState();

		orderCancelService.onOrderCancelResponse(partialEntryResponseDENIED);

		getModelService().refresh(currentCancelRecord);
		//refresh OrderEntryModificationRecordEntries so that any references to (now-should-be-gone) OrderEntries are discarded.
		for (final OrderEntryModificationRecordEntryModel oemrem : currentCancelRecord.getOrderEntriesModificationEntries())
		{
			getModelService().refresh(oemrem);
		}
		assertPartialDeniedState(2);
	}

	/**
	 * Given an Order that contains a set of order entries containing items (products) and Order Cancel Entry with
	 * "pending" state then after Cancel Partially Successful Warehouse response
	 * <p>
	 * Order Cancel Entry should be updated with "completed" status and reason code, and list of product entries that
	 * were cancelled. Canceled order entries should be removed. Order entries subject to partial cancel should have
	 * their quantity reduced. Order should be recalculated according to new set of Order Entries. State of the Order is
	 * restored to original value (before cancel request).
	 *
	 * @throws Exception
	 */
	@Test
	public void testWarehouseResponsePartialCancelPartialOK() throws Exception
	{
		injectStateMappingStrategy(STATE_STRATEGY_SENT_TO_WAREHOUSE);
		requestPartialOrderCancelAfterWarehouse(partialRequest);

		assertTemporaryPendingState();

		orderCancelService.onOrderCancelResponse(partialEntryResponseOK);

		getModelService().refresh(currentCancelRecord);
		//refresh OrderEntryModificationRecordEntries so that any references to (now-should-be-gone) OrderEntries are discarded.
		for (final OrderEntryModificationRecordEntryModel oemrem : currentCancelRecord.getOrderEntriesModificationEntries())
		{
			getModelService().refresh(oemrem);
		}

		assertPartialSuccessfulState(3, 2, 3);
	}

	/**
	 * This verifies that method {@link OrderCancelService#getAllCancelableEntries(OrderModel, PrincipalModel)} returns
	 * information that every entry can be completely canceled when order has no consignments
	 */
	@Test
	public void testAllCancelableOrderEntriesNoConsignments()
	{
		Assert.assertEquals("Order should not have any consignments", 0, this.getOrder().getConsignments().size());
		final Map<AbstractOrderEntryModel, Long> cancelableEntries = orderCancelService
				.getAllCancelableEntries(this.getOrder(), this.currentPrincipal);
		for (final AbstractOrderEntryModel entry : this.getOrder().getEntries())
		{
			final Long cancelableQuantity = cancelableEntries.get(entry);
			Assert.assertEquals("Total quantity of Order Entry should be cancelable", entry.getQuantity(), cancelableQuantity);
		}
	}

	/**
	 * This verifies that method {@link OrderCancelService#getAllCancelableEntries(OrderModel, PrincipalModel)} returns
	 * information that every entry can be completely canceled when order is splitted into consignments and every
	 * consignment has a status READY and no item has been shipped
	 */
	@Test
	public void testAllCancelableOrderEntriesWithConsignments()
	{

		Assert.assertTrue(this.getOrder().getEntries().size() >= 3);

		//final List<AbstractOrderEntryModel> orderEntryList = new ArrayList<AbstractOrderEntryModel>();

		final DeliveryModeModel deliveryMode1 = getModelService().create(DeliveryModeModel.class);
		deliveryMode1.setCode("deliveryMode1");
		final DeliveryModeModel deliveryMode2 = getModelService().create(DeliveryModeModel.class);
		deliveryMode2.setCode("deliveryMode2");
		final DeliveryModeModel deliveryMode3 = getModelService().create(DeliveryModeModel.class);
		deliveryMode3.setCode("deliveryMode3");

		final AbstractOrderEntryModel entry1 = this.getOrder().getEntries().get(0);
		entry1.setDeliveryMode(deliveryMode1);

		final AbstractOrderEntryModel entry2 = this.getOrder().getEntries().get(1);
		entry2.setDeliveryMode(deliveryMode2);

		final AbstractOrderEntryModel entry3 = this.getOrder().getEntries().get(2);
		entry3.setDeliveryMode(deliveryMode3);

		createConsignment(this.getOrder(), "testConsignment1", Arrays.asList(new AbstractOrderEntryModel[] { entry1, entry2 }));

		createConsignment(this.getOrder(), "testConsignment2", Arrays.asList(new AbstractOrderEntryModel[] { entry3 }));

		Assert.assertEquals("Order should have 2 consignments", 2, this.getOrder().getConsignments().size());

		final Map<AbstractOrderEntryModel, Long> cancelableEntries = orderCancelService
				.getAllCancelableEntries(this.getOrder(), this.currentPrincipal);
		for (final AbstractOrderEntryModel entry : this.getOrder().getEntries())
		{
			final Long cancelableQuantity = cancelableEntries.get(entry);
			Assert.assertEquals("Total quantity of Order Entry should be cancelable", entry.getQuantity(), cancelableQuantity);
		}

	}

	/**
	 * This verifies that method {@link OrderCancelService#getAllCancelableEntries(OrderModel, PrincipalModel)} returns
	 * information that:
	 * <ul>
	 * <li>order entries that belongs to consignments with status SHIPPED cannot be cancelled</li>
	 * <li>order entries that belongs to consignments with status READY and no items are shipped can be cancelled
	 * completely</li>
	 * <li>order entries that belongs to consignments with status READY and some items are shipped can be cancelled only
	 * partially</li>
	 */
	@Test
	public void testSomeCancelableOrderEntriesWithConsignments()
	{

		Assert.assertTrue(this.getOrder().getEntries().size() >= 3);

		//final List<AbstractOrderEntryModel> orderEntryList = new ArrayList<AbstractOrderEntryModel>();

		final DeliveryModeModel deliveryMode1 = getModelService().create(DeliveryModeModel.class);
		deliveryMode1.setCode("deliveryMode1");
		final DeliveryModeModel deliveryMode2 = getModelService().create(DeliveryModeModel.class);
		deliveryMode2.setCode("deliveryMode2");
		final DeliveryModeModel deliveryMode3 = getModelService().create(DeliveryModeModel.class);
		deliveryMode3.setCode("deliveryMode3");

		final AbstractOrderEntryModel entry1 = this.getOrder().getEntries().get(0);
		entry1.setDeliveryMode(deliveryMode1);

		final AbstractOrderEntryModel entry2 = this.getOrder().getEntries().get(1);
		entry2.setDeliveryMode(deliveryMode2);

		final AbstractOrderEntryModel entry3 = this.getOrder().getEntries().get(2);
		entry3.setDeliveryMode(deliveryMode3);

		final ConsignmentModel cons1 = createConsignment(this.getOrder(), "testConsignment1",
				Arrays.asList(new AbstractOrderEntryModel[] { entry1 }));

		final ConsignmentModel cons2 = createConsignment(this.getOrder(), "testConsignment2",
				Arrays.asList(new AbstractOrderEntryModel[] { entry2 }));

		final ConsignmentModel cons3 = createConsignment(this.getOrder(), "testConsignment3",
				Arrays.asList(new AbstractOrderEntryModel[] { entry3 }));

		cons1.setStatus(ConsignmentStatus.READY);
		cons2.setStatus(ConsignmentStatus.SHIPPED);
		cons3.getConsignmentEntries().iterator().next().setShippedQuantity(Long.valueOf(2l));

		Assert.assertEquals("Order should have 3 consignments:", 3, this.getOrder().getConsignments().size());

		final Map<AbstractOrderEntryModel, Long> cancelableEntries = orderCancelService
				.getAllCancelableEntries(this.getOrder(), this.currentPrincipal);

		Assert.assertTrue(cancelableEntries.containsKey(entry1));
		Assert.assertFalse(cancelableEntries.containsKey(entry2));
		Assert.assertTrue(cancelableEntries.containsKey(entry3));

		Assert.assertEquals("All quantity of entry1 should be cancelable", Long.valueOf(1), cancelableEntries.get(entry1));
		Assert.assertEquals("Only 1 item of entry3 should be cancelable as 2 are already shipped", Long.valueOf(1),
				cancelableEntries.get(entry3));
	}

	private ConsignmentModel createConsignment(final OrderModel order, final String code,
			final List<AbstractOrderEntryModel> orderEntries)
	{
		final ConsignmentModel cons = getModelService().create(ConsignmentModel.class);

		cons.setStatus(ConsignmentStatus.READY);
		cons.setConsignmentEntries(new HashSet<ConsignmentEntryModel>());
		cons.setCode(code);

		if (order != null)
		{
			cons.setShippingAddress(order.getDeliveryAddress());
		}

		for (final AbstractOrderEntryModel orderEntry : orderEntries)
		{
			final ConsignmentEntryModel entry = getModelService().create(ConsignmentEntryModel.class);

			entry.setOrderEntry(orderEntry);
			entry.setQuantity(orderEntry.getQuantity());
			entry.setConsignment(cons);
			cons.getConsignmentEntries().add(entry);
			cons.setDeliveryMode(orderEntry.getDeliveryMode());
		}

		cons.setOrder(order);

		final WarehouseModel warehouse = getModelService().create(WarehouseModel.class);
		warehouse.setCode(code + "_warehouse");

		final VendorModel vendor = getModelService().create(VendorModel.class);
		vendor.setCode(code + "_vendor");
		warehouse.setVendor(vendor);

		cons.setWarehouse(warehouse);
		getModelService().save(cons);
		//modelService.refresh(order);
		return cons;
	}

	private void requestPartialOrderCancelBeforeWarehouse(final OrderCancelRequest request) throws OrderCancelException
	{
		currentCancelRecord = orderCancelService.requestOrderCancel(request, currentPrincipal);

		//refresh OrderEntryModificationRecordEntries so that any references to (now-should-be-gone) OrderEntries are discarded.
		for (final OrderEntryModificationRecordEntryModel oemrem : currentCancelRecord.getOrderEntriesModificationEntries())
		{
			getModelService().refresh(oemrem);
		}
	}

	private void requestPartialOrderCancelAfterWarehouse(final OrderCancelRequest request) throws OrderCancelException
	{
		currentCancelRecord = orderCancelService.requestOrderCancel(request, currentPrincipal);

		//refresh OrderEntryModificationRecordEntries so that any references to (now-should-be-gone) OrderEntries are discarded.
		for (final OrderEntryModificationRecordEntryModel oemrem : currentCancelRecord.getOrderEntriesModificationEntries())
		{
			getModelService().refresh(oemrem);
		}
	}

	/**
	 *
	 */
	private void assertTemporaryPendingState()
	{
		Assert.assertTrue("Order cancel record should be in progress ", currentCancelRecord.getModificationRecord().isInProgress());

		Assert.assertEquals("Order cancel record entry should be " + OrderModificationEntryStatus.INPROGRESS,
				OrderModificationEntryStatus.INPROGRESS, currentCancelRecord.getStatus());

		Assert.assertEquals("Order cancel record entry status should be " + OrderCancelEntryStatus.PARTIAL.getCode(),
				OrderCancelEntryStatus.PARTIAL, currentCancelRecord.getCancelResult());

		Assert.assertEquals("Order should be in CANCELLING state", OrderStatus.CANCELLING, getOrder().getStatus());
	}

	/**
	 *
	 */
	private void assertPartialSuccessfulState(final int expectedRequestedAmount, final int expectedCancelledAmount,
			final int expectedOrderEntriesAmount)
	{
		Assert.assertFalse("Order cancel record should NOT be in progress ",
				currentCancelRecord.getModificationRecord().isInProgress());

		Assert.assertEquals("Order cancel record entry should be " + OrderModificationEntryStatus.SUCCESSFULL,
				OrderModificationEntryStatus.SUCCESSFULL, currentCancelRecord.getStatus());

		Assert.assertEquals("Order cancel record entry status should be " + OrderCancelEntryStatus.PARTIAL.getCode(),
				OrderCancelEntryStatus.PARTIAL, currentCancelRecord.getCancelResult());

		Assert.assertEquals("Order entry cancel record cancel request amount should be " + expectedRequestedAmount,
				expectedRequestedAmount,
				((OrderEntryCancelRecordEntryModel) currentCancelRecord.getOrderEntriesModificationEntries().iterator().next())
						.getCancelRequestQuantity().longValue());

		Assert.assertEquals("Order entry cancel record cancelled amount should be " + expectedCancelledAmount,
				expectedCancelledAmount,
				((OrderEntryCancelRecordEntryModel) currentCancelRecord.getOrderEntriesModificationEntries().iterator().next())
						.getCancelledQuantity().longValue());

		Assert.assertEquals("Order should have only " + expectedOrderEntriesAmount + " entries", expectedOrderEntriesAmount,
				getOrder().getEntries().size());
	}

	private void assertPartialDeniedState(final int expectedRequestedAmount)
	{
		Assert.assertFalse("Order cancel record should NOT be in progress ",
				currentCancelRecord.getModificationRecord().isInProgress());

		Assert.assertEquals("Order cancel record entry should be " + OrderModificationEntryStatus.SUCCESSFULL,
				OrderModificationEntryStatus.SUCCESSFULL, currentCancelRecord.getStatus());

		Assert.assertEquals("Order cancel record entry status should be " + OrderCancelEntryStatus.DENIED.getCode(),
				OrderCancelEntryStatus.DENIED, currentCancelRecord.getCancelResult());

		Assert.assertEquals("Order entry cancel record cancel request amount should be " + expectedRequestedAmount,
				expectedRequestedAmount,
				((OrderEntryCancelRecordEntryModel) currentCancelRecord.getOrderEntriesModificationEntries().iterator().next())
						.getCancelRequestQuantity().longValue());
	}

}
