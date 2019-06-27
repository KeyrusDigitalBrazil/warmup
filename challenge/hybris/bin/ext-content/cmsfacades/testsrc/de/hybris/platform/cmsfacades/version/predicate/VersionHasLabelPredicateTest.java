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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.version.service.CMSVersionService;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VersionHasLabelPredicateTest
{
	private static final String VALID_VERSION_UID = "test-uid";
	private static final String INVALID_VERSION_UID = "faulty-uid";

	@InjectMocks
	private VersionHasLabelPredicate predicate;

	@Mock
	private CMSVersionService cmsVersionService;

	@Mock
	private CMSVersionModel versionModel;

	@Before
	public void setUp()
	{
		when(cmsVersionService.getVersionByUid(VALID_VERSION_UID)).thenReturn(Optional.of(versionModel));
		when(cmsVersionService.getVersionByUid(INVALID_VERSION_UID)).thenReturn(Optional.empty());

	}

	@Test
	public void shouldBeTrueWhenVersionHasLabel()
	{
		when(versionModel.getLabel()).thenReturn("Test Version Label");

		final boolean result = predicate.test(VALID_VERSION_UID);

		assertTrue("Should be true when version uid is valid and version has a label", result);
	}

	@Test
	public void shouldBeFalseWhenVersionHasNullLabel()
	{
		when(versionModel.getLabel()).thenReturn(null);

		final boolean result = predicate.test(VALID_VERSION_UID);

		assertFalse("Should be false when version uid is valid and label is null", result);
	}

	@Test
	public void shouldBeFalseWhenVersionHasEmptyLabel()
	{
		when(versionModel.getLabel()).thenReturn("");

		final boolean result = predicate.test(VALID_VERSION_UID);

		assertFalse("Should be false when version uid is valid and label is empty", result);
	}

	@Test
	public void shouldBeFalseWhenVersionNotFound()
	{
		final boolean result = predicate.test(INVALID_VERSION_UID);

		assertFalse("Should be false when version uid is invalid", result);
	}

}
