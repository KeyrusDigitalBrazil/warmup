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
package de.hybris.platform.ruleengineservices.converters.populator;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.CategoryRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountValueRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Test for {@link CartRaoPopulator} and dependent populators
 */
@IntegrationTest
public class CartRaoPopulatorTest extends ServicelayerTest
{
	@Resource
	private CartRaoPopulator cartRaoPopulator;

	@Test
	public void testCartRaoPopulator()
	{
		final CartModel cartModel = CartModelBuilder.newCart("cartCode").setCurrency("isoCode").setTotalPrice(123.3d)
				.setDelivery("deliveryModeCode", 3.3d).addProduct("product1", 3, 2.33, 2, "cat1", "cat2").addDiscount("USD", 1.2)
				.addDiscount("USD", 2.5).setUser("testUser", "testUserGroup1").getModel();
		final CartRAO cartRao = new CartRAO();

		cartRaoPopulator.populate(cartModel, cartRao);

		assertEquals(cartModel.getCode(), cartRao.getCode());
		assertEquals(cartModel.getCurrency().getIsocode(), cartRao.getCurrencyIsoCode());
		assertEquals(cartModel.getTotalPrice().doubleValue(), cartRao.getTotal().doubleValue(), 0.001);
		assertEquals(cartModel.getTotalPrice().doubleValue(), cartRao.getOriginalTotal().doubleValue(), 0.001);
		assertEquals(cartModel.getEntries().size(), cartRao.getEntries().size());
		assertEquals(cartModel.getUser().getUid(), cartRao.getUser().getId());
		assertEquals(cartModel.getUser().getPk().getLongValueAsString(), cartRao.getUser().getPk());

		for (final AbstractOrderEntryModel entryModel : cartModel.getEntries())
		{
			boolean foundEntry = false;
			for (final OrderEntryRAO entryRao : cartRao.getEntries())
			{
				if (entryModel.getProduct().getCode().equals(entryRao.getProduct().getCode())
						&& entryModel.getQuantity().intValue() == entryRao.getQuantity())
				{
					assertEquals(entryModel.getBasePrice().doubleValue(), entryRao.getBasePrice().doubleValue(), 0.0001d);
					assertEquals(entryModel.getEntryNumber(), entryRao.getEntryNumber());

					assertEquals(entryModel.getProduct().getSupercategories().size(), entryRao.getProduct().getCategories().size());
					for (final CategoryModel catModel : entryModel.getProduct().getSupercategories())
					{
						boolean foundCategory = false;
						for (final CategoryRAO catRao : entryRao.getProduct().getCategories())
						{
							if (catModel.getCode().equals(catRao.getCode()))
							{
								foundCategory = true;
								break;
							}
						}
						assertTrue(foundCategory);
					}
					foundEntry = true;
					break;
				}
			}
			assertTrue(foundEntry);
		}

		for (final DiscountModel discountModel : cartModel.getDiscounts())
		{
			boolean foundDiscount = false;
			for (final DiscountValueRAO discountValueRao : cartRao.getDiscountValues())
			{
				if (discountModel.getCurrency().getIsocode().equals(discountValueRao.getCurrencyIsoCode())
						&& discountModel.getValue().doubleValue() == discountValueRao.getValue().doubleValue())
				{
					foundDiscount = true;
					break;
				}
			}
			assertTrue(foundDiscount);
		}

	}
}
