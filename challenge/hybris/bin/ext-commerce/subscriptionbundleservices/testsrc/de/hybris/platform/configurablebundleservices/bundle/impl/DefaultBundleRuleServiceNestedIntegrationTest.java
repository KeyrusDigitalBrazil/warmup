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
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Resource;

import java.util.List;

import static de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants.NEW_BUNDLE;
import static de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants.NO_BUNDLE;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;


/**
 * Integration test suite for {@link FindBundlePricingWithCurrentPriceFactoryStrategy} and
 * {@link DefaultBundleRuleService}
 */
@IntegrationTest
public class DefaultBundleRuleServiceNestedIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultBundleRuleServiceNestedIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

    protected static final String PRODUCT01 = "PRODUCT01";
	protected static final String PRODUCT02 = "PRODUCT02";
	protected static final String PRODUCT05 = "PRODUCT05";
	protected static final String PRODUCT06 = "PRODUCT06";
	protected static final String PREMIUM01 = "PREMIUM01";
    protected static final String PREMIUM02 = "PREMIUM02";

	protected static final String REGULAR_COMPONENT = "ProductComponent1";
    protected static final String OPTIONAL_COMPONENT = "OptionalComponent";
	protected static final String LEAF_BUNDLE_TEMPLATE_1 = "LeafBundleTemptate1";
	protected static final String LEAF_BUNDLE_TEMPLATE_2 = "LeafBundleTemptate2";

	@Rule
    public ExpectedException thrown = ExpectedException.none();

	protected CartModel cart;
	protected UnitModel unitModel;
	protected CurrencyModel currency;
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
	private BaseSiteService baseSiteService;
	@Resource
	private BundleRuleService bundleRuleService;
	@Resource
	private CommonI18NService commonI18NService;

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
		currency = commonI18NService.getCurrency("USD");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
	}

    @Test
    public void shouldDisableInOneComponent() throws CommerceCartModificationException
    {
        final BundleTemplateModel component = getBundleTemplate(REGULAR_COMPONENT);
        final ProductModel product = getProduct(PRODUCT01);
        bundleCommerceCartService.addToCart(cart, product, 1, unitModel, true, NEW_BUNDLE, component, false);
        thrown.expect(CommerceCartModificationException.class);
        thrown.expectMessage("1");
        bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT06), 1, unitModel, true, 1, component, false);
    }

    @Test
    public void shouldDisableInDifferentComponents() throws CommerceCartModificationException
    {
        final BundleTemplateModel component = getBundleTemplate(REGULAR_COMPONENT);
        final ProductModel product = getProduct(PRODUCT01);
        bundleCommerceCartService.addToCart(cart, product, 1, unitModel, true, NEW_BUNDLE, component, false);
        thrown.expect(CommerceCartModificationException.class);
        thrown.expectMessage("1");
        bundleCommerceCartService.addToCart(cart, getProduct(PRODUCT06), 1, unitModel, true, 1, getBundleTemplate(OPTIONAL_COMPONENT), false);
    }

    @Test
    public void gettingDisablingRulesShouldHandleNullOrder()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Parameter masterAbstractOrder can not be null");
        bundleRuleService.getDisableRuleForBundleProduct(null, getProduct(PRODUCT01), getBundleTemplate(REGULAR_COMPONENT), 1, false);
    }

    @Test
    public void gettingDisablingRuleShouldHandleNullProduct()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Parameter product can not be null");
        bundleRuleService.getDisableRuleForBundleProduct(cart, null, getBundleTemplate(REGULAR_COMPONENT), 1, false);
    }

    @Test
    public void gettingDisablingRuleShouldHandleNullComponent()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Parameter bundleTemplate can not be null");
        bundleRuleService.getDisableRuleForBundleProduct(cart, getProduct(PRODUCT01), null, 1, false);
    }

    @Test
    public void gettingDisablingRuleShouldHandleInvalidBundleId() throws CommerceCartModificationException
    {
        final BundleTemplateModel component = getBundleTemplate(REGULAR_COMPONENT);
        final ProductModel product = getProduct(PRODUCT01);
        bundleCommerceCartService.addToCart(cart, product, 1, unitModel, true, NEW_BUNDLE, component, false);
        final DisableProductBundleRuleModel rule = bundleRuleService.getDisableRuleForBundleProduct(cart, product, component, 2, false);
        assertNull(rule);
    }

    @Test
    public void shouldGetDisablingRuleForBundleProduct()
    {
        final DisableProductBundleRuleModel rule = bundleRuleService
                .getDisableRuleForBundleProduct(getBundleTemplate(REGULAR_COMPONENT), getProduct(PRODUCT01), getProduct(PRODUCT06));
        assertNotNull(rule);
    }

    @Test
    public void shouldNotReturnDisablingRulesForOneProduct()
    {
        final DisableProductBundleRuleModel rule = bundleRuleService
                .getDisableRuleForBundleProduct(getBundleTemplate(REGULAR_COMPONENT), getProduct(PRODUCT01), getProduct(PRODUCT05));
        assertNull(rule);
    }

    @Test
    public void shouldNotReturnDisablingRulesForForeignProduct()
    {
        final DisableProductBundleRuleModel rule = bundleRuleService
                .getDisableRuleForBundleProduct(getBundleTemplate(REGULAR_COMPONENT), getProduct(PRODUCT01), getProduct(PREMIUM01));
        assertNull(rule);
    }

    @Test
    public void shouldRespectComponentWhileGettingDisablingRules()
    {
        final ProductModel conditionalProduct = getProduct(PRODUCT01);
        final ProductModel targetProduct = getProduct(PRODUCT06);
        final DisableProductBundleRuleModel rule = bundleRuleService
                .getDisableRuleForBundleProduct(getBundleTemplate(OPTIONAL_COMPONENT), conditionalProduct, targetProduct);
        assertNotNull(rule);
        assertThat(rule.getConditionalProducts(), hasItem(conditionalProduct));
        assertThat(rule.getTargetProducts(), hasItem(targetProduct));
    }

    @Test
    public void gettingDisablingRulesShouldHandleNullBundleId()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Parameter targetComponent can not be null");
        bundleRuleService.getDisableRuleForBundleProduct(null, getProduct(PRODUCT01), getProduct(PRODUCT05));
    }

    @Test
    public void gettingDisablingRulesShouldHandleFirstProductOfNull()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Parameter product1 can not be null");
        bundleRuleService.getDisableRuleForBundleProduct(getBundleTemplate(OPTIONAL_COMPONENT), null, getProduct(PRODUCT05));
    }

    @Test
    public void gettingDisablingRulesShouldHandleSecondProductOfNull()
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Parameter product2 can not be null");
        bundleRuleService.getDisableRuleForBundleProduct(getBundleTemplate(OPTIONAL_COMPONENT), getProduct(PRODUCT01), null);
    }

	@Test
	public void shouldNotReturnChangePriceRuleWhenNoSuchConditionalProductsInChangePriceRule()
	{
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_1);
		final ProductModel conditionalProduct = getProduct(PRODUCT06);
		final ProductModel targetProduct = getProduct(PRODUCT02);

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(
				component, targetProduct, conditionalProduct, currency);
		assertNull(priceRule);
	}

	@Test
	public void shouldNotReturnChangePriceRuleWhenNoSuchTargetProductsInChangePriceBundleRule()
	{
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_1);
		final ProductModel conditionalProduct = getProduct(PRODUCT06);
		final ProductModel targetProduct = getProduct(PRODUCT01);

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(
				component, targetProduct, conditionalProduct, currency);
		assertNull(priceRule);
	}

	@Test
	public void shouldNotReturnChangePriceRuleWhenNoSuchCurrencyInChangePriceRule()
	{
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_2);
		final ProductModel conditionalProduct = getProduct(PREMIUM02);
		final ProductModel targetProduct = getProduct(PREMIUM01);
		final CurrencyModel jpyCurrency = commonI18NService.getCurrency("JPY");

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(
				component, targetProduct, conditionalProduct, jpyCurrency);
		assertNull(priceRule);
	}

	@Test
	public void shouldNotReturnChangePriceRuleForDifferentCurrencies()
	{
		final CurrencyModel jpyCurrency = commonI18NService.getCurrency("JPY");
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_1);
		final ProductModel conditionalProduct = getProduct(PRODUCT01);
		final ProductModel targetProduct = getProduct(PRODUCT02);

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(
				component, targetProduct, conditionalProduct, jpyCurrency);
		assertNotNull(priceRule);
		assertEquals("price__PRODUCT02_with_PRODUCT01_JPY", priceRule.getId());
		assertEquals(100, priceRule.getPrice().intValue());
		assertEquals(jpyCurrency, priceRule.getCurrency());

		priceRule = getBundleRuleService().getChangePriceBundleRule(
				component, targetProduct, conditionalProduct, currency);
		assertNotNull(priceRule);
		assertEquals("price_PRODUCT02_with_PRODUCT01", priceRule.getId());
		assertEquals(1, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());
	}

	@Test
	public void shouldReturnChangePriceRuleForProductsInOneComponent()
	{
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_1);
		final ProductModel conditionalProduct = getProduct(PRODUCT01);
		final ProductModel targetProduct = getProduct(PRODUCT02);

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(
				component, targetProduct, conditionalProduct, currency);
		assertNotNull(priceRule);
		assertEquals("price_PRODUCT02_with_PRODUCT01", priceRule.getId());
		assertEquals(1, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());
	}

	@Test
	public void shouldReturnChangePriceRuleForProductsInDifferentComponents()
	{
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_1);
		final ProductModel conditionalProduct = getProduct(PREMIUM01);
		final ProductModel targetProduct = getProduct(PRODUCT01);

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(
				component, targetProduct, conditionalProduct, currency);
		assertNotNull(priceRule);
		assertEquals("price_PRODUCT01_with_PREMIUM01", priceRule.getId());
		assertEquals(99, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());
	}

	@Test
	public void shouldNotReturnChangePriceRuleWhenRuleTypeIsAll()
	{
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_1);
		final ProductModel conditionalProduct = getProduct(PRODUCT05);
		final ProductModel targetProduct = getProduct(PRODUCT01);

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(
				component, targetProduct, conditionalProduct, currency);
		assertNull(priceRule);
	}

	@Test
	public void shouldReturnChangePriceRuleWithLowestPrice()
	{
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_2);
		final ProductModel conditionalProduct = getProduct(PREMIUM02);
		final ProductModel targetProduct = getProduct(PREMIUM01);

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(
				component, targetProduct, conditionalProduct, currency);
		assertNotNull(priceRule);
		assertEquals("price_PREMIUM01_with_PREMIUM02_cheap", priceRule.getId());
		assertEquals(99, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());
	}

	@Test
	public void shouldNotReturnChangePriceRuleByCartWhenNoSuchConditionalProductInChangePriceRule() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_1);
		final ProductModel conditionalProduct = getProduct(PRODUCT06);
		final ProductModel targetProduct = getProduct(PRODUCT02);

		List<CommerceCartModification> mods = bundleCommerceCartService.addToCart(
				cart, conditionalProduct, 1, unitModel, false, NEW_BUNDLE, component, false, "<no xml>");
		assertEquals(1, mods.size());
		assertEquals(1, cart.getEntries().size());
		final int bundleNo1 = mods.iterator().next().getEntry().getBundleNo().intValue();

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(cart,
				component, targetProduct, bundleNo1);

		assertNull(priceRule);
	}

	@Test
	public void shouldNotReturnChangePriceRuleByCartWhenNoSuchTargetProductInChangePriceRule() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_1);
		final ProductModel conditionalProduct = getProduct(PRODUCT06);
		final ProductModel targetProduct = getProduct(PRODUCT01);

		List<CommerceCartModification> mods = bundleCommerceCartService.addToCart(
				cart, conditionalProduct, 1, unitModel, false, NEW_BUNDLE, component, false, "<no xml>");
		assertEquals(1, mods.size());
		assertEquals(1, cart.getEntries().size());
		final int bundleNo1 = mods.iterator().next().getEntry().getBundleNo().intValue();

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(cart,
				component, targetProduct, bundleNo1);

		assertNull(priceRule);
	}

	@Test
	public void shouldNotReturnChangePriceRuleByCartWhenNoSuchCurrencyInChangePriceRule() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_2);
		final ProductModel conditionalProduct = getProduct(PREMIUM01);
		final ProductModel targetProduct = getProduct(PREMIUM02);

		List<CommerceCartModification> mods = bundleCommerceCartService.addToCart(
				cart, conditionalProduct, 1, unitModel, false, NEW_BUNDLE, component, false, "<no xml>");
		assertEquals(1, mods.size());
		assertEquals(1, cart.getEntries().size());
		final int bundleNo1 = mods.iterator().next().getEntry().getBundleNo().intValue();

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(cart,
				component, targetProduct, bundleNo1);

		assertNull(priceRule);
	}

	@Test
	public void shouldReturnChangePriceRuleByCartForProductsInOneComponent() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_1);
		final ProductModel conditionalProduct = getProduct(PRODUCT01);
		final ProductModel targetProduct = getProduct(PRODUCT02);

		List<CommerceCartModification> mods = bundleCommerceCartService.addToCart(
				cart, conditionalProduct, 1, unitModel, false, NEW_BUNDLE, component, false, "<no xml>");
		assertEquals(1, mods.size());
		assertEquals(1, cart.getEntries().size());

		final int bundleNo1 = mods.iterator().next().getEntry().getBundleNo().intValue();

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(cart,
				component, targetProduct, bundleNo1);

		assertNotNull(priceRule);
		assertTrue(priceRule.getTargetProducts().contains(targetProduct));
		assertTrue(priceRule.getConditionalProducts().contains(conditionalProduct));
		assertEquals("price_PRODUCT02_with_PRODUCT01", priceRule.getId());
		assertEquals(1, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());
	}

	@Test
	public void shouldReturnChangePriceRuleByCartForProductsInDifferentComponents() throws CommerceCartModificationException
	{
		final BundleTemplateModel conditionalComponent = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_2);
		final BundleTemplateModel targetComponent = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_1);
		final ProductModel conditionalProduct = getProduct(PREMIUM01);
		final ProductModel targetProduct = getProduct(PRODUCT01);

		List<CommerceCartModification> mods = bundleCommerceCartService.addToCart(
				cart, conditionalProduct, 1, unitModel, false, NEW_BUNDLE, conditionalComponent, false, "<no xml>");
		assertEquals(1, mods.size());
		assertEquals(1, cart.getEntries().size());

		final int bundleNo1 = mods.iterator().next().getEntry().getBundleNo().intValue();

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(cart,
				targetComponent, targetProduct, bundleNo1);

		assertNotNull(priceRule);
		assertTrue(priceRule.getTargetProducts().contains(targetProduct));
		assertTrue(priceRule.getConditionalProducts().contains(conditionalProduct));
		assertEquals("price_PRODUCT01_with_PREMIUM01", priceRule.getId());
		assertEquals(99, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());
	}

	@Test
	public void shouldReturnChangePriceRuleByCartWhenRuleTypeIsAll() throws CommerceCartModificationException
	{
		final BundleTemplateModel component1 = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_1);
		final BundleTemplateModel component2 = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_2);
		final ProductModel conditionalProduct1 = getProduct(PREMIUM02);
		final ProductModel conditionalProduct2 = getProduct(PRODUCT05);
		final ProductModel targetProduct = getProduct(PRODUCT01);

		List<CommerceCartModification> mods = bundleCommerceCartService.addToCart(
				cart, conditionalProduct1, 1, unitModel, false, NEW_BUNDLE, component2, false, "<no xml>");
		assertEquals(1, mods.size());
		assertEquals(1, cart.getEntries().size());

		final int bundleNo1 = mods.iterator().next().getEntry().getBundleNo().intValue();

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(cart,
				component1, targetProduct, bundleNo1);

		assertNotNull(priceRule);
		assertTrue(priceRule.getTargetProducts().contains(targetProduct));
		assertTrue(priceRule.getConditionalProducts().contains(conditionalProduct1));
		assertEquals("price_PRODUCT01_with_PREMIUM02", priceRule.getId());
		assertEquals(5, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());

		mods = bundleCommerceCartService.addToCart(
				cart, conditionalProduct2, 1, unitModel, false, bundleNo1, component1, false, "<no xml>");
		assertEquals(1, mods.size());
		assertEquals(2, cart.getEntries().size());

		priceRule = getBundleRuleService().getChangePriceBundleRule(cart,
				component1, targetProduct, bundleNo1);

		assertNotNull(priceRule);
		assertTrue(priceRule.getTargetProducts().contains(targetProduct));
		assertTrue(priceRule.getConditionalProducts().contains(conditionalProduct1));
		assertTrue(priceRule.getConditionalProducts().contains(conditionalProduct2));
		assertEquals("price_PRODUCT01_with_PREMIUM01_and_PRODUCT05", priceRule.getId());
		assertEquals(1, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());
	}

	@Test
	public void shouldReturnChangePriceRuleWithLowestPriceByCart() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_2);
		final ProductModel conditionalProduct = getProduct(PREMIUM02);
		final ProductModel targetProduct = getProduct(PREMIUM01);

		List<CommerceCartModification> mods = bundleCommerceCartService.addToCart(
				cart, conditionalProduct, 1, unitModel, false, NEW_BUNDLE, component, false, "<no xml>");
		assertEquals(1, mods.size());
		assertEquals(1, cart.getEntries().size());

		final int bundleNo1 = mods.iterator().next().getEntry().getBundleNo().intValue();

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(cart,
				component, targetProduct, bundleNo1);

		assertNotNull(priceRule);
		assertTrue(priceRule.getTargetProducts().contains(targetProduct));
		assertTrue(priceRule.getConditionalProducts().contains(conditionalProduct));
		assertEquals("price_PREMIUM01_with_PREMIUM02_cheap", priceRule.getId());
		assertEquals(99, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());
	}

	@Test
	public void shouldReturnChangePriceRuleWithLowestPriceByOrderEntry() throws CommerceCartModificationException
	{
		final BundleTemplateModel component = getBundleTemplate(LEAF_BUNDLE_TEMPLATE_2);
		final ProductModel conditionalProduct = getProduct(PREMIUM02);
		final ProductModel targetProduct = getProduct(PREMIUM01);

		List<CommerceCartModification> mods = bundleCommerceCartService.addToCart(
				cart, conditionalProduct, 1, unitModel, false, NEW_BUNDLE, component, false, "<no xml>");
		assertEquals(1, mods.size());
		assertEquals(1, cart.getEntries().size());

		final int bundleNo1 = mods.iterator().next().getEntry().getBundleNo().intValue();

		mods = bundleCommerceCartService.addToCart(
				cart, targetProduct, 1, unitModel, false, bundleNo1, component, false, "<no xml>");
		assertEquals(1, mods.size());
		assertEquals(2, cart.getEntries().size());

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(cart
				.getEntries().get(1));

		assertNotNull(priceRule);
		assertTrue(priceRule.getTargetProducts().contains(targetProduct));
		assertTrue(priceRule.getConditionalProducts().contains(conditionalProduct));
		assertEquals("price_PREMIUM01_with_PREMIUM02_cheap", priceRule.getId());
		assertEquals(99, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());
	}

	@Test
	public void shouldNotReturnChangePriceRuleWithLowestPriceForForeignProduct() throws CommerceCartModificationException
	{
		final ProductModel targetProduct = getProduct(PREMIUM02);

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService()
				.getChangePriceBundleRuleWithLowestPrice(targetProduct, currency);

		assertNull(priceRule);
	}

	@Test
	public void shouldNotReturnChangePriceRuleWithLowestPriceForForeignCurrency() throws CommerceCartModificationException
	{
		final CurrencyModel jpyCurrency = commonI18NService.getCurrency("JPY");
		final ProductModel targetProduct = getProduct(PRODUCT01);

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService()
				.getChangePriceBundleRuleWithLowestPrice(targetProduct, jpyCurrency);

		assertNull(priceRule);
	}

	@Test
	public void shouldNotReturnChangePriceRuleWithLowestPriceIfRuleTypeAll() throws CommerceCartModificationException
	{
		final ProductModel targetProduct = getProduct(PRODUCT06);

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService()
				.getChangePriceBundleRuleWithLowestPrice(targetProduct, currency);

		assertNotNull(priceRule);
		assertTrue(priceRule.getTargetProducts().contains(targetProduct));
		assertEquals("price_PRODUCT02_or_PRODUCT06_with_PREMIUM01_and_PRODUCT05", priceRule.getId());
		assertEquals(400, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());
	}

	@Test
	public void shouldReturnChangePriceRuleWithLowestPriceForCorrespondingProduct() throws CommerceCartModificationException
	{
		final CurrencyModel jpyCurrency = commonI18NService.getCurrency("JPY");
		final ProductModel targetProduct = getProduct(PRODUCT02);

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService()
				.getChangePriceBundleRuleWithLowestPrice(targetProduct, currency);

		assertNotNull(priceRule);
		assertTrue(priceRule.getTargetProducts().contains(targetProduct));
		assertEquals("price_PRODUCT02_with_PRODUCT01", priceRule.getId());
		assertEquals(1, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());

		priceRule = getBundleRuleService()
				.getChangePriceBundleRuleWithLowestPrice(targetProduct, jpyCurrency);

		assertNotNull(priceRule);
		assertTrue(priceRule.getTargetProducts().contains(targetProduct));
		assertEquals("price__PRODUCT02_with_PRODUCT01_JPY", priceRule.getId());
		assertEquals(100, priceRule.getPrice().intValue());
		assertEquals(jpyCurrency, priceRule.getCurrency());
	}

	protected BundleRuleService getBundleRuleService()
	{
		return bundleRuleService;
	}

	public void setBundleRuleService(final BundleRuleService bundleRuleService)
	{
		this.bundleRuleService = bundleRuleService;
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
				cart, getProduct(productCode), 1, unitModel, false, NO_BUNDLE, null, false);
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
