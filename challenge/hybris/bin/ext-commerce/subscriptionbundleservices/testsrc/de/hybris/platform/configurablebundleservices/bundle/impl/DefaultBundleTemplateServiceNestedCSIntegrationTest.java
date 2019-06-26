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
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateStatusModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.util.Config;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Nested integration test suite for {@link BundleTemplateService}. Contains tests moved from
 * {@link DefaultBundleTemplateServiceNestedIntegrationTest} which rely on using
 * {@link BundleCommerceCartService}.
 */
@IntegrationTest
public class DefaultBundleTemplateServiceNestedCSIntegrationTest extends ServicelayerTest
{
    private static final Logger LOG = Logger.getLogger(DefaultBundleTemplateServiceNestedCSIntegrationTest.class);

    public static final String REGULAR_COMPONENT = "ProductComponent1";
    public static final String PRODUCT01 = "PRODUCT01";
    public static final String CATALOG_ID = "testCatalog";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Resource
    protected BundleTemplateService bundleTemplateService;

    @Resource
    private UnitService unitService;

    @Resource
    private BundleCommerceCartService bundleCommerceCartService;

    @Resource
    private UserService userService;

    @Resource
    private ProductService productService;

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Resource
    private CatalogVersionService catalogVersionService;

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
    public void shouldKnowWhetherBundleIsUsedByCart() throws CommerceCartModificationException
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel component = getBundleTemplateByIdAndCatalogVersion(REGULAR_COMPONENT, catalog);
        final BundleTemplateModel parent = component.getParentTemplate();
        final BundleTemplateModel root = bundleTemplateService.getRootBundleTemplate(component);

        assertFalse(bundleTemplateService.isBundleTemplateUsed(component));
        assertFalse(bundleTemplateService.isBundleTemplateUsed(parent));
        assertFalse(bundleTemplateService.isBundleTemplateUsed(root));
        bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT01), 1, unitModel, true, -1, component, false);
        assertTrue(bundleTemplateService.isBundleTemplateUsed(component));
        assertFalse(bundleTemplateService.isBundleTemplateUsed(parent));
        assertFalse(bundleTemplateService.isBundleTemplateUsed(root));
    }

    @Test
    public void shouldCorrectlyDetermineAutopickComponents()
    {
        final CatalogVersionModel catalog = getCatalog();
        assertFalse(bundleTemplateService.isAutoPickComponent(getBundleTemplateByIdAndCatalogVersion(REGULAR_COMPONENT, catalog)));
    }

    private BundleTemplateModel getBundleTemplateByIdAndCatalogVersion(final String bundleId,
                                                                       final CatalogVersionModel catalogVersionModel)
    {
        final BundleTemplateModel exampleModel = new BundleTemplateModel();
        exampleModel.setId(bundleId);
        exampleModel.setCatalogVersion(catalogVersionModel);

        return flexibleSearchService.getModelByExample(exampleModel);
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
