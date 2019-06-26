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
package de.hybris.deltadetection.interceptors;

import de.hybris.deltadetection.enums.ItemVersionMarkerStatus;
import de.hybris.deltadetection.model.ItemVersionMarkerModel;
import de.hybris.deltadetection.model.StreamConfigurationContainerModel;
import de.hybris.deltadetection.model.StreamConfigurationModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class StreamConfigurationRemoveInterceptorTest extends ServicelayerBaseTest
{
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;
	private StreamConfigurationContainerModel container;
	private TitleModel title;

	@Before
	public void setUp() throws Exception
	{
		container = createContainer();
		title = createTitle();
	}

	private StreamConfigurationContainerModel createContainer()
	{
		final StreamConfigurationContainerModel container = modelService.create(StreamConfigurationContainerModel.class);
		container.setId("TEST_CONTAINER");

		modelService.save(container);

		return container;
	}

	private TitleModel createTitle()
	{
		final TitleModel title = modelService.create(TitleModel.class);
		title.setCode("test1");

		modelService.save(title);

		return title;
	}

	@Test
	public void shouldRemoveCorrespondingItemVersionMarkesUponStreamConfigRemoval() throws Exception
	{
		// given
		final StreamConfigurationModel configuration = createStreamConfiguration(container);
        final ItemVersionMarkerModel ivm = createVersionMarker(configuration);

        // when
        modelService.remove(configuration);
        Thread.sleep(6000);

		// then
        assertThat(modelService.isRemoved(configuration)).isTrue();
        assertThat(modelService.isRemoved(ivm)).isTrue();
	}

	private StreamConfigurationModel createStreamConfiguration(final StreamConfigurationContainerModel container)
	{
		final StreamConfigurationModel config = modelService.create(StreamConfigurationModel.class);
		config.setStreamId("TEST_CONFIG");
		config.setItemTypeForStream(typeService.getComposedTypeForCode("Title"));
		config.setContainer(container);

		modelService.save(config);

		return config;
	}

	private ItemVersionMarkerModel createVersionMarker(final StreamConfigurationModel config)
	{
		final ItemVersionMarkerModel ivm = modelService.create(ItemVersionMarkerModel.class);
		ivm.setStreamId(config.getStreamId());
		ivm.setItemComposedType(config.getItemTypeForStream());
		ivm.setStatus(ItemVersionMarkerStatus.ACTIVE);
		ivm.setVersionTS(new Date());
        ivm.setItemPK(title.getPk().getLong());

		modelService.save(ivm);

		return ivm;
	}

}
