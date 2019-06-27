/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.scimservices.interceptors;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ScimUserValidateInterceptorTest
{

	private static String SAMPLE_EXTERNAL_ID = "id";

	@InjectMocks
	private final ScimUserValidateInterceptor validator = new ScimUserValidateInterceptor();

	@Mock
	private UserModel userModel;

	@Mock
	private InterceptorContext ctx;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Test
	public void testOnValidateWhenScimUserIdIsEmpty() throws InterceptorException
	{
		Mockito.when(userModel.getScimUserId()).thenReturn(null);
		validator.onValidate(userModel, ctx);

		Mockito.verifyZeroInteractions(flexibleSearchService);
	}

	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenScimUserIdExistsButNotUniqueAndModelIsNew() throws InterceptorException
	{
		Mockito.when(userModel.getScimUserId()).thenReturn(SAMPLE_EXTERNAL_ID);

		final UserModel sampleUserModel = Mockito.mock(UserModel.class);
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any()))
				.thenReturn(Collections.singletonList(sampleUserModel));
		Mockito.when(ctx.isNew(userModel)).thenReturn(Boolean.TRUE);

		validator.onValidate(userModel, ctx);
	}

	@Test
	public void testOnValidateWhenExternaIdExistsIsUniqueAndModelIsNew() throws InterceptorException
	{
		Mockito.when(userModel.getScimUserId()).thenReturn(SAMPLE_EXTERNAL_ID);

		final UserModel sampleUserModel = Mockito.mock(UserModel.class);
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);
		Mockito.when(ctx.isNew(userModel)).thenReturn(Boolean.TRUE);

		validator.onValidate(userModel, ctx);
	}

	@Test
	public void testOnValidateWhenScimUserIdExistsIsUniqueAndModelIsUpdated() throws InterceptorException
	{
		Mockito.when(userModel.getScimUserId()).thenReturn(SAMPLE_EXTERNAL_ID);

		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(userModel));
		Mockito.when(ctx.isNew(userModel)).thenReturn(Boolean.FALSE);

		validator.onValidate(userModel, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void testOnValidateWhenScimUserIdExistsNotUniqueAndModelIsUpdated() throws InterceptorException
	{
		Mockito.when(userModel.getScimUserId()).thenReturn(SAMPLE_EXTERNAL_ID);

		final UserModel sampleUserModel = Mockito.mock(UserModel.class);
		Mockito.when(flexibleSearchService.getModelsByExample(Mockito.any()))
				.thenReturn(Collections.singletonList(sampleUserModel));
		Mockito.when(ctx.isNew(userModel)).thenReturn(Boolean.FALSE);

		validator.onValidate(userModel, ctx);
	}

}
