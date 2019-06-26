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

package de.hybris.platform.configurablebundlefacades.order.converters.populator;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
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
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@IntegrationTest
public class BundleCartPopulatorIntegrationTest extends ServicelayerTest
{
    private static final Logger LOG = Logger.getLogger(BundleCartPopulatorIntegrationTest.class);
    public static final String PRODUCT01 = "PRODUCT01";
    public static final String PRODUCT02 = "PRODUCT02";
    public static final String PRODUCT03 = "PRODUCT03";
    public static final String PREMIUM01 = "PREMIUM01";
    public static final String PREMIUM02 = "PREMIUM02";
    public static final String REGULAR_COMPONENT = "ProductComponent1";
    public static final String PREMIUM_COMPONENT = "PremiumComponent2";
    public static final String OPTIONAL_COMPONENT = "OptionalComponent";
    public static final String SECOND_COMPONENT = "SecondComponent1";
    private static final String PRODUCT_STANDALONE = "STANDALONE01";

    protected CartModel cart;
    protected UnitModel unitModel;
    @Resource
    protected UserService userService;
    @Resource
    private UnitService unitService;
    @Resource
    protected BundleCommerceCartService bundleCommerceCartService;
    @Resource
    private CatalogVersionService catalogVersionService;
    @Resource
    protected ProductService productService;
    @Resource
    private FlexibleSearchService flexibleSearchService;
    @Resource
    private BundleCartPopulator<CartModel, CartData> bundleCartPopulator;

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
        unitModel = unitService.getUnitForCode("pieces");
    }

    @Test
    public void regularProductsOrderShouldBeTheSameTest() throws CommerceCartModificationException, CalculationException
    {
        CartData cartData = new CartData();
        final List<OrderEntryData> entries = new ArrayList<>(7);
        entries.add(createOrderEntryData(PRODUCT03));
        entries.add(createOrderEntryData(PRODUCT01));
        entries.add(createOrderEntryData(PRODUCT02));
        cartData.setEntries(entries);
        bundleCartPopulator.populate(cart, cartData);
        assertEquals(PRODUCT03, cartData.getEntries().get(0).getProduct().getCode());
        assertEquals(PRODUCT01, cartData.getEntries().get(1).getProduct().getCode());
        assertEquals(PRODUCT02, cartData.getEntries().get(2).getProduct().getCode());
    }

    @Test
    public void oneBundleEntriesOrderTest() throws CommerceCartModificationException, CalculationException
    {
        addToCart(PRODUCT01, OPTIONAL_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        addToCart(PREMIUM01, PREMIUM_COMPONENT, 1);
        addToCart(PRODUCT02, REGULAR_COMPONENT, 1);
        CartData cartData = new CartData();
        final List<OrderEntryData> entries = new ArrayList<>(7);
        entries.add(createOrderEntryData(PRODUCT01, 1, OPTIONAL_COMPONENT, "1.0"));
        entries.add(createOrderEntryData(PREMIUM01, 1, PREMIUM_COMPONENT, "1.0"));
        entries.add(createOrderEntryData(PRODUCT02, 1, REGULAR_COMPONENT, "1.0"));
        cartData.setEntries(entries);
        bundleCartPopulator.populate(cart, cartData);
        assertEquals(PRODUCT02, cartData.getEntries().get(0).getProduct().getCode());
        assertEquals(PRODUCT01, cartData.getEntries().get(1).getProduct().getCode());
        assertEquals(PREMIUM01, cartData.getEntries().get(2).getProduct().getCode());
    }

    @Test
    public void productsFromDifferentPackages() throws CommerceCartModificationException
    {
        assertThat(cart.getEntries(), emptyIterable());
        addToCart(PRODUCT_STANDALONE);
        addToCart(PRODUCT01, SECOND_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        addToCart(PRODUCT01, REGULAR_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        addToCart(PRODUCT02, REGULAR_COMPONENT, 2);
        addToCart(PRODUCT02, SECOND_COMPONENT, 1);
        CartData cartData = new CartData();
        cartData.setEntries(Arrays.asList(
                createOrderEntryData(PRODUCT01, 1, SECOND_COMPONENT, "1.0"),
                createOrderEntryData(PRODUCT01, 2, REGULAR_COMPONENT, "1.0"),
                createOrderEntryData(PRODUCT02, 2, REGULAR_COMPONENT, "1.0"),
                createOrderEntryData(PRODUCT02, 1, SECOND_COMPONENT, "1.0"),
                createOrderEntryData(PRODUCT_STANDALONE)
        ));
        bundleCartPopulator.populate(cart, cartData);

        // First added components on top
        assertEquals(SECOND_COMPONENT, cartData.getEntries().get(0).getComponent().getId());
        // Product inside single component go in order of adding to cart
        assertEquals(PRODUCT01, cartData.getEntries().get(0).getProduct().getCode());
        assertEquals(SECOND_COMPONENT, cartData.getEntries().get(1).getComponent().getId());
        assertEquals(PRODUCT02, cartData.getEntries().get(1).getProduct().getCode());
        // Next should go promotions of bundle "SecondPackage", let's not test them here
        // ...
        // Than components added on the second step
        assertEquals(REGULAR_COMPONENT, cartData.getEntries().get(4).getComponent().getId());
        assertEquals(PRODUCT01, cartData.getEntries().get(4).getProduct().getCode());
        assertEquals(REGULAR_COMPONENT, cartData.getEntries().get(5).getComponent().getId());
        assertEquals(PRODUCT02, cartData.getEntries().get(5).getProduct().getCode());
        // Standalone products are on the end of the list
        final int lastIdx = cartData.getEntries().size() - 1;
        assertEquals(PRODUCT_STANDALONE, cartData.getEntries().get(lastIdx).getProduct().getCode());
        assertNull(cartData.getEntries().get(lastIdx).getComponent());
    }

    @Test
    public void twoBundleEntriesOrderTest() throws CommerceCartModificationException, CalculationException
    {
        addToCart(PRODUCT01, OPTIONAL_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        addToCart(PREMIUM02, PREMIUM_COMPONENT, 1);
        addToCart(PRODUCT03, REGULAR_COMPONENT, 1);
        addToCart(PRODUCT01, OPTIONAL_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        addToCart(PREMIUM01, PREMIUM_COMPONENT, 2);
        addToCart(PRODUCT02, REGULAR_COMPONENT, 2);
        CartData cartData = new CartData();
        final List<OrderEntryData> entries = new ArrayList<>(7);
        entries.add(createOrderEntryData(PRODUCT01, 1, OPTIONAL_COMPONENT, "1.0"));
        entries.add(createOrderEntryData(PREMIUM02, 1, PREMIUM_COMPONENT, "1.0"));
        entries.add(createOrderEntryData(PRODUCT03, 1, REGULAR_COMPONENT, "1.0"));
        entries.add(createOrderEntryData(PRODUCT01, 2, OPTIONAL_COMPONENT, "1.0"));
        entries.add(createOrderEntryData(PREMIUM01, 2, PREMIUM_COMPONENT, "1.0"));
        entries.add(createOrderEntryData(PRODUCT02, 2, REGULAR_COMPONENT, "1.0"));
        cartData.setEntries(entries);
        bundleCartPopulator.populate(cart, cartData);
        assertEquals(PRODUCT03, cartData.getEntries().get(0).getProduct().getCode());
        assertEquals(PRODUCT01, cartData.getEntries().get(1).getProduct().getCode());
        assertEquals(PREMIUM02, cartData.getEntries().get(2).getProduct().getCode());
        assertEquals(PRODUCT02, cartData.getEntries().get(3).getProduct().getCode());
        assertEquals(PRODUCT01, cartData.getEntries().get(4).getProduct().getCode());
        assertEquals(PREMIUM01, cartData.getEntries().get(5).getProduct().getCode());
        assertEquals(1, cartData.getEntries().get(0).getBundleNo());
        assertEquals(1, cartData.getEntries().get(1).getBundleNo());
        assertEquals(1, cartData.getEntries().get(2).getBundleNo());
        assertEquals(2, cartData.getEntries().get(3).getBundleNo());
        assertEquals(2, cartData.getEntries().get(4).getBundleNo());
        assertEquals(2, cartData.getEntries().get(5).getBundleNo());
    }

    @Test
    public void firstIsRegularProductSecondIsBundleEntriesOrderTest() throws CommerceCartModificationException, CalculationException
    {
        addToCart(PRODUCT02);
        addToCart(PRODUCT01, OPTIONAL_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        addToCart(PREMIUM02, PREMIUM_COMPONENT, 1);
        addToCart(PRODUCT03, REGULAR_COMPONENT, 1);

        CartData cartData = new CartData();
        final List<OrderEntryData> entries = new ArrayList<>(7);
        entries.add(createOrderEntryData(PRODUCT02));
        entries.add(createOrderEntryData(PRODUCT01, 1, OPTIONAL_COMPONENT, "1.0"));
        entries.add(createOrderEntryData(PREMIUM02, 1, PREMIUM_COMPONENT, "1.0"));
        entries.add(createOrderEntryData(PRODUCT03, 1, REGULAR_COMPONENT, "1.0"));

        cartData.setEntries(entries);
        bundleCartPopulator.populate(cart, cartData);
        assertEquals(PRODUCT03, cartData.getEntries().get(0).getProduct().getCode());
        assertEquals(PRODUCT01, cartData.getEntries().get(1).getProduct().getCode());
        assertEquals(PREMIUM02, cartData.getEntries().get(2).getProduct().getCode());
        assertEquals(PRODUCT02, cartData.getEntries().get(3).getProduct().getCode());
    }

    @Test
    public void firstIsBundleSecondIsRegularProductEntriesOrderTest() throws CommerceCartModificationException, CalculationException
    {
        addToCart(PRODUCT01, OPTIONAL_COMPONENT, ConfigurableBundleServicesConstants.NEW_BUNDLE);
        addToCart(PREMIUM02, PREMIUM_COMPONENT, 1);
        addToCart(PRODUCT03, REGULAR_COMPONENT, 1);
        addToCart(PRODUCT02);

        CartData cartData = new CartData();
        final List<OrderEntryData> entries = new ArrayList<>(7);
        entries.add(createOrderEntryData(PRODUCT01, 1, OPTIONAL_COMPONENT, "1.0"));
        entries.add(createOrderEntryData(PREMIUM02, 1, PREMIUM_COMPONENT, "1.0"));
        entries.add(createOrderEntryData(PRODUCT03, 1, REGULAR_COMPONENT, "1.0"));
        entries.add(createOrderEntryData(PRODUCT02));

        cartData.setEntries(entries);
        bundleCartPopulator.populate(cart, cartData);
        assertEquals(PRODUCT03, cartData.getEntries().get(0).getProduct().getCode());
        assertEquals(PRODUCT01, cartData.getEntries().get(1).getProduct().getCode());
        assertEquals(PREMIUM02, cartData.getEntries().get(2).getProduct().getCode());
        assertEquals(PRODUCT02, cartData.getEntries().get(3).getProduct().getCode());
    }

    protected OrderEntryData createOrderEntryData(String productCode) {
        return createOrderEntryData(productCode, ConfigurableBundleServicesConstants.NO_BUNDLE, null, null);
    }

    protected OrderEntryData createOrderEntryData(String productCode, int bundleNo, String componentId, String componentVersion) {
        OrderEntryData orderEntryData1 = new OrderEntryData();
        ProductData productData1 = new ProductData();
        productData1.setCode(productCode);
        orderEntryData1.setProduct(productData1);
        orderEntryData1.setBundleNo(bundleNo);
        if (componentId != null) {
            BundleTemplateData component = new BundleTemplateData();
            component.setId(componentId);
            component.setVersion(componentVersion);
            orderEntryData1.setComponent(component);
        }
        return orderEntryData1;
    }

    protected void addToCart(final String productCode, final String componentId, final int bundleNo)
            throws CommerceCartModificationException
    {
        bundleCommerceCartService.addToCart(
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
