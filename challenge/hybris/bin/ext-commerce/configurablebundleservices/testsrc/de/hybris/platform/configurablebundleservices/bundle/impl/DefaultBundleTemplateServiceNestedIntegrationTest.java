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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@IntegrationTest
public class DefaultBundleTemplateServiceNestedIntegrationTest extends ServicelayerTest
{
    private static final Logger LOG = Logger.getLogger(DefaultBundleTemplateServiceNestedIntegrationTest.class);

    public static final String PARENT_PACKAGE = "ParentPackage";
    public static final String SECOND_PACKAGE = "SecondPackage";
    public static final String THIRD_PACKAGE = "ThirdPackage";
    public static final String FOURTH_PACKAGE = "FourthPackage";
    public static final String STAGED_PACKAGE = "StagedPackage";

    public static final String REGULAR_COMPONENT = "ProductComponent1";
    public static final String OPTIONAL_COMPONENT = "OptionalComponent";
    public static final String PREMIUM_COMPONENT = "PremiumComponent2";
    public static final String ROOT_COMPONENT = "RootComponent";
    public static final String NESTED_COMPONENT = "NestedComponent1";
    public static final String ROOT_BUNDLE_TEMPLATE = "RootBundleTemptate";

    public static final String PRODUCT01 = "PRODUCT01";
    public static final String PREMIUM01 = "PREMIUM01";
    public static final String PRODUCT02 = "PRODUCT02";
    public static final String STANDALONE01 = "STANDALONE01";

    public static final String CATALOG_ID = "testCatalog";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Resource
    protected BundleTemplateService bundleTemplateService;

    @Resource
    private UnitService unitService;

    @Resource
    private UserService userService;

    @Resource
    private ProductService productService;

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private ModelService modelService;

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
    public void shouldGetLeafComponents()
    {
        final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(REGULAR_COMPONENT);
        final List<BundleTemplateModel> leafs = bundleTemplateService.getLeafComponents(component);
        assertFalse(leafs.isEmpty());
        assertTrue(leafs.contains(component));
    }

    @Test
    public void shouldGetLeafsByNonLeafComponent()
    {
        final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(NESTED_COMPONENT);
        final List<BundleTemplateModel> leafs = bundleTemplateService.getLeafComponents(component);
        assertFalse(leafs.isEmpty());
        assertThat(leafs, not(hasItem(component)));
        assertThat(leafs, hasItem(hasProperty(BundleTemplateModel.ID, is(REGULAR_COMPONENT))));
        assertThat(leafs, hasItem(hasProperty(BundleTemplateModel.ID, is(PREMIUM_COMPONENT))));
    }

    @Test
    public void shouldReturnLeafsByRoot()
    {
        final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode(PARENT_PACKAGE);
        final List<BundleTemplateModel> leafs = bundleTemplateService.getLeafComponents(component);
        assertFalse(leafs.isEmpty());
        assertThat(leafs, not(hasItem(component)));
        assertThat(leafs, hasItem(hasProperty(BundleTemplateModel.ID, is(REGULAR_COMPONENT))));
        assertThat(leafs, hasItem(hasProperty(BundleTemplateModel.ID, is(PREMIUM_COMPONENT))));
    }

    @Test
    public void leafListShouldBeConsistentRelativeToSourceComponent()
    {
        final BundleTemplateModel component1 = bundleTemplateService.getBundleTemplateForCode(REGULAR_COMPONENT);
        final BundleTemplateModel component2 = bundleTemplateService.getBundleTemplateForCode("NestedGroup2");
        assertEquals(
                bundleTemplateService.getLeafComponents(component1),
                bundleTemplateService.getLeafComponents(component2)
        );
    }

