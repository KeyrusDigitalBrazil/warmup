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

import de.hybris.platform.personalizationservices.dynamic.CxVariationActiveAttributeHandler;
import de.hybris.platform.personalizationservices.dynamic.CxVariationRankAttributeHandler;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.strategies.impl.DefaultRankAssignmentStrategy;
import de.hybris.platform.servicelayer.session.MockSessionService;
import de.hybris.platform.servicelayer.time.impl.DefaultTimeService;


public class CxVariationModelStub extends CxVariationModel
{
	private final CxVariationRankAttributeHandler rankHandler;
	private final CxVariationActiveAttributeHandler activeHandler;

	public CxVariationModelStub()
	{
		rankHandler = new CxVariationRankAttributeHandler();
		rankHandler.setRankAssigmentStrategy(new DefaultRankAssignmentStrategy());

		final DefaultTimeService timeService = new DefaultTimeService();
		timeService.setSessionService(new MockSessionService());

		activeHandler = new CxVariationActiveAttributeHandler();
	}

	@Override
	public Integer getRank()
	{
		return rankHandler.get(this);
	}

	@Override
	public void setRank(final Integer value)
	{
		rankHandler.set(this, value);
	}

	@Override
	public boolean isActive()
	{
		return activeHandler.get(this).booleanValue();
	}
}
