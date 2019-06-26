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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.deltadetection.model.StreamConfigurationModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.y2ysync.jalo.Y2YStreamConfigurationContainer;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.UUID;


@IntegrationTest
public class Y2YColumnDefinitionModelTest extends ServicelayerBaseTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private TypeService typeService;

	@Test
	public void shouldSaveLongY2YColumnDefinitionAttributes()
	{
		final ComposedTypeModel productComposedType = typeService.getComposedTypeForClass(ProductModel.class);

		final Y2YStreamConfigurationContainerModel container = createContainer();
		final Y2YStreamConfigurationModel streamConfiguration = createStreamConfiguration(productComposedType, container);

		final Y2YColumnDefinitionModel y2yColumnDefinition = modelService.create(Y2YColumnDefinitionModel.class);
		y2yColumnDefinition.setColumnName(UUID.randomUUID().toString());
		y2yColumnDefinition.setAttributeDescriptor(getAttributeDescriptor(productComposedType));
		y2yColumnDefinition.setStreamConfiguration(streamConfiguration);
		y2yColumnDefinition.setImpexHeader(RandomStringUtils.randomAlphanumeric(10_000));

		modelService.saveAll(y2yColumnDefinition);
	}

	private AttributeDescriptorModel getAttributeDescriptor(final ComposedTypeModel productComposedType)
	{
		final Collection<AttributeDescriptorModel> attributes = productComposedType.getDeclaredattributedescriptors();
		return attributes.iterator().next();
	}

	private Y2YStreamConfigurationModel createStreamConfiguration(final ComposedTypeModel productComposedType,
			final Y2YStreamConfigurationContainerModel container)
	{
		final Y2YStreamConfigurationModel streamConfiguration = modelService.create(Y2YStreamConfigurationModel.class);
		streamConfiguration.setStreamId(UUID.randomUUID().toString());
		streamConfiguration.setItemTypeForStream(productComposedType);
		streamConfiguration.setContainer(container);
		modelService.save(streamConfiguration);
		return streamConfiguration;
	}

	private Y2YStreamConfigurationContainerModel createContainer()
	{
		final Y2YStreamConfigurationContainerModel container = modelService.create(Y2YStreamConfigurationContainerModel.class);
		container.setId(UUID.randomUUID().toString());
		modelService.save(container);
		return container;
	}
}
