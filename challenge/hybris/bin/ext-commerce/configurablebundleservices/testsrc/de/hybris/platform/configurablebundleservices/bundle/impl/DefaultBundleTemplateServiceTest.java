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

package de.hybris.platform.configurablebundleservices.bundle.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.daos.impl.DefaultBundleTemplateDao;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link BundleTemplateService}
 */
@UnitTest
public class DefaultBundleTemplateServiceTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DefaultBundleTemplateService bundleTemplateService;
	private BundleTemplateModel bundleTemplateModelRoot;
	private BundleTemplateModel bundleTemplateModelChild1;
	private BundleTemplateModel bundleTemplateModelChild2;
	private DefaultBundleTemplateDao bundleTemplateDao;

	@Mock
	private EntryGroupService entryGroupService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		bundleTemplateService = new DefaultBundleTemplateService();
		bundleTemplateDao = new DefaultBundleTemplateDao(); // for exceptions throwing
		bundleTemplateService.setBundleTemplateDao(bundleTemplateDao);
		bundleTemplateService.setEntryGroupService(entryGroupService);

		bundleTemplateModelRoot = mock(BundleTemplateModel.class);
		bundleTemplateModelChild1 = mock(BundleTemplateModel.class);
		bundleTemplateModelChild2 = mock(BundleTemplateModel.class);

		given(bundleTemplateModelRoot.getParentTemplate()).willReturn(null);
		given(bundleTemplateModelChild1.getParentTemplate()).willReturn(bundleTemplateModelRoot);
		given(bundleTemplateModelChild2.getParentTemplate()).willReturn(bundleTemplateModelChild1);
	}

	@Test
	public void testGetRootBundleTemplate()
	{
		BundleTemplateModel bundleTemplate = bundleTemplateService.getRootBundleTemplate(bundleTemplateModelRoot);
		Assert.assertEquals(bundleTemplateModelRoot, bundleTemplate);

		bundleTemplate = bundleTemplateService.getRootBundleTemplate(bundleTemplateModelChild1);
		Assert.assertEquals(bundleTemplateModelRoot, bundleTemplate);

		bundleTemplate = bundleTemplateService.getRootBundleTemplate(bundleTemplateModelChild2);
		Assert.assertEquals(bundleTemplateModelRoot, bundleTemplate);
	}

	@Test
	public void testGetBundleTemplatesByProductWhenNoProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("productModel can not be null");

		bundleTemplateService.getBundleTemplatesByProduct(null);
	}

	@Test
	public void testGetSubsequentBundleTemplateWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		bundleTemplateService.getSubsequentBundleTemplate(null);
	}

	@Test
	public void testGetPreviousBundleTemplateWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		bundleTemplateService.getPreviousBundleTemplate(null);
	}

	@Test
	public void testGetRelativeBundleTemplateWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		bundleTemplateService.getRelativeBundleTemplate(null, 0);
	}

	@Test
	public void testGetRootBundleTemplateWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Bundle template cannot be null");

		bundleTemplateService.getRootBundleTemplate(null);
	}

	@Test
	public void testGetTemplatesForMasterOrderAndBundleNoWhenNoOrder()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterAbstractOrder can not be null");

		bundleTemplateService.getTemplatesForMasterOrderAndBundleNo(null, 1);
	}

	@Test
	public void testContainsComponenentProductsOfTypeWhenNoTemplate()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bundleTemplate can not be null");

		bundleTemplateService.containsComponenentProductsOfType(null, ProductModel.class);
	}

	@Test
	public void testContainsComponenentProductsOfTypeWhenNoClazz()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("clazz can not be null");

		bundleTemplateService.containsComponenentProductsOfType(new BundleTemplateModel(), (Class<? extends ProductModel>[]) null);
	}

	@Test
	public void testGetFirstComponentOfTypeWhenNoClazz()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("clazzes can not be null");

		bundleTemplateService.getAllComponentsOfType(new BundleTemplateModel(), (Class<? extends ProductModel>[]) null);
	}

	@Test
	public void testGetBundleEntryGroupMoreBundles()
	{
		final CartModel cart = new CartModel();
		cart.setCode("cart1");
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEntryGroupNumbers(Stream.of(1, 2, 3).collect(Collectors.toCollection(HashSet::new)));
		parameter.setBundleTemplate(null);
		parameter.setCart(cart);

		final EntryGroup bundleGroup1 = new EntryGroup();
		bundleGroup1.setGroupNumber(1);
		bundleGroup1.setExternalReferenceId("1");
		bundleGroup1.setGroupType(GroupType.CONFIGURABLEBUNDLE);

		final EntryGroup bundleGroup2 = new EntryGroup();
		bundleGroup2.setGroupNumber(2);
		bundleGroup2.setExternalReferenceId("2");
		bundleGroup2.setGroupType(GroupType.CONFIGURABLEBUNDLE);

		final EntryGroup standaloneGroup = new EntryGroup();
		standaloneGroup.setGroupNumber(3);
		standaloneGroup.setExternalReferenceId("3");
		standaloneGroup.setGroupType(GroupType.STANDALONE);
		
		given(entryGroupService.getGroupOfType(cart, parameter.getEntryGroupNumbers(), GroupType.CONFIGURABLEBUNDLE)).willThrow(
				AmbiguousIdentifierException.class);

		given(entryGroupService.getGroup(cart, Integer.valueOf(1))).willReturn(bundleGroup1);
		given(entryGroupService.getGroup(cart, Integer.valueOf(2))).willReturn(bundleGroup2);
		given(entryGroupService.getGroup(cart, Integer.valueOf(3))).willReturn(standaloneGroup);

		thrown.expect(AmbiguousIdentifierException.class);

		bundleTemplateService.getBundleEntryGroup(cart, parameter.getEntryGroupNumbers());
	}

	@Test
	public void testGetBundleEntryGroup()
	{
		final CartModel cart = new CartModel();
		cart.setCode("cart1");
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEntryGroupNumbers(
				Stream.of(Integer.valueOf(1), Integer.valueOf(3)).collect(Collectors.toCollection(HashSet::new)));
		parameter.setBundleTemplate(null);
		parameter.setCart(cart);

		final EntryGroup bundleGroup1 = new EntryGroup();
		bundleGroup1.setGroupNumber(Integer.valueOf(1));
		bundleGroup1.setExternalReferenceId("1");
		bundleGroup1.setGroupType(GroupType.CONFIGURABLEBUNDLE);

		final EntryGroup standaloneGroup = new EntryGroup();
		standaloneGroup.setGroupNumber(Integer.valueOf(3));
		standaloneGroup.setExternalReferenceId("3");
		standaloneGroup.setGroupType(GroupType.STANDALONE);

		given(entryGroupService.getGroupOfType(cart, parameter.getEntryGroupNumbers(), GroupType.CONFIGURABLEBUNDLE)).willReturn(bundleGroup1);

		final EntryGroup group = bundleTemplateService.getBundleEntryGroup(cart, parameter.getEntryGroupNumbers());
		assertEquals(Integer.valueOf(1), group.getGroupNumber());
	}

	@Test
	public void testGetPositionInParentForRoot()
	{
		final int position = bundleTemplateService.getPositionInParent(bundleTemplateModelRoot);
		
		assertEquals(-1, position);
	}

	@Test
	public void testGetBundleEntryGroupNoGroupsForEntry()
	{
		final CartEntryModel entry = new CartEntryModel();
		entry.setEntryGroupNumbers(Collections.emptySet());
		
		final EntryGroup group = bundleTemplateService.getBundleEntryGroup(entry);
		assertNull(group);
	}

	@Test
	public void testGetBundleEntryGroupNoGroupsInCart()
	{
		final String cartCode = "cart1";
		final CartModel cart = new CartModel();
		cart.setCode(cartCode);
		cart.setEntryGroups(Collections.emptyList());
		
		final CartEntryModel entry = new CartEntryModel();
		entry.setEntryGroupNumbers(Stream.of(Integer.valueOf(1)).collect(Collectors.toCollection(HashSet::new)));
		entry.setOrder(cart);
		
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("The order " + cartCode + " does not contain any groups.");

		final EntryGroup group = bundleTemplateService.getBundleEntryGroup(entry);
		assertNull(group);
	}
}
