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
package de.hybris.platform.commerceservices.order.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.strategies.EntryMergeStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.strategies.ModifiableChecker;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;


/**
 * JUnit test suite for {@link DefaultCommerceUpdateCartEntryStrategy}
 */
@UnitTest
public class DefaultCommerceUpdateCartEntryStrategyTest
{
	@InjectMocks
	private final DefaultCommerceUpdateCartEntryStrategy commerceUpdateCartEntryStrategy = new DefaultCommerceUpdateCartEntryStrategy();
	@Mock
	private ModifiableChecker<AbstractOrderEntryModel> entryOrderChecker;
	@Mock
	private EntryMergeStrategy entryMergeStrategy;
	@Mock
	private ModelService modelService;
	@Mock
	private CartService cartService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CommerceStockService commerceStockService;
	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;

	private CommerceCartParameter commerceCartParameter;
	private CartModel cartModel;
	private PointOfServiceModel pointOfServiceModel;
	private CartEntryModel entryMergeCandidate;
	private ProductModel entryMergeTargetProduct;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		commerceCartParameter = new CommerceCartParameter();
		cartModel = new CartModel();
		pointOfServiceModel = new PointOfServiceModel();

		commerceCartParameter.setCart(cartModel);
		commerceCartParameter.setPointOfService(pointOfServiceModel);
		commerceCartParameter.setEntryNumber(1);

		entryMergeCandidate = new CartEntryModel();
		entryMergeCandidate.setEntryNumber(Integer.valueOf(1));
		entryMergeCandidate.setQuantity(Long.valueOf(3));

		given(Boolean.valueOf(entryOrderChecker.canModify(any(AbstractOrderEntryModel.class)))).willReturn(Boolean.TRUE);

		final CartEntryModel entryMergeTarget = new CartEntryModel();
		entryMergeTarget.setEntryNumber(Integer.valueOf(4));
		entryMergeTarget.setQuantity(Long.valueOf(2));
		entryMergeTargetProduct = new ProductModel();
		entryMergeTargetProduct.setMaxOrderQuantity(Integer.valueOf(10));
		entryMergeTarget.setProduct(entryMergeTargetProduct);
		entryMergeTarget.setEntryGroupNumbers(Sets.newHashSet(Integer.valueOf(7)));

		given(entryMergeStrategy.getEntryToMerge(anyListOf(AbstractOrderEntryModel.class), any(AbstractOrderEntryModel.class)))
				.willReturn(entryMergeTarget);
		given(cartService.getEntriesForProduct(eq(cartModel), eq(entryMergeTargetProduct)))
				.willReturn(Collections.singletonList(entryMergeTarget));

		cartModel.setEntries(Arrays.asList(entryMergeCandidate, entryMergeTarget));

		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);
		given(Boolean.valueOf(commerceStockService.isStockSystemEnabled(any(BaseStoreModel.class)))).willReturn(Boolean.FALSE);
	}

	@Test
	public void shouldFailUpdatePosIfCartNotProvided() throws CommerceCartModificationException
	{
		commerceCartParameter.setCart(null);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Cart model");

		commerceUpdateCartEntryStrategy.updatePointOfServiceForCartEntry(commerceCartParameter);
	}

	@Test
	public void shouldFailUpdatePosIfPosNotProvided() throws CommerceCartModificationException
	{
		commerceCartParameter.setPointOfService(null);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("PointOfService Model");

		commerceUpdateCartEntryStrategy.updatePointOfServiceForCartEntry(commerceCartParameter);
	}

	@Test
	public void shouldFailUpdatePosIfEntryNotFound() throws CommerceCartModificationException
	{
		cartModel.setEntries(Collections.emptyList());
		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("Unknown entry number");

		commerceUpdateCartEntryStrategy.updatePointOfServiceForCartEntry(commerceCartParameter);
	}

	@Test
	public void shouldFailUpdatePosIfEntryNotUpdatable() throws CommerceCartModificationException
	{
		given(Boolean.valueOf(entryOrderChecker.canModify(any(AbstractOrderEntryModel.class)))).willReturn(Boolean.FALSE);

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("Entry is not updatable");

		commerceUpdateCartEntryStrategy.updatePointOfServiceForCartEntry(commerceCartParameter);
	}

	@Test
	public void updatePosShouldMergeEntries() throws CommerceCartModificationException
	{
		final CommerceCartModification cartModification = commerceUpdateCartEntryStrategy
				.updatePointOfServiceForCartEntry(commerceCartParameter);

		verify(modelService).remove(entryMergeCandidate);

		assertThat(cartModification.getQuantity()).isEqualTo(5);
		assertThat(cartModification.getQuantityAdded()).isEqualTo(3);
		assertThat(cartModification.getStatusCode()).isEqualTo(CommerceCartModificationStatus.SUCCESS);
	}

	@Test
	public void updatePosShouldSucceedIfNoEntry() throws CommerceCartModificationException
	{
		given(entryMergeStrategy.getEntryToMerge(anyListOf(AbstractOrderEntryModel.class), any(AbstractOrderEntryModel.class)))
				.willReturn(null);

		final CommerceCartModification cartModification = commerceUpdateCartEntryStrategy
				.updatePointOfServiceForCartEntry(commerceCartParameter);

		assertThat(entryMergeCandidate.getDeliveryPointOfService()).isEqualTo(pointOfServiceModel);
		assertThat(cartModification.getEntry()).isEqualTo(entryMergeCandidate);
		assertThat(cartModification.getStatusCode()).isEqualTo(CommerceCartModificationStatus.SUCCESS);
	}

	@Test
	public void updatePosShouldCheckStock() throws CommerceCartModificationException
	{
		given(entryMergeStrategy.getEntryToMerge(anyListOf(AbstractOrderEntryModel.class), any(AbstractOrderEntryModel.class)))
				.willReturn(null);
		given(Boolean.valueOf(commerceStockService.isStockSystemEnabled(any(BaseStoreModel.class)))).willReturn(Boolean.TRUE);
		given(commerceStockService.getStockLevelForProductAndPointOfService(any(ProductModel.class),
				any(PointOfServiceModel.class))).willReturn(Long.valueOf(1));

		final CommerceCartModification cartModification = commerceUpdateCartEntryStrategy
				.updatePointOfServiceForCartEntry(commerceCartParameter);

		assertThat(entryMergeCandidate.getDeliveryPointOfService()).isEqualTo(pointOfServiceModel);
		assertThat(entryMergeCandidate.getQuantity()).isEqualTo(Long.valueOf(1));
		assertThat(cartModification.getEntry()).isEqualTo(entryMergeCandidate);
		assertThat(cartModification.getQuantity()).isEqualTo(Long.valueOf(1));
		assertThat(cartModification.getStatusCode()).isEqualTo(CommerceCartModificationStatus.LOW_STOCK);
	}

	@Test
	public void updateShippingModeShouldMergeEntries() throws CommerceCartModificationException
	{
		entryMergeCandidate.setDeliveryPointOfService(pointOfServiceModel);
		final CommerceCartModification cartModification = commerceUpdateCartEntryStrategy
				.updateToShippingModeForCartEntry(commerceCartParameter);

		verify(modelService).remove(entryMergeCandidate);

		assertThat(cartModification.getQuantity()).isEqualTo(5);
		assertThat(cartModification.getQuantityAdded()).isEqualTo(3);
		assertThat(cartModification.getStatusCode()).isEqualTo(CommerceCartModificationStatus.SUCCESS);
	}
}