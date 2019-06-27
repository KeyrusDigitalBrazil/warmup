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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Integration test suite for {@link DefaultCartBundleComponentEditableChecker}
 */
@IntegrationTest
public class DefaultCartBundleComponentEditableCheckerNestedIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultCartBundleComponentEditableCheckerNestedIntegrationTest.class);

	public static final String SECOND_COMPONENT_1 = "SecondComponent1";
	public static final String AUTOPICK_COMPONENT = "AutomaticComponent";
	public static final String REQUIRED_COMPONENT = "RequiredComponent";
	public static final String DEPENDENT_COMPONENT = "DependentComponent";
	public static final String ANOTHER_COMPONENT = "AnotherComponent";
	public static final String FOURTH_ROOT_COMPONENT = "FourthPackage";
	public static final String FOURTH_NON_LEAF_COMPONENT = "FourthGroup3";
	public static final String BASIC_COMPONENT = "FourthComponentBasic";
	public static final String FOURTH_COMPONENT_1 = "FourthComponent1";
	public static final String FOURTH_COMPONENT_2 = "FourthComponent2";
	public static final String FOURTH_COMPONENT_3 = "FourthComponent3";
	public static final String FOURTH_COMPONENT_4 = "FourthComponent4";
	public static final String FOURTH_COMPONENT_5 = "FourthComponent5";
	public static final String FOURTH_COMPONENT_6 = "FourthComponent6";
	public static final String FOURTH_COMPONENT_7 = "FourthComponent7";
	public static final String FOURTH_COMPONENT_8 = "FourthComponent8";
	public static final String FOURTH_COMPONENT_9 = "FourthComponent9";
	public static final String FOURTH_COMPONENT_10 = "FourthComponent10";
	public static final String FOURTH_COMPONENT_11 = "FourthComponent11";
	public static final String FOURTH_COMPONENT_12 = "FourthComponent12";
	public static final String FOURTH_COMPONENT_0 = "FourthComponent0";

	public static final String PRODUCT01 = "PRODUCT01";
	public static final String PRODUCT02 = "PRODUCT02";
	public static final String AUTOPICK_PRODUCT = "MANDATORY01";

	public static final String CATALOG_ID = "testCatalog";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Resource
	private AutoPickCartBundleComponentEditableChecker autoPickCartBundleComponentEditableChecker;

	@Resource
	protected BundleTemplateService bundleTemplateService;
	@Resource
	private UserService userService;
	@Resource
	private UnitService unitService;
	@Resource
	private ProductService productService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private BundleCommerceCartService bundleCommerceCartService;

	protected UnitModel unitModel;
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
			importCsv("/subscriptionbundleservices/test/nestedBundleTemplates.impex", "utf-8");
		}
		finally
		{
			Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);
		}

		cart = userService.getUserForUID("bundle").getCarts().iterator().next();
		unitModel = unitService.getUnitForCode("pieces");
	}

	@Test
	public void isEditableIfComponentIsAutoPick() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(SECOND_COMPONENT_1);
		final BundleTemplateModel autopickComponent = bundleTemplateService.getBundleTemplateForCode(AUTOPICK_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, component, false);
		final boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, autopickComponent, 1);

		assertFalse(isEditable);
	}

	@Test
	public void isEditableIfComponentWithNoRequiredComponents() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(SECOND_COMPONENT_1);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, component, false);
		boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);

		assertTrue(isEditable);
	}

	@Test
	public void isNotEditableIfComponentWithRequiredComponentNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(DEPENDENT_COMPONENT);
		final BundleTemplateModel anotherComponent = bundleTemplateService.getBundleTemplateForCode(ANOTHER_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, anotherComponent, false);
		final boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertFalse(isEditable);
	}

	@Test
	public void isEditableIfComponentWithRequiredComponentInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(REQUIRED_COMPONENT);
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(DEPENDENT_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);

		assertTrue(isEditable);
	}

	@Test
	public void isNotEditableIfComponentWithRequiredComponentHasPick1to2CriteriaNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_6);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		final boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertFalse(isEditable);
	}

	@Test
	public void isEditableIfComponentWithRequiredComponentHasPick1to2CriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_5);
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_6);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		final boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);

		assertTrue(isEditable);
	}

	@Test
	public void isNotEditableIfComponentWithRequiredComponentHasPick0to1CriteriaNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_4);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		final boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertFalse(isEditable);
	}

	@Test
	public void isEditableIfComponentWithRequiredComponentHasPick0to1CriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_3);
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_4);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		final boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);

		assertTrue(isEditable);
	}

	@Test
	public void isNotEditableIfComponentWithTransitiveRequiredComponentHasNoPickCriteriaNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_8);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		final boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertFalse(isEditable);
	}

	@Test
	public void isEditableIfComponentWithTransitiveRequiredComponentHasNoPickCriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_8);
		BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_0);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertFalse(isEditable);

		requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_7);
		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, 1, requiredComponent, false);
		isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertTrue(isEditable);
	}

	@Test
	public void isNotEditableIfComponentWithRequiredComponentHasExactly1CriteriaNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_2);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		final boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertFalse(isEditable);
	}

	@Test
	public void isEditableIfComponentWithRequiredComponentHasExactly1CriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_1);
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_2);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		final boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);

		assertTrue(isEditable);
	}

	@Test
	public void isNotEditableIfComponentWithRequiredComponentHasExactly2CriteriaNotEnoughInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_9);
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_10);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertFalse(isEditable);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, 1, requiredComponent, false);
		isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertFalse(isEditable);
	}

	@Test
	public void isEditableIfComponentWithRequiredComponentHasExactly2CriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_9);
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_10);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT02), 1, unitModel, false, 1, requiredComponent, false);
		final boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertTrue(isEditable);
	}

	@Test
	public void isNotEditableForRootComponent() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_ROOT_COMPONENT);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		final boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertEquals(Boolean.FALSE, isEditable);
	}

	@Test
	public void isNotEditableForIntermediateComponent() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_NON_LEAF_COMPONENT);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		final boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertEquals(Boolean.FALSE, isEditable);
	}

	@Test
	public void isEditableIfComponentWithRequiredComponentFromAnotherNodeInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_12);
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_11);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		final boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertTrue(isEditable);
	}

	@Test
	public void isEditableIfComponentInCorrectBundleOnly() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_2);

		BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_1);
		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_3);
		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);

		boolean isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 1);
		assertTrue(isEditable);

		isEditable = autoPickCartBundleComponentEditableChecker.canEdit(cart, component, 2);
		assertFalse(isEditable);
	}

	@Test
	public void criteriaIsFullfilledIfComponentWithNoCriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(REQUIRED_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, component, false);
		final boolean isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);

		assertTrue(isCriteriaMet);
	}

	@Test
	public void  criteriaIsFullfilledIfComponentWithNoCriteriaNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(REQUIRED_COMPONENT);
		final BundleTemplateModel anotherComponent = bundleTemplateService.getBundleTemplateForCode(ANOTHER_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, anotherComponent, false);
		final boolean isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);
		assertTrue(isCriteriaMet);
	}

	@Test
	public void criteriaIsNotFullfilledIfComponentWithPick1to2CriteriaNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_5);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		final boolean isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);
		assertFalse(isCriteriaMet);
	}

	@Test
	public void criteriaIsFullfilledIfComponentWithPick1to2CriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_5);
		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, component, false);
		final boolean isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);
		assertTrue(isCriteriaMet);
	}

	@Test
	public void criteriaIsFullfilledIfComponentWithPick0to1CriteriaNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_3);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		boolean isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);
		assertTrue(isCriteriaMet);
	}

	@Test
	public void criteriaIsFullfilledIfComponentWithPick0to1CriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_3);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, component, false);
		final boolean isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);
		assertTrue(isCriteriaMet);
	}

	@Test
	public void criteriaIsNotFullfilledIfComponentWithPickExactly1CriteriaNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_1);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		boolean isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);
		assertFalse(isCriteriaMet);
	}

	@Test
	public void criteriaIsFullfilledIfComponentWithPickExactly1CriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_1);
		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, component, false);
		boolean isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);
		assertTrue(isCriteriaMet);
	}

	@Test
	public void criteriaIsNotFullfilledIfComponentWithPickExactly2CriteriaNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_9);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		boolean isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);
		assertFalse(isCriteriaMet);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, 1, component, false);
		isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);
		assertFalse(isCriteriaMet);
	}

	@Test
	public void criteriaIsFullfilledIfComponentWithPickExactly2CriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_9);
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_10);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT02), 1, unitModel, false, 1, requiredComponent, false);
		final boolean isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);
		assertTrue(isCriteriaMet);
	}

	@Test
	public void criteriaIsNotFullfilledForRootComponent() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_ROOT_COMPONENT);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		final boolean isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);
		assertEquals(Boolean.FALSE, isCriteriaMet);
	}

	@Test
	public void criteriaIsNotFullfilledForIntermediateComponent() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_NON_LEAF_COMPONENT);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		final boolean isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);
		assertEquals(Boolean.FALSE, isCriteriaMet);
	}

	@Test
	public void criteriaIsNotFullfilledIfComponentInCorrectBundles() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_1);
		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, component, false);
		final BundleTemplateModel anotherComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_3);
		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, anotherComponent, false);

		boolean isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 1);
		assertTrue(isCriteriaMet);

		isCriteriaMet = autoPickCartBundleComponentEditableChecker.isComponentSelectionCriteriaMet(cart, component, 2);
		assertFalse(isCriteriaMet);
	}

	@Test
	public void dependencyIsResolvedIfComponentWithNoRequiredComponents() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(SECOND_COMPONENT_1);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, component, false);
		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);

		assertTrue(isDependencyMet);
	}

	@Test
	public void dependencyIsNotResolvedIfComponentWithRequiredComponentsNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(DEPENDENT_COMPONENT);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertFalse(isDependencyMet);
	}

	@Test
	public void dependencyIsResolvedIfComponentWithRequiredComponentsInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(REQUIRED_COMPONENT);
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(DEPENDENT_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);

		assertTrue(isDependencyMet);
	}

	@Test
	public void dependencyIsNotResolvedIfComponentWithRequiredComponentHasPick1to2CriteriaNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_6);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertFalse(isDependencyMet);
	}

	@Test
	public void dependencyIsResolvedIfComponentWithRequiredComponentHasPick1to2CriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_5);
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_6);
		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertTrue(isDependencyMet);
	}

	@Test
	public void dependencyIsNotResolvedIfComponentWithRequiredComponentHasPick0to1CriteriaNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_4);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertFalse(isDependencyMet);
	}

	@Test
	public void dependencyIsResolvedIfComponentWithRequiredComponentHasPick0to1CriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_3);
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_4);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);

		assertTrue(isDependencyMet);
	}

	@Test
	public void dependencyIsNotResolvedIfComponentWithTransitiveRequiredComponentHasNoPickCriteriaNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_8);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertFalse(isDependencyMet);
	}

	@Test
	public void dependencyIsNotResolvedIfComponentWithTransitiveRequiredComponentHasNoPickCriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_8);
		BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_0);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertFalse(isDependencyMet);

		requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_7);
		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, 1, requiredComponent, false);
		isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertTrue(isDependencyMet);
	}

	@Test
	public void dependencyIsNotResolvedIfComponentWithRequiredComponentHasPickExactly1CriteriaNotInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_2);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertFalse(isDependencyMet);
	}

	@Test
	public void dependencyIsResolvedIfComponentWithRequiredComponentHasPickExactly1CriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_1);
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_2);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);

		assertTrue(isDependencyMet);
	}

	@Test
	public void dependencyIsNotResolvedIfComponentWithRequiredComponentHasPickExactly2CriteriaNotEnoughInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_9);
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_10);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertFalse(isDependencyMet);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, 1, requiredComponent, false);
		isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertFalse(isDependencyMet);
	}

	@Test
	public void dependencyIsResolvedIfComponentWithRequiredComponentHasPickExactly2CriteriaInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_9);
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_10);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertFalse(isDependencyMet);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, 1, requiredComponent, false);
		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT02), 1, unitModel, false, 1, requiredComponent, false);
		isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertTrue(isDependencyMet);
	}

	@Test
	public void dependencyIsNotResolvedForRootComponent() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_ROOT_COMPONENT);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		final boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertEquals(Boolean.FALSE, isDependencyMet);
	}

	@Test
	public void dependencyIsNotResolvedForIntermediateComponent() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_NON_LEAF_COMPONENT);
		final BundleTemplateModel basicComponent = bundleTemplateService.getBundleTemplateForCode(BASIC_COMPONENT);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, basicComponent, false);
		final boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertEquals(Boolean.FALSE, isDependencyMet);
	}

	@Test
	public void dependencyIsResolvedIfComponentWithRequiredComponentFromAnotherNodeInCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_12);
		final BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_11);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		final boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertTrue(isDependencyMet);
	}

	@Test
	public void dependencyIsNotResolvedIfComponentInCorrectBundles() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_2);
		BundleTemplateModel requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_1);

		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);
		requiredComponent = bundleTemplateService.getBundleTemplateForCode(FOURTH_COMPONENT_3);
		bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, false, -1, requiredComponent, false);

		boolean isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 1);
		assertTrue(isDependencyMet);

		isDependencyMet = autoPickCartBundleComponentEditableChecker.isComponentDependencyMet(cart, component, 2);
		assertFalse(isDependencyMet);
	}

	protected CatalogVersionModel getCatalog()
	{
		return catalogVersionService.getCatalogVersion(CATALOG_ID, "Online");
	}

	protected ProductModel getProduct(final String code)
	{
		return productService.getProductForCode(getCatalog(), code);
	}
}