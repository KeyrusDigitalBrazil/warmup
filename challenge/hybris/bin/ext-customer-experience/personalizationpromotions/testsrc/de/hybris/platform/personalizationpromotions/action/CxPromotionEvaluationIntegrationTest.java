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
package de.hybris.platform.personalizationpromotions.action;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.ruleengineservices.RuleEngineServiceException;
import de.hybris.platform.ruleengineservices.maintenance.RuleCompilerPublisherResult;
import de.hybris.platform.ruleengineservices.maintenance.RuleCompilerPublisherResult.Result;
import de.hybris.platform.ruleengineservices.maintenance.RuleMaintenanceService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.stock.StockService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class CxPromotionEvaluationIntegrationTest extends ServicelayerTest
{
	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private UnitService unitService;

	@Resource
	private ProductService productService;

	@Resource
	private CommerceCartService commerceCartService;

	@Resource
	private CommerceCheckoutService commerceCheckoutService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private CartService cartService;

	@Resource
	private CxService cxService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private RuleMaintenanceService ruleMaintenanceService;

	@Resource
	private ModelService modelService;

	@Resource
	private StockService stockService;

	private CatalogVersionModel catalogVersionModel;
	private ProductModel product;
	private UnitModel unit;
	private CommerceCartParameter parameter;


	@Before
	public void setupCoreCxData() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importData(new ClasspathImpExResource("/personalizationservices/test/testdata_personalizationservices.impex", "UTF-8"));
		importData(new ClasspathImpExResource("/personalizationservices/test/testdata_cxsite.impex", "UTF-8"));
		importData(new ClasspathImpExResource("/personalizationpromotions/test/testdata_personalizationpromotions.impex", "UTF-8"));

		publishPromotions("promotionRule2");


		catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		product = productService.getProductForCode(catalogVersionModel, "HW1210-3423");
		unit = unitService.getUnitForCode("pieces");

		baseSiteService.setCurrentBaseSite("testSite", false);

		parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartService.getSessionCart());
		parameter.setProduct(product);
		parameter.setQuantity(1);
		parameter.setUnit(unit);
		parameter.setCreateNewEntry(false);

		stockService.setInStockStatus(product, baseSiteService.getCurrentBaseSite().getStores().get(0).getWarehouses(),
				InStockStatus.FORCEINSTOCK);
	}

	protected void publishPromotions(final String... codes) throws RuleEngineServiceException
	{
		final List<PromotionSourceRuleModel> rules = new ArrayList<>();

		for (final String code : codes)
		{
			rules.add(getPromotionRule(code));
		}
		final RuleCompilerPublisherResult result = ruleMaintenanceService.compileAndPublishRules(rules, "promotions-module", false);

		if (result.getResult() != Result.SUCCESS)
		{
			throw new IllegalArgumentException();
		}
	}

	protected PromotionSourceRuleModel getPromotionRule(final String code)
	{
		final PromotionSourceRuleModel sample = new PromotionSourceRuleModel();
		sample.setCode(code);
		final PromotionSourceRuleModel model = flexibleSearchService.getModelByExample(sample);
		modelService.refresh(model);
		return model;
	}

	@Test
	public void testNoPromotionForCart() throws Exception
	{
		//given
		final UserModel user = userService.getUserForUID("defaultcxcustomer");
		userService.setCurrentUser(user);

		cxService.calculateAndStorePersonalization(user, catalogVersionModel);
		cxService.loadPersonalizationInSession(user, Collections.singleton(catalogVersionModel));

		final CartModel cart = cartService.getSessionCart();

		//when
		commerceCartService.addToCart(parameter);

		//then
		Assert.assertEquals(0d, cart.getTotalDiscounts().doubleValue(), 0.001D);
		Assert.assertEquals(Boolean.TRUE, cart.getCalculated());
	}

	@Test
	public void testNoPromotionForCheckout() throws Exception
	{
		//given
		final UserModel user = userService.getUserForUID("defaultcxcustomer");
		userService.setCurrentUser(user);
		cxService.calculateAndStorePersonalization(user, catalogVersionModel);

		final CartModel cart = cartService.getSessionCart();
		commerceCartService.addToCart(parameter);
		cart.setCalculated(Boolean.FALSE);
		final CommerceCheckoutParameter checkoutParameter = new CommerceCheckoutParameter();
		checkoutParameter.setEnableHooks(true);
		checkoutParameter.setCart(cart);

		//when
		commerceCheckoutService.calculateCart(checkoutParameter);

		//then
		Assert.assertEquals(0d, cart.getTotalDiscounts().doubleValue(), 0.001D);
		Assert.assertEquals(Boolean.TRUE, cart.getCalculated());
	}

	@Test
	public void testCxPromotionForCart() throws Exception
	{
		//given
		final CartModel cart = cartService.getSessionCart();
		final UserModel user = userService.getUserForUID("customer1@hybris.com");
		userService.setCurrentUser(user);

		cxService.calculateAndStorePersonalization(user, catalogVersionModel);
		cxService.loadPersonalizationInSession(user, Collections.singleton(catalogVersionModel));

		//when
		commerceCartService.addToCart(parameter);

		Assert.assertEquals(6.795d, cart.getTotalDiscounts().doubleValue(), 0.01d);
		Assert.assertEquals(Boolean.TRUE, cart.getCalculated());
	}

	@Test
	public void testCxPromotionForCheckout() throws Exception
	{
		//given
		final CartModel cart = cartService.getSessionCart();

		final UserModel user = userService.getUserForUID("customer1@hybris.com");
		userService.setCurrentUser(user);

		cxService.calculateAndStorePersonalization(user, catalogVersionModel);
		cxService.loadPersonalizationInSession(user, Collections.singleton(catalogVersionModel));

		commerceCartService.addToCart(parameter);
		cart.setCalculated(Boolean.FALSE);
		final CommerceCheckoutParameter checkoutParameter = new CommerceCheckoutParameter();
		checkoutParameter.setEnableHooks(true);
		checkoutParameter.setCart(cart);

		//when
		commerceCheckoutService.calculateCart(checkoutParameter);

		//then
		Assert.assertEquals(6.795d, cart.getTotalDiscounts().doubleValue(), 0.01d);
		Assert.assertEquals(Boolean.TRUE, cart.getCalculated());
	}

}
