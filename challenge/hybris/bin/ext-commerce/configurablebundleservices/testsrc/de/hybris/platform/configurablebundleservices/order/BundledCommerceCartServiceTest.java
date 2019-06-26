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
package de.hybris.platform.configurablebundleservices.order;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.Collections;
import java.util.HashSet;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class BundledCommerceCartServiceTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(BundledCommerceCartServiceTest.class);

	@Resource
	protected CommerceCartService commerceCartService;
	@Resource
	protected ModelService modelService;
	@Resource
	protected UserService userService;
	@Resource
	protected ProductService productService;
	@Resource
	protected BundleTemplateService bundleTemplateService;
	@Resource
	protected EntryGroupService entryGroupService;

	protected CartModel cart;

	@Before
	public void setUp() throws Exception
	{
		LOG.debug("Preparing test data");
		final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		try
		{
			importCsv("/configurablebundleservices/test/cartRegistration.impex", "utf-8");
			importCsv("/configurablebundleservices/test/nestedBundleTemplates.impex", "utf-8");
		}
		finally
		{
			Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);
		}

		cart = userService.getUserForUID("bundle").getCarts().iterator().next();
	}

	@Test
	public void shouldAssignBundlePropertiesToEntry() throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setProduct(productService.getProductForCode("PRODUCT01"));
		parameter.setCart(cart);
		final BundleTemplateModel productComponent = bundleTemplateService.getBundleTemplateForCode("ProductComponent1");
		parameter.setBundleTemplate(productComponent);
		parameter.setQuantity(1);

		final CommerceCartModification result = commerceCartService.addToCart(parameter);

		assertNotNull(result);
		assertNotNull(result.getEntry());
		assertNotNull(result.getEntry().getBundleNo());
		assertEquals(productComponent, result.getEntry().getBundleTemplate());
	}

	@Test
	public void shouldMergeEntries() throws CommerceCartModificationException
	{
		// GIVEN There is a bundle entry in cart
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setProduct(productService.getProductForCode("PRODUCT01"));
		parameter.setCart(cart);
		final BundleTemplateModel productComponent = bundleTemplateService.getBundleTemplateForCode("ProductComponent1");
		parameter.setBundleTemplate(productComponent);
		parameter.setQuantity(1);

		final CommerceCartModification modification1 = commerceCartService.addToCart(parameter);

		// WHEN We add another instance of the product to the bundle
		parameter.setBundleTemplate(null);
		parameter.setEntryGroupNumbers(modification1.getEntryGroupNumbers());

		final CommerceCartModification modification2 = commerceCartService.addToCart(parameter);

		// THEN Entry merge should occur
		assertEquals(modification1.getEntry(), modification2.getEntry());
		assertEquals(2L, modification2.getEntry().getQuantity().longValue());
	}

	@Test
	public void shouldApplyDiscount() throws CommerceCartModificationException
	{
		// GIVEN A bundle in cart with PRODUCT01
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setProduct(productService.getProductForCode("PRODUCT05"));
		parameter.setCart(cart);
		final BundleTemplateModel productComponent = bundleTemplateService.getBundleTemplateForCode("ProductComponent1");
		parameter.setBundleTemplate(productComponent);
		parameter.setQuantity(1);

		final CommerceCartModification modification1 = commerceCartService.addToCart(parameter);
		assertEquals(650.0, modification1.getEntry().getTotalPrice().doubleValue(), 0.005);

		// WHEN We add a conditional product to the bundle
		final EntryGroup bundleRoot = entryGroupService.getRoot(cart, modification1.getEntryGroupNumbers().iterator().next());
		final EntryGroup premiumComponent1 = entryGroupService.getLeaves(bundleRoot).stream()
				.filter(e -> "PremiumComponent2".equals(e.getExternalReferenceId()))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Bundle does not have premium component"));
		parameter.setBundleTemplate(null);
		parameter.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(premiumComponent1.getGroupNumber())));
		parameter.setProduct(productService.getProductForCode("PREMIUM01"));

		commerceCartService.addToCart(parameter);

		// THEN The first product's price should change due to price rule
		assertEquals(500.0, modification1.getEntry().getTotalPrice().doubleValue(), 0.005);
	}
}
