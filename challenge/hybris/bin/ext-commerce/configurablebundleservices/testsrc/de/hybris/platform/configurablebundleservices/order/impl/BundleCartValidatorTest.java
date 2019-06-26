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
package de.hybris.platform.configurablebundleservices.order.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.bundle.AbstractBundleComponentEditableChecker;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.order.BundleCartValidator;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;


@UnitTest
public class BundleCartValidatorTest
{
	@InjectMocks
	private final BundleCartValidator validator = new BundleCartValidator();
	@Mock
	private ModelService modelService;
	@Mock
	private BundleTemplateService bundleTemplateService;
	@Mock
	private AbstractBundleComponentEditableChecker<AbstractOrderModel> bundleComponentEditableChecker;

	protected CartModel cart;
	protected EntryGroup rootGroup;
	protected EntryGroup groupOk;
	protected EntryGroup groupErr;
	protected BundleTemplateModel component1;
	protected BundleTemplateModel component2;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		cart = new CartModel();
		rootGroup = new EntryGroup();
		rootGroup.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		rootGroup.setGroupNumber(Integer.valueOf(1));
		rootGroup.setErroneous(Boolean.FALSE);
		groupOk = new EntryGroup();
		groupOk.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		groupOk.setGroupNumber(Integer.valueOf(200));
		groupOk.setErroneous(Boolean.FALSE);
		groupOk.setExternalReferenceId("COMPONENT1");
		final CartEntryModel entry1 = new CartEntryModel();
		entry1.setOrder(cart);
		entry1.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(200))));
		entry1.setEntryNumber(Integer.valueOf(1));
		entry1.setQuantity(Long.valueOf(2));
		component1 = new BundleTemplateModel();
		component1.setId("COMPONENT1");
		final PickNToMBundleSelectionCriteriaModel criteria1 = new PickNToMBundleSelectionCriteriaModel();
		component1.setBundleSelectionCriteria(criteria1);
		criteria1.setN(Integer.valueOf(1));
		criteria1.setM(Integer.valueOf(10));

		groupErr = new EntryGroup();
		groupErr.setGroupType(GroupType.CONFIGURABLEBUNDLE);
		groupErr.setGroupNumber(Integer.valueOf(500));
		groupErr.setErroneous(Boolean.FALSE);
		groupErr.setExternalReferenceId("COMPONENT2");
		component2 = new BundleTemplateModel();
		component2.setId("COMPONENT2");
		final PickExactlyNBundleSelectionCriteriaModel criteria2 = new PickExactlyNBundleSelectionCriteriaModel();
		component2.setBundleSelectionCriteria(criteria2);
		criteria2.setN(Integer.valueOf(2));
		final CartEntryModel entry2 = new CartEntryModel();
		entry2.setOrder(cart);
		entry2.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(500))));
		entry2.setEntryNumber(Integer.valueOf(1));
		entry2.setQuantity(Long.valueOf(1));
		cart.setEntries(Arrays.asList(entry1, entry2));
		final CartEntryModel entry3 = new CartEntryModel();
		entry3.setOrder(cart);
		entry3.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(500))));
		entry3.setEntryNumber(Integer.valueOf(1));
		entry3.setQuantity(Long.valueOf(2));
		cart.setEntries(Arrays.asList(entry1, entry2, entry3));
		given(bundleTemplateService.getBundleTemplateForCode("COMPONENT1")).willReturn(component1);
		given(bundleTemplateService.getBundleTemplateForCode("COMPONENT2")).willReturn(component2);
		given(bundleComponentEditableChecker.isRequiredDependencyMet(any(), any(), any())).willReturn(true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCheckTheListIsNotNull()
	{
		validator.updateErroneousGroups(null, new CartModel());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCheckTheCartIsNotNull()
	{
		validator.updateErroneousGroups(Collections.singletonList(new EntryGroup()), null);
	}

	@Test
	public void shouldWorkWithEmptyGroupList()
	{
		validator.updateErroneousGroups(Collections.emptyList(), new CartModel());
	}

	@Test
	public void shouldCheckSelectionCriteria()
	{
		validator.updateErroneousGroups(Arrays.asList(rootGroup, groupErr, groupOk), cart);

		assertFalse(groupOk.getErroneous().booleanValue());
		assertTrue(groupErr.getErroneous().booleanValue());
	}

	@Test
	public void requiredShouldInvalidateIfEntryAdded()
	{
		final BundleTemplateModel requiredComponent = new BundleTemplateModel();
		requiredComponent.setId("COMPONENT3");
		component1.setRequiredBundleTemplates(Collections.singletonList(requiredComponent));
		given(bundleComponentEditableChecker.isRequiredDependencyMet(any(), any(), any())).willReturn(false);

		validator.updateErroneousGroups(Arrays.asList(rootGroup, groupErr, groupOk), cart);

		assertTrue(groupOk.getErroneous().booleanValue());
	}

	@Test
	public void requiredShouldNotInvalidateEntryNotAdded()
	{
		final BundleTemplateModel requiredComponent = new BundleTemplateModel();
		requiredComponent.setId("COMPONENT3");
		component1.setRequiredBundleTemplates(Collections.singletonList(requiredComponent));
		given(bundleComponentEditableChecker.isRequiredDependencyMet(any(), any(), any())).willReturn(false);
		cart.setEntries(Collections.emptyList());

		validator.updateErroneousGroups(Arrays.asList(rootGroup, groupErr, groupOk), cart);

		assertFalse(groupOk.getErroneous().booleanValue());
	}

	@Test
	public void shouldIgnoreNonBundleGroups()
	{
		final BundleTemplateModel requiredComponent = new BundleTemplateModel();
		requiredComponent.setId("COMPONENT3");
		component1.setRequiredBundleTemplates(Collections.singletonList(requiredComponent));
		final EntryGroup groupReq = new EntryGroup();
		groupReq.setGroupNumber(Integer.valueOf(501));
		groupReq.setExternalReferenceId("COMPONENT2");
		groupReq.setGroupType(GroupType.STANDALONE);
		groupReq.setErroneous(Boolean.FALSE);
		given(bundleTemplateService.getBundleTemplateForCode("COMPONENT2")).willReturn(component2);

		validator.updateErroneousGroups(Arrays.asList(rootGroup, groupErr, groupOk, groupReq), cart);

		assertFalse(groupReq.getErroneous().booleanValue());
	}

	@Test
	public void shouldReturnTrueIfErroneousAnyFlagHasChanged()
	{
		final boolean result = validator.updateErroneousGroups(Arrays.asList(rootGroup, groupErr, groupOk), cart);

		assertTrue("The changes in the groups will not be saved!", result);
	}

	@Test
	public void shouldReturnFalseIfErroneousFlagsHaveNotChanged()
	{
		groupErr.setErroneous(Boolean.TRUE);
		final boolean result = validator.updateErroneousGroups(Arrays.asList(rootGroup, groupErr, groupOk), cart);

		assertFalse("Performance issue: no changes, but the cart is forced for save", result);
	}
}
