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
package de.hybris.platform.ruleengineservices.rao.providers.impl;

import com.google.common.collect.ImmutableSet;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.ruleengineservices.calculation.RuleEngineCalculationService;
import de.hybris.platform.ruleengineservices.converters.populator.CartRaoPopulator;
import de.hybris.platform.ruleengineservices.converters.populator.CategoryRaoPopulator;
import de.hybris.platform.ruleengineservices.converters.populator.DiscountValueRaoPopulator;
import de.hybris.platform.ruleengineservices.converters.populator.OrderEntryRaoPopulator;
import de.hybris.platform.ruleengineservices.converters.populator.PaymentModeRaoPopulator;
import de.hybris.platform.ruleengineservices.converters.populator.ProductRaoPopulator;
import de.hybris.platform.ruleengineservices.converters.populator.UserGroupRaoPopulator;
import de.hybris.platform.ruleengineservices.converters.populator.UserRaoPopulator;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.CategoryRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountValueRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.PaymentModeRAO;
import de.hybris.platform.ruleengineservices.rao.ProductRAO;
import de.hybris.platform.ruleengineservices.rao.UserGroupRAO;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.ruleengineservices.ruleengine.impl.CartTestContextBuilder;
import de.hybris.platform.ruleengineservices.util.ProductUtils;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.hybris.platform.ruleengineservices.rao.providers.impl.DefaultCartRAOProvider.EXPAND_CATEGORIES;
import static de.hybris.platform.ruleengineservices.rao.providers.impl.DefaultCartRAOProvider.EXPAND_DISCOUNTS;
import static de.hybris.platform.ruleengineservices.rao.providers.impl.DefaultCartRAOProvider.EXPAND_ENTRIES;
import static de.hybris.platform.ruleengineservices.rao.providers.impl.DefaultCartRAOProvider.EXPAND_PAYMENT_MODE;
import static de.hybris.platform.ruleengineservices.rao.providers.impl.DefaultCartRAOProvider.EXPAND_PRODUCTS;
import static de.hybris.platform.ruleengineservices.rao.providers.impl.DefaultCartRAOProvider.EXPAND_USERS;
import static de.hybris.platform.ruleengineservices.util.TestUtil.createNewConverter;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


@UnitTest
public class DefaultCartRAOProviderUnitTest
{
	private final static String PAYMENT_MODE_CODE = "paymentCode";
	private DefaultCartRAOProvider cartRAOProvider;
	private CartTestContextBuilder cartContext;
	private CartRaoPopulator cartRaoPopulator;

	private final UserService userService = mock(UserService.class);

	@Before
	public void setUp() throws Exception
	{
		cartContext = createNewCartTestContextBuilder();

		cartRaoPopulator = new CartRaoPopulator();
		cartRaoPopulator.setPaymentModeConverter(createNewConverter(PaymentModeRAO.class, new PaymentModeRaoPopulator()));
		cartRaoPopulator.setDiscountConverter(createNewConverter(DiscountValueRAO.class, new DiscountValueRaoPopulator()));
		final OrderEntryRaoPopulator orderEntryPopulator = new OrderEntryRaoPopulator();
		final ProductRaoPopulator productRaoPopulator = new ProductRaoPopulator();
		productRaoPopulator.setProductUtils(mock(ProductUtils.class));
		productRaoPopulator.setCategoryConverter(createNewConverter(CategoryRAO.class, new CategoryRaoPopulator()));
		productRaoPopulator.setCategoryService(mock(CategoryService.class));
		orderEntryPopulator.setProductConverter(createNewConverter(ProductRAO.class, productRaoPopulator));
		cartRaoPopulator.setEntryConverter(createNewConverter(OrderEntryRAO.class, orderEntryPopulator));
		final UserRaoPopulator userRaoPopulator = new UserRaoPopulator();
		userRaoPopulator.setUserService(userService);
		userRaoPopulator.setUserGroupConverter(createNewConverter(UserGroupRAO.class, new UserGroupRaoPopulator()));
		cartRaoPopulator.setUserConverter(createNewConverter(UserRAO.class, userRaoPopulator));

		final AbstractPopulatingConverter<AbstractOrderModel, CartRAO> cartRaoConverter = new AbstractPopulatingConverter<>();
		cartRaoConverter.setTargetClass(CartRAO.class);
		cartRaoConverter.setPopulators(getCartPopulators());

		final RuleEngineCalculationService ruleEngineCalculationService = mock(RuleEngineCalculationService.class);

		cartRAOProvider = createNewCartRaoProvider();
		cartRAOProvider.setCartRaoConverter(cartRaoConverter);
		cartRAOProvider.setRuleEngineCalculationService(ruleEngineCalculationService);

		cartRAOProvider = spy(cartRAOProvider);
	}

