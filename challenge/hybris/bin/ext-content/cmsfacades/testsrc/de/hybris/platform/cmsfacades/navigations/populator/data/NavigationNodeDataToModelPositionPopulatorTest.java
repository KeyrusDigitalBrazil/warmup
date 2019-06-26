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
package de.hybris.platform.cmsfacades.navigations.populator.data;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NavigationNodeDataToModelPositionPopulatorTest
{
	private static final String NODE_UID = "UID";
	@Mock
	private AbstractPopulatingConverter<CMSNavigationNodeModel, NavigationNodeData> navigationModelToDataConverter;

	@InjectMocks
	private NavigationNodeDataToModelPositionPopulator populator;

	@Mock
	private NavigationNodeData source;
	@Mock
	private NavigationNodeData currentNavigationNode;

	private final CMSNavigationNodeModel target = new CMSNavigationNodeModel();

	@Before
	public void setup()
	{
		final CMSNavigationNodeModel parent = new CMSNavigationNodeModel();
		parent.setUid("parent-node");
		final CMSNavigationNodeModel node1 = mock(CMSNavigationNodeModel.class);
		final CMSNavigationNodeModel node3 = mock(CMSNavigationNodeModel.class);
		when(node1.getUid()).thenReturn("node-1");
		when(node3.getUid()).thenReturn("node-3");
		parent.setChildren(Arrays.asList(node1, target, node3));

		target.setUid(NODE_UID);
		target.setParent(parent);
		when(navigationModelToDataConverter.convert(target)).thenReturn(currentNavigationNode);
	}

	@Test
	public void testPopulateNavigationNodeDataToFirstPosition()
	{
		when(source.getPosition()).thenReturn(0);
		when(currentNavigationNode.getPosition()).thenReturn(1);

		populator.populate(source, target);
		assertThat(target.getParent().getChildren().size(), is(3));
		assertThat(target.getParent().getChildren().get(0), is(target));
	}

	@Test
	public void testPopulateNavigationNodeDataToFirstPositionWhenPositionIsLessThanZero()
	{
		when(source.getPosition()).thenReturn(-100);
		when(currentNavigationNode.getPosition()).thenReturn(1);

		populator.populate(source, target);
		assertThat(target.getParent().getChildren().size(), is(3));
		assertThat(target.getParent().getChildren().get(0), is(target));
	}

	@Test
	public void testPopulateNavigationNodeDataToLastPosition()
	{
		when(source.getPosition()).thenReturn(2);
		when(currentNavigationNode.getPosition()).thenReturn(1);

		populator.populate(source, target);
		assertThat(target.getParent().getChildren().size(), is(3));
		assertThat(target.getParent().getChildren().get(2), is(target));
	}


	@Test
	public void testPopulateNavigationNodeDataToLastPositionWhenExceeds()
	{
		when(source.getPosition()).thenReturn(1000);
		when(currentNavigationNode.getPosition()).thenReturn(1);

		populator.populate(source, target);
		assertThat(target.getParent().getChildren().size(), is(3));
		assertThat(target.getParent().getChildren().get(2), is(target));
	}

	@Test
	public void testPopulateNavigationNodeDataToMiddlePosition()
	{
		when(source.getPosition()).thenReturn(1);
		when(currentNavigationNode.getPosition()).thenReturn(2);

		populator.populate(source, target);
		assertThat(target.getParent().getChildren().size(), is(3));
		assertThat(target.getParent().getChildren().get(1), is(target));
	}

}
