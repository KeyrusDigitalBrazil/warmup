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
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.cmsitems.predicates.PageUpdateRequiresValidationPredicate;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PageUpdateRequiresValidationPredicateTest
{
	private final String PAGE_UID = "SOME UID";

	@Mock
	private CMSAdminPageService adminPageService;

	@Mock
	private AbstractPageModel pageBeingValidated;

	@InjectMocks
	private PageUpdateRequiresValidationPredicate predicate;

	@Before
	public void setUp()
	{
		when(pageBeingValidated.getUid()).thenReturn(PAGE_UID);
		when(adminPageService.getPageForIdFromActiveCatalogVersionByPageStatuses(PAGE_UID,
				Arrays.asList(CmsPageStatus.values()))).thenReturn(pageBeingValidated);
	}

	@Test
	public void givenPageExistsAndWillBeActive_WhenPredicateIsTested_ReturnsTrue()
	{
		// GIVEN
		when(pageBeingValidated.getPageStatus()).thenReturn(CmsPageStatus.ACTIVE);

		// WHEN
		boolean result = predicate.test(pageBeingValidated);

		// THEN
		assertTrue(result);
	}

	@Test
	public void givenPageExistsAndWillBeInTrash_WhenPredicateIsTested_ReturnsFalse()
	{
		// GIVEN
		when(pageBeingValidated.getPageStatus()).thenReturn(CmsPageStatus.DELETED);

		// WHEN
		boolean result = predicate.test(pageBeingValidated);

		// THEN
		assertFalse(result);
	}

	@Test
	public void givenPageDoesNotExist_WhenPredicateIsTested_ReturnsFalse()
	{
		// GIVEN
		when(adminPageService.getPageForIdFromActiveCatalogVersionByPageStatuses(any(), any())).thenThrow(UnknownIdentifierException.class);

		// WHEN
		boolean result = predicate.test(pageBeingValidated);

		// THEN
		assertFalse(result);
	}

}
