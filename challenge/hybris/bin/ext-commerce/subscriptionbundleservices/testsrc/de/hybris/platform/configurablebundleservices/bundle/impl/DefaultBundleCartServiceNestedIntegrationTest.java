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
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants.NEW_BUNDLE;
import static de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants.NO_BUNDLE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@IntegrationTest
public class DefaultBundleCartServiceNestedIntegrationTest extends ServicelayerTest
{
    private static final Logger LOG = Logger.getLogger(DefaultBundleCartServiceNestedIntegrationTest.class);

    protected static final String REGULAR_COMPONENT = "ProductComponent1";
    protected static final String PREMIUM_COMPONENT = "PremiumComponent2";
    protected static final String PRODUCT01 = "PRODUCT01";
    protected static final String PREMIUM01 = "PREMIUM01";
    protected static final String PRODUCT02 = "PRODUCT02";
    protected static final String PRODUCT05 = "PRODUCT05";
    protected static final String PREMIUM02 = "PREMIUM02";

    @Resource
    protected BundleCommerceCartService bundleCommerceCartService;
    @Resource
    protected UserService userService;
    @Resource
    protected ProductService productService;
    @Resource
    private CatalogVersionService catalogVersionService;
    @Resource
    private UnitService unitService;
    @Resource
    private FlexibleSearchService flexibleSearchService;

