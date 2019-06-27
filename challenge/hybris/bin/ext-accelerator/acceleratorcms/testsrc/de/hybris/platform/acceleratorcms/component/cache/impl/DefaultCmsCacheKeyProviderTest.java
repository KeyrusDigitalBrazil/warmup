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
package de.hybris.platform.acceleratorcms.component.cache.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.RestrictionEvaluationException;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


@UnitTest
public class DefaultCmsCacheKeyProviderTest
{

	@Test
	public void handleRestrictionEvaluationExceptionTest()
	{
		final DefaultCmsCacheKeyProvider prov = new DefaultCmsCacheKeyProvider();
		final HttpServletRequest request = null;
		final SimpleCMSComponentModel component = null;
		final AbstractRestrictionModel restriction = null;
		final RestrictionEvaluationException e = null;
		assertThat(prov.handleRestrictionEvaluationException(request, component, restriction, e))
				.isEqualToIgnoringCase(StringUtils.EMPTY);

	}

}
