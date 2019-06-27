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
package de.hybris.y2ysync.model;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.y2ysync.services.SyncConfigService;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;


@IntegrationTest
public class Y2YStreamConfigurationValidateInterceptorTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private SyncConfigService syncConfigService;

	@Resource
	TypeService typeService;

	@Test
	public void shouldNotPermitColumnsWithAttributesNotFromType()
	{
		// given
		final Y2YStreamConfigurationContainerModel testContainer = createContainer("testContainer");
		final Y2YColumnDefinitionModel y2yColumnDefinition = createColumn("foo",
				randomAttributeDescriptorForClass(MediaModel.class));
		final Y2YStreamConfigurationModel streamConfiguration = syncConfigService.createStreamConfiguration(testContainer, "User",
				ImmutableSet.of(y2yColumnDefinition));

		try
		{
			// when
			modelService.save(streamConfiguration);
			fail();
		}
		catch (final ModelSavingException exception)
		{
			// then
			assertThat(exception.getCause()).isExactlyInstanceOf(InterceptorException.class);
		}
	}

	private Y2YColumnDefinitionModel createColumn(final String name, final AttributeDescriptorModel attributeDescriptor)
	{
		final Y2YColumnDefinitionModel y2yColumnDefinition = modelService.create(Y2YColumnDefinitionModel.class);
		y2yColumnDefinition.setColumnName(name);
		y2yColumnDefinition.setAttributeDescriptor(attributeDescriptor);

		return y2yColumnDefinition;
	}

	private Y2YStreamConfigurationContainerModel createContainer(final String id)
	{
		return syncConfigService.createStreamConfigurationContainer(id);
	}

	private AttributeDescriptorModel randomAttributeDescriptorForClass(final Class clazz)
	{
		final ComposedTypeModel composedMedia = typeService.getComposedTypeForClass(clazz);
		final Collection<AttributeDescriptorModel> attributes = composedMedia.getDeclaredattributedescriptors();
		return attributes.iterator().next();
	}

}
