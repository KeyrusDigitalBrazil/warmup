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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;

import org.junit.Test;


@UnitTest
public class IsNotDeletedPagePredicateTest
{
	private final IsNotDeletedPagePredicate predicate = new IsNotDeletedPagePredicate();

	@Test
	public void whenItemModelIsNotPageTypeShouldBeTrue()
	{
		final boolean result = predicate.test(new CMSParagraphComponentModel());

		assertThat(result, is(true));
	}

	@Test
	public void whenItemModelIsPageTypeAndActiveShouldBeTrue()
	{
		final AbstractPageModel pageModel = new AbstractPageModel();
		pageModel.setPageStatus(CmsPageStatus.ACTIVE);

		final boolean result = predicate.test(pageModel);

		assertThat(result, is(true));
	}

	@Test
	public void whenItemModelIsPageTypeAndDeletedShouldBeFalse()
	{
		final AbstractPageModel pageModel = new AbstractPageModel();
		pageModel.setPageStatus(CmsPageStatus.DELETED);

		final boolean result = predicate.test(pageModel);

		assertThat(result, is(false));
	}

}
