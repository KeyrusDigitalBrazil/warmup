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
package de.hybris.platform.apiregistryservices.constraints;

import static de.hybris.platform.apiregistryservices.utils.EventExportUtils.DELIMITER_PROP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.order.events.SubmitOrderEvent;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.apiregistryservices.enums.EventMappingType;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.apiregistryservices.model.events.EventPropertyConfigurationModel;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;

import de.hybris.platform.util.Config;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;


@UnitTest
@RunWith(PowerMockRunner.class)
@PrepareForTest(Config.class)
public class EventMappingValidValidatorTest
{
	private static final String DELIMITER = "\\.";
	private static final String BEAN = "bean";

	private static final String KEY_REGEXP = "^[A-Za-z0-9]*$";
	private static final String VALUE_REGEXP = "^[A-Za-z0-9]+(?:.[A-Za-z0-9]+)*$";

	private EventMappingValidValidator validator;
	@Mock
	private EventMappingValid parameters;

	@Mock
	private ConstraintValidatorContext context;

	@Before
	public void setUp() throws JaloSecurityException
	{
		validator = new EventMappingValidValidator();
		when(parameters.keyFlags()).thenReturn(new Pattern.Flag[]
		{ Pattern.Flag.UNICODE_CASE });
		when(parameters.valueFlags()).thenReturn(new Pattern.Flag[]
		{ Pattern.Flag.UNICODE_CASE });
		when(parameters.keyRegexp()).thenReturn(KEY_REGEXP);
		when(parameters.valueRegexp()).thenReturn(VALUE_REGEXP);
		PowerMockito.mockStatic(Config.class);
		PowerMockito.when(Config.getString(eq(DELIMITER_PROP), anyString())).thenReturn(DELIMITER);
	}

	public EventConfigurationModel getValidInactiveConfiguration()
	{
		final EventConfigurationModel configuration = new EventConfigurationModel();
		configuration.setMappingType(EventMappingType.GENERIC);
		configuration.setEventClass(SubmitOrderEvent.class.getCanonicalName());
		final List<EventPropertyConfigurationModel> list = new ArrayList<>();
		list.add(buildEventPCM("event.order.code", "orderCode"));
		list.add(buildEventPCM("event.order.totalPrice", "totalPrice"));
		configuration.setEventPropertyConfigurations(list);
		return configuration;
	}

	public EventConfigurationModel getValidActiveConfiguration()
	{
		final EventConfigurationModel configuration = getValidInactiveConfiguration();
		configuration.setExportFlag(true);
		return configuration;
	}

	public EventConfigurationModel getInactiveConfigurationWithInexistentProperty()
	{
		final EventConfigurationModel configuration = getValidInactiveConfiguration();
		configuration.getEventPropertyConfigurations().add(buildEventPCM("event.inexistentProperty", "inexistentProperty"));
		return configuration;
	}

	public EventConfigurationModel getActiveConfigurationWithInexistentProperty()
	{
		final EventConfigurationModel configuration = getInactiveConfigurationWithInexistentProperty();
		configuration.setExportFlag(true);
		return configuration;
	}

	@Test
	public void testInitialize()
	{
		validator.initialize(parameters);
	}

	@Test
	public void testIsValidWhenExportFlagIsFalseAndMappingInvalid()
	{
		final EventConfigurationModel configuration = getInactiveConfigurationWithInexistentProperty();
		validator.initialize(parameters);
		assertThat(validator.isValid(configuration, context)).isTrue();
	}

	@Test
	public void testIsInvalidWhenExportFlagIsTrueAndMappingInvalid()
	{
		final EventConfigurationModel configuration = getActiveConfigurationWithInexistentProperty();
		validator.initialize(parameters);
		assertThat(validator.isValid(configuration, context)).isFalse();
	}

	@Test
	public void testIsValidWhenExportFlagIsTrueAndMappingValid()
	{
		final EventConfigurationModel configuration = getValidActiveConfiguration();
		validator.initialize(parameters);
		assertThat(validator.isValid(configuration, context)).isTrue();
		configuration.getEventPropertyConfigurations().add(buildEventPCM("event.seSite", "site"));
		assertThat(validator.isValid(configuration, context)).isFalse();
		configuration.getEventPropertyConfigurations().add(buildEventPCM("event", "site"));
		assertThat(validator.isValid(configuration, context)).isFalse();
	}

	@Test
	public void testIsValidWhenExportFlagIsTrueAndMappingValidForPropertyConfiguration()
	{
		final EventConfigurationModel configuration = getValidActiveConfiguration();
		validator.initialize(parameters);
		final EventPropertyConfigurationModel eventPropertyConfigurationModel = configuration.getEventPropertyConfigurations().get(0);
		eventPropertyConfigurationModel.setEventConfiguration(configuration);
		assertThat(validator.isValid(eventPropertyConfigurationModel, context)).isTrue();
	}

	@Test
	public void testIsValidWhenMappingIsEmptyOrNull()
	{
		final EventConfigurationModel configuration = new EventConfigurationModel();
		configuration.setExportFlag(true);
		configuration.setMappingType(EventMappingType.GENERIC);
		configuration.setEventClass(SubmitOrderEvent.class.getCanonicalName());
		validator.initialize(parameters);
		assertThat(validator.isValid(configuration, context)).isTrue();

		configuration.setEventPropertyConfigurations(new ArrayList<>());
		assertThat(validator.isValid(configuration, context)).isTrue();
	}

	@PrepareForTest(
	{ Registry.class, Config.class })
	@Test
	public void testBeanConfiguration()
	{
		final EventConfigurationModel configuration = new EventConfigurationModel();
		configuration.setMappingType(EventMappingType.GENERIC);
		configuration.setConverterBean(BEAN);
		configuration.setEventClass(SubmitOrderEvent.class.getCanonicalName());

		final Converter c = mock(Converter.class);
		final ApplicationContext ac = mock(ApplicationContext.class);
		PowerMockito.mockStatic(Registry.class);
		PowerMockito.when(Registry.getApplicationContext()).thenReturn(ac);
		when(ac.getBean(BEAN)).thenReturn(c);

		assertTrue(validator.isValid(configuration, context));
	}

	protected EventPropertyConfigurationModel buildEventPCM(final String mapping, final String name)
	{
		final EventPropertyConfigurationModel eventPCM = new EventPropertyConfigurationModel();
		eventPCM.setPropertyMapping(mapping);
		eventPCM.setPropertyName(name);
		eventPCM.setType("string");
		eventPCM.setTitle("test");
		return eventPCM;
	}
}
