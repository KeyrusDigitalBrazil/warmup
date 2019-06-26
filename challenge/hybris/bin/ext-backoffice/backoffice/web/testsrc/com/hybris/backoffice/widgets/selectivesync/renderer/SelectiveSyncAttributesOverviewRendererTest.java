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
package com.hybris.backoffice.widgets.selectivesync.renderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.SyncAttributeDescriptorConfigModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.Arrays;
import java.util.Collection;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.core.WidgetSocket;


@RunWith(MockitoJUnitRunner.class)
public class SelectiveSyncAttributesOverviewRendererTest
{

	private Collection<SyncAttributeDescriptorConfigModel> syncAttributeConfigurations;

	@Spy
	private SelectiveSyncAttributesOverviewRenderer renderer;

	@Mock
	private WidgetSocket socket1;
	@Mock
	private WidgetSocket socket2;

	@Before
	public void setUp()
	{
		final SyncAttributeDescriptorConfigModel notIncludedAttribute1 = mock(SyncAttributeDescriptorConfigModel.class);
		final SyncAttributeDescriptorConfigModel notIncludedAttribute2 = mock(SyncAttributeDescriptorConfigModel.class);
		final SyncAttributeDescriptorConfigModel notIncludedAttribute3 = mock(SyncAttributeDescriptorConfigModel.class);
		final SyncAttributeDescriptorConfigModel partiallyIncludedAttribute1 = mock(SyncAttributeDescriptorConfigModel.class);
		final SyncAttributeDescriptorConfigModel partiallyIncludedAttribute2 = mock(SyncAttributeDescriptorConfigModel.class);
		final SyncAttributeDescriptorConfigModel fullyIncludedAttribute1 = mock(SyncAttributeDescriptorConfigModel.class);
		final SyncAttributeDescriptorConfigModel fullyIncludedAttribute2 = mock(SyncAttributeDescriptorConfigModel.class);

		when(notIncludedAttribute1.getIncludedInSync()).thenReturn(Boolean.FALSE);
		when(notIncludedAttribute2.getIncludedInSync()).thenReturn(Boolean.FALSE);
		when(notIncludedAttribute3.getIncludedInSync()).thenReturn(Boolean.FALSE);
		when(partiallyIncludedAttribute1.getIncludedInSync()).thenReturn(Boolean.TRUE);
		when(partiallyIncludedAttribute2.getIncludedInSync()).thenReturn(Boolean.FALSE);
		when(fullyIncludedAttribute1.getIncludedInSync()).thenReturn(Boolean.TRUE);
		when(fullyIncludedAttribute2.getIncludedInSync()).thenReturn(Boolean.TRUE);

		final AttributeDescriptorModel notIncludedDesc = mock(AttributeDescriptorModel.class);
		final AttributeDescriptorModel partiallyIncludedDesc = mock(AttributeDescriptorModel.class);
		final AttributeDescriptorModel fullyIncludedDesc = mock(AttributeDescriptorModel.class);

		when(notIncludedAttribute1.getAttributeDescriptor()).thenReturn(notIncludedDesc);
		when(notIncludedAttribute2.getAttributeDescriptor()).thenReturn(notIncludedDesc);
		when(notIncludedAttribute3.getAttributeDescriptor()).thenReturn(notIncludedDesc);
		when(partiallyIncludedAttribute1.getAttributeDescriptor()).thenReturn(partiallyIncludedDesc);
		when(partiallyIncludedAttribute2.getAttributeDescriptor()).thenReturn(partiallyIncludedDesc);
		when(fullyIncludedAttribute1.getAttributeDescriptor()).thenReturn(fullyIncludedDesc);
		when(fullyIncludedAttribute2.getAttributeDescriptor()).thenReturn(fullyIncludedDesc);

		final ComposedTypeModel notIncludedType = mock(ComposedTypeModel.class);
		final ComposedTypeModel partiallyIncludedType = mock(ComposedTypeModel.class);
		final ComposedTypeModel fullyIncludedType = mock(ComposedTypeModel.class);

		when(notIncludedDesc.getEnclosingType()).thenReturn(notIncludedType);
		when(partiallyIncludedDesc.getEnclosingType()).thenReturn(partiallyIncludedType);
		when(fullyIncludedDesc.getEnclosingType()).thenReturn(fullyIncludedType);

		syncAttributeConfigurations = Arrays.asList(notIncludedAttribute1, notIncludedAttribute2, notIncludedAttribute3,
				partiallyIncludedAttribute1, partiallyIncludedAttribute2, fullyIncludedAttribute1, fullyIncludedAttribute2);
	}

	@Test
	public void testCalculateExcludedAttributes()
	{
		assertThat(renderer.calculateExcludedAttributes(syncAttributeConfigurations)).isEqualTo(4);
	}

	@Test
	public void testCalculateIncludedAttributes()
	{
		assertThat(renderer.calculateIncludedAttributes(syncAttributeConfigurations)).isEqualTo(3);
	}

	@Test
	public void testCalculateExcludedTypes()
	{
		assertThat(renderer.calculateExcludedTypes(syncAttributeConfigurations)).isEqualTo(1);
	}

	@Test
	public void testCalculateIncludedTypes()
	{
		assertThat(renderer.calculateIncludedTypes(syncAttributeConfigurations)).isEqualTo(2);
	}

	@Test
	public void currentObjectSocketShouldBeAvailable()
	{
		doReturn("socket1").when(socket1).getId();
		doReturn(SelectiveSyncAttributesOverviewRenderer.SOCKET_OUTPUT_SEL_SYNC_OBJECT).when(socket2).getId();
		doReturn(Arrays.asList(socket1, socket2)).when(renderer).getAllOutputs(Mockito.any());

		assertThat(renderer.isCurrentObjectSocketAvailable(null)).isEqualTo(true);
	}

	@Test
	public void currentObjectSocketShouldBeUnavailableAmongOtherSockets()
	{
		doReturn("socket1").when(socket1).getId();
		doReturn("socket2").when(socket2).getId();
		doReturn(Arrays.asList(socket1, socket2)).when(renderer).getAllOutputs(Mockito.any());

		assertThat(renderer.isCurrentObjectSocketAvailable(null)).isEqualTo(false);
	}

	@Test
	public void currentObjectSocketShouldBeUnavailableWhenNoSockets()
	{
		doReturn(Lists.emptyList()).when(renderer).getAllOutputs(Mockito.any());

		assertThat(renderer.isCurrentObjectSocketAvailable(null)).isEqualTo(false);
	}

}
