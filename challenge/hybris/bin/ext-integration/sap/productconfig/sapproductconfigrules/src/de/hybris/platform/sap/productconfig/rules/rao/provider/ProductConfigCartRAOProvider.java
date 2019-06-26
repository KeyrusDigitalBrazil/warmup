/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.rules.rao.provider;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.UserGroupRAO;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * CPQ provider implementation for {@link CartRAO}.<br>
 * Simplified Cart Model RAO Converter, only mapping attributes relevant for rule evaluation within product
 * configuration context.
 */
public class ProductConfigCartRAOProvider implements RAOProvider<CartModel>
{

	private Converter<CartModel, CartRAO> cartRaoConverter;

	@Override
	public Set<Object> expandFactModel(final CartModel modelFact)
	{
		final Set<Object> raoSet = new LinkedHashSet<Object>();

		final CartRAO cartRAO = createRAO(modelFact);
		raoSet.add(cartRAO);

		addUserAndGroupsRAOs(raoSet, cartRAO);

		final Set<OrderEntryRAO> entries = cartRAO.getEntries();
		if (isEmpty(entries))
		{
			return raoSet;
		}
		raoSet.addAll(entries);
		for (final OrderEntryRAO entryRAO : entries)
		{
			final ProductConfigRAO productConfigRAO = entryRAO.getProductConfiguration();
			if (productConfigRAO != null)
			{
				raoSet.add(productConfigRAO);
				addCsticAndValueRAOs(raoSet, productConfigRAO);
			}
		}

		return raoSet;
	}

	protected void addUserAndGroupsRAOs(final Set<Object> raoSet, final CartRAO cartRAO)
	{

		final UserRAO userRAO = cartRAO.getUser();
		if (userRAO != null)
		{
			raoSet.add(userRAO);
			final Set<UserGroupRAO> groups = userRAO.getGroups();
			if (CollectionUtils.isNotEmpty(groups))
			{
				raoSet.addAll(groups);
			}
		}
	}

	protected void addCsticAndValueRAOs(final Set<Object> raoSet, final ProductConfigRAO productConfigRAO)
	{
		if (isEmpty(productConfigRAO.getCstics()))
		{
			return;
		}
		for (final CsticRAO csticRAO : productConfigRAO.getCstics())
		{
			raoSet.add(csticRAO);
			if (isNotEmpty(csticRAO.getAssignedValues()))
			{
				for (final CsticValueRAO csticValueRAO : csticRAO.getAssignedValues())
				{
					raoSet.add(csticValueRAO);
				}
			}
		}
		return;
	}

	protected CartRAO createRAO(final CartModel modelFact)
	{
		return getCartRaoConverter().convert(modelFact);
	}

	protected Converter<CartModel, CartRAO> getCartRaoConverter()
	{
		return cartRaoConverter;
	}

	/**
	 * @param cartRaoConverter
	 *
	 */
	@Required
	public void setCartRaoConverter(final Converter<CartModel, CartRAO> cartRaoConverter)
	{
		this.cartRaoConverter = cartRaoConverter;
	}

}
