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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import de.hybris.platform.core.order.EntryGroup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test to see when an order entry can be deleted
 */
@UnitTest
public class BundleCommerceOrderEntryRemoveableCheckerTest
{

	@Mock
	private BundleTemplateService bundleTemplateService;
	@InjectMocks
	private final BundleCommerceOrderEntryRemoveableChecker checker = new BundleCommerceOrderEntryRemoveableChecker();

	private CartModel order;
	private CartEntryModel cartEntry;
	private EntryGroup entryGroup;
	private BundleTemplateModel bundleTemplate;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		order = new CartModel();
		cartEntry = new CartEntryModel();
		cartEntry.setOrder(order);
		cartEntry.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(5))));
		order.setEntries(new ArrayList<>());
		order.getEntries().add(cartEntry);
		bundleTemplate = new BundleTemplateModel();
		entryGroup = new EntryGroup();
		entryGroup.setGroupNumber(Integer.valueOf(5));
		entryGroup.setExternalReferenceId("bundleID");
		when(bundleTemplateService.getBundleTemplateForCode(eq("bundleID"))).thenReturn(bundleTemplate);
		when(bundleTemplateService.getBundleEntryGroup(any(AbstractOrderEntryModel.class))).thenReturn(entryGroup);
	}

	@Test
	public void shouldRemoveNonBundleEntry()
	{
		when(bundleTemplateService.getBundleEntryGroup(any(AbstractOrderEntryModel.class))).thenReturn(null);
		Assert.assertTrue(checker.canRemove(cartEntry));
	}

	@Test
	public void shouldNotDeleteItemsWhichDontSatisfyMinCondition()
	{
		final BundleSelectionCriteriaModel pickExactly6 = new PickExactlyNBundleSelectionCriteriaModel();
		((PickExactlyNBundleSelectionCriteriaModel) pickExactly6).setN(Integer.valueOf(6));
		bundleTemplate.setBundleSelectionCriteria(pickExactly6);
		final CartEntryModel cartEntryFromGroup1 = new CartEntryModel();
		cartEntryFromGroup1.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(5))));
		order.getEntries().add(cartEntryFromGroup1);

		Assert.assertFalse(checker.canRemove(cartEntry));
	}

	@Test
	public void shouldDeleteItemsWhichSatisfyMinCondition()
	{
		final BundleSelectionCriteriaModel pick2to4 = new PickNToMBundleSelectionCriteriaModel();
		((PickNToMBundleSelectionCriteriaModel) pick2to4).setN(Integer.valueOf(2));
		((PickNToMBundleSelectionCriteriaModel) pick2to4).setM(Integer.valueOf(4));
		bundleTemplate.setBundleSelectionCriteria(pick2to4);
		final CartEntryModel cartEntryFromGroup1 = new CartEntryModel();
		cartEntryFromGroup1.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(5))));
		order.getEntries().add(cartEntryFromGroup1);
		final CartEntryModel cartEntryFromGroup2 = new CartEntryModel();
		cartEntryFromGroup2.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(5))));
		order.getEntries().add(cartEntryFromGroup2);

		Assert.assertTrue(checker.canRemove(cartEntry));

	}

	@Test
	public void shouldDeleteItemWithNullSelectionCriteria()
	{
		bundleTemplate.setBundleSelectionCriteria(null);

		Assert.assertTrue(checker.canRemove(cartEntry));
	}

}
