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
package de.hybris.platform.promotionengineservices.action.strategies;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.droolsruleengineservices.compiler.impl.DefaultDroolsRuleActionContext;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.CategoryRAO;
import de.hybris.platform.ruleengineservices.rao.DisplayMessageRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.ProductRAO;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.ActionSupplementStrategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class QualifyingCategoryPotentialPromotionMessageActionSupplementStrategyUnitTest
{
	private static final String UUID = "234234-dfged-23423sdfr23-sdfwer23-edrwe";

	private static final Integer PRODUCT_QUANTITY_PARAM = Integer.valueOf(3);

	private static final String PRODUCT1_CODE = "product1";
	private static final String PRODUCT2_CODE = "product2";
	private static final String CATEGORY1_CODE = "cat1";
	private static final String CATEGORY2_CODE = "cat2";

	private final ActionSupplementStrategy strategy = new QualifyingCategoryPotentialPromotionMessageActionSupplementStrategy();

	@Mock
	private DisplayMessageRAO displayMessageRAO;

	private RuleActionContext context;

	@Mock
	private CartRAO cartRao;

	@Mock
	private CategoryRAO category1;

	@Mock
	private CategoryRAO category2;

	Map<String, Object> srcParameters = new HashMap<>();

	private OrderEntryRAO orderEntry1;
	private OrderEntryRAO orderEntry2;
	private ProductRAO product1;
	private ProductRAO product2;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		context = new DefaultDroolsRuleActionContext(new HashMap<String, Object>()
		{
			{
				put(CartRAO.class.getName(), cartRao);
			}
		}, null);
		context.setParameters(srcParameters);

		when(category1.getCode()).thenReturn(CATEGORY1_CODE);
		when(category2.getCode()).thenReturn(CATEGORY2_CODE);
		product1 = new ProductRAO();
		product1.setCategories(new HashSet<CategoryRAO>()
		{
			{
				add(category1);
				add(category2);
			}
		});
		product1.setCode(PRODUCT1_CODE);

		product2 = new ProductRAO();
		product2.setCategories(new HashSet<CategoryRAO>()
		{
			{
				add(category2);
			}
		});
		product2.setCode(PRODUCT2_CODE);

		orderEntry1 = new OrderEntryRAO();
		orderEntry1.setQuantity(1);
		orderEntry1.setProduct(product1);
		orderEntry1.setEntryNumber(Integer.valueOf(1));
		orderEntry2 = new OrderEntryRAO();
		orderEntry2.setQuantity(1);
		orderEntry2.setProduct(product2);
		orderEntry2.setEntryNumber(Integer.valueOf(2));
		when(cartRao.getEntries()).thenReturn(new HashSet<OrderEntryRAO>()
		{
			{
				add(orderEntry1);
				add(orderEntry2);
			}
		});

		srcParameters.put(
				QualifyingCategoryPotentialPromotionMessageActionSupplementStrategy.CATEGORIZIED_PRODUCTS_QUANTITY_PARAMETER,
				PRODUCT_QUANTITY_PARAM);
		srcParameters.put(
				QualifyingCategoryPotentialPromotionMessageActionSupplementStrategy.CATEGORIZIED_PRODUCTS_QUANTITY_PARAMETER_UUID,
				UUID);
		srcParameters.put(QualifyingCategoryPotentialPromotionMessageActionSupplementStrategy.CATEGORIES_PARAMETER,
				Arrays.asList(CATEGORY2_CODE));
		srcParameters.put(QualifyingCategoryPotentialPromotionMessageActionSupplementStrategy.CATEGORIES_PARAMETER_UUID, UUID);
	}

	@Test
	public void testPostProcessActionForQualifiedCategoryQuantity()
	{
		final HashMap<String, Object> targetParams = new HashMap<String, Object>();
		when(displayMessageRAO.getParameters()).thenReturn(targetParams);
		strategy.postProcessAction(displayMessageRAO, context);
		// both order entries has the same category CATEGORY2_CODE
		Assert.assertEquals(PRODUCT_QUANTITY_PARAM.intValue() - (orderEntry1.getQuantity() + orderEntry2.getQuantity()),
				((Integer) targetParams.get(UUID)).intValue());
	}
}
