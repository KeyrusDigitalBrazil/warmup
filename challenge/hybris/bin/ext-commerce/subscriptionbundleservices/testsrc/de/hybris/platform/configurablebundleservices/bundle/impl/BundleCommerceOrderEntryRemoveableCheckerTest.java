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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.order.EntryGroup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


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
	public void autoPickProductShouldNotBeDeleted()
	{
		final BundleSelectionCriteriaModel autopick = new AutoPickBundleSelectionCriteriaModel();
		bundleTemplate.setBundleSelectionCriteria(autopick);
		final CartEntryModel cartEntry = new CartEntryModel();

		Assert.assertFalse(checker.canRemove(cartEntry));
	}
}