	@Test
	public void testCreateRAO()
	{
		cartContext = cartContext.withPaymentModeModel(PAYMENT_MODE_CODE).withPaymentModeRAO(PAYMENT_MODE_CODE);

		final CartRAO cartRao = cartRAOProvider.createRAO(cartContext.getCartModel());
		assertThat(cartRao).isNotNull();
		assertThat(cartRao.getPaymentMode()).isEqualTo(cartContext.getPaymentModeRAO());
	}

	@Test
	public void testExpandRAOpaymentMode()
	{
		cartContext = cartContext.withPaymentModeModel(PAYMENT_MODE_CODE).withPaymentModeRAO(PAYMENT_MODE_CODE);

		final Set<Object> raoObjects = cartRAOProvider.expandRAO(cartContext.getCartRAO(), singletonList(EXPAND_PAYMENT_MODE));

		assertThat(raoObjects).isNotEmpty().containsOnly(cartContext.getPaymentModeRAO());
	}

	@Test
	public void testExpandFactModelWithCartModel()
	{
		cartContext = cartContext.withPaymentModeModel(PAYMENT_MODE_CODE).withPaymentModeRAO(PAYMENT_MODE_CODE);

		final Set facts = cartRAOProvider.expandFactModel(cartContext.getCartModel());
		verify(cartRAOProvider, Mockito.times(1)).createRAO(any(CartModel.class));
		final CartRAO cartRAO = cartContext.getCartRAO();
		cartRAO.setCode(cartContext.getCartModel().getCode());
		assertThat(facts).isNotEmpty().containsOnly(cartRAO, cartContext.getPaymentModeRAO());
	}

	@Test
	public void testExpandFactModelOnlyPaymentMode()
	{
		cartContext = cartContext.withPaymentModeModel(PAYMENT_MODE_CODE).withPaymentModeRAO(PAYMENT_MODE_CODE);

		final Set facts = cartRAOProvider.expandFactModel(cartContext.getCartModel(), singletonList(EXPAND_PAYMENT_MODE));
		verify(cartRAOProvider, Mockito.times(1)).createRAO(any(CartModel.class));
		final CartRAO cartRAO = cartContext.getCartRAO();
		cartRAO.setCode(cartContext.getCartModel().getCode());
		assertThat(facts).isNotEmpty().containsOnly(cartRAO, cartContext.getPaymentModeRAO());
	}

	@Test
	public void testExpandFactModelDiscounts()
	{
		final DiscountModel discount = new DiscountModel();
		cartContext = cartContext.withDiscounts(singletonList(discount));

		final Set facts = cartRAOProvider.expandFactModel(cartContext.getCartModel(), singletonList(EXPAND_DISCOUNTS));
		verify(cartRAOProvider, Mockito.times(1)).createRAO(any(CartModel.class));
		final CartRAO cartRAO = cartContext.getCartRAO();
		cartRAO.setCode(cartContext.getCartModel().getCode());
		assertThat(facts).isNotEmpty().contains(cartRAO);
		final List discountsList = (List) facts.stream().filter(f -> f instanceof DiscountValueRAO).collect(Collectors.toList());
		assertThat(discountsList).hasSize(1);
	}

	@Test
	public void testExpandFactModelInvalidOption()
	{
		cartContext = cartContext.withDiscounts(singletonList(new DiscountModel()));

		final Set facts = cartRAOProvider.expandFactModel(cartContext.getCartModel(), singletonList("NON_VALID_OPTION"));
		verify(cartRAOProvider, Mockito.times(1)).createRAO(any(CartModel.class));
		final CartRAO cartRAO = cartContext.getCartRAO();
		cartRAO.setCode(cartContext.getCartModel().getCode());
		assertThat(facts).isNotEmpty().containsOnly(cartRAO);
	}

