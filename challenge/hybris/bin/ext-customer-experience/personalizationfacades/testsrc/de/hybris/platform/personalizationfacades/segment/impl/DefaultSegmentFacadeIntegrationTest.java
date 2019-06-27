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
package de.hybris.platform.personalizationfacades.segment.impl;


import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.personalizationfacades.AbstractFacadeIntegrationTest;
import de.hybris.platform.personalizationfacades.data.SegmentData;
import de.hybris.platform.personalizationfacades.exceptions.AlreadyExistsException;
import de.hybris.platform.personalizationfacades.segment.SegmentFacade;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class DefaultSegmentFacadeIntegrationTest extends AbstractFacadeIntegrationTest
{
	private static final String NEW_SEGMENT_ID = "newSegment";
	private static final String NEW_SEGMENT_DESCRIPTION = "newSegmentDescription";

	@Resource(name = "defaultCxSegmentFacade")
	private SegmentFacade segmentFacade;

	@Test
	public void getSegmentTest()
	{
		//when
		final SegmentData segment = segmentFacade.getSegment(SEGMENT_ID);

		//then
		assertNotNull(segment);
		assertTrue(SEGMENT_ID.equals(segment.getCode()));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getNotExistingSegmentTest()
	{
		//when
		segmentFacade.getSegment(NOTEXISTING_SEGMENT_ID);
	}

	@Test
	public void createSegmentTest()
	{
		//given
		final SegmentData segmentData = new SegmentData();
		segmentData.setCode(NEW_SEGMENT_ID);
		segmentData.setDescription(NEW_SEGMENT_DESCRIPTION);

		//when
		final SegmentData result = segmentFacade.createSegment(segmentData);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(NEW_SEGMENT_ID, result.getCode());
		Assert.assertEquals(NEW_SEGMENT_DESCRIPTION, result.getDescription());
	}

	@Test(expected = AlreadyExistsException.class)
	public void createAltreadyExistedSegmentTest()
	{
		//given
		final SegmentData segmentData = new SegmentData();
		segmentData.setCode(SEGMENT_ID);

		//when
		segmentFacade.createSegment(segmentData);
	}

	@Test
	public void updateSegmentTest()
	{
		//given
		final SegmentData segmentData = new SegmentData();
		segmentData.setCode(SEGMENT_ID);
		segmentData.setDescription(NEW_SEGMENT_DESCRIPTION);

		//when
		final SegmentData result = segmentFacade.updateSegment(SEGMENT_ID, segmentData);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(result.getCode(), segmentData.getCode());
		Assert.assertEquals(result.getDescription(), segmentData.getDescription());
	}


	@Test(expected = UnknownIdentifierException.class)
	public void updateNotExistingSegmentTest()
	{
		//given
		final SegmentData segmentData = new SegmentData();
		segmentData.setCode(NOTEXISTING_SEGMENT_ID);

		//when
		segmentFacade.updateSegment(NOTEXISTING_SEGMENT_ID, segmentData);
	}

	@Test
	public void deleteSegmentRelatedToSegmentTriggerTest()
	{
		try
		{
			//when
			segmentFacade.deleteSegment(SEGMENT_ID_1);
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
	public void deleteSegmentTest()
	{
		//given
		boolean segmentRemoved = false;

		//when
		segmentFacade.deleteSegment(SEGMENT_ID_2);

		//then
		try
		{
			segmentFacade.getSegment(SEGMENT_ID_2);
		}
		catch (final UnknownIdentifierException e)
		{
			segmentRemoved = true;
		}
		assertTrue(segmentRemoved);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteNotExistingSegmentTest()
	{
		//when
		segmentFacade.deleteSegment(NOTEXISTING_SEGMENT_ID);
	}

}
