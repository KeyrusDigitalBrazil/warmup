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
package de.hybris.platform.cmsfacades.version.predicate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.version.service.CMSVersionService;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class LabelExistsInCMSVersionsPredicateTest
{
	private static final String ITEM_UUID = "item-uuid";
	private static final String VALID_LABEL = "valid-label";
	private static final String INVALID_LABEL = "invalid-label";

	@InjectMocks
	private LabelExistsInCMSVersionsPredicate predicate;

	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private CMSVersionService cmsVersionService;

	@Mock
	private CMSItemModel cmsItemModel;
	@Mock
	private CMSVersionModel cmsVersionModel;

	@Test
	public void whenLabelExistsShouldReturnTrue()
	{
		when(uniqueItemIdentifierService.getItemModel(ITEM_UUID, CMSItemModel.class)).thenReturn(Optional.of(cmsItemModel));
		when(cmsVersionService.getVersionByLabel(cmsItemModel, VALID_LABEL)).thenReturn(Optional.of(cmsVersionModel));

		assertThat(predicate.test(ITEM_UUID, VALID_LABEL), is(true));
	}

	@Test
	public void whenLabelDoesNotExistShouldReturnFalse()
	{
		when(uniqueItemIdentifierService.getItemModel(ITEM_UUID, CMSItemModel.class)).thenReturn(Optional.of(cmsItemModel));
		when(cmsVersionService.getVersionByLabel(cmsItemModel, INVALID_LABEL)).thenReturn(Optional.empty());

		assertThat(predicate.test(ITEM_UUID, INVALID_LABEL), is(false));
	}

	@Test
	public void whenCMSItemDoesNotExistShouldReturnFalse()
	{
		when(uniqueItemIdentifierService.getItemModel(ITEM_UUID, CMSItemModel.class)).thenThrow(new UnknownIdentifierException(""));

		assertThat(predicate.test(ITEM_UUID, VALID_LABEL), is(false));
	}
}
