/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.widgets.actions.enumeration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.expression.EvaluationException;

import com.google.common.collect.Sets;
import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacadeOperationResult;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectAccessException;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.type.ObjectValueService;


@NullSafeWidget(false)
public class EnumerationActionTest extends AbstractActionUnitTest<EnumerationAction>
{
	@Mock
	private NotificationService notificationService;
	@Mock
	private ObjectValueService objectValueService;
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private ModelService modelService;
	@Mock
	private SessionService sessionService;
	@Spy
	@InjectMocks
	private EnumerationAction enumerationAction;

	@Override
	public EnumerationAction getActionInstance()
	{
		return enumerationAction;
	}

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		doAnswer(inv -> ((SessionExecutionBody) inv.getArguments()[1]).execute()).when(sessionService)
				.executeInLocalViewWithParams(any(), any());
	}

	@Test
	public void shouldExceptionBeThrownWhenQualifierIsAbsent()
	{
		// given
		final ActionContext<Collection<Object>> context = mock(ActionContext.class);
		given(context.getParameter(EnumerationAction.PARAMETER_QUALIFIER)).willReturn(null);

		// except
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> enumerationAction.canPerform(context));
	}

	@Test
	public void shouldActionBeDisabledWhenNoItemsAreSelected()
	{
		// given
		final ActionContext<Collection<Object>> context = mockContext(Collections.emptyList(), null);

		// when
		final boolean result = enumerationAction.canPerform(context);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldActionBeEnabledWhenItemsAreSelected()
	{
		// given
		final ActionContext<Collection<Object>> context = mockContext(Lists.newArrayList(new Object(), new Object()), null);
		doReturn(Optional.empty()).when(enumerationAction).getEnumerationValidator(context);

		// when
		final boolean result = enumerationAction.canPerform(context);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldDefaultValidationBeLaunchedWhenValidationIdIsNotProvided()
	{
		// given
		final ActionContext<Collection<Object>> context = mockContext(Lists.newArrayList(new Object()), null);
		doReturn(Optional.of(mock(EnumerationValidator.class))).when(enumerationAction)
				.getBean(EnumerationAction.DEFAULT_ENUMERATION_VALIDATOR_BEAN_ID);

		// when
		final Optional<EnumerationValidator> validator = enumerationAction.getEnumerationValidator(context);

		// then
		assertThat(validator.isPresent()).isTrue();
	}

	@Test
	public void shouldCustomValidationBeLaunchedWhenValidationIdIsProvided()
	{
		// given
		final String someBeanId = "someBeanId";
		final ActionContext<Collection<Object>> context = mockContext(Lists.newArrayList(new Object()), someBeanId);
		doReturn(Optional.of(mock(EnumerationValidator.class))).when(enumerationAction).getBean(someBeanId);

		// when
		final Optional<EnumerationValidator> validator = enumerationAction.getEnumerationValidator(context);

		// then
		assertThat(validator.isPresent()).isTrue();
	}

	@Test
	public void shouldTransactionBeRolledBackWhenExceptionIsThrown()
	{
		// given
		final List<Object> list = Lists.newArrayList(new Object(), new Object());
		final ObjectFacadeOperationResult result = mockResult(false);
		given(objectFacade.save(list, null)).willReturn(result);
		doThrow(EvaluationException.class).when(objectValueService).setValue(any(), any(), any());

		// when
		enumerationAction.save(null, null, list, null);

		// then
		verify(enumerationAction).notifyUpdateFailed(any(), any());
		verify(enumerationAction).revertChanges(list);
	}

	protected ActionContext<Collection<Object>> mockContext(final Collection<Object> collection, final String validatorId)
	{
		final ActionContext<Collection<Object>> context = mock(ActionContext.class);
		doReturn(collection).when(context).getData();
		doReturn("any").when(context).getParameter(EnumerationAction.PARAMETER_QUALIFIER);
		doReturn(validatorId).when(context).getParameter(EnumerationAction.PARAMETER_VALIDATOR_ID);
		return context;
	}

	protected ObjectFacadeOperationResult<Object> mockResult(final boolean failure)
	{
		final ObjectFacadeOperationResult mock = mock(ObjectFacadeOperationResult.class);
		given(mock.hasError()).willReturn(failure);
		final Object o = new Object();
		given(mock.getFailedObjects()).willReturn(failure ? Sets.newHashSet(o) : new HashSet());
		given(mock.getErrorForObject(o)).willReturn(mock(ObjectAccessException.class));
		return mock;
	}

}
