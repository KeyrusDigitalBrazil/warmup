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
package de.hybris.deltadetection;


import de.hybris.deltadetection.model.StreamConfigurationContainerModel;
import de.hybris.deltadetection.model.StreamConfigurationModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Y2YTestDataGenerator
{
	private final ModelService modelService;
	private final TypeService typeService;


	public Y2YTestDataGenerator(final ModelService modelService, final TypeService typeService)
	{
		this.modelService = modelService;
		this.typeService = typeService;
	}

	public TitlesFixture generateTitles(final int titlesNumber)
	{
		final List<TitleModel> titles = new ArrayList<>();

		for (int i = 0; i < titlesNumber; ++i)
		{
			final TitleModel title = modelService.create(TitleModel.class);
			title.setCode(UUID.randomUUID().toString());
			titles.add(title);
		}
		modelService.saveAll();

		final ComposedTypeModel unitComposedType = typeService.getComposedTypeForClass(TitleModel.class);

		final StreamConfigurationContainerModel streamCfgContainer = modelService.create(StreamConfigurationContainerModel.class);
		streamCfgContainer.setId(UUID.randomUUID().toString());
		modelService.save(streamCfgContainer);

		final String streamId = UUID.randomUUID().toString();

		final StreamConfigurationModel streamCfg = modelService.create(StreamConfigurationModel.class);
		streamCfg.setStreamId(streamId);
		streamCfg.setContainer(streamCfgContainer);
		streamCfg.setItemTypeForStream(unitComposedType);
		streamCfg.setWhereClause("not used");
		streamCfg.setInfoExpression("#{getPk()}");

		modelService.save(streamCfg);

		return new TitlesFixture(streamId, unitComposedType);
	}


	public static class TitlesFixture
	{
		private final String streamId;
		private final ComposedTypeModel composedType;

		public TitlesFixture(final String streamId, final ComposedTypeModel composedType)
		{
			this.streamId = streamId;
			this.composedType = composedType;
		}

		public String getStreamId()
		{
			return streamId;
		}

		public ComposedTypeModel getComposedType()
		{
			return composedType;
		}
	}
}
