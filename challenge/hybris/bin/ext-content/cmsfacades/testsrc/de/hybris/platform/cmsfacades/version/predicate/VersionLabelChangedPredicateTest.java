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
import de.hybris.platform.cms2.version.service.CMSVersionService;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VersionLabelChangedPredicateTest
{
	private static final String VERSION_UID = "someVersionUID";
	private static final String LABEL_1 = "someLabel1";
	private static final String LABEL_2 = "someLabel2";

	@InjectMocks
	private VersionLabelChangedPredicate predicate;

	@Mock
	private CMSVersionService cmsVersionService;

	@Mock
	private CMSVersionModel cmsVersionModel;

	@Test
	public void whenLabelChangedShouldReturnTrue()
	{
		when(cmsVersionService.getVersionByUid(VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));
		when(cmsVersionModel.getLabel()).thenReturn(LABEL_1);

		assertThat(predicate.test(VERSION_UID, LABEL_2), is(true));
	}

	@Test
	public void whenLabelNotChangedShouldReturnFalse()
	{
		when(cmsVersionService.getVersionByUid(VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));
		when(cmsVersionModel.getLabel()).thenReturn(LABEL_1);

		assertThat(predicate.test(VERSION_UID, LABEL_1), is(false));
	}

	@Test
	public void whenLabelNullShouldReturnTrue()
	{
		when(cmsVersionService.getVersionByUid(VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));
		when(cmsVersionModel.getLabel()).thenReturn(null);

		assertThat(predicate.test(VERSION_UID, LABEL_1), is(true));
	}

	@Test
	public void whenCMSItemDoesNotExistShouldReturnFalse()
	{
		when(cmsVersionService.getVersionByUid(VERSION_UID)).thenReturn(Optional.empty());

		assertThat(predicate.test(VERSION_UID, LABEL_1), is(false));
	}
}
