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
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.RemoveEntryGroupParameter;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.subscriptionservices.subscription.impl.DefaultSubscriptionCommerceCartService;
import de.hybris.platform.util.Config;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Resource;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


/**
 * Integration test suite for {@link DefaultBundleCommerceCartService}. This test class inherits from
 * {@link DefaultSubscriptionCommerceCartService} in order to run the super class' test cases against the new
 * DefaultBundleCommerceCartService to guarantee compatibility
 */
@IntegrationTest
public class DefaultBundleCommerceCartServiceNestedIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultBundleCommerceCartServiceNestedIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

    private static final String PRODUCT01 = "PRODUCT01";
    private static final String PRODUCT02 = "PRODUCT02";
    private static final String PRODUCT03 = "PRODUCT03";
    private static final String PREMIUM01 = "PREMIUM01";
    private static final String PREMIUM02 = "PREMIUM02";
    private static final String PREMIUM03 = "PREMIUM03";
    private static final String NO_STOCK_PRODUCT_MODEL_CODE = "NO_STOCK_PRODUCT";
    private static final String REGULAR_COMPONENT = "ProductComponent1";
    private static final String OPTIONAL_COMPONENT = "OptionalComponent";
    private static final String PREMIUM_COMPONENT = "PremiumComponent2";
    public static final String PRODUCT_AUTO = "MANDATORY01";
    public static final String SECOND_COMPONENT1 = "SecondComponent1";
    public static final String AUTOPICK_COMPONENT = "AutomaticComponent";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    protected CartModel cart;
	protected UnitModel unitModel;

	@Resource
	protected UserService userService;
	@Resource
	private UnitService unitService;
	@Resource
	protected BundleTemplateService bundleTemplateService;
	@Resource
	protected BundleCommerceCartService bundleCommerceCartService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	protected ProductService productService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private CartService cartService;
	@Resource
	private EntryGroupService entryGroupService;

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

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
	}

	@Test
	public void shouldNotDeleteItemsWhichDontSatisfyMinCondition() throws CommerceCartModificationException, CalculationException
	{
		addToCart(PRODUCT01, REGULAR_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);
		assertEquals(1, cart.getEntries().size());
		CartEntryModel abstractOrderEntryModel = (CartEntryModel)cart.getEntries().iterator().next();
		assertFalse(bundleCommerceCartService.checkIsEntryRemovable(abstractOrderEntryModel));
	}

	@Test
	public void populateBundleBoxEntryForNotRemovableProducts() throws CommerceCartModificationException, CalculationException
	{
		addToCart(PRODUCT01, REGULAR_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);
		assertEquals(1, cart.getEntries().size());
		CartEntryModel abstractOrderEntryModel = (CartEntryModel)cart.getEntries().iterator().next();
		String reason = bundleCommerceCartService.checkAndGetReasonForNotRemovableEntry(abstractOrderEntryModel);
		assertEquals("Product 'null' cannot be removed as a minimum of 3 item(s) needs to be selected for component 'ProductComponent1'.",
                reason);
	}

	@Test
	public void shouldNotUpdateExistingBundles() throws CommerceCartModificationException, CalculationException
	{
		addToCart(PRODUCT01, REGULAR_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);
		assertEquals(1, cart.getEntries().size());
		CartEntryModel abstractOrderEntryModel = (CartEntryModel)cart.getEntries().iterator().next();
		assertFalse(bundleCommerceCartService.checkIsEntryUpdateable(abstractOrderEntryModel));
	}

	@Test
	public void shouldUpdateRegularProduct() throws CommerceCartModificationException, CalculationException
	{
		addToCart(PRODUCT01);
		assertEquals(1, cart.getEntries().size());
		CartEntryModel abstractOrderEntryModel = (CartEntryModel)cart.getEntries().iterator().next();
		assertTrue(bundleCommerceCartService.checkIsEntryUpdateable(abstractOrderEntryModel));
	}


	@Test
	public void testAddToCart2BundleProducts() throws CommerceCartModificationException
	{
        final ProductModel product01 = getProduct(PRODUCT01);
        final ProductModel product02 = getProduct(PRODUCT02);
        final ProductModel premium01 = getProduct(PREMIUM01);
        final BundleTemplateModel regularComponent = getBundleTemplate(REGULAR_COMPONENT);
        final BundleTemplateModel premiumComponent = getBundleTemplate(PREMIUM_COMPONENT);
		final List<CommerceCartModification> results = bundleCommerceCartService.addToCart(cart, unitModel,
				ConfigurableBundleServicesConstants.NEW_BUNDLE,
				product01, regularComponent, premium01,
                premiumComponent, "<no xml>", "<no xml>");

		assertEquals(2, cart.getEntries().size());
		assertEquals(2, results.size());
		assertEquals(2, cart.getLastModifiedEntries().size());


		assertNotNull(results.get(0).getEntry());
		assertEquals(Integer.valueOf(1), results.get(0).getEntry().getBundleNo());
		assertEquals(1, results.get(0).getEntry().getQuantity().longValue());
		assertEquals(CommerceCartModificationStatus.SUCCESS, results.get(0).getStatusCode());
		Assert.assertTrue(cart.getLastModifiedEntries().contains(results.get(0).getEntry()));

		assertEquals(PRODUCT01, results.get(0).getEntry().getProduct().getCode());
		assertEquals(regularComponent, results.get(0).getEntry().getBundleTemplate());

		assertNotNull(results.get(1).getEntry());
		assertEquals(Integer.valueOf(1), results.get(1).getEntry().getBundleNo());
		assertEquals(1, results.get(1).getEntry().getQuantity().longValue());
		assertEquals(CommerceCartModificationStatus.SUCCESS, results.get(1).getStatusCode());
		Assert.assertTrue(cart.getLastModifiedEntries().contains(results.get(1).getEntry()));

		assertEquals(PREMIUM01, results.get(1).getEntry().getProduct().getCode());
		assertEquals(premiumComponent, results.get(1).getEntry().getBundleTemplate());

		final List<CommerceCartModification> results2 = bundleCommerceCartService.addToCart(cart, unitModel,
				ConfigurableBundleServicesConstants.NEW_BUNDLE,
				product02, regularComponent, premium01, premiumComponent,
				"<no xml>", "<no xml>");

		assertEquals(4, cart.getEntries().size());
		assertEquals(2, results2.size());
		assertEquals(2, cart.getLastModifiedEntries().size());


		assertNotNull(results2.get(0).getEntry());
		assertEquals(Integer.valueOf(2), results2.get(0).getEntry().getBundleNo());
		assertEquals(1, results2.get(0).getEntry().getQuantity().longValue());
		assertEquals(CommerceCartModificationStatus.SUCCESS, results2.get(0).getStatusCode());
		Assert.assertTrue(cart.getLastModifiedEntries().contains(results2.get(0).getEntry()));

		assertEquals(PRODUCT02, results2.get(0).getEntry().getProduct().getCode());
		assertEquals(regularComponent, results2.get(0).getEntry().getBundleTemplate());

		assertNotNull(results2.get(1).getEntry());
		assertEquals(Integer.valueOf(2), results2.get(1).getEntry().getBundleNo());
		assertEquals(1, results2.get(1).getEntry().getQuantity().longValue());
		assertEquals(CommerceCartModificationStatus.SUCCESS, results2.get(1).getStatusCode());
		Assert.assertTrue(cart.getLastModifiedEntries().contains(results2.get(1).getEntry()));

		assertEquals(PREMIUM01, results2.get(1).getEntry().getProduct().getCode());
		assertEquals(premiumComponent, results2.get(1).getEntry().getBundleTemplate());
	}

	@Test
	public void testAddToCart2BundleProductsNoStock() throws CommerceCartModificationException
	{
        final ProductModel product = getProduct(NO_STOCK_PRODUCT_MODEL_CODE);
        final ProductModel premium01 = getProduct(PREMIUM01);
        final BundleTemplateModel regularComponent = getBundleTemplate(REGULAR_COMPONENT);
        final BundleTemplateModel premiumComponent = getBundleTemplate(PREMIUM_COMPONENT);
		final List<CommerceCartModification> results = bundleCommerceCartService.addToCart(cart, unitModel,
				ConfigurableBundleServicesConstants.NEW_BUNDLE,
				product, regularComponent, premium01, premiumComponent,
				"<no xml>", "<no xml>");

		assertEquals(1, cart.getEntries().size());
		assertEquals(2, results.size());

		assertEquals(product, results.get(0).getEntry().getProduct());
		assertEquals(CommerceCartModificationStatus.NO_STOCK, results.get(0).getStatusCode());

		assertEquals(premium01, results.get(1).getEntry().getProduct());
		assertEquals(premiumComponent, results.get(1).getEntry().getBundleTemplate());
		assertEquals(Integer.valueOf(1), results.get(1).getEntry().getBundleNo());
		assertEquals(1, results.get(1).getEntry().getQuantity().longValue());
		assertEquals(CommerceCartModificationStatus.SUCCESS, results.get(1).getStatusCode());


		final List<CommerceCartModification> results2 =  bundleCommerceCartService.addToCart(cart, unitModel,
				ConfigurableBundleServicesConstants.NEW_BUNDLE,
				premium01, premiumComponent, product, regularComponent,
				"<no xml>", "<no xml>");

		assertEquals(2, cart.getEntries().size());
		assertEquals(2, results2.size());

		assertEquals(product, results2.get(1).getEntry().getProduct());
		assertEquals(CommerceCartModificationStatus.NO_STOCK, results2.get(1).getStatusCode());
		assertEquals(0, results2.get(1).getQuantityAdded());

		assertEquals(premium01, results2.get(0).getEntry().getProduct());
		assertEquals(premiumComponent, results2.get(0).getEntry().getBundleTemplate());
		assertEquals(Integer.valueOf(2), results2.get(0).getEntry().getBundleNo());
		assertEquals(1, results2.get(0).getEntry().getQuantity().longValue());
		assertEquals(CommerceCartModificationStatus.SUCCESS, results2.get(0).getStatusCode());

		final List<CommerceCartModification> results3 = bundleCommerceCartService.addToCart(cart, unitModel,
				ConfigurableBundleServicesConstants.NEW_BUNDLE,
				product, regularComponent, product, regularComponent, "<no xml>",
				"<no xml>");

		assertEquals(2, cart.getEntries().size());
		assertEquals(2, results3.size());


		assertEquals(0, results3.get(0).getQuantityAdded());
		assertEquals(CommerceCartModificationStatus.NO_STOCK, results3.get(0).getStatusCode());

		assertEquals(0, results3.get(1).getQuantityAdded());
		assertEquals(CommerceCartModificationStatus.NO_STOCK, results3.get(1).getStatusCode());
	}

	@Test
	public void testAddToCart2ProductsAutoPick() throws CommerceCartModificationException
	{
        final ProductModel product01 = getProduct(PRODUCT01);
        final ProductModel product02 = getProduct(PRODUCT02);
        final ProductModel premium01 = getProduct(PREMIUM01);
        final BundleTemplateModel regularComponent = getBundleTemplate(REGULAR_COMPONENT);
        final BundleTemplateModel premiumComponent = getBundleTemplate(PREMIUM_COMPONENT);
		List<CommerceCartModification> modifications = bundleCommerceCartService.addToCart(cart, unitModel,
				ConfigurableBundleServicesConstants.NEW_BUNDLE,
				product01, regularComponent, premium01,
				premiumComponent, "<no xml>", "<no xml>");

		assertEquals(2, modifications.size());
		assertEquals(2, cart.getEntries().size());

		final CartEntryModel prodEntry = cartService.getEntriesForProduct(cart, product01).iterator()
				.next();
		assertNotNull(prodEntry);

		final CartEntryModel planEntry = cartService.getEntriesForProduct(cart, premium01)
				.iterator().next();
		assertNotNull(planEntry);

		assertEquals(2, cart.getLastModifiedEntries().size());
		Assert.assertTrue(cart.getLastModifiedEntries().contains(prodEntry));
		Assert.assertTrue(cart.getLastModifiedEntries().contains(planEntry));

		assertEquals(unitModel, prodEntry.getUnit());
		assertEquals(1, prodEntry.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), prodEntry.getBundleNo());
		assertEquals(regularComponent, prodEntry.getBundleTemplate());

		assertEquals(unitModel, planEntry.getUnit());
		assertEquals(1, planEntry.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), planEntry.getBundleNo());
		assertEquals(premiumComponent, planEntry.getBundleTemplate());

		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(cart, product02, 1, unitModel, false, 1,
				regularComponent, false, "<no xml>");

		assertEquals(3, cart.getEntries().size());
		assertEquals(1, modifications.size());
		assertEquals(1, cart.getLastModifiedEntries().size());
		assertEquals(modifications.iterator().next().getEntry(), cart.getLastModifiedEntries().iterator().next());

		for (final AbstractOrderEntryModel cartEntry : cart.getEntries())
		{
			assertEquals(unitModel, cartEntry.getUnit());
			assertEquals(1, cartEntry.getQuantity().longValue());
			assertEquals(Integer.valueOf(1), cartEntry.getBundleNo());
		}
	}

    @Test
    public void shouldReplaceBundleProductInCart() throws CommerceCartModificationException
    {
        final BundleTemplateModel component = getBundleTemplate(REGULAR_COMPONENT);
        bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, true, ConfigurableBundleServicesConstants.NEW_BUNDLE,
                component, false);
        assertEquals(new Double(600.0), cart.getTotalPrice());
        bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT02), 1, unitModel, true, 1, component, true);
        assertEquals(new Double(650.0), cart.getTotalPrice());
    }

    @Test
    public void shouldRejectReplacementInUnknownBundle() throws CommerceCartModificationException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No entry for bundleNo=1 in cart bundleCart1");
        bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, true, 1, getBundleTemplate(REGULAR_COMPONENT), false);
    }

    @Test
    public void shouldRejectReplacementInNegativeBundle() throws CommerceCartModificationException
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("The bundleNo must not be lower then '-1', given bundleNo: -2");
        bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, true, -2, getBundleTemplate(REGULAR_COMPONENT), false);
    }

    @Test
    public void shouldRejectReplacementWithNullComponent() throws CommerceCartModificationException
    {
        final ProductModel product = getProduct(PRODUCT01);
        bundleCommerceCartService.addToCart(cart, product, 1, unitModel, true, ConfigurableBundleServicesConstants.NEW_BUNDLE,
                getBundleTemplate(REGULAR_COMPONENT), false);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Parameter bundleTemplate can not be null");
        bundleCommerceCartService.addToCart(cart, product, 1, unitModel, true, 1, null, false);
    }

    @Test
    public void shouldRejectReplacementWithNullProduct() throws CommerceCartModificationException
    {
        final BundleTemplateModel component = getBundleTemplate(REGULAR_COMPONENT);
        bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, true, ConfigurableBundleServicesConstants.NEW_BUNDLE,
                component, false);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("product can not be null");
        bundleCommerceCartService.addToCart(cart, null, 1, unitModel, true, 1, component, false);
    }

    @Test
    public void replacingWithAnotherComponentShouldSimplyAdd() throws CommerceCartModificationException
    {
        final BundleTemplateModel firstComponent = getBundleTemplate(REGULAR_COMPONENT);
        final BundleTemplateModel secondComponent = getBundleTemplate(OPTIONAL_COMPONENT);
        final ProductModel product = getProduct(PRODUCT01);
        bundleCommerceCartService.addToCart(cart, product, 1, unitModel, true, ConfigurableBundleServicesConstants.NEW_BUNDLE,
                firstComponent, false);
        assertEquals(new Double(600.0), cart.getTotalPrice());
        bundleCommerceCartService.addToCart(cart, product, 1, unitModel, true, 1, secondComponent, true);
        assertEquals(new Double(1200.0), cart.getTotalPrice());
        assertThat(cart.getEntries(), hasItem(hasProperty(AbstractOrderEntryModel.BUNDLETEMPLATE, is(firstComponent))));
        assertThat(cart.getEntries(), hasItem(hasProperty(AbstractOrderEntryModel.BUNDLETEMPLATE, is(secondComponent))));
    }

    @Test
    public void replacementShouldAddToNewBundle() throws CommerceCartModificationException
    {
        final BundleTemplateModel component = getBundleTemplate(REGULAR_COMPONENT);
        bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, true, ConfigurableBundleServicesConstants.NEW_BUNDLE,
                component, false);
        assertEquals(new Double(600.0), cart.getTotalPrice());
        bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT02), 1, unitModel, true, ConfigurableBundleServicesConstants.NEW_BUNDLE,
                component, true);
        assertEquals(new Double(1250.0), cart.getTotalPrice());
    }

    @Test
    public void testGetTemplatesForMasterOrderAndBundleNo() throws CommerceCartModificationException
    {
        final BundleTemplateModel regularComponent = getBundleTemplate(REGULAR_COMPONENT);
        final BundleTemplateModel premiumComponent = getBundleTemplate(PREMIUM_COMPONENT);
        final List<CommerceCartModification> results = bundleCommerceCartService.addToCart(cart, unitModel, -1,
                getProduct(PRODUCT01), regularComponent, getProduct(PREMIUM01), premiumComponent,
                "<no xml>", "<no xml>");

        assertEquals(2, results.size());
        final AbstractOrderEntryModel entry = results.iterator().next().getEntry();
        assertNotNull(entry);
        final List<BundleTemplateModel> templates = bundleTemplateService.getTemplatesForMasterOrderAndBundleNo(cart,
                entry.getBundleNo());
        assertEquals(2, templates.size());
        Assert.assertTrue(templates.contains(regularComponent));
        Assert.assertTrue(templates.contains(premiumComponent));
    }

	@Test
	public void testFirstInvalidComponentInCartShouldBeRegularComponent() throws CommerceCartModificationException
	{
		List<CommerceCartModification> results = addToCart(PRODUCT01, REGULAR_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);
		assertEquals(1, results.size());
		BundleTemplateModel invalidComponent = bundleCommerceCartService.getFirstInvalidComponentInCart(cart);
		assertEquals(getBundleTemplate(REGULAR_COMPONENT), invalidComponent);
	}

	@Test
	public void testFirstInvalidComponentInCartShouldBePremiumComponent() throws CommerceCartModificationException
	{
		List<CommerceCartModification> results = addToCart(PRODUCT01, REGULAR_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);

		assertEquals(1, results.size());
		final int bundleNo = results.iterator().next().getEntry().getBundleNo();

		results.clear();
		results = addToCart(PRODUCT02, REGULAR_COMPONENT, bundleNo);
		assertEquals(1, results.size());
		results = addToCart(PRODUCT03, REGULAR_COMPONENT, bundleNo);
		results.clear();
		addToCart(PREMIUM01, PREMIUM_COMPONENT, bundleNo);
		BundleTemplateModel invalidComponent = bundleCommerceCartService.getFirstInvalidComponentInCart(cart);
		assertEquals(getBundleTemplate(PREMIUM_COMPONENT), invalidComponent);
	}

	@Test
	public void testGetFirstInvalidComponentInCartShouldBeBull() throws CommerceCartModificationException
	{
		List<CommerceCartModification> results = addToCart(PRODUCT01, REGULAR_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);

		assertEquals(1, results.size());
		final int bundleNo = results.iterator().next().getEntry().getBundleNo();
		results.clear();
		results = addToCart(PRODUCT02, REGULAR_COMPONENT, bundleNo);
		assertEquals(1, results.size());
		results = addToCart(PRODUCT03, REGULAR_COMPONENT, bundleNo);
		results.clear();
		addToCart(PREMIUM01, PREMIUM_COMPONENT, bundleNo);
		addToCart(PREMIUM02, PREMIUM_COMPONENT, bundleNo);
		addToCart(PREMIUM03, PREMIUM_COMPONENT, bundleNo);
		BundleTemplateModel invalidComponent = bundleCommerceCartService.getFirstInvalidComponentInCart(cart);
		assertNull(invalidComponent);
	}

    @Test
    public void shouldDenyRemovalOfAutopickEntry() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, SECOND_COMPONENT1, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        int autopickEntry = cart.getEntries().stream()
                .filter(entry -> AUTOPICK_COMPONENT.equals(entry.getBundleTemplate().getId()))
                .map(AbstractOrderEntryModel::getEntryNumber)
                .findAny()
                .get();
        thrown.expect(CommerceCartModificationException.class);
        thrown.expectMessage("Auto-pick product 'MANDATORY01' cannot be removed from bundle/cart via API call.");
        bundleCommerceCartService.updateQuantityForCartEntry(cart, autopickEntry, 0);
    }

    @Test
    public void shouldRemoveAutopickAlongWithOwner() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, SECOND_COMPONENT1, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        addToCart(PREMIUM01, PREMIUM_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        assertThat(cart.getEntries(), containsInAnyOrder(
                hasProperty(AbstractOrderEntryModel.BUNDLETEMPLATE, hasProperty(BundleTemplateModel.ID, is(SECOND_COMPONENT1))),
                hasProperty(AbstractOrderEntryModel.BUNDLETEMPLATE, hasProperty(BundleTemplateModel.ID, is(PREMIUM_COMPONENT))),
                hasProperty(AbstractOrderEntryModel.BUNDLETEMPLATE, hasProperty(BundleTemplateModel.ID, is(AUTOPICK_COMPONENT)))
        ));
        bundleCommerceCartService.removeAllEntries(cart, 1);
        assertThat(cart.getEntries(), contains(hasProperty(AbstractOrderEntryModel.PRODUCT, hasProperty(ProductModel.CODE, is(PREMIUM01)))));
    }


    @Test
    public void autopickProductCanNotBeAddedExplicitly() throws CommerceCartModificationException
    {
        thrown.expect(CommerceCartModificationException.class);
        thrown.expectMessage("Auto-pick product 'MANDATORY01' cannot be added to bundle/cart via API call.");
        addToCart(PRODUCT_AUTO, "AutomaticComponent", ConfigurableBundleServicesConstants.NEW_BUNDLE);
    }

    @Test
    public void autopickProductIsAddedAutomatically() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, SECOND_COMPONENT1, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        assertThat(cart.getEntries(), hasItem(hasProperty(AbstractOrderEntryModel.PRODUCT, hasProperty(ProductModel.CODE, is(PRODUCT_AUTO)))));
    }

    @Test
    public void autopickProductShouldBeAddedToEveryBundle() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, SECOND_COMPONENT1, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        addToCart(PRODUCT02, SECOND_COMPONENT1, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        assertThat(cart.getEntries(), containsInAnyOrder(
                hasProperty(AbstractOrderEntryModel.PRODUCT, hasProperty(ProductModel.CODE, is(PRODUCT01))),
                hasProperty(AbstractOrderEntryModel.PRODUCT, hasProperty(ProductModel.CODE, is(PRODUCT02))),
                hasProperty(AbstractOrderEntryModel.PRODUCT, hasProperty(ProductModel.CODE, is(PRODUCT_AUTO))),
                hasProperty(AbstractOrderEntryModel.PRODUCT, hasProperty(ProductModel.CODE, is(PRODUCT_AUTO)))
        ));
    }

    @Test
    public void autopickProductShouldBeAddedOnlyOnce() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, SECOND_COMPONENT1, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        addToCart(PRODUCT02, SECOND_COMPONENT1, 1);
        assertThat(cart.getEntries(), containsInAnyOrder(
                hasProperty(AbstractOrderEntryModel.PRODUCT, hasProperty(ProductModel.CODE, is(PRODUCT01))),
                hasProperty(AbstractOrderEntryModel.PRODUCT, hasProperty(ProductModel.CODE, is(PRODUCT02))),
                hasProperty(AbstractOrderEntryModel.PRODUCT, hasProperty(ProductModel.CODE, is(PRODUCT_AUTO)))
        ));
    }

	@Test
	public void shouldDenyRemovalOfAutoPicked() throws CommerceCartModificationException
	{
		addToCart(PRODUCT01, SECOND_COMPONENT1, ConfigurableBundleServicesConstants.NEW_BUNDLE);

		final AbstractOrderEntryModel autopickEntry = cart.getEntries().stream()
				.filter(e -> "MANDATORY01".equals(e.getProduct().getCode()))
				.findAny().get();
		CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(cart);
		parameter.setEnableHooks(true);
		parameter.setEntryNumber(autopickEntry.getEntryNumber().longValue());
		parameter.setQuantity(0L);

		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("Auto-pick product 'MANDATORY01' cannot be removed from bundle/cart via API call.");

		bundleCommerceCartService.updateQuantityForCartEntry(parameter);
	}

	@Test
	public void autopickCanBeRemovedWithTheBundle() throws CommerceCartModificationException
	{
		addToCart(PRODUCT01, SECOND_COMPONENT1, ConfigurableBundleServicesConstants.NEW_BUNDLE);
		final RemoveEntryGroupParameter parameter = new RemoveEntryGroupParameter();
		parameter.setCart(cart);
		parameter.setEntryGroupNumber(
				entryGroupService.getRoot(cart, cart.getEntries().get(0).getEntryGroupNumbers().iterator().next()).getGroupNumber());
		bundleCommerceCartService.removeEntryGroup(parameter);
	}

    protected List<CommerceCartModification> addToCart(final String productCode, final String componentId, final int bundleNo)
			throws CommerceCartModificationException
	{
		return bundleCommerceCartService.addToCart(
				cart, getProduct(productCode), 1, unitModel, false, bundleNo, getBundleTemplate(componentId), false);
    }

	protected void addToCart(final String productCode)
			throws CommerceCartModificationException
	{
		bundleCommerceCartService.addToCart(
				cart, getProduct(productCode), 1, unitModel, false, ConfigurableBundleServicesConstants.NO_BUNDLE, null, false);
	}

	protected ProductModel getProduct(final String code)
	{
		return productService.getProductForCode(getCatalog(), code);
	}

	protected BundleTemplateModel getBundleTemplate(final String templateId)
	{
		final BundleTemplateModel exampleModel = new BundleTemplateModel();
		exampleModel.setId(templateId);
		exampleModel.setCatalogVersion(getCatalog());
		return flexibleSearchService.getModelByExample(exampleModel);
	}

	protected CatalogVersionModel getCatalog()
	{
		return catalogVersionService.getCatalogVersion("testCatalog", "Online");
	}

}
