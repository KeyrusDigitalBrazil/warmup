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

package de.hybris.platform.configurablebundlefacades.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.strategies.ModifiableChecker;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Unit tests for {@link BundleCommerceOrderEntryPopulator}
 */
@UnitTest
public class BundleCommerceOrderEntryPopulatorTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter;
	@Mock
	private BundleTemplateService bundleTemplateService;
	@Mock
	private ModifiableChecker<AbstractOrderEntryModel> entryOrderChecker;
	@Mock
	private OrderEntryData target;
	@InjectMocks
	private final BundleCommerceOrderEntryPopulator bundleOrderEntryPopulator = new BundleCommerceOrderEntryPopulator();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSourceParamCannotBeNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter source can not be null");

		bundleOrderEntryPopulator.populate(null, new OrderEntryData());
	}

	@Test
	public void testTargetParamCannotBeNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter target can not be null");

		bundleOrderEntryPopulator.populate(new OrderEntryModel(), null);
	}

	@Test
	public void testPopulateNullOrder()
	{
		bundleOrderEntryPopulator.populate(new OrderEntryModel(), target);

		verifyZeroInteractions(target);
	}

	@Test
	public void testPopulateNullBillingTime()
	{
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		orderEntryModel.setOrder(new OrderModel());

		bundleOrderEntryPopulator.populate(new OrderEntryModel(), target);

		verifyZeroInteractions(target);
	}

	@Test
	public void testPopulateNoBundleTemplate()
	{
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		orderEntryModel.setQuantity(3L);
		orderEntryModel.setEntryNumber(4);
		final OrderModel orderModel = new OrderModel();
		orderEntryModel.setOrder(orderModel);

		given(entryOrderChecker.canModify(any(AbstractOrderEntryModel.class))).willReturn(true);

		bundleOrderEntryPopulator.populate(orderEntryModel, target);

		verify(target).setEntryNumber(4);
		verify(target).setQuantity(3L);
		verify(target).setUpdateable(true);
		verify(target).setBundleNo(0);
		verify(entryOrderChecker).canModify(orderEntryModel);
	}

	@Test
	public void testPopulateWithBundleTemplateNoParentTemplate()
	{
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		orderEntryModel.setQuantity(3L);
		orderEntryModel.setEntryNumber(4);
		orderEntryModel.setBundleNo(5);
		final BundleTemplateModel bundleTemplateModel = new BundleTemplateModel();
		orderEntryModel.setBundleTemplate(bundleTemplateModel);
		final OrderModel orderModel = new OrderModel();
		orderEntryModel.setOrder(orderModel);

		given(entryOrderChecker.canModify(any(AbstractOrderEntryModel.class))).willReturn(true);
		final BundleTemplateData bundleTemplateData = new BundleTemplateData();
		given(bundleTemplateConverter.convert(any(BundleTemplateModel.class))).willReturn(bundleTemplateData);

		bundleOrderEntryPopulator.populate(orderEntryModel, target);

		verify(bundleTemplateConverter).convert(bundleTemplateModel);
		verify(target).setBundleNo(5);
		verify(target).setComponent(bundleTemplateData);
	}

	@Test
	public void testPopulateWithBundleTemplateAndParentTemplate()
	{
		final OrderEntryModel orderEntryModel = new OrderEntryModel();
		orderEntryModel.setQuantity(3L);
		orderEntryModel.setEntryNumber(4);
		orderEntryModel.setBundleNo(5);
		final BundleTemplateModel bundleTemplateModel = new BundleTemplateModel();
		final BundleTemplateModel parentBundleTemplateModel = new BundleTemplateModel();
		bundleTemplateModel.setParentTemplate(parentBundleTemplateModel);
		orderEntryModel.setBundleTemplate(bundleTemplateModel);
		final OrderModel orderModel = new OrderModel();
		orderEntryModel.setOrder(orderModel);

		given(bundleTemplateService.getRootBundleTemplate(any(BundleTemplateModel.class))).willReturn(parentBundleTemplateModel);
		given(entryOrderChecker.canModify(any(AbstractOrderEntryModel.class))).willReturn(true);
		final BundleTemplateData bundleTemplateData = new BundleTemplateData();
		given(bundleTemplateConverter.convert(eq(bundleTemplateModel))).willReturn(bundleTemplateData);
		final BundleTemplateData parentBundleTemplateData = new BundleTemplateData();
		given(bundleTemplateConverter.convert(eq(parentBundleTemplateModel))).willReturn(parentBundleTemplateData);

		bundleOrderEntryPopulator.populate(orderEntryModel, target);

		verify(bundleTemplateConverter).convert(bundleTemplateModel);
		verify(bundleTemplateConverter).convert(bundleTemplateModel);
		verify(bundleTemplateConverter).convert(parentBundleTemplateModel);
		verify(bundleTemplateService).getRootBundleTemplate(parentBundleTemplateModel);
		verify(target).setRootBundleTemplate(parentBundleTemplateData);
	}
}
