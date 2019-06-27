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
package de.hybris.platform.configurablebundleservices.order.hook;


import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.bundle.AbstractBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.order.BundleCartValidator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class BundleUpdateCartEntryHookTest
{
	@InjectMocks
	private final BundleUpdateCartEntryHook hook = new BundleUpdateCartEntryHook();
	@Mock
	private BundleCartValidator bundleCartValidator;
	@Mock
	private BundleTemplateService bundleTemplateService;
	@Mock
	private ModelService modelService;
	@Mock
	private EntryGroupService entryGroupService;
	@Mock
	private BundleCartHookHelper bundleCartHookHelper;
	@Mock
	private AbstractBundleComponentEditableChecker<CartModel> bundleComponentEditableChecker;
	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldSkipNonBundleBefore()
	{
		final CommerceCartParameter parameter = createParameter();

		getHook().beforeUpdateCartEntry(parameter);

		verify(bundleTemplateService, never()).getBundleTemplateForCode(any(String.class));
	}

	@Test
	public void shouldFailIfComponentDoesNotExist()
	{
		final CommerceCartParameter parameter = createParameter();
		final EntryGroup group = new EntryGroup();
		group.setExternalReferenceId("TEST");
		given(bundleTemplateService.getBundleTemplateForCode(any())).willThrow(new ModelNotFoundException(""));
		given(bundleTemplateService.getBundleEntryGroup(any())).willReturn(group);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Bundle template TEST was not found");

		getHook().beforeUpdateCartEntry(parameter);
	}

	@Test
	public void shouldLimitQuantityAccordingToNSelectionCriteria()
	{
		final CommerceCartParameter parameter = createParameter();
		parameter.setQuantity(12L);
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(2));
		group.setExternalReferenceId("TEST");
		given(bundleTemplateService.getBundleEntryGroup(any())).willReturn(group);
		final BundleTemplateModel component = new BundleTemplateModel();
		final PickExactlyNBundleSelectionCriteriaModel selectionCriteria = new PickExactlyNBundleSelectionCriteriaModel();
		selectionCriteria.setN(Integer.valueOf(10));
		component.setBundleSelectionCriteria(selectionCriteria);
		given(bundleTemplateService.getBundleTemplateForCode("TEST")).willReturn(component);
		given(bundleComponentEditableChecker.isRequiredDependencyMet(any(), eq(component), eq(2))).willReturn(true);

		getHook().beforeUpdateCartEntry(parameter);

		assertEquals(10, parameter.getQuantity());
	}

	@Test
	public void shouldLimitQuantityAccordingToNtoMSelectionCriteria()
	{
		final CommerceCartParameter parameter = createParameter();
		parameter.setQuantity(12L);
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(2));
		group.setExternalReferenceId("TEST");
		given(bundleTemplateService.getBundleEntryGroup(any())).willReturn(group);
		final BundleTemplateModel component = new BundleTemplateModel();
		final PickNToMBundleSelectionCriteriaModel selectionCriteria = new PickNToMBundleSelectionCriteriaModel();
		selectionCriteria.setN(Integer.valueOf(0));
		selectionCriteria.setM(Integer.valueOf(10));
		component.setBundleSelectionCriteria(selectionCriteria);
		given(bundleTemplateService.getBundleTemplateForCode("TEST")).willReturn(component);
		given(bundleComponentEditableChecker.isRequiredDependencyMet(any(), eq(component), eq(2))).willReturn(true);

		getHook().beforeUpdateCartEntry(parameter);

		assertEquals(10, parameter.getQuantity());
	}

	@Test
	public void shouldSkipNonBundleAfter()
	{
		final CommerceCartParameter parameter = createParameter();

		getHook().afterUpdateCartEntry(parameter, null);

		verify(entryGroupService, never()).getRoot(any(), any());
	}

	@Test
	public void shouldReValidateTheWholeBundle()
	{
		final CommerceCartParameter parameter = createParameter();
		parameter.getCart().setCalculated(Boolean.TRUE);
		parameter.getCart().getEntries().get(0).setCalculated(Boolean.TRUE);
		parameter.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(1))));
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(1));
		final EntryGroup root = new EntryGroup();
		root.setGroupNumber(Integer.valueOf(2));
		group.setExternalReferenceId("TEST");
		given(bundleTemplateService.getBundleEntryGroup(any())).willReturn(group);
		final BundleTemplateModel component = new BundleTemplateModel();
		given(bundleTemplateService.getBundleTemplateForCode("TEST")).willReturn(component);
		given(entryGroupService.getRoot(any(), any())).willReturn(root);
		given(bundleTemplateService.getBundleEntryGroup(parameter.getCart(), parameter.getEntryGroupNumbers())).willReturn(group);
		given(entryGroupService.getGroup(parameter.getCart(), Integer.valueOf(1))).willReturn(group);
		given(entryGroupService.getNestedGroups(root)).willReturn(Arrays.asList(root, group));
		given(commerceCartCalculationStrategy.calculateCart(any(CommerceCartParameter.class))).willReturn(Boolean.TRUE);

		getHook().afterUpdateCartEntry(parameter, null);

		verify(bundleCartValidator).updateErroneousGroups(any(), any());
		verify(bundleCartHookHelper).invalidateBundleEntries(any(), any());
	}

	protected CommerceCartParameter createParameter()
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(new CartModel());
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setEntryNumber(1);
		entry.setProduct(new ProductModel());
		entry.getProduct().setCode("PRODUCT");
		entry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(1)));
		entry.setOrder(parameter.getCart());
		parameter.getCart().setEntries(Collections.singletonList(entry));
		parameter.setEntryNumber(1);
		parameter.setQuantity(1L);
		return parameter;
	}

	protected BundleUpdateCartEntryHook getHook()
	{
		return hook;
	}
}