    @Test
    public void shouldPreventLeafOrder()
    {
        final BundleTemplateModel component = bundleTemplateService.getBundleTemplateForCode("NestedGroup1");
        final List<BundleTemplateModel> leafs = bundleTemplateService.getLeafComponents(component);
        for (int i = 0; i < leafs.size() - 1; i++) {
            final BundleTemplateModel leaf1 = leafs.get(i);
            final BundleTemplateModel leaf2 = leafs.get(i + 1);
            final List<BundleTemplateModel> hierarchy1 = getHierarchy(leaf1);
            final List<BundleTemplateModel> hierarchy2 = getHierarchy(leaf2);
            // components belong to the same package, so they must have a common root
            assertEquals(hierarchy1.get(0), hierarchy2.get(0));
            while (hierarchy1.get(1).equals(hierarchy2.get(1)))
            {
                hierarchy1.remove(0);
                hierarchy2.remove(0);
            }
            assertTrue(hierarchy1.get(0).getChildTemplates().indexOf(hierarchy1.get(1))
                    < hierarchy2.get(0).getChildTemplates().indexOf(hierarchy2.get(1)));
        }
    }

    @Test
    public void shouldReturnAllRootTemplates()
    {
        final CatalogVersionModel catalog = getCatalog();
        List<BundleTemplateModel> roots = bundleTemplateService.getAllRootBundleTemplates(catalog);
        assertThat(roots, containsInAnyOrder(
                getBundleTemplateByIdAndCatalogVersion(PARENT_PACKAGE, catalog),
                getBundleTemplateByIdAndCatalogVersion(ROOT_COMPONENT, catalog),
                getBundleTemplateByIdAndCatalogVersion(SECOND_PACKAGE, catalog),
                getBundleTemplateByIdAndCatalogVersion(THIRD_PACKAGE, catalog),
                getBundleTemplateByIdAndCatalogVersion(FOURTH_PACKAGE, catalog),
                getBundleTemplateByIdAndCatalogVersion(ROOT_BUNDLE_TEMPLATE, catalog)
        ));
    }

