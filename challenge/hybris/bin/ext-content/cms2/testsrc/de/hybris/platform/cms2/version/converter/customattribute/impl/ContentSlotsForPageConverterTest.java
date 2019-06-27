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
package de.hybris.platform.cms2.version.converter.customattribute.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.version.converter.attribute.data.VersionPayloadDescriptor;
import de.hybris.platform.cms2.version.converter.attribute.impl.CMSItemToDataConverter;
import de.hybris.platform.cms2.version.converter.customattribute.data.ContentSlotForPageRelationData;
import de.hybris.platform.cms2.version.service.CMSVersionSessionContextProvider;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;

import java.util.Arrays;
import java.util.List;

import static de.hybris.platform.core.PK.fromLong;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ContentSlotsForPageConverterTest
{

	private static final String PK_VALUE = "123";
	private static final String POSITION = "somePosition";
	private static final String DELIMITER = "__::__";

	@InjectMocks
	private ContentSlotsForPageConverter contentSlotsForPageConverter;

	@Mock
	private CMSAdminContentSlotService cmsAdminContentSlotService;

	@Mock
	private CMSItemToDataConverter cmsItemToDataConverter;

	@Mock
	private ObjectFactory<ContentSlotForPageRelationData> contentSlotRelationDataFactory;

	@Mock
	private Converter<VersionPayloadDescriptor, ItemModel> pkDataToModelConverter;

	@Mock
	private ContentSlotForPageRelationData contentSlotRelationsDataGenerator;

	@Mock
	private ContentSlotModel contentSlotModel1;

	@Mock
	private ContentSlotModel contentSlotModel2;

	@Mock
	private ContentPageModel contentPage;

	@Mock
	private CMSVersionSessionContextProvider cmsVersionSessionContextProvider;

	@Captor
	private ArgumentCaptor<ContentSlotForPageModel> propertyCaptor;

	private final PK pk = fromLong(Long.valueOf(PK_VALUE));

	@Before
	public void setup()
	{

		// GIVEN
		doReturn(contentSlotRelationsDataGenerator).when(contentSlotRelationDataFactory).getObject();

		final ContentSlotForPageModel contentSlotForPageModel1 = new ContentSlotForPageModel();
		contentSlotForPageModel1.setPosition("1");
		contentSlotForPageModel1.setContentSlot(contentSlotModel1);

		final ContentSlotForPageModel contentSlotForPageModel2 = new ContentSlotForPageModel();
		contentSlotForPageModel2.setPosition("2");
		contentSlotForPageModel2.setContentSlot(contentSlotModel2);

		when(cmsItemToDataConverter.convert(contentSlotModel1)).thenReturn(pk);
		when(cmsItemToDataConverter.convert(contentSlotModel2)).thenReturn(pk);

		when(cmsAdminContentSlotService.findAllContentSlotRelationsByPage(contentPage))
				.thenReturn(Arrays.asList(contentSlotForPageModel1, contentSlotForPageModel2));
	}

	@Test
	public void shouldReturnListOfDataGeneratorsWhenModelToDataIsCalled()
	{

		// WHEN
		final List<ContentSlotForPageRelationData> contentSlotsForPageList = contentSlotsForPageConverter
				.convertModelToData(contentPage);

		// THEN
		assertThat(contentSlotsForPageList, hasSize(2));
		verify(cmsAdminContentSlotService).findAllContentSlotRelationsByPage(contentPage);
		verify(cmsItemToDataConverter).convert(contentSlotModel1);
		verify(cmsItemToDataConverter).convert(contentSlotModel2);

	}

	@Test
	public void shouldCreateSlotForPageModelAndSaveItToSessionWhenDataToModelIsCalled()
	{
		// GIVEN
		when(pkDataToModelConverter.convert(any(VersionPayloadDescriptor.class))).thenReturn(contentSlotModel1);
		doReturn(DELIMITER).when(contentSlotRelationsDataGenerator).getDelimiter();
		when(contentSlotRelationsDataGenerator.getPk()).thenReturn(PK.parse(PK_VALUE));
		when(contentSlotRelationsDataGenerator.getPosition()).thenReturn(POSITION);

		final String valueToConvert = POSITION + DELIMITER + PK_VALUE;

		// WHEN
		contentSlotsForPageConverter.populateItemModel(contentPage, valueToConvert);

		// THEN
		verify(cmsVersionSessionContextProvider).addContentSlotForPageToCache(propertyCaptor.capture());
		ContentSlotForPageModel resultContentSlotForPageModel = propertyCaptor.getValue();
		assertThat(resultContentSlotForPageModel.getPosition(), is(POSITION));
		assertThat(resultContentSlotForPageModel.getPage(), is(contentPage));
		assertThat(resultContentSlotForPageModel.getContentSlot(), is(contentSlotModel1));
	}

}
