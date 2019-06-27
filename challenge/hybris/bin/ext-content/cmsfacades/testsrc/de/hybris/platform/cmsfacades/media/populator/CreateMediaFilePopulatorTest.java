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
package de.hybris.platform.cmsfacades.media.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.dto.MediaFileDto;
import de.hybris.platform.core.model.media.MediaModel;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CreateMediaFilePopulatorTest
{
	private static final String MEDIA_MIME = "image/jpeg";
	private static final String MEDIA_NAME = "somemediafile.jpeg";
	private static final long MEDIA_SIZE = 1024l;

	private final CreateMediaFilePopulator populator = new CreateMediaFilePopulator();

	private MediaFileDto source;
	private MediaModel target;

	@Before
	public void setUp()
	{
		target = new MediaModel();
		source = new MediaFileDto();
		source.setMime(MEDIA_MIME);
		source.setName(MEDIA_NAME);
		source.setSize(MEDIA_SIZE);
	}

	@Test
	public void shouldPopulateAllFields()
	{
		populator.populate(source, target);

		assertEquals(source.getMime(), target.getMime());
		assertEquals(source.getName(), target.getRealFileName());
		assertTrue(source.getSize() == target.getSize());
	}

}
