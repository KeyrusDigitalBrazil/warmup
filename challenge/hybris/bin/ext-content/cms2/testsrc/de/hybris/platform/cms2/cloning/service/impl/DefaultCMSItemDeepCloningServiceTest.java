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
package de.hybris.platform.cms2.cloning.service.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSItemDeepCloningServiceTest
{
	private static final Object TEST_UID = "1122";
	private static final String BASE_NAME = "some_name";

	@Mock
	private PersistentKeyGenerator cloneUidGenerator;

	@InjectMocks
	private DefaultCMSItemDeepCloningService itemDeepCloningService;

	@Before
	public void setUp()
	{
		when(cloneUidGenerator.generate()).thenReturn(TEST_UID);
	}

	@Test
	public void generateCloneUid()
	{
		// WHEN
		final String newUid = itemDeepCloningService.generateCloneItemUid();

		// THEN
		assertThat(newUid, equalTo("clone_" + TEST_UID));
	}

	@Test
	public void givenComponentNameWithoutClonePrefix_WhenGenerateCloneComponentNameIsCalled_ThenItReturnsANameWithTheNewPostfix()
	{
		// GIVEN
		final String expectedResult = BASE_NAME + " " + TEST_UID;

		// WHEN
		final String result = itemDeepCloningService.generateCloneComponentName(BASE_NAME);

		// THEN
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void givenComponentNameWithClonePrefix_WhenGenerateCloneComponentNameIsCalled_ThenItReturnsANameOnlyWithANewPostfix()
	{
		// GIVEN
		final String otherUid = "54321";
		final String originalName = BASE_NAME + " " + otherUid;
		final String expectedResult = BASE_NAME + " " + TEST_UID;

		// WHEN
		final String result = itemDeepCloningService.generateCloneComponentName(originalName);

		// THEN
		assertThat(result, equalTo(expectedResult));
	}

}
