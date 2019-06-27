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
package com.hybris.backoffice.spring;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.context.WebApplicationContext;

import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.util.CockpitSessionService;
import com.hybris.cockpitng.util.impl.DefaultCockpitSessionService;


@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class SessionBeansGuardPostProcessorTest extends AbstractCockpitngUnitTest<SessionBeansGuardPostProcessor>
{
	public static final String BEAN_ID = "cockpitSessionService";

	@Spy
	@InjectMocks
	private SessionBeansGuardPostProcessor processor;

	@Mock
	private ConfigurableListableBeanFactory applicationContext;

	@Mock
	private BeanDefinition beanDefinition;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(Boolean.TRUE).when(applicationContext).containsBeanDefinition(BEAN_ID);
		Mockito.when(applicationContext.getBeanDefinition(BEAN_ID)).thenReturn(beanDefinition);
		Mockito.when(beanDefinition.getScope()).thenReturn(WebApplicationContext.SCOPE_SESSION);
	}

	@Test
	public void verifySerializable()
	{
		final CockpitSessionService cockpitSessionService = new DefaultCockpitSessionService();
		Assertions.assertThat(processor.postProcessBeforeInitialization(cockpitSessionService, BEAN_ID))
				.isSameAs(cockpitSessionService);
		Mockito.verify(processor, Mockito.times(1)).canSerializeBean(cockpitSessionService, BEAN_ID);
		Assertions.assertThat(processor.canSerializeBean(cockpitSessionService, BEAN_ID)).isTrue();
	}

	@Test
	public void verifyNonSerializable()
	{
		final Object nonSerializable = new NonSerializable();
		Assertions.assertThat(processor.postProcessBeforeInitialization(nonSerializable, BEAN_ID)).isSameAs(nonSerializable);
		Mockito.verify(processor, Mockito.times(1)).canSerializeBean(nonSerializable, BEAN_ID);
		Assertions.assertThat(processor.canSerializeBean(nonSerializable, BEAN_ID)).isFalse();
	}

	@Override
	protected Class<SessionBeansGuardPostProcessor> getWidgetType()
	{
		return SessionBeansGuardPostProcessor.class;
	}

	private class NonSerializable
	{
		// NOPMD
	}
}
