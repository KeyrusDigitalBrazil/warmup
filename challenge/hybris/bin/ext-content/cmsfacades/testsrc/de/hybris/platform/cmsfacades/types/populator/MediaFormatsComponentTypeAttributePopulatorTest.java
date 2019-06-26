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
package de.hybris.platform.cmsfacades.types.populator;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.media.service.CMSMediaFormatService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MediaFormatsComponentTypeAttributePopulatorTest
{
	private static final String TABLET = "Tablet";
	private static final String DESKTOP = "Desktop";
	private static final String MOBILE = "Mobile";

	@InjectMocks
	private final MediaFormatsComponentTypeAttributePopulator populator = new MediaFormatsComponentTypeAttributePopulator();

	@Mock
	private TypeService typeService;
	@Mock
	private CMSMediaFormatService cmsMediaFormatService;

	@Mock
	private AttributeDescriptorModel source;
	@Mock
	private ComposedTypeModel type;
	@Mock
	private MediaFormatModel mobileFormat;
	@Mock
	private MediaFormatModel desktopFormat;
	@Mock
	private MediaFormatModel tabletFormat;

	private ComponentTypeAttributeData target;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp()
	{
		final ItemModel dummy = new DummyComponent();

		target = new ComponentTypeAttributeData();

		when(source.getEnclosingType()).thenReturn(type);
		when(typeService.getModelClass(type)).thenReturn((Class<ItemModel>) dummy.getClass());
		when(cmsMediaFormatService.getMediaFormatsByComponentType(DummyComponent.class))
		.thenReturn(Lists.newArrayList(mobileFormat, desktopFormat, tabletFormat));

		when(mobileFormat.getQualifier()).thenReturn(MOBILE);
		when(desktopFormat.getQualifier()).thenReturn(DESKTOP);
		when(tabletFormat.getQualifier()).thenReturn(TABLET);
	}

	@Test
	public void shouldNotPopulateMediaFormats_NoneFound()
	{
		when(cmsMediaFormatService.getMediaFormatsByComponentType(DummyComponent.class)).thenReturn(Collections.emptyList());
		populator.populate(source, target);
		assertThat(target.getOptions().size(), equalTo(0));
	}

	@Test
	public void shouldPopulateMediaFormats()
	{
		populator.populate(source, target);

		assertThat(target.getOptions().size(), equalTo(3));
		assertThat(target.getOptions().get(0).getId(), equalTo(MOBILE));
		assertThat(target.getOptions().get(0).getLabel(), equalTo("cms.media.format.mobile"));
		assertThat(target.getOptions().get(1).getId(), equalTo(DESKTOP));
		assertThat(target.getOptions().get(1).getLabel(), equalTo("cms.media.format.desktop"));
		assertThat(target.getOptions().get(2).getId(), equalTo(TABLET));
		assertThat(target.getOptions().get(2).getLabel(), equalTo("cms.media.format.tablet"));
	}

	private class DummyComponent extends AbstractCMSComponentModel
	{
		// Dummy component type
	}
}
