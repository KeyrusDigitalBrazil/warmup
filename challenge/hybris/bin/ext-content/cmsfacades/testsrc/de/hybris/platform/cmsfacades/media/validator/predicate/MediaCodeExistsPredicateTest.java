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
package de.hybris.platform.cmsfacades.media.validator.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MediaCodeExistsPredicateTest
{
	private static final String VALID_CODE = "testMediaCode";
	private static final String INVALID_CODE = "invalid";

	@Mock
	private MediaService mediaService;
	@Mock
	private MediaModel media;

	@InjectMocks
	private MediaCodeExistsPredicate predicate;

	@Test
	public void shouldFindMediaByCode()
	{
		when(mediaService.getMedia(VALID_CODE)).thenReturn(media);
		final boolean result = predicate.test(VALID_CODE);
		assertTrue(result);
	}

	@Test
	public void shouldNotFindMediaByCode()
	{
		when(mediaService.getMedia(INVALID_CODE)).thenThrow(new UnknownIdentifierException("code is invalid"));
		final boolean result = predicate.test(INVALID_CODE);
		assertFalse(result);
	}

	@Test
	public void shouldFindMultipleMediaByCode()
	{
		when(mediaService.getMedia(VALID_CODE)).thenThrow(new AmbiguousIdentifierException("multiple entries found for code"));
		final boolean result = predicate.test(VALID_CODE);
		assertTrue(result);
	}

}
