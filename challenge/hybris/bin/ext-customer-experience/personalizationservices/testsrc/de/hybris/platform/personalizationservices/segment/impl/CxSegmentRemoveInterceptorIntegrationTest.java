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
package de.hybris.platform.personalizationservices.segment.impl;


import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.personalizationservices.AbstractCxServiceTest;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.segment.CxSegmentService;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Optional;



@IntegrationTest
public class CxSegmentRemoveInterceptorIntegrationTest extends AbstractCxServiceTest
{

	protected final static String SEGMENT_REL_TO_TRIGGER_CODE = "segment4";
	protected final static String SEGMENT_NOT_REL_TO_TRIGGER_CODE = "segment5";


	@Resource
	private CxSegmentService cxSegmentService;

	@Resource
	private ModelService modelService;

	@Test
	public void shouldThrowInterceptorExceptionWhenSegmentIsRelatedToSegmentTrigger()
	{
		// given
		final Optional<CxSegmentModel> segment = cxSegmentService.getSegment(SEGMENT_REL_TO_TRIGGER_CODE);

		try
		{
			// when
			modelService.remove(segment.get());
			fail("should throw InterceptorException");
		}
		catch (final ModelRemovalException e)
		{
			// then
			assertThat(e.getMessage()).contains("Segments that are related to triggers are not allowed to be removed!");
			assertThat(e.getCause()).isInstanceOf(InterceptorException.class);
		}

	}

	@Test
	public void shouldNotThrowInterceptorExceptionWhenSegmentIsNotRelatedToSegmentTrigger()
	{
		// given
		final Optional<CxSegmentModel> segment = cxSegmentService.getSegment(SEGMENT_NOT_REL_TO_TRIGGER_CODE);

		try
		{
			// when
			modelService.remove(segment.get());
		}
		catch (final ModelRemovalException e)
		{
			// then
			fail("should NOT throw InterceptorException");
		}
	}

}
