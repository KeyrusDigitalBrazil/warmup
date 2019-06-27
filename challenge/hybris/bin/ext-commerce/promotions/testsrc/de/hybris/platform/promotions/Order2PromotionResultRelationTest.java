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
package de.hybris.platform.promotions;

import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.jalo.CachingStrategy;
import de.hybris.platform.promotions.jalo.GeneratedPromotionResult;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.model.AbstractPromotionActionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;


/**
 * Tests the relation between {@link AbstractOrderModel} and {@link PromotionResultModel}.
 */
@IntegrationTest
public class Order2PromotionResultRelationTest extends AbstractPromotionServiceTest
{
	private static final Logger LOG = LoggerFactory.getLogger(Order2PromotionResultRelationTest.class);
	private static final String PROMOTIONS_CACHING_STRATEGY_BEAN_ID = "promotionsCachingStrategy";

	private ProductModel product;
	private CartModel cart;

	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private ProductService productService;
	@Resource
	private UserService userService;
	@Resource
	private CartFactory cartFactory;
	@Resource
	private CartService cartService;
	@Resource
	private OrderService orderService;
	@Resource
	private CalculationService calculationService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ModelService modelService;
	@Resource
	private PromotionsService defaultPromotionsService;
	@Resource
	private FlexibleSearchService flexibleSearchService;

	private boolean isCacheActivated = false;
	private CachingStrategy cachingStrategy;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();

		final CatalogVersionModel version = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(version);
		product = productService.getProductForCode(version, "HW1210-3411");
		final UserModel user = userService.getUserForUID("demo");
		userService.setCurrentUser(user);
		final CurrencyModel currency = commonI18NService.getCurrency("EUR");
		commonI18NService.setCurrentCurrency(currency);
		cart = cartFactory.createCart();

		try
		{
			cachingStrategy = (CachingStrategy) Registry.getApplicationContext().getBean(PROMOTIONS_CACHING_STRATEGY_BEAN_ID);
			isCacheActivated = true;
		}
		catch (final NoSuchBeanDefinitionException e)
		{
			LOG.debug("No bean found with name [{}]: {}. Caching will be disabled", PROMOTIONS_CACHING_STRATEGY_BEAN_ID, e);
			isCacheActivated = false;
		}
	}

	/**
	 * Tests the relation between {@link AbstractOrderModel} and {@link PromotionResultModel}, and between
	 * {@link PromotionResultModel} and {@link AbstractPromotionActionModel}. See PRO-66 for more details. If
	 * promotionsCachingStrategy bean is activated then instead getting promotionResults from db, data is obtained from
	 * cache.
	 * <ul>
	 * <li>adds a product to cart, adds a product promotion, and update promotions for the cart,</li>
	 * <li>tests both table sizes before and after the updatePromotion method call,</li>
	 * <li>adds an order promotion, and update promotion for the cart again,</li>
	 * <li>tests both table sizes again,</li>
	 * <li>creates an order from cart without updatePromotion method call, and tests both table sizes,</li>
	 * <li>removes the cart, and tests both table sizes,</li>
	 * <li>updates promotion for the order, and tests its table size,</li>
	 * <li>removes the order, and tests both table sizes.</li>
	 * </ul>
	 */
	@Test
	public void testOrderPromotionResultRelationTest() throws CalculationException
	{
		cartService.addNewEntry(cart, product, 1, product.getUnit());
		modelService.save(cart);
		calculationService.calculate(cart);

		//there is only a cart, no promotion result or action
		testResultsSize(0);
		testActionsSize(0);

		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		promotionGroups.add(defaultPromotionsService.getPromotionGroup("prGroup3"));
		defaultPromotionsService.updatePromotions(promotionGroups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());

		//one product promotion applied, one potential(higher priority), and one action for the applied one
		testResultsSize(2);
		testActionsSize(1);

		final PromotionGroupModel promotionGroup = defaultPromotionsService.getPromotionGroup("prGroup5");
		promotionGroups.add(promotionGroup);
		defaultPromotionsService.updatePromotions(promotionGroups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		//one more order promotion applied, so one more action than last time
		testResultsSize(3);
		testActionsSize(2);

		final PromotionGroupModel promotionGroup2 = defaultPromotionsService.getPromotionGroup("prGroup2");
		promotionGroups.add(promotionGroup2);
		defaultPromotionsService.updatePromotions(promotionGroups, cart, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		//new situation: one product promotion and one order promotion applied, and each has an action
		//the potential promotion result disappears since its has a lower priority this time
		testResultsSize(2);
		testActionsSize(2);

		OrderModel order = null;
		try
		{
			order = orderService.createOrderFromCart(cart);
			modelService.save(order);

			//if isCacheActivated true then order is copied, if false PromotionResults for cart is 0 and order.clone will not copy that.
			final int expectedSize = isCacheActivated ? 2 : 4;
			testResultsSize(expectedSize);
			testActionsSize(expectedSize);
		}
		catch (final InvalidCartException ice)
		{
			LOG.info(ice.getMessage(), ice);
		}

		modelService.remove(cart);

		//after cart is removed, those promotion results and actions should also be removed
		testResultsSize(2);
		testActionsSize(isCacheActivated ? 0 : 2);
		defaultPromotionsService.updatePromotions(promotionGroups, order, false, AutoApplyMode.APPLY_ALL, AutoApplyMode.APPLY_ALL,
				new Date());
		//update promotion for order, and this behavior should not affect the table size, since no more promotion group is added
		testResultsSize(2);
		testActionsSize(isCacheActivated ? 0 : 2);

		modelService.remove(order);
		//after order is removed, all promotion result and actions should be removed
		testResultsSize(isCacheActivated ? 2 : 0);
		testActionsSize(0);
	}

	private void testResultsSize(final int promotionResultSize)
	{
		int resultsSize = 0;
		if (isCacheActivated)
		{
			final List<PromotionResult> promotionResults = cachingStrategy.get(cart.getCode());
			resultsSize = isNull(promotionResults) ? 0 : promotionResults.size();
		}
		else
		{
			resultsSize = getTableSize(PromotionResultModel._TYPECODE);
		}
		assertEquals("wrong promotion result size", promotionResultSize, resultsSize);
	}

	private void testActionsSize(final int promotionActionSize)
	{
		int actionsSize = 0;
		if (isCacheActivated)
		{
			final List<PromotionResult> promotionResults = cachingStrategy.get(cart.getCode());
			if (promotionResults != null)
			{
				actionsSize = promotionResults.stream().map(GeneratedPromotionResult::getActions) //
						.filter(CollectionUtils::isNotEmpty) //
						.map(Collection::size) //
						.reduce(Integer.valueOf(0), (a, b) -> Integer.valueOf(a.intValue() + b.intValue())).intValue();
			}
		}
		else
		{
			actionsSize = getActionTableSize();
		}
		assertEquals("wrong promotion action size", promotionActionSize, actionsSize);
	}

	private int getTableSize(final String itemTypeCode)
	{
		final String query = "select count(*) from {" + itemTypeCode + "}";
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		final List<Class> resultClassList = new ArrayList<Class>();
		resultClassList.add(Integer.class);
		searchQuery.setResultClassList(resultClassList);
		final Integer totalSize = (Integer) flexibleSearchService.search(searchQuery).getResult().iterator().next();
		return totalSize.intValue();
	}

	private int getActionTableSize()
	{
		final String query = "select {pk} from {" + AbstractPromotionActionModel._TYPECODE + "}";
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);

		final List result = flexibleSearchService.search(searchQuery).getResult();
		return result.size();
	}

}
