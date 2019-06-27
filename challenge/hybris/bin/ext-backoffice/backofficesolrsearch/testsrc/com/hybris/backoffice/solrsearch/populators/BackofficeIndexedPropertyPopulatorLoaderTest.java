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
package com.hybris.backoffice.solrsearch.populators;

import static com.hybris.backoffice.solrsearch.populators.BackofficeIndexedPropertyPopulatorLoader.BACKOFFICE_INDEXED_PROPERTY_POPULATOR_BEAN_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BackofficeIndexedPropertyPopulatorLoaderTest
{

	@InjectMocks
	private BackofficeIndexedPropertyPopulatorLoader populatorLoader;

	@Mock
	private BeanFactory beanFacotry;
	@Mock
	private ApplicationContext applicationContext;
	@Mock
	private BackofficeIndexedPropertyPopulator populator;

	@Before
	public void setUp()
	{
		doReturn(new String[]
		{ BackofficeIndexedPropertyPopulatorLoader.INDEXED_PROPERTY_CONVERTER_ALIAS }).when(beanFacotry).getAliases(any());

		doReturn(populator).when(applicationContext).getBean(BACKOFFICE_INDEXED_PROPERTY_POPULATOR_BEAN_NAME);
	}

	@Test
	public void postProcessAfterInitialization()
	{
		//given
		final AbstractPopulatingConverter populatingConverter = Mockito.mock(AbstractPopulatingConverter.class);
		doReturn(Collections.singletonList(mock(Populator.class))).when(populatingConverter).getPopulators();

		//when
		populatorLoader.postProcessAfterInitialization(populatingConverter, BACKOFFICE_INDEXED_PROPERTY_POPULATOR_BEAN_NAME);

		//then
		final ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
		verify(populatingConverter).setPopulators(argumentCaptor.capture());
		assertThat(argumentCaptor.getValue()).hasSize(2);
	}
}
