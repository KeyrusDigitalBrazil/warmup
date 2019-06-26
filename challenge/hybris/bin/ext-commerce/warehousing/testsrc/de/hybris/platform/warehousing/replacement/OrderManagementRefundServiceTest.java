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
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.refund.RefundService;
import de.hybris.platform.refund.RefundServiceTest;
import de.hybris.platform.refund.impl.DefaultRefundService;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;

import org.junit.Test;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


/**
 * Re-implements test {@link RefundServiceTest} to provide missing information required when warehousing extensions is present
 */
@IntegrationTest(replaces = RefundServiceTest.class)
public class OrderManagementRefundServiceTest extends RefundServiceTest
{
	@Resource
	protected CartService cartService;
	@Resource
	protected UserService userService;
	@Resource
	protected OrderService orderService;
	@Resource
	protected ReturnService returnService;
	@Resource
	protected RefundService refundService;
	@Resource
	protected ModelService modelService;
	@Resource
	protected OrderHistoryService orderHistoryService;
	@Resource
	protected CalculationService calculationService;
	@Resource
	protected UnitService unitService;
	@Resource
	protected CatalogVersionService catalogVersionService;
	@Resource
	protected CommonI18NService commonI18NService;

	@Override
	@Test
	public void testRefundCalculation() throws Exception
	{
		final OrderModel order = prepareOrder();

		final RefundEntryModel refundEntry1 = createRefundEntryModel(order, Long.valueOf(1));

		refundService.apply(Arrays.asList(refundEntry1), order);

		assertEquals("Wrong refund (preview)!", BigDecimal.valueOf(5.0), BigDecimal.valueOf(order.getTotalPrice().doubleValue()));

		//assertNull("There shouldn't exists any record entry yet!",
		//		((DefaultRefundService) refundService).getModificationHandler().getReturnRecord(order));

		assertEquals("Wrong refund (apply)!", BigDecimal.valueOf(5.0), BigDecimal.valueOf(order.getTotalPrice().doubleValue()));

		// Turn on history validation when OMSE-1826 is done
		// validateHistory(order);
	}

	@Override
	@Test
	public void testOrderCalcWhenQuantityEqualTo0() throws Exception
	{
		final OrderModel order = prepareOrder();
		final RefundEntryModel refundEntry1 = createRefundEntryModel(order, Long.valueOf(2));

		refundService.apply(Arrays.asList(refundEntry1), order);

		assertNull("There shouldn't exists any record entry yet!",
				((DefaultRefundService) refundService).getModificationHandler().getReturnRecord(order));

		assertEquals("Wrong refund (apply)!", BigDecimal.valueOf(0.0), BigDecimal.valueOf(order.getTotalPrice().doubleValue()));

		// Turn on history validation when OMSE-1826 is done
		// validateHistory(order);
	}

	protected OrderModel prepareOrder() throws Exception
	{
		final ProductModel product1 = new ProductModel();
		product1.setCode("test");
		product1.setUnit(unitService.getUnitForCode("kg"));
		product1.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));

		final PriceRowModel prmodel = modelService.create(PriceRowModel.class);
		prmodel.setCurrency(commonI18NService.getCurrency("EUR"));
		prmodel.setMinqtd(Long.valueOf(1));
		prmodel.setNet(Boolean.TRUE);
		prmodel.setPrice(Double.valueOf(5.00));
		prmodel.setUnit(unitService.getUnitForCode("kg"));
		prmodel.setProduct(product1);
		prmodel.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
		modelService.saveAll(Arrays.asList(prmodel, product1));

		final CartModel cart = cartService.getSessionCart();
		final UserModel user = userService.getCurrentUser();

		cartService.addNewEntry(cart, product1, 2, null);

		final AddressModel deliveryAddress = new AddressModel();
		deliveryAddress.setOwner(user);
		deliveryAddress.setFirstname("Juergen");
		deliveryAddress.setLastname("Albertsen");
		deliveryAddress.setTown("Muenchen");
		modelService.saveAll(Arrays.asList(deliveryAddress));

		final DebitPaymentInfoModel paymentInfo = new DebitPaymentInfoModel();
		paymentInfo.setOwner(cart);
		paymentInfo.setCode("debit");
		paymentInfo.setBank("MeineBank");
		paymentInfo.setUser(user);
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("Ich");
		modelService.saveAll(Arrays.asList(paymentInfo));

		// the original order the customer wants to have a refund for
		cart.setDeliveryAddress(deliveryAddress);
		cart.setPaymentInfo(paymentInfo);
		cart.setPaymentAddress(deliveryAddress);
		final OrderModel order = orderService.createOrderFromCart(cart);

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

		final ConsignmentModel consignment = new ConsignmentModel();
		consignment.setOrder(order);
		consignment.setCode("consignmentCode");
		consignment.setShippingAddress(deliveryAddress);
		consignment.setStatus(ConsignmentStatus.SHIPPED);
		consignment.setWarehouse(warehouse);
		modelService.save(consignment);
		final ConsignmentEntryModel consignmentEntryModel = new ConsignmentEntryModel();
		consignmentEntryModel.setOrderEntry(order.getEntries().get(0));
		consignmentEntryModel.setQuantity(2L);
		consignmentEntryModel.setConsignment(consignment);
		consignmentEntryModel.setShippedQuantity(2L);
		modelService.save(consignmentEntryModel);
		consignment.setConsignmentEntries(Sets.newHashSet(consignmentEntryModel));
		modelService.save(consignment);

		order.getEntries().get(0).setConsignmentEntries(Sets.newHashSet(consignmentEntryModel));
		calculationService.calculate(order);

		return order;
	}

	protected RefundEntryModel createRefundEntryModel(OrderModel order, final Long quantityToRefund)
	{
		// lets create a RMA for it
		final ReturnRequestModel request = returnService.createReturnRequest(order);
		returnService.createRMA(request);

		final AbstractOrderEntryModel productToRefund1 = order.getEntries().iterator().next(); // has quantity of 2
		final RefundEntryModel refundEntry1 = returnService
				.createRefund(request, productToRefund1, "no.1", quantityToRefund, ReturnAction.IMMEDIATE, RefundReason.LATEDELIVERY);

		assertEquals("Unexpected order price!", BigDecimal.valueOf(10.0), BigDecimal.valueOf(order.getTotalPrice().doubleValue()));
		return refundEntry1;
	}

	protected void validateHistory(OrderModel order)
	{
		final Collection<OrderHistoryEntryModel> histories = orderHistoryService.getHistoryEntries(order, null, null);

		assertEquals("Wrong count of history entries!", 1, histories.size());

		final OrderHistoryEntryModel history = histories.iterator().next();

		assertEquals("Unexpected orderhistory price!", BigDecimal.valueOf(10.0),
				BigDecimal.valueOf(history.getPreviousOrderVersion().getTotalPrice().doubleValue()));
	}
}

