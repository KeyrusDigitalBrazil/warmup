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
package de.hybris.platform.warehousing.replacement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReplacementReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.returns.ReturnsServiceTest;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReplacementEntryModel;
import de.hybris.platform.returns.model.ReplacementOrderModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;


/**
 * Re-implements test {@link ReturnsServiceTest} to provide missing information required when warehousing extensions is present
 */
@IntegrationTest(replaces = ReturnsServiceTest.class)
public class OrderManagementReturnsServiceTest extends ReturnsServiceTest
{
	@Resource
	private ReturnService returnService;
	@Resource
	private CartService cartService;
	@Resource
	private OrderService orderService;
	@Resource
	private ProductService productService;
	@Resource
	private UserService userService;
	@Resource
	private ModelService modelService;
	@Resource
	private CalculationService calculationService;

	private OrderModel order;

	@Override
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();

		final ProductModel product0 = productService.getProductForCode("testProduct0");
		final ProductModel product1 = productService.getProductForCode("testProduct1");
		final ProductModel product2 = productService.getProductForCode("testProduct2");
		final CartModel cart = cartService.getSessionCart();
		final UserModel user = userService.getCurrentUser();
		cartService.addNewEntry(cart, product0, 2, null);
		cartService.addNewEntry(cart, product1, 2, null);
		cartService.addNewEntry(cart, product2, 2, null);

		final AddressModel deliveryAddress = new AddressModel();
		deliveryAddress.setOwner(user);
		deliveryAddress.setFirstname("Albert");
		deliveryAddress.setLastname("Einstein");
		deliveryAddress.setTown("Munich");

		final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
		paymentInfo.setOwner(cart);
		paymentInfo.setCode("Debit");
		paymentInfo.setBank("MyBank");
		paymentInfo.setUser(user);
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("Ich");

		cart.setDeliveryAddress(deliveryAddress);
		cart.setPaymentInfo(paymentInfo);
		cart.setPaymentAddress(deliveryAddress);
		order = orderService.createOrderFromCart(cart);

		BaseStoreModel basestore = new BaseStoreModel();
		basestore.setUid("testStore");
		basestore.setName("test", Locale.ENGLISH);
		modelService.save(basestore);

		VendorModel vendor = new VendorModel();
		vendor.setCode("testVendor");
		modelService.save(vendor);

		WarehouseModel warehouse = new WarehouseModel();
		warehouse.setCode("testWarehouse");
		warehouse.setBaseStores(Lists.newArrayList(basestore));
		warehouse.setVendor(vendor);
		modelService.save(warehouse);

		ConsignmentModel consignment = new ConsignmentModel();
		consignment.setOrder(order);
		consignment.setCode("consignmentCode");
		consignment.setShippingAddress(deliveryAddress);
		consignment.setStatus(ConsignmentStatus.SHIPPED);
		consignment.setWarehouse(warehouse);
		modelService.save(consignment);
		final ConsignmentEntryModel consignmentEntryModel = getConsignmentEntryModel(order.getEntries().get(0), consignment);
		final ConsignmentEntryModel consignmentEntryModel1 = getConsignmentEntryModel(order.getEntries().get(1), consignment);
		final ConsignmentEntryModel consignmentEntryModel2 = getConsignmentEntryModel(order.getEntries().get(2), consignment);
		consignment.setConsignmentEntries(Sets.newHashSet(consignmentEntryModel, consignmentEntryModel1, consignmentEntryModel2));
		modelService.save(consignment);

