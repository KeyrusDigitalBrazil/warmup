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
package de.hybris.platform.personalizationservices.stub;

import de.hybris.platform.personalizationservices.dynamic.CxCustomizationActiveAttributeHandler;
import de.hybris.platform.personalizationservices.dynamic.CxCustomizationRankAttributeHandler;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.strategies.impl.DefaultRankAssignmentStrategy;
import de.hybris.platform.servicelayer.time.TimeService;


public class CxCustomizationModelStub extends CxCustomizationModel
{
	private final CxCustomizationRankAttributeHandler rankHandler;
	private final CxCustomizationActiveAttributeHandler activeHandler;

	public CxCustomizationModelStub()
	{
		this(new MockTimeService());
	}

	public CxCustomizationModelStub(final TimeService timeService)
	{
		rankHandler = new CxCustomizationRankAttributeHandler();
		rankHandler.setRankAssigmentStrategy(new DefaultRankAssignmentStrategy());

		activeHandler = new CxCustomizationActiveAttributeHandler();
		activeHandler.setTimeService(timeService);
	}

	@Override
	public void setRank(final Integer value)
	{
		rankHandler.set(this, value);
	}

	@Override
	public Integer getRank()
	{
		return rankHandler.get(this);
	}

	@Override
	public boolean isActive()
	{
		return activeHandler.get(this).booleanValue();
	}
}