	@Test
	public void testExpandFactModelExpandEntries()
	{
		cartContext = cartContext.addEntry(new AbstractOrderEntryModel());

		final Set facts = cartRAOProvider.expandFactModel(cartContext.getCartModel(), singletonList(EXPAND_ENTRIES));
		verify(cartRAOProvider, Mockito.times(1)).createRAO(any(CartModel.class));
		final CartRAO cartRAO = cartContext.getCartRAO();
		cartRAO.setCode(cartContext.getCartModel().getCode());
		assertThat(facts).isNotEmpty().contains(cartRAO);
		final List entriesList = (List) facts.stream().filter(f -> f instanceof OrderEntryRAO).collect(Collectors.toList());
		assertThat(entriesList).hasSize(1);
	}

	@Test
	public void testExpandFactModelExpandProducts()
	{
		cartContext = cartContext.addNewEntry(new ProductModel());

		final Set facts = cartRAOProvider.expandFactModel(cartContext.getCartModel(), singletonList(EXPAND_PRODUCTS));
		verify(cartRAOProvider, Mockito.times(1)).createRAO(any(CartModel.class));
		final CartRAO cartRAO = cartContext.getCartRAO();
		cartRAO.setCode(cartContext.getCartModel().getCode());
		assertThat(facts).isNotEmpty().contains(cartRAO);
		final List productsList = (List) facts.stream().filter(f -> f instanceof ProductRAO).collect(Collectors.toList());
		assertThat(productsList).hasSize(1);
	}

	@Test
	public void testExpandFactModelExpandCategories()
	{
		cartContext = cartContext.addNewEntry(new CategoryModel());

		final Set facts = cartRAOProvider.expandFactModel(cartContext.getCartModel(), singletonList(EXPAND_CATEGORIES));
		verify(cartRAOProvider, Mockito.times(1)).createRAO(any(CartModel.class));
		final CartRAO cartRAO = cartContext.getCartRAO();
		cartRAO.setCode(cartContext.getCartModel().getCode());
		assertThat(facts).isNotEmpty().contains(cartRAO);
		final List categoryList = (List) facts.stream().filter(f -> f instanceof CategoryRAO).collect(Collectors.toList());
		assertThat(categoryList).hasSize(1);
	}

	@Test
	public void testExpandFactModelExpandUsers()
	{
		cartContext = cartContext.withUser("testUser");

		final Set facts = cartRAOProvider.expandFactModel(cartContext.getCartModel(), singletonList(EXPAND_USERS));
		verify(cartRAOProvider, Mockito.times(1)).createRAO(any(CartModel.class));
		final UserRAO userRAO = new UserRAO();
		userRAO.setId("testUser");
		userRAO.setPk(cartContext.getCartModel().getUser().getPk().getLongValueAsString());
		final CartRAO cartRAO = cartContext.getCartRAO();
		cartRAO.setCode(cartContext.getCartModel().getCode());
		assertThat(facts).isNotEmpty().containsOnly(cartRAO, userRAO);
	}

	@Test
	public void testExpandFactModelExpandUsersWithGroups()
	{
		cartContext = cartContext.withUserGroups("testUser", new PrincipalGroupModel());
		final UserGroupModel userGroupModel = new UserGroupModel();
		Mockito.when(userService.getAllUserGroupsForUser(cartContext.getCartModel().getUser())).thenReturn(
				ImmutableSet.of(userGroupModel));

		final Set facts = cartRAOProvider.expandFactModel(cartContext.getCartModel(), singletonList(EXPAND_USERS));
		verify(cartRAOProvider, Mockito.times(1)).createRAO(any(CartModel.class));
		final UserRAO userRAO = new UserRAO();
		userRAO.setId("testUser");
		userRAO.setPk(cartContext.getCartModel().getUser().getPk().getLongValueAsString());
		final UserGroupRAO userGroupRAO = new UserGroupRAO();
		final CartRAO cartRAO = cartContext.getCartRAO();
		cartRAO.setCode(cartContext.getCartModel().getCode());
		assertThat(facts).isNotEmpty().containsOnly(cartRAO, userRAO, userGroupRAO);
	}

	protected CartTestContextBuilder getCartTestContextBuilder()
	{
		return cartContext;
	}

	protected DefaultCartRAOProvider getCartRAOProvider()
	{
		return cartRAOProvider;
	}

	protected CartTestContextBuilder createNewCartTestContextBuilder()
	{
		return new CartTestContextBuilder();
	}

	protected DefaultCartRAOProvider createNewCartRaoProvider()
	{
		return new DefaultCartRAOProvider();
	}

	protected List<Populator<AbstractOrderModel, CartRAO>> getCartPopulators()
	{
		return singletonList(cartRaoPopulator);
	}
}
