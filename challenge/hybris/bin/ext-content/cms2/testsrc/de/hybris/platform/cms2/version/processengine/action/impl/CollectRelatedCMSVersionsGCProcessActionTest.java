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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.processing.CMSVersionGCProcessModel;
import de.hybris.platform.cms2.version.processengine.action.AbstractCMSVersionGCProcessAction;
import de.hybris.platform.cms2.version.service.CMSVersionGCService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.*;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CollectRelatedCMSVersionsGCProcessActionTest
{
	private static final int MAX_AGE_DAYS = 5;
	private static final int MAX_NUMBER_VERSIONS = 20;

	@InjectMocks
	private CollectRelatedCMSVersionsGCProcessAction action;

	@Mock
	private CMSVersionGCService cmsVersionGCService;

	@Mock
	private ModelService modelService;

	@Mock
	private Predicate<CMSVersionGCProcessModel> cmsVersionGCProcessPredicate;

	@Mock
	private Predicate<CMSVersionGCProcessModel> cmsVersionGCProcessNegatedPredicate;

	@Mock
	private CMSVersionGCProcessModel cmsVersionGCProcessModel;

	@Mock
	private CMSVersionModel parentCMSVersionModel;

	@Mock
	private CMSVersionModel childCMSVersionModel;

	final ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);

	@Before
	public void setup()
	{
		when(cmsVersionGCProcessPredicate.negate()).thenReturn(cmsVersionGCProcessNegatedPredicate);

		when(cmsVersionGCProcessModel.getMaxAgeDays()).thenReturn(MAX_AGE_DAYS);
		when(cmsVersionGCProcessModel.getMaxNumberVersions()).thenReturn(MAX_NUMBER_VERSIONS);

		// Set needs to be mutable so we instantiate a HashSet instead of using Collections.singleton(...)
		when(cmsVersionGCProcessModel.getRetainableVersions()).thenReturn(new HashSet<>(Collections.singletonList(parentCMSVersionModel)));
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
	public void givenNoRelatedChildrenThenActionReturnsSucceeded() throws Exception
	{
		// GIVEN
		when(parentCMSVersionModel.getRelatedChildren()).thenReturn(Collections.emptyList());

		// WHEN
		final AbstractCMSVersionGCProcessAction.Transition result = action.executeAction(cmsVersionGCProcessModel);

		// THEN
		assertThat(result, equalTo(AbstractCMSVersionGCProcessAction.Transition.SUCCEEDED));

		// VERIFY
		verify(modelService).save(cmsVersionGCProcessModel);
	}

	@Test
	public void givenNoExceptionThrownThenActionReturnsSucceeded() throws Exception
	{
		// GIVEN
		when(parentCMSVersionModel.getRelatedChildren()).thenReturn(new ArrayList<>(Collections.singletonList(childCMSVersionModel)));

		// WHEN
		final AbstractCMSVersionGCProcessAction.Transition result = action.executeAction(cmsVersionGCProcessModel);

		// THEN
		assertThat(result, equalTo(AbstractCMSVersionGCProcessAction.Transition.SUCCEEDED));

		// VERIFY
		verify(modelService).save(cmsVersionGCProcessModel);
	}

	@Test
	public void givenListOfRetainedVersionsWhenAllRelatedRetrievedThenProcessModelShouldContainBothLists()
	{
		// WHEN
		when(parentCMSVersionModel.getRelatedChildren()).thenReturn(new ArrayList<>(Collections.singletonList(childCMSVersionModel)));
		action.executeAction(cmsVersionGCProcessModel);

		// THEN
		verify(cmsVersionGCProcessModel).setRetainableVersions(captor.capture());
		assertEquals(2, captor.getValue().size());
	}

	@Test
	public void givenExceptionThrownThenActionReturnsError() throws Exception
	{
		// GIVEN
		when(parentCMSVersionModel.getRelatedChildren()).thenThrow(Exception.class);

		// WHEN
		final AbstractCMSVersionGCProcessAction.Transition result = action.executeAction(cmsVersionGCProcessModel);

		// THEN
		assertThat(result, equalTo(AbstractCMSVersionGCProcessAction.Transition.ERROR));
	}
}
