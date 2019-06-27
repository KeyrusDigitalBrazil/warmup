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
package de.hybris.platform.personalizationservices.process.strategies;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mock;


public abstract class BaseCxProcessParameterStrategyTest
{
	private static final String SEGMENT1_CODE = "s1";
	private static final String SEGMENT2_CODE = "s2";

	@Mock
	protected ProcessParameterHelper processParameterHelper;

	protected void setUserToSegments(final UserModel user, final List<CxSegmentModel> segments)
	{
		user.setUserToSegments(new ArrayList<CxUserToSegmentModel>());

		for (final CxSegmentModel segment : segments)
		{
			final CxUserToSegmentModel uts = new CxUserToSegmentModel();
			uts.setSegment(segment);
			uts.setUser(user);
			uts.setAffinity(BigDecimal.ONE);
			segment.setUserToSegments(new ArrayList<CxUserToSegmentModel>());
			segment.getUserToSegments().add(uts);
			user.getUserToSegments().add(uts);
		}
	}

	protected List<CxSegmentModel> createSegments()
	{
		final List<CxSegmentModel> segments = new ArrayList<>();
		final CxSegmentModel s1 = new CxSegmentModel();
		s1.setCode(SEGMENT1_CODE);
		segments.add(s1);
		final CxSegmentModel s2 = new CxSegmentModel();
		s2.setCode(SEGMENT2_CODE);
		segments.add(s2);
		return segments;
	}

	protected BusinessProcessParameterModel createBusinessProcessParameterModel(final String parameterName, final Object value)
	{
		final BusinessProcessParameterModel processParameter = new BusinessProcessParameterModel();
		processParameter.setName(parameterName);
		processParameter.setValue(value);
		return processParameter;
	}
}
