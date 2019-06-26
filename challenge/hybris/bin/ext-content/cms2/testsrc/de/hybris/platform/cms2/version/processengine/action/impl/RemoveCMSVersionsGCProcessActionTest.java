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
package de.hybris.platform.cms2.version.processengine.action.impl;

import static de.hybris.platform.cms2.constants.Cms2Constants.DEFAULT_VERSION_GC_PAGE_SIZE;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.processing.CMSVersionGCProcessModel;
import de.hybris.platform.cms2.version.processengine.action.AbstractCMSVersionGCProcessAction;
import de.hybris.platform.cms2.version.service.CMSVersionGCService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RemoveCMSVersionsGCProcessActionTest
{
	private static final int MAX_AGE_DAYS = 5;
	private static final int MAX_NUMBER_VERSIONS = 20;

	@InjectMocks
	private RemoveCMSVersionsGCProcessAction action;

	@Mock
	private CMSVersionGCService cmsVersionGCService;

	@Mock
	private ModelService modelService;

	@Mock
	private Predicate<CMSVersionGCProcessModel> cmsVersionGCProcessPredicate;

	@Mock
	private Predicate<CMSVersionGCProcessModel> cmsVersionGCProcessNegatedPredicate;

	@Mock
	private ObjectFactory<PageableData> pageableDataFactory;

	@Mock
	private CMSVersionGCProcessModel cmsVersionGCProcessModel;

	@Mock
	private CMSVersionModel cmsVersionModelToKeep;

	@Mock
	private List<CMSVersionModel> cmsVersionModelsToRemove;

	@Mock
	private PageableData pageableData;

	@Mock
	private SearchResult<CMSVersionModel> searchResult;

	@Before
	public void setup()
	{
		when(cmsVersionGCProcessPredicate.negate()).thenReturn(cmsVersionGCProcessNegatedPredicate);

		when(cmsVersionGCProcessModel.getMaxAgeDays()).thenReturn(MAX_AGE_DAYS);
		when(cmsVersionGCProcessModel.getMaxNumberVersions()).thenReturn(MAX_NUMBER_VERSIONS);

		// Set needs to be mutable so we instantiate a HashSet instead of using Collections.singleton(...)
		when(cmsVersionGCProcessModel.getRetainableVersions()).thenReturn(new HashSet<>(Collections.singletonList(cmsVersionModelToKeep)));

		when(pageableDataFactory.getObject()).thenReturn(pageableData);
	}

	@Test
	public void givenInvalidProcessModelThenActionReturnsFailed() throws Exception
	{
		// GIVEN
		when(cmsVersionGCProcessNegatedPredicate.test(cmsVersionGCProcessModel)).thenReturn(Boolean.TRUE);

		// WHEN
		final String result = action.execute(cmsVersionGCProcessModel);

		// THEN
		assertThat(result, equalTo(AbstractCMSVersionGCProcessAction.Transition.FAILED.toString()));
	}

	@Test
	public void givenNoExceptionThrownThenActionReturnsSucceeded() throws Exception
	{
		// GIVEN
		when(cmsVersionGCService.getVersionsExcludedBy(cmsVersionGCProcessModel.getRetainableVersions(), pageableData)).thenReturn(searchResult);
		when(searchResult.getResult()).thenReturn(cmsVersionModelsToRemove);
		when(searchResult.getCount()).thenReturn(1);
		when(searchResult.getRequestedCount()).thenReturn(DEFAULT_VERSION_GC_PAGE_SIZE);

		// WHEN
		final AbstractCMSVersionGCProcessAction.Transition result = action.executeAction(cmsVersionGCProcessModel);

		// THEN
		assertThat(result, equalTo(AbstractCMSVersionGCProcessAction.Transition.SUCCEEDED));

		// VERIFY
		verify(modelService).removeAll(cmsVersionModelsToRemove);
	}

	@Test
	public void givenExceptionThrownThenActionReturnsError() throws Exception
	{
		// GIVEN
		when(cmsVersionGCService.getVersionsExcludedBy(Collections.singletonList(cmsVersionModelToKeep), pageableData)).thenThrow(Exception.class);

		// WHEN
		final AbstractCMSVersionGCProcessAction.Transition result = action.executeAction(cmsVersionGCProcessModel);

		// THEN
		assertThat(result, equalTo(AbstractCMSVersionGCProcessAction.Transition.ERROR));
	}
}
