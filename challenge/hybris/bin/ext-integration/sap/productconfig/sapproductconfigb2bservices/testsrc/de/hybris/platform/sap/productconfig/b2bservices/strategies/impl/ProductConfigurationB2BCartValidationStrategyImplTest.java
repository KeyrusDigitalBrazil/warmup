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
package de.hybris.platform.sap.productconfig.b2bservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.productconfig.services.strategies.impl.ProductConfigurationCartValidationStrategyImplTest;

import org.mockito.InjectMocks;


/**
 * Unit tests
 */
@UnitTest
public class ProductConfigurationB2BCartValidationStrategyImplTest extends ProductConfigurationCartValidationStrategyImplTest
{
	@InjectMocks
	private final ProductConfigurationB2BCartValidationStrategyImpl classUnderTest = new ProductConfigurationB2BCartValidationStrategyImpl();

	@Override
	protected CommerceCartModification validateCartEntry(final CartModel cartModel, final CartEntryModel cartEntryModel)
	{
		return classUnderTest.validateCartEntry(cartModel, cartEntryModel);
	}
}
