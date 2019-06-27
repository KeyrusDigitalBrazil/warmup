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

import static de.hybris.platform.core.enums.GroupType.CONFIGURABLEBUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.daos.OrderEntryDao;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link DefaultCartBundleComponentEditableChecker}
 */
@UnitTest
public class DefaultCartBundleComponentEditableCheckerTest
{
	public static final String BUNDLE_TEMPLATE_ID = "bundleTemplateId";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DefaultCartBundleComponentEditableChecker bundleComponentEditableChecker;
	@Mock
	private EntryGroupService entryGroupService;
	@Mock
	private OrderEntryDao cartEntryDao;

	private CartModel masterCartModel;
	private CartEntryModel cartEntryModel;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		bundleComponentEditableChecker = new DefaultCartBundleComponentEditableChecker();
		bundleComponentEditableChecker.setOrderEntryDao(cartEntryDao);
		bundleComponentEditableChecker.setEntryGroupService(entryGroupService);

		cartEntryModel = mock(CartEntryModel.class);
		masterCartModel = mock(CartModel.class);
	}

	@Test
	public void testCanEdit()
	{
		final BundleTemplateModel bundleTemplateDevice = mock(BundleTemplateModel.class);
		final BundleTemplateModel bundleTemplatePlan = mock(BundleTemplateModel.class);
		final BundleTemplateModel bundleTemplateFee = mock(BundleTemplateModel.class);
		final BundleTemplateModel bundleTemplateAddOn = mock(BundleTemplateModel.class);

		// bundleTemplateAddOn has no selection dependency
		boolean isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.TRUE, Boolean.valueOf(isEditable));

		// bundleTemplateAddOn has a selection dependency which is empty
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.TRUE, Boolean.valueOf(isEditable));

		// bundleTemplateAddOn has a selection dependency (to device component) but the dependent component has no cart entries
		final Collection<BundleTemplateModel> requiredTemplates = new HashSet<BundleTemplateModel>();
		requiredTemplates.add(bundleTemplateDevice);
		given(bundleTemplateAddOn.getRequiredBundleTemplates()).willReturn(requiredTemplates);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.FALSE, Boolean.valueOf(isEditable));

		// device component gets pick 1 to 2 selection criteria and 1 cart entry
		final PickNToMBundleSelectionCriteriaModel pickNToMBundleSelectionCriteria = mock(PickNToMBundleSelectionCriteriaModel.class);
		given(bundleTemplateDevice.getBundleSelectionCriteria()).willReturn(pickNToMBundleSelectionCriteria);
		given(pickNToMBundleSelectionCriteria.getN()).willReturn(Integer.valueOf(1));
		given(pickNToMBundleSelectionCriteria.getM()).willReturn(Integer.valueOf(2));
		final List<CartEntryModel> deviceEntries = new ArrayList<CartEntryModel>();
		deviceEntries.add(cartEntryModel);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(masterCartModel, 1, bundleTemplateDevice)).willReturn(
				deviceEntries);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.TRUE, Boolean.valueOf(isEditable));

		// bundleTemplateAddOn gets another selection dependency (to plan component) which has no pick criteria and no cart entries
		requiredTemplates.add(bundleTemplatePlan);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.FALSE, Boolean.valueOf(isEditable));

		// plan component gets a pick exactly 1 criteria but has still no cart entries yet
		final PickExactlyNBundleSelectionCriteriaModel pickExactlyNBundleSelectionCriteria = mock(PickExactlyNBundleSelectionCriteriaModel.class);
		given(bundleTemplatePlan.getBundleSelectionCriteria()).willReturn(pickExactlyNBundleSelectionCriteria);
		given(pickExactlyNBundleSelectionCriteria.getN()).willReturn(Integer.valueOf(1));
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.FALSE, Boolean.valueOf(isEditable));

		// add a product to plan component
		final List<CartEntryModel> planEntries = new ArrayList<CartEntryModel>();
		planEntries.add(cartEntryModel);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(masterCartModel, 1, bundleTemplatePlan)).willReturn(
				planEntries);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.TRUE, Boolean.valueOf(isEditable));

		// bundleTemplateAddOn gets a 3rd selection dependency (to fee component) which has no pick criteria and no cart entries
		requiredTemplates.add(bundleTemplateFee);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.FALSE, Boolean.valueOf(isEditable));

		// fee component gets an cart entry but no selection criteria yet
		final List<CartEntryModel> feeEntries = new ArrayList<CartEntryModel>();
		feeEntries.add(cartEntryModel);
		given(cartEntryDao.findEntriesByMasterCartAndBundleNoAndTemplate(masterCartModel, 1, bundleTemplateFee)).willReturn(
				feeEntries);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.TRUE, Boolean.valueOf(isEditable));

		// add auto-pick products to fee component
		final List<ProductModel> products = new ArrayList<ProductModel>();
		products.add(new ProductModel());
		given(bundleTemplateFee.getProducts()).willReturn(products);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.TRUE, Boolean.valueOf(isEditable));

		// add a 2nd product to plan component (only 1 pick allowed)
		final CartEntryModel cartEntryModel2 = mock(CartEntryModel.class);
		planEntries.add(cartEntryModel2);
		isEditable = bundleComponentEditableChecker.canEdit(masterCartModel, bundleTemplateAddOn, 1);
		assertEquals(Boolean.FALSE, Boolean.valueOf(isEditable));
	}

	@Test
	public void testCheckComponentIsLeafWhenChildTemplatesListIsNull() throws CommerceCartModificationException
	{
		final BundleTemplateModel bundleTemplate = mock(BundleTemplateModel.class);
		given(bundleTemplate.getChildTemplates()).willReturn(null);

		bundleComponentEditableChecker.checkComponentIsLeaf(bundleTemplate);
	}

	@Test
	public void testCheckComponentIsLeafWhenChildTemplatesListIsEmpty() throws CommerceCartModificationException
	{
		final BundleTemplateModel bundleTemplate = mock(BundleTemplateModel.class);
		given(bundleTemplate.getChildTemplates()).willReturn(Collections.emptyList());

		bundleComponentEditableChecker.checkComponentIsLeaf(bundleTemplate);
	}

	@Test
	public void testCheckComponentIsLeafWhenChildTemplatesListIsNotEmpty() throws CommerceCartModificationException
	{
		final BundleTemplateModel bundleTemplate = mock(BundleTemplateModel.class);

		final List<BundleTemplateModel> childList = new ArrayList<>();
		childList.add(new BundleTemplateModel());

		given(bundleTemplate.getChildTemplates()).willReturn(childList);
		given(bundleTemplate.getId()).willReturn(BUNDLE_TEMPLATE_ID);

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("Component '" + BUNDLE_TEMPLATE_ID + "' cannot be modified as it has non-emptpy list of child components");

		bundleComponentEditableChecker.checkComponentIsLeaf(bundleTemplate);
	}

	@Test
	public void testIsRequiredDependencyMetNoRequiredComponents()
	{
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();

		assertTrue(bundleComponentEditableChecker.isRequiredDependencyMet(masterCartModel, bundleTemplate, Integer.valueOf(1)));
	}

	@Test
	public void testIsRequiredDependencyMetAllFilled()
	{
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final BundleTemplateModel requiredTemplate1 = new BundleTemplateModel();
		requiredTemplate1.setId("Template1");
		final BundleTemplateModel requiredTemplate2 = new BundleTemplateModel();
		requiredTemplate2.setId("Template2");
		bundleTemplate.setRequiredBundleTemplates(Arrays.asList(requiredTemplate1, requiredTemplate2));

		final EntryGroup rootGroup = new EntryGroup();
		given(entryGroupService.getRoot(masterCartModel, Integer.valueOf(2))).willReturn(rootGroup);
		final EntryGroup leafGroup1 = new EntryGroup();
		leafGroup1.setGroupType(CONFIGURABLEBUNDLE);
		leafGroup1.setGroupNumber(Integer.valueOf(5));
		leafGroup1.setExternalReferenceId("Template1");
		final EntryGroup leafGroup2 = new EntryGroup();
		leafGroup2.setGroupType(CONFIGURABLEBUNDLE);
		leafGroup2.setGroupNumber(Integer.valueOf(6));
		leafGroup2.setExternalReferenceId("Template2");
		final EntryGroup leafGroup3 = new EntryGroup();
		leafGroup3.setGroupType(CONFIGURABLEBUNDLE);
		leafGroup3.setGroupNumber(Integer.valueOf(7));
		leafGroup3.setExternalReferenceId("Template3");
		given(entryGroupService.getLeaves(rootGroup)).willReturn(Arrays.asList(leafGroup1, leafGroup2, leafGroup3));

		final CartEntryModel entry1 = new CartEntryModel();
		entry1.setEntryGroupNumbers(Sets.newHashSet(Integer.valueOf(5)));
		final CartEntryModel entry2 = new CartEntryModel();
		entry2.setEntryGroupNumbers(Sets.newHashSet(Integer.valueOf(6)));
		final CartEntryModel entry3 = new CartEntryModel();
		entry3.setEntryGroupNumbers(Sets.newHashSet(Integer.valueOf(7)));
		given(masterCartModel.getEntries()).willReturn(Arrays.asList(entry1, entry2, entry3));

		assertTrue(bundleComponentEditableChecker.isRequiredDependencyMet(masterCartModel, bundleTemplate, Integer.valueOf(2)));
	}

	@Test
	public void testIsRequiredDependencyMetNotAllFilled()
	{
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final BundleTemplateModel requiredTemplate1 = new BundleTemplateModel();
		requiredTemplate1.setId("Template1");
		final BundleTemplateModel requiredTemplate2 = new BundleTemplateModel();
		requiredTemplate2.setId("Template2");
		bundleTemplate.setRequiredBundleTemplates(Arrays.asList(requiredTemplate1, requiredTemplate2));

		final EntryGroup rootGroup = new EntryGroup();
		given(entryGroupService.getRoot(masterCartModel, Integer.valueOf(2))).willReturn(rootGroup);
		final EntryGroup leafGroup1 = new EntryGroup();
		leafGroup1.setGroupType(CONFIGURABLEBUNDLE);
		leafGroup1.setGroupNumber(Integer.valueOf(5));
		leafGroup1.setExternalReferenceId("Template1");
		final EntryGroup leafGroup2 = new EntryGroup();
		leafGroup2.setGroupType(CONFIGURABLEBUNDLE);
		leafGroup2.setGroupNumber(Integer.valueOf(6));
		leafGroup2.setExternalReferenceId("Template2");
		final EntryGroup leafGroup3 = new EntryGroup();
		leafGroup3.setGroupType(CONFIGURABLEBUNDLE);
		leafGroup3.setGroupNumber(Integer.valueOf(7));
		leafGroup3.setExternalReferenceId("Template3");
		given(entryGroupService.getLeaves(rootGroup)).willReturn(Arrays.asList(leafGroup1, leafGroup2, leafGroup3));

		final CartEntryModel entry1 = new CartEntryModel();
		entry1.setEntryGroupNumbers(Sets.newHashSet(Integer.valueOf(5)));
		final CartEntryModel entry3 = new CartEntryModel();
		entry3.setEntryGroupNumbers(Sets.newHashSet(Integer.valueOf(7)));
		given(masterCartModel.getEntries()).willReturn(Arrays.asList(entry1, entry3));

		assertFalse(bundleComponentEditableChecker.isRequiredDependencyMet(masterCartModel, bundleTemplate, Integer.valueOf(2)));
	}
}