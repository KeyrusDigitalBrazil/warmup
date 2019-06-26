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

import de.hybris.platform.apiregistryservices.model.ProcessEventConfigurationModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.apiregistryservices.enums.EventMappingType;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.apiregistryservices.model.events.EventPropertyConfigurationModel;
import de.hybris.platform.apiregistryservices.utils.EventExportUtils;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;


/**
 * Validates that the mappingConfiguration of the given
 * {@link de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel} is configured correctly.
 */
public class EventMappingValidValidator implements ConstraintValidator<EventMappingValid, Object>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(EventMappingValidValidator.class);
	private Pattern keyPattern;
	private Pattern valuePattern;

	@Override
	public void initialize(final EventMappingValid eventMappingValid)
	{
		keyPattern = generatePattern(eventMappingValid.keyRegexp(), eventMappingValid.keyFlags());
		valuePattern = generatePattern(eventMappingValid.valueRegexp(), eventMappingValid.valueFlags());
	}

	protected Pattern generatePattern(final String regexp, final javax.validation.constraints.Pattern.Flag[] flags)
	{
		int intFlag = 0;

		for (final javax.validation.constraints.Pattern.Flag flag : flags)
		{
			intFlag |= flag.getValue();
		}

		return Pattern.compile(regexp, intFlag);
	}

	@Override
	public boolean isValid(final Object o, final ConstraintValidatorContext constraintValidatorContext)
	{
		final EventConfigurationModel eventConfiguration;
		final List<EventPropertyConfigurationModel> eventPropertyConfigurations;

		if (o instanceof EventConfigurationModel)
		{
			eventConfiguration = (EventConfigurationModel) o;
			eventPropertyConfigurations = eventConfiguration.getEventPropertyConfigurations();
		}
		else if (o instanceof EventPropertyConfigurationModel)
		{
			final EventPropertyConfigurationModel eventPropertyConfigurationModel = (EventPropertyConfigurationModel) o;
			eventConfiguration = eventPropertyConfigurationModel.getEventConfiguration();
			eventPropertyConfigurations = Arrays.asList(eventPropertyConfigurationModel);
		}
		else
		{
			LOGGER.error("Provided object is not an instance of EventConfigurationModel or EventPropertyConfigurationModel: {}", o.getClass());
			return false;
		}

		if (!eventConfiguration.isExportFlag())
		{
			return true;
		}

		final Class propertyClass;
		try
		{
			if(eventConfiguration instanceof ProcessEventConfigurationModel)
			{
				propertyClass = Class.forName(((ProcessEventConfigurationModel)eventConfiguration).getProcess());
			}
			else
			{
				propertyClass = Class.forName(eventConfiguration.getEventClass());
			}
		}
		catch (final ClassNotFoundException e)
		{
			LOGGER.error(String.format("Event Class : %s , is not found. Correct the name or set Export Flag to \"false\"",
					eventConfiguration.getEventClass()), e);
			return false;
		}

		if (!EventMappingType.BEAN.equals(eventConfiguration.getMappingType()))
		{
			if (CollectionUtils.isEmpty(eventConfiguration.getEventPropertyConfigurations()))
			{
				// It's possible to save an empty list
				return true;
			}

			return eventPropertyConfigurations.stream().allMatch(this::eventPropertyConfigIsValid)
				&& eventPropertyConfigurations.stream().allMatch(eventPC -> validPropertyMapping(propertyClass, eventPC));
		}
		else
		{
			return beanExistAndHasCorrectType(eventConfiguration.getConverterBean());
		}
	}

	protected boolean validPropertyMapping(final Class reflectedClass, final EventPropertyConfigurationModel eventPC)
	{
		final String delimiter = EventExportUtils.getDelimiter();
		final String propertyMapping = eventPC.getPropertyMapping();
		if (StringUtils.isEmpty(propertyMapping) || !EventExportUtils.canSplitReference(propertyMapping, delimiter))
		{
			return false;
		}
		return existsAttribute(reflectedClass, propertyMapping.split(delimiter, 2)[1], delimiter);
	}

	protected boolean existsAttribute(final Class reflectedClass, final String reference, final String delimiter)
	{
		final String[] splitMappingReference = EventExportUtils.splitReference(reference, delimiter);

		final Optional<PropertyDescriptor> method = findMethod(reflectedClass, splitMappingReference[0]);

		if (!method.isPresent())
		{
			return false;
		}

		return splitMappingReference.length <= 1
				|| existsAttribute(method.get().getPropertyType(), splitMappingReference[1], delimiter);
	}

	protected Optional<PropertyDescriptor> findMethod(final Class reflectedClass, final String propertyName)
	{
		return Arrays.stream(PropertyUtils.getPropertyDescriptors(reflectedClass))
				.filter(method -> method.getName().equals(propertyName)).findFirst();
	}

	protected boolean beanExistAndHasCorrectType(final String beanName)
	{
		final ApplicationContext applicationContext = Registry.getApplicationContext();
		try
		{
			final Object bean = applicationContext.getBean(beanName);
			if (bean instanceof Converter)
			{
				return true;
			}
			else
			{
				LOGGER.error("Bean : {} , has not correct type. Current type is {}, but Converter expected. Correct the ConverterBean or set Export Flag to \"false\"", beanName, bean.getClass());
				return false;
			}
		}
		catch (IllegalArgumentException | NoSuchBeanDefinitionException e)
		{
			LOGGER.error(
					String.format("Bean : %s , is not found. Correct the ConverterBean or set Export Flag to \"false\"", beanName), e);
			return false;
		}
	}

	protected boolean eventPropertyConfigIsValid(final EventPropertyConfigurationModel eventPC)
	{
		return keyPattern.matcher(eventPC.getPropertyName()).matches()
			&& valuePattern.matcher(eventPC.getPropertyMapping()).matches();
	}
}
