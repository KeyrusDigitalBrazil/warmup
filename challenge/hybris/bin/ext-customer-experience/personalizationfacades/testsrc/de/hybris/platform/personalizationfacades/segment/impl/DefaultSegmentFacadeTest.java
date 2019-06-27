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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationfacades.converters.ConfigurableConverter;
import de.hybris.platform.personalizationfacades.data.SegmentData;
import de.hybris.platform.personalizationfacades.enums.SegmentConversionOptions;
import de.hybris.platform.personalizationfacades.exceptions.AlreadyExistsException;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.segment.CxSegmentService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultSegmentFacadeTest
{
	private static final String SEGMENT_ID = "segmentId";
	private static final String NOTEXISTING_SEGMENT_ID = "notExistingSegment";
	private static final String NEW_SEGMENT_ID = "newSegment";
	private static final String NEW_SEGMENT_DESCRIPTION = "newSegmentDescription";


	private final DefaultSegmentFacade segmentFacade = new DefaultSegmentFacade();
	@Mock
	private ModelService modelService;
	@Mock
	private CxSegmentService segmentService;
	@Mock
	private ConfigurableConverter<CxSegmentModel, SegmentData, SegmentConversionOptions> segmentConverter;
	@Mock
	private Converter<SegmentData, CxSegmentModel> segmentReverseConverter;

	private CxSegmentModel segment;
	private SegmentData segmentData;
	@Mock
	private SearchResult<CxSegmentModel> searchResult;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		segmentFacade.setModelService(modelService);
		segmentFacade.setSegmentService(segmentService);
		segmentFacade.setSegmentConverter(segmentConverter);
		segmentFacade.setSegmentReverseConverter(segmentReverseConverter);

		segment = new CxSegmentModel();
		segment.setCode(SEGMENT_ID);

		segmentData = new SegmentData();
		segmentData.setCode(SEGMENT_ID);

		Mockito.when(searchResult.getResult()).thenReturn(Collections.singletonList(segment));
	}

	//Tests for getSegment
	@Test
	public void getSegmentTest()
	{
		//given
		Mockito.when(segmentService.getSegment(SEGMENT_ID)).thenReturn(Optional.of(segment));
		Mockito.when(segmentConverter.convert(segment, Arrays.asList(SegmentConversionOptions.FULL))).thenReturn(segmentData);

		//when
		final SegmentData result = segmentFacade.getSegment(SEGMENT_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertSame(segmentData, result);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getNotExistingSegmentTest()
	{
		//given
		Mockito.when(segmentService.getSegment(NOTEXISTING_SEGMENT_ID)).thenReturn(Optional.empty());

		//when
		segmentFacade.getSegment(NOTEXISTING_SEGMENT_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getSegmentWithNullCode()
	{
		//when
		segmentFacade.getSegment(null);
	}

	//Tests for create method

	@Test
	public void createSegmentTest()
	{
		//given
		segmentData.setCode(NEW_SEGMENT_ID);
		segmentData.setDescription(NEW_SEGMENT_DESCRIPTION);
		segment.setCode(NEW_SEGMENT_ID);
		Mockito.when(segmentService.getSegment(NEW_SEGMENT_ID)).thenReturn(Optional.empty());
		Mockito.when(segmentReverseConverter.convert(segmentData)).thenReturn(segment);
		Mockito.when(segmentConverter.convert(segment)).thenReturn(segmentData);

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
		segmentData.setCode(NEW_SEGMENT_ID);
		segmentData.setDescription(NEW_SEGMENT_DESCRIPTION);
		segment.setCode(NEW_SEGMENT_ID);
		Mockito.when(segmentService.getSegment(NEW_SEGMENT_ID)).thenReturn(Optional.of(segment));

		//when
		segmentFacade.createSegment(segmentData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createSegmentWithNullDataTest()
	{
		//when
		segmentFacade.createSegment(null);
	}

	//update method tests
	@Test
	public void updateSegmentTest()
	{
		//given
		final CxSegmentModel updatedSegment = new CxSegmentModel();
		Mockito.when(segmentService.getSegment(SEGMENT_ID)).thenReturn(Optional.of(segment));
		Mockito.when(segmentReverseConverter.convert(segmentData, segment)).thenReturn(updatedSegment);
		Mockito.when(segmentConverter.convert(updatedSegment)).thenReturn(segmentData);

		//when
		final SegmentData result = segmentFacade.updateSegment(SEGMENT_ID, segmentData);

		//then
		Assert.assertNotNull(result);
		Assert.assertSame(segmentData, result);
		Mockito.verify(modelService).save(updatedSegment);
	}


	@Test(expected = UnknownIdentifierException.class)
	public void updateNotExistingSegmentTest()
	{
		//given
		Mockito.when(segmentService.getSegment(NOTEXISTING_SEGMENT_ID)).thenReturn(Optional.empty());

		//when
		segmentFacade.updateSegment(NOTEXISTING_SEGMENT_ID, segmentData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateSegmentWithNullIdTest()
	{
		//when
		segmentFacade.updateSegment(null, segmentData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateSegmentWithNullDataTest()
	{
		//when
		segmentFacade.updateSegment(SEGMENT_ID, null);
	}

	//delete method tests
	@Test
	public void deleteSegmentTest()
	{
		//given
		Mockito.when(segmentService.getSegment(SEGMENT_ID)).thenReturn(Optional.of(segment));

		//when
		segmentFacade.deleteSegment(SEGMENT_ID);

		//then
		Mockito.verify(modelService).remove(segment);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteNotExistingSegmentTest()
	{
		//given
		Mockito.when(segmentService.getSegment(NOTEXISTING_SEGMENT_ID)).thenReturn(Optional.empty());

		//when
		segmentFacade.deleteSegment(NOTEXISTING_SEGMENT_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteSegmentWithNullIdTest()
	{
		//when
		segmentFacade.deleteSegment(null);
	}
}
