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
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ContentPageLabelOrIdExistsPredicateTest
{
	@InjectMocks
	private ContentPageLabelOrIdExistsPredicate predicate;

	@Mock
	private CMSPageService cmsPageService;

	@Mock
	private ContentPageModel contentPageModel;

	private String VALID_LABEL_OR_ID = "validLabelOrId";
	private String INVALID_LABEL_OR_ID = "invalidLabelOrId";


	@Test
	public void shouldReturnTrueIfLabelOrIdExists() throws CMSItemNotFoundException
	{
		// GIVEN
		when(cmsPageService.getPageForLabelOrId(VALID_LABEL_OR_ID)).thenReturn(contentPageModel);

		// WHEN
		boolean result = predicate.test(VALID_LABEL_OR_ID);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldReturnFalseIfLabelOrIdNotExists() throws CMSItemNotFoundException
	{
		// GIVEN
		when(cmsPageService.getPageForLabelOrId(INVALID_LABEL_OR_ID)).thenThrow(new RuntimeException(""));

		// WHEN
		boolean result = predicate.test(INVALID_LABEL_OR_ID);

		// THEN
		assertFalse(result);
	}
}