		order.getEntries().get(0).setConsignmentEntries(Sets.newHashSet(consignmentEntryModel));
		calculationService.calculate(order);
	}

	protected ConsignmentEntryModel getConsignmentEntryModel(AbstractOrderEntryModel orderEntryModel, ConsignmentModel consignment)
	{
		final ConsignmentEntryModel consignmentEntryModel = new ConsignmentEntryModel();
		consignmentEntryModel.setOrderEntry(orderEntryModel);
		consignmentEntryModel.setQuantity(2L);
		consignmentEntryModel.setConsignment(consignment);
		consignmentEntryModel.setShippedQuantity(2L);
		modelService.save(consignmentEntryModel);
		return consignmentEntryModel;
	}

	@Override
	@Test
	public void testCreateReturnRequest()
	{
		// testing setup data
		assertNotNull("Missing order instance ", order);
		assertEquals("There should be no existing ReturnRequest instance for the assigned order", 0, returnService
				.getReturnRequests(order.getCode()).size());

		// 'ReturnRequest': create, search, order reference check
		returnService.createReturnRequest(order); // no.1

		// testing 'search by order'
		final List<ReturnRequestModel> requests = returnService.getReturnRequests(order.getCode());
		assertEquals("Search should returns one ReturnRequest instance", 1, returnService.getReturnRequests(order.getCode()).size());

		returnService.createReturnRequest(order); // no.2
		assertEquals("Search should returns two ReturnRequest instance", 2, returnService.getReturnRequests(order.getCode()).size());

		// checking Order reference
		for (final ReturnRequestModel request : requests)
		{
			assertEquals("Wrong order assigned", order.getCode(), request.getOrder().getCode());
		}
	}

	@Override
	@Test
	public void testRMAgeneration()
	{
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		assertNotNull("RMA shouldn't be <null> ", request.getRMA());
		assertEquals("Searching for RMA with the help of ReturnsService failed", request.getRMA(), returnService.getRMA(request));
	}

	@Override
	@Test
	public void testReplacementOrderCreation()
	{
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		request.setRMA(returnService.createRMA(request));

		final AbstractOrderEntryModel originalEntry = order.getEntries().iterator().next();
		final ReplacementOrderModel replacementOrder = returnService.createReplacementOrder(request);

		assertNotNull("ReplacementOrder instance shouldn't be <null> ", replacementOrder);
		assertNotNull("Returned ReplacementOrder instance shouldn't be <null> ", request.getReplacementOrder());

		final ReplacementEntryModel replacementEntry = returnService.createReplacement(request, originalEntry, "no.1", Long
				.valueOf(3), ReturnAction.IMMEDIATE, ReplacementReason.LATEDELIVERY);
		assertNotNull("ReplacementEntry shouldn't be <null> ", replacementEntry);
		assertEquals("Wrong reason assigned", replacementEntry.getReason(), ReplacementReason.LATEDELIVERY);
		assertEquals("Wrong reason assigned", replacementEntry.getAction(), ReturnAction.IMMEDIATE);
		assertEquals("Wrong product reference", replacementEntry.getOrderEntry().getProduct(), originalEntry.getProduct());
		assertEquals("Wrong expected quantity", replacementEntry.getExpectedQuantity(), Long.valueOf(3));

		returnService.addReplacementOrderEntries(replacementOrder, Arrays.asList(replacementEntry));

		assertEquals("There should be already an order entry assigned", 1, request.getReplacementOrder().getEntries().size());
		assertEquals("Wrong product reference", request.getReplacementOrder().getEntries().iterator().next().getProduct(),
				replacementEntry.getOrderEntry().getProduct());
	}

	@Override
	@Test
	public void testRefundEntryCreation()
	{
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		request.setRMA(returnService.createRMA(request));

		final AbstractOrderEntryModel originalEntry = order.getEntries().iterator().next();

		final RefundEntryModel refundEntry = returnService.createRefund(request, originalEntry, "no.1", Long.valueOf(2),
				ReturnAction.IMMEDIATE, RefundReason.LATEDELIVERY);
		assertNotNull("ReplacementEntry shouldn't be <null> ", refundEntry);
		assertEquals("Wrong reason assigned", refundEntry.getReason(), RefundReason.LATEDELIVERY);
		assertEquals("Wrong reason assigned", refundEntry.getAction(), ReturnAction.IMMEDIATE);
		assertEquals("Wrong product reference", refundEntry.getOrderEntry().getProduct(), originalEntry.getProduct());
		assertEquals("Wrong expected quantity", refundEntry.getExpectedQuantity(), Long.valueOf(2));

	}

	@Override
	@Test
	public void testReturnsEntryCreation()
	{
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		request.setRMA(returnService.createRMA(request));

		final AbstractOrderEntryModel originalEntry = order.getEntries().iterator().next();
		final ReplacementEntryModel replacementEntry = returnService.createReplacement(request, originalEntry, "no.1", Long
				.valueOf(3), ReturnAction.IMMEDIATE, ReplacementReason.LATEDELIVERY);

		final List<ReturnEntryModel> returnsEntry1 = returnService.getReturnEntries(originalEntry.getProduct());
		assertEquals("Search by product returns wrong 'returns' entry", returnsEntry1.iterator().next(), replacementEntry);
		final List<ReturnEntryModel> returnsEntry2 = returnService.getReturnEntry(originalEntry);
		assertEquals("Search by order entry returns wrong 'returns' entry", returnsEntry2.iterator().next(), replacementEntry);
	}

	@Override
	@Test
	public void isReturnableTest()
	{
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		request.setRMA(returnService.createRMA(request));
		final AbstractOrderEntryModel originalEntry = order.getEntries().iterator().next();
		returnService.createRefund(request, originalEntry, "no.3", Long.valueOf(1), ReturnAction.IMMEDIATE,
				RefundReason.LATEDELIVERY);

		assertFalse(returnService.isReturnable(order, originalEntry, 1)); // missing consignment
		assertFalse(returnService.isReturnable(order, originalEntry, 2)); // wrong quantity
	}

	@Override
	public void getAllReturnableEntries()
	{
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		request.setRMA(returnService.createRMA(request));
		final Map<AbstractOrderEntryModel, Long> returnables = returnService.getAllReturnableEntries(order);
		for (final Map.Entry entry : returnables.entrySet())
		{
			assertEquals("Unexpected 'returnable'!", Long.valueOf(2), entry.getValue());
		}
	}
}

