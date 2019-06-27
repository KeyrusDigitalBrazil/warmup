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
package de.hybris.platform.acceleratorservices.order.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAcceleratorCheckoutServiceTest
{
	@InjectMocks
	private final DefaultAcceleratorCheckoutService defaultAcceleratorCheckoutService = new DefaultAcceleratorCheckoutService();

	@Mock
	private CartService cartService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private CommerceStockService commerceStockService;
	@Mock
	private BaseStoreService baseStoreService; // NOSONAR

	@Mock
	private CartModel cartModel;
	@Mock
	private ProductModel productModel;
	@Mock
	private PointOfServiceModel consolidatedPickupPointModel;

	private CartEntryModel entry0;
	private CartEntryModel entry1;
	private CartEntryModel entry3;
	private CartEntryModel entry4;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		final List<CartEntryModel> cartEntries = new ArrayList<>();

		// entry0 anchor entry
		entry0 = new CartEntryModel();
		entry0.setDeliveryPointOfService(consolidatedPickupPointModel);
		entry0.setQuantity(Long.valueOf(1));
		entry0.setProduct(productModel);
		cartEntries.add(entry0);

		// entry1
		entry1 = new CartEntryModel();
		final PointOfServiceModel pos0 = new PointOfServiceModel();
		entry1.setDeliveryPointOfService(pos0);
		entry1.setQuantity(Long.valueOf(2));
		entry1.setProduct(productModel);
		cartEntries.add(entry1);

		// entry2
		final CartEntryModel entry2 = new CartEntryModel(); // does not count since no POS for this entry
		entry2.setQuantity(Long.valueOf(1));
		entry2.setProduct(productModel);
		cartEntries.add(entry2);
		when(cartService.getEntriesForProduct(cartModel, productModel)).thenReturn(cartEntries);

		// entry3
		entry3 = new CartEntryModel();
		final PointOfServiceModel pos1 = new PointOfServiceModel();
		entry3.setDeliveryPointOfService(pos1);
		entry3.setQuantity(Long.valueOf(2));
		final ProductModel product0 = new ProductModel();
		entry3.setProduct(product0);

		// entry4
		entry4 = new CartEntryModel();
		entry4.setDeliveryPointOfService(consolidatedPickupPointModel);
		entry4.setQuantity(Long.valueOf(2));
		entry4.setProduct(product0);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entry0);
		entries.add(entry1);
		entries.add(entry2);
		entries.add(entry3);
		entries.add(entry4);
		when(cartModel.getEntries()).thenReturn(entries);
	}

	@Test
	public void shouldCalculateProductQtyInCart()
	{
		final long productQty = defaultAcceleratorCheckoutService.calculateProductQtyInCart(productModel, cartModel);
		Assert.assertEquals("product qty is wrong", 3, productQty);
	}

	@Test
	public void shouldGetAnchorEntryToConsolidate()
	{
		final AbstractOrderEntryModel anchorEntry = defaultAcceleratorCheckoutService.getAnchorEntryToConsolidate(entry1, cartModel,
				consolidatedPickupPointModel);
		Assert.assertEquals("Anchor entry is wrong", entry0, anchorEntry);
	}

	@Test
	public void shouldGetEntriesToConsolidate() throws CommerceCartModificationException
	{
		final List<AbstractOrderEntryModel> entriesToBeRemovedDueToPOS = new ArrayList<>();
		final List<AbstractOrderEntryModel> consolidatedEntriesToBeRemoved = new ArrayList<>();
		final List<CommerceCartModification> unsuccessfulModifications = new ArrayList<>();
		final List<AbstractOrderEntryModel> entriesToConsolidate = defaultAcceleratorCheckoutService.getEntriesToConsolidate(
				cartModel, consolidatedPickupPointModel, entriesToBeRemovedDueToPOS, consolidatedEntriesToBeRemoved,
				unsuccessfulModifications);

		Assert.assertEquals("Size is wrong", 2, entriesToConsolidate.size());
		Assert.assertEquals("Should be entry0", entry0, entriesToConsolidate.get(0));
		Assert.assertEquals("Should be entry4", entry4, entriesToConsolidate.get(1));
	}

	@Test
	public void shouldGetExistingAnchorEntryByProduct()
	{
		final List<AbstractOrderEntryModel> entriesToConsolidate = new ArrayList<>();
		entriesToConsolidate.add(entry0);
		entriesToConsolidate.add(entry3);
		final AbstractOrderEntryModel anchorEntry = defaultAcceleratorCheckoutService.getExistingAnchorEntryByProduct(productModel,
				entriesToConsolidate);

		Assert.assertEquals("Anchor entry is wrong", entry0, anchorEntry);
	}

	@Test
	public void shouldInStock()
	{
		when(Boolean.valueOf(commerceStockService.isStockSystemEnabled(any()))).thenReturn(Boolean.TRUE);
		when(commerceStockService.getStockLevelForProductAndPointOfService(productModel, consolidatedPickupPointModel))
				.thenReturn(Long.valueOf(10));

		Assert.assertEquals("Should be in stock", Boolean.TRUE,
				Boolean.valueOf(defaultAcceleratorCheckoutService.isInStock(productModel, consolidatedPickupPointModel)));
	}

	@Test
	public void shouldForceInStock()
	{
		when(Boolean.valueOf(commerceStockService.isStockSystemEnabled(any()))).thenReturn(Boolean.FALSE);

		Assert.assertEquals("Should be in stock", Boolean.TRUE,
				Boolean.valueOf(defaultAcceleratorCheckoutService.isInStock(productModel, consolidatedPickupPointModel)));
	}

	@Test
	public void shouldNotInStock()
	{
		when(Boolean.valueOf(commerceStockService.isStockSystemEnabled(any()))).thenReturn(Boolean.TRUE);
		when(commerceStockService.getStockLevelForProductAndPointOfService(productModel, consolidatedPickupPointModel))
				.thenReturn(Long.valueOf(0));

		Assert.assertEquals("Should not be in stock", Boolean.FALSE,
				Boolean.valueOf(defaultAcceleratorCheckoutService.isInStock(productModel, consolidatedPickupPointModel)));
	}

	@Test
	public void shouldUpdatePOS() throws CommerceCartModificationException
	{
		final List<CommerceCartModification> unsuccessfulModifications = new ArrayList<>();
		entry1.setEntryNumber(Integer.valueOf(1));
		final CommerceCartModification modification = new CommerceCartModification();
		modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
		when(commerceCartService.updatePointOfServiceForCartEntry(any(CommerceCartParameter.class))).thenReturn(modification);
		defaultAcceleratorCheckoutService.updatePOS(cartModel, consolidatedPickupPointModel, unsuccessfulModifications, entry1);
		verify(commerceCartService, times(1)).updatePointOfServiceForCartEntry(any(CommerceCartParameter.class));

		Assert.assertEquals("Should be 1", Integer.valueOf(1), Integer.valueOf(unsuccessfulModifications.size()));
		Assert.assertEquals("modification is wrong", modification, unsuccessfulModifications.get(0));
	}
}