    @Test
    public void shouldReturnAllRootTemplatesIncludingArchived()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel secondPackage = getBundleTemplateByIdAndCatalogVersion(SECOND_PACKAGE, catalog);
        final BundleTemplateStatusModel statusSample = new BundleTemplateStatusModel();
        statusSample.setId("testBundleArchived");
        statusSample.setCatalogVersion(catalog);
        secondPackage.setStatus(flexibleSearchService.getModelByExample(statusSample));
        modelService.save(secondPackage);
        List<BundleTemplateModel> roots = bundleTemplateService.getAllRootBundleTemplates(catalog);
        assertThat(roots, containsInAnyOrder(
                getBundleTemplateByIdAndCatalogVersion(PARENT_PACKAGE, catalog),
                getBundleTemplateByIdAndCatalogVersion(ROOT_COMPONENT, catalog),
                getBundleTemplateByIdAndCatalogVersion(THIRD_PACKAGE, catalog),
                getBundleTemplateByIdAndCatalogVersion(FOURTH_PACKAGE, catalog),
                getBundleTemplateByIdAndCatalogVersion(ROOT_BUNDLE_TEMPLATE, catalog),
                secondPackage));
    }

    @Test
    public void shouldReturnPositionInPackage()
    {
        final CatalogVersionModel catalog = getCatalog();
        assertEquals(0, bundleTemplateService.getPositionInParent(getBundleTemplateByIdAndCatalogVersion(REGULAR_COMPONENT, catalog)));
        assertEquals(1, bundleTemplateService.getPositionInParent(getBundleTemplateByIdAndCatalogVersion(OPTIONAL_COMPONENT, catalog)));
        assertEquals(2, bundleTemplateService.getPositionInParent(getBundleTemplateByIdAndCatalogVersion(PREMIUM_COMPONENT, catalog)));
    }

    @Test
    public void shouldNotReturnPositionInParentForNonLeafComponents()
    {
        final CatalogVersionModel catalog = getCatalog();
        assertEquals(-1, bundleTemplateService.getPositionInParent(getBundleTemplateByIdAndCatalogVersion(NESTED_COMPONENT, catalog)));
    }

    @Test
    public void shouldReturnNextComponentOfTheSameGroup()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel thisComponent = getBundleTemplateByIdAndCatalogVersion(REGULAR_COMPONENT, catalog);
        final BundleTemplateModel nextComponent = bundleTemplateService.getSubsequentBundleTemplate(thisComponent);
        assertNotNull(nextComponent);
        assertEquals(OPTIONAL_COMPONENT, nextComponent.getId());
    }

    @Test
    public void shouldReturnNextComponentAcrossGroups()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel thisComponent = getBundleTemplateByIdAndCatalogVersion(OPTIONAL_COMPONENT, catalog);
        final BundleTemplateModel nextComponent = bundleTemplateService.getSubsequentBundleTemplate(thisComponent);
        assertNotNull(nextComponent);
        assertEquals(PREMIUM_COMPONENT, nextComponent.getId());
    }

    @Test
    public void shouldReturnNullForNextOfTheLast()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel thisComponent = getBundleTemplateByIdAndCatalogVersion(PREMIUM_COMPONENT, catalog);
        assertNull(bundleTemplateService.getSubsequentBundleTemplate(thisComponent));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectNextOfNull()
    {
        bundleTemplateService.getSubsequentBundleTemplate(null);
    }

    @Test
    public void shouldReturnPrecedingComponentOfTheSameGroup()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel thisComponent = getBundleTemplateByIdAndCatalogVersion(OPTIONAL_COMPONENT, catalog);
        final BundleTemplateModel prevComponent = bundleTemplateService.getPreviousBundleTemplate(thisComponent);
        assertNotNull(prevComponent);
        assertEquals(REGULAR_COMPONENT, prevComponent.getId());
    }

    @Test
    public void shouldReturnPrecedingComponentAcrossGroups()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel thisComponent = getBundleTemplateByIdAndCatalogVersion(PREMIUM_COMPONENT, catalog);
        final BundleTemplateModel prevComponent = bundleTemplateService.getPreviousBundleTemplate(thisComponent);
        assertNotNull(prevComponent);
        assertEquals(OPTIONAL_COMPONENT, prevComponent.getId());
    }

    @Test
    public void shouldReturnNullForPrevOfTheFirst()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel thisComponent = getBundleTemplateByIdAndCatalogVersion(REGULAR_COMPONENT, catalog);
        assertNull(bundleTemplateService.getPreviousBundleTemplate(thisComponent));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectPrevOfNull()
    {
        bundleTemplateService.getPreviousBundleTemplate(null);
    }

    @Test
    public void relativeShouldWorkAsNext()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel thisComponent = getBundleTemplateByIdAndCatalogVersion(OPTIONAL_COMPONENT, catalog);
        final BundleTemplateModel relativeComponent = bundleTemplateService.getRelativeBundleTemplate(thisComponent, 1);
        assertNotNull(relativeComponent);
        assertEquals(PREMIUM_COMPONENT, relativeComponent.getId());
    }

    @Test
    public void relativeShouldWorkAsPrev()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel thisComponent = getBundleTemplateByIdAndCatalogVersion(OPTIONAL_COMPONENT, catalog);
        final BundleTemplateModel relativeComponent = bundleTemplateService.getRelativeBundleTemplate(thisComponent, -1);
        assertNotNull(relativeComponent);
        assertEquals(REGULAR_COMPONENT, relativeComponent.getId());
    }

    @Test
    public void relativeShouldBeAbleToJumpForward()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel thisComponent = getBundleTemplateByIdAndCatalogVersion(REGULAR_COMPONENT, catalog);
        final BundleTemplateModel relativeComponent = bundleTemplateService.getRelativeBundleTemplate(thisComponent, 2);
        assertNotNull(relativeComponent);
        assertEquals(PREMIUM_COMPONENT, relativeComponent.getId());
    }

    @Test
    public void relativeShouldBeAbleToJumpBackward()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel thisComponent = getBundleTemplateByIdAndCatalogVersion(PREMIUM_COMPONENT, catalog);
        final BundleTemplateModel relativeComponent = bundleTemplateService.getRelativeBundleTemplate(thisComponent, -2);
        assertNotNull(relativeComponent);
        assertEquals(REGULAR_COMPONENT, relativeComponent.getId());
    }

    @Test
    public void relativeShouldHandleUnderflow()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel thisComponent = getBundleTemplateByIdAndCatalogVersion(REGULAR_COMPONENT, catalog);
        final BundleTemplateModel relativeComponent = bundleTemplateService.getRelativeBundleTemplate(thisComponent, -1);
        assertNull(relativeComponent);
    }

    @Test
    public void relativeShouldHandleOverflow()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel thisComponent = getBundleTemplateByIdAndCatalogVersion(REGULAR_COMPONENT, catalog);
        final BundleTemplateModel relativeComponent = bundleTemplateService.getRelativeBundleTemplate(thisComponent, 3);
        assertNull(relativeComponent);
    }

    @Test
    public void shouldGetComponentsByProduct()
    {
        final ProductModel product = getProduct(PRODUCT01);
        final List<BundleTemplateModel> components = bundleTemplateService.getBundleTemplatesByProduct(product);
        assertThat(components, not(emptyIterable()));
        components.forEach(item -> {
            assertThat(item.getProducts(), hasItem(product));
            assertThat(item.getChildTemplates(), emptyIterable());
        });
    }

    @Test
    public void shouldReturnEmptyComponentListForStandaloneProduct()
    {
        final List<BundleTemplateModel> components = bundleTemplateService.getBundleTemplatesByProduct(getProduct(STANDALONE01));
        assertThat(components, emptyIterable());
    }

    @Test
    public void gettingComponentsByProductShouldHandleNonPersistentProduct()
    {
        final ProductModel product = new ProductModel();
        product.setCode("test");
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Model used as a search parameter must contain PK!");
        bundleTemplateService.getBundleTemplatesByProduct(product);
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingComponentsByProductShouldHandleNullArgument()
    {
        bundleTemplateService.getBundleTemplatesByProduct(null);
    }

    @Test
    public void shouldGetRootBundleTemplates()
    {
        final List<BundleTemplateModel> templates = bundleTemplateService.getAllApprovedRootBundleTemplates(getCatalog());
        assertThat(templates, not(emptyIterable()));
    }

    @Test
    public void shouldGetOnlyApprovedBundleTemplates()
    {
        final CatalogVersionModel catalog = getCatalog();
        final BundleTemplateModel template = getBundleTemplateByIdAndCatalogVersion(PARENT_PACKAGE, catalog);
        final BundleTemplateStatusModel status = getStatus("testBundleArchived");
        template.setStatus(status);
        modelService.save(template);

        final List<BundleTemplateModel> templates = bundleTemplateService.getAllApprovedRootBundleTemplates(catalog);

        assertThat(templates, not(emptyIterable()));
        assertThat(templates, not(hasItem(hasProperty(BundleTemplateModel.STATUS, is(status)))));
    }

    @Test
    public void shouldGetTemplatesOnlyFromGivenCatalog()
    {
        final List<BundleTemplateModel> onlineTemplates = bundleTemplateService.getAllApprovedRootBundleTemplates(getCatalog());
        assertThat(onlineTemplates, not(hasItem(hasProperty(BundleTemplateModel.ID, is(STAGED_PACKAGE)))));
        final List<BundleTemplateModel> stagedTemplates = bundleTemplateService.getAllApprovedRootBundleTemplates(
                catalogVersionService.getCatalogVersion(CATALOG_ID, "Staged"));
        assertThat(stagedTemplates, hasItem(hasProperty(BundleTemplateModel.ID, is(STAGED_PACKAGE))));
        assertFalse(CollectionUtils.containsAny(onlineTemplates, stagedTemplates));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldHandleNullCatalogWhileGettingTemplates()
    {
        final List<BundleTemplateModel> templates = bundleTemplateService.getAllApprovedRootBundleTemplates(null);
        assertThat(templates, emptyIterable());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldHandleNonPersistentArgumentWhileGettingTemplates()
    {
        final CatalogVersionModel catalog = new CatalogVersionModel();
        bundleTemplateService.getAllApprovedRootBundleTemplates(catalog);
    }

    protected BundleTemplateStatusModel getStatus(final String id)
    {
        final BundleTemplateStatusModel sample = new BundleTemplateStatusModel();
        sample.setId(id);
        sample.setCatalogVersion(getCatalog());
        return flexibleSearchService.getModelByExample(sample);
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

    protected List<BundleTemplateModel> getHierarchy(BundleTemplateModel node)
    {

        final List<BundleTemplateModel> hierarchy = new ArrayList<>();
        while (node != null)
        {
            hierarchy.add(0, node);
            node = node.getParentTemplate();
        }
        return hierarchy;
    }
}