    protected CartModel cart;
    protected UnitModel unitModel;

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
    public void shouldGetEntryByComponent() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        final List<CartEntryModel> entries = bundleCommerceCartService
                .getCartEntriesForComponentInBundle(cart, getBundleTemplate(REGULAR_COMPONENT), 1);
        assertEquals(1, entries.size());
        assertEquals(1, (int) entries.get(0).getBundleNo());
        assertEquals(REGULAR_COMPONENT, entries.get(0).getBundleTemplate().getId());
        assertEquals(PRODUCT01, entries.get(0).getProduct().getCode());
    }

    @Test
    public void shouldSelectByComponent() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        addToCart(PREMIUM01, PREMIUM_COMPONENT, NEW_BUNDLE);
        assertEquals(
                1,
                bundleCommerceCartService.getCartEntriesForComponentInBundle(cart, getBundleTemplate(REGULAR_COMPONENT), 1).size()
        );
        assertEquals(
                1,
                bundleCommerceCartService.getCartEntriesForComponentInBundle(cart, getBundleTemplate(PREMIUM_COMPONENT), 2).size()
        );
        assertTrue(
                bundleCommerceCartService.getCartEntriesForComponentInBundle(cart, getBundleTemplate(REGULAR_COMPONENT), 2).isEmpty()
        );
    }

    @Test
    public void shouldSelectByBundle() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        addToCart(PRODUCT02, REGULAR_COMPONENT, NEW_BUNDLE);
        List<CartEntryModel> entries = bundleCommerceCartService
                .getCartEntriesForComponentInBundle(cart, getBundleTemplate(REGULAR_COMPONENT), 1);
        assertEquals(1, entries.size());
        assertEquals(PRODUCT01, entries.get(0).getProduct().getCode());
        entries = bundleCommerceCartService
                .getCartEntriesForComponentInBundle(cart, getBundleTemplate(REGULAR_COMPONENT), 2);
        assertEquals(1, entries.size());
        assertEquals(PRODUCT02, entries.get(0).getProduct().getCode());
    }

    @Test
    public void shouldHandleComponentNotFound() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        final List<CartEntryModel> entries = bundleCommerceCartService
                .getCartEntriesForComponentInBundle(cart, getBundleTemplate(PREMIUM_COMPONENT), 1);
        assertNotNull(entries);
        assertTrue(entries.isEmpty());
    }

    @Test
    public void shouldHandleBundleNotFound() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        final List<CartEntryModel> entries = bundleCommerceCartService
                .getCartEntriesForComponentInBundle(cart, getBundleTemplate(REGULAR_COMPONENT), 2);
        assertNotNull(entries);
        assertTrue(entries.isEmpty());
    }

    @Test
    public void testAdd2Cart2Products() throws CommerceCartModificationException, CalculationException
    {
        final ProductModel regularProduct = getProduct(PRODUCT05);
        final List<CommerceCartModification> modifications = bundleCommerceCartService.addToCart(cart, regularProduct, 1,
                unitModel, false, NO_BUNDLE, null, false, "<no xml>");
        assertThat(modifications, contains(hasProperty("entry", hasProperty("totalPrice", is(650.0)))));
        assertEquals(Double.valueOf(650), cart.getTotalPrice());
        assertEquals(1, cart.getEntries().size());
        assertEquals(0, cart.getChildren().size());

        final ProductModel premiumProduct = getProduct(PREMIUM02);
        final List<CommerceCartModification> mods = bundleCommerceCartService.addToCart(cart, unitModel, NEW_BUNDLE,
                regularProduct, getBundleTemplate(REGULAR_COMPONENT), premiumProduct, getBundleTemplate(PREMIUM_COMPONENT),
                "<no xml>", "<no xml>");

        assertThat(mods, containsInAnyOrder(
                hasProperty("entry", hasProperty("product", is(regularProduct))),
                hasProperty("entry", hasProperty("product", is(premiumProduct)))
        ));
        assertEquals(3, cart.getEntries().size());
        assertEquals(Double.valueOf(1870), cart.getTotalPrice());
    }

    @Test
    public void shouldGetCartEntryByProduct() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        final List<CartEntryModel> entries = bundleCommerceCartService.getCartEntriesForProductInBundle(cart, getProduct(PRODUCT01), 1);
        assertThat(entries, iterableWithSize(1));
        assertEquals(PRODUCT01, entries.get(0).getProduct().getCode());
        assertEquals(REGULAR_COMPONENT, entries.get(0).getBundleTemplate().getId());
        assertEquals(1, entries.get(0).getBundleNo().intValue());
    }

    @Test
    public void gettingEntriesByProductShouldWorkWithEmptyCart()
    {
        final List<CartEntryModel> entries = bundleCommerceCartService.getCartEntriesForProductInBundle(cart, getProduct(PRODUCT01), 1);
        assertThat(entries, emptyIterable());
    }

    @Test
    public void shouldFilterCartEntriesByProduct() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        addToCart(PRODUCT02, REGULAR_COMPONENT, 1);
        final List<CartEntryModel> entries = bundleCommerceCartService.getCartEntriesForProductInBundle(cart, getProduct(PRODUCT01), 1);
        assertThat(entries, iterableWithSize(1));
        assertEquals(PRODUCT01, entries.get(0).getProduct().getCode());
        assertEquals(REGULAR_COMPONENT, entries.get(0).getBundleTemplate().getId());
        assertEquals(1, entries.get(0).getBundleNo().intValue());
    }

    @Test
    public void shouldFilterCartEntriesByBundleNo() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        final List<CartEntryModel> entries = bundleCommerceCartService.getCartEntriesForProductInBundle(cart, getProduct(PRODUCT01), 2);
        assertThat(entries, iterableWithSize(1));
        assertEquals(PRODUCT01, entries.get(0).getProduct().getCode());
        assertEquals(REGULAR_COMPONENT, entries.get(0).getBundleTemplate().getId());
        assertEquals(2, entries.get(0).getBundleNo().intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getByProductShouldValidateCart() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        bundleCommerceCartService.getCartEntriesForProductInBundle(null, getProduct(PRODUCT01), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getByProductShouldValidateProduct() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        bundleCommerceCartService.getCartEntriesForProductInBundle(cart, null, 1);
    }

    @Test
    public void getByProductShouldValidateBundleNo() throws CommerceCartModificationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        final List<CartEntryModel> entries = bundleCommerceCartService.getCartEntriesForProductInBundle(cart, getProduct(PRODUCT01), 0);
        assertThat(entries, emptyIterable());
    }


    protected void addToCart(final String productCode, final String componentId, final int bundleNo)
            throws CommerceCartModificationException
    {
        bundleCommerceCartService.addToCart(
                cart, getProduct(productCode), 1, unitModel, false, bundleNo, getBundleTemplate(componentId), false);
    }

    protected BundleTemplateModel getBundleTemplate(final String templateId)
    {
        final BundleTemplateModel exampleModel = new BundleTemplateModel();
        exampleModel.setId(templateId);
        exampleModel.setCatalogVersion(getCatalog());
        return flexibleSearchService.getModelByExample(exampleModel);
    }

    protected ProductModel getProduct(final String code)
    {
        return productService.getProductForCode(getCatalog(), code);
    }

    protected CatalogVersionModel getCatalog()
    {
        return catalogVersionService.getCatalogVersion("testCatalog", "Online");
    }

    @Test
    public void testRemoveAllEntriesBundle() throws CommerceCartModificationException, CalculationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        addToCart(PREMIUM01, PREMIUM_COMPONENT, NEW_BUNDLE);
        bundleCommerceCartService.addToCart(cart, getProduct(PREMIUM01), 2, unitModel, false, NO_BUNDLE, null, false, "<no xml>");

        assertEquals(3, cart.getEntries().size());

        bundleCommerceCartService.removeAllEntries(cart, 2);
        assertEquals(2, cart.getEntries().size());
        assertTrue(CollectionUtils.isEmpty(cart.getLastModifiedEntries()));

        // Remove stand-alone
        bundleCommerceCartService.removeAllEntries(cart, 0);
        assertEquals(1, cart.getEntries().size());
        assertTrue(CollectionUtils.isEmpty(cart.getLastModifiedEntries()));

        // Remove bundle 1
        bundleCommerceCartService.removeAllEntries(cart, 1);
        assertEquals(0, cart.getEntries().size());
        assertTrue(CollectionUtils.isEmpty(cart.getLastModifiedEntries()));
    }

    @Test(expected = CommerceCartModificationException.class)
    public void testRemoveAllEntriesBundleBundleDoesNotExist() throws CommerceCartModificationException, CalculationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        addToCart(PREMIUM01, PREMIUM_COMPONENT, NEW_BUNDLE);
        bundleCommerceCartService.addToCart(cart, getProduct(PREMIUM01), 2, unitModel, false, NO_BUNDLE, null, false, "<no xml>");
        bundleCommerceCartService.removeAllEntries(cart, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveAllEntriesBundleNullCart() throws CommerceCartModificationException, CalculationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        addToCart(PREMIUM01, PREMIUM_COMPONENT, NEW_BUNDLE);
        bundleCommerceCartService.addToCart(cart, getProduct(PREMIUM01), 2, unitModel, false, NO_BUNDLE, null, false, "<no xml>");
        bundleCommerceCartService.removeAllEntries(null, 2);
    }

    @Test(expected = CommerceCartModificationException.class)
    public void testRemoveAllEntriesBundleBundleNoIsNegative() throws CommerceCartModificationException, CalculationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        addToCart(PREMIUM01, PREMIUM_COMPONENT, NEW_BUNDLE);
        bundleCommerceCartService.addToCart(cart, getProduct(PREMIUM01), 2, unitModel, false, NO_BUNDLE, null, false, "<no xml>");
        bundleCommerceCartService.removeAllEntries(cart, -1);
    }

    @Test
    public void testRemoveAllEntriesBundleBundleNoIsZero() throws CommerceCartModificationException, CalculationException
    {
        addToCart(PRODUCT01, REGULAR_COMPONENT, NEW_BUNDLE);
        addToCart(PREMIUM01, PREMIUM_COMPONENT, NEW_BUNDLE);
        bundleCommerceCartService.addToCart(cart, getProduct(PREMIUM01), 2, unitModel, false, NO_BUNDLE, null, false, "<no xml>");
        bundleCommerceCartService.removeAllEntries(cart, 0);
        assertEquals(2, cart.getEntries().size());
        assertTrue(CollectionUtils.isEmpty(cart.getLastModifiedEntries()));
    }
}

