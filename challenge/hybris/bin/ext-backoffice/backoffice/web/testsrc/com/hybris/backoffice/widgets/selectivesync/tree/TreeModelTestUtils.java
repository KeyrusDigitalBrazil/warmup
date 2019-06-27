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
package com.hybris.backoffice.widgets.selectivesync.tree;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import de.hybris.platform.catalog.model.SyncAttributeDescriptorConfigModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;


class TreeModelTestUtils
{
	private TreeModelTestUtils()
	{
		// blocks creating an instance
	}

	static SyncAttributeDescriptorConfigModel createAndAddSyncAttribute(final ComposedTypeModel spyType, final String name)
	{
		final SyncAttributeDescriptorConfigModel syncAttribute = spy(new SyncAttributeDescriptorConfigModel());
		final AttributeDescriptorModel attribute = mock(AttributeDescriptorModel.class);
		doReturn(attribute).when(syncAttribute).getAttributeDescriptor();
		doReturn(name).when(attribute).getName();
		doReturn(name).when(attribute).getQualifier();
		doReturn(spyType).when(attribute).getEnclosingType();
		doReturn(true).when(syncAttribute).getIncludedInSync();

		return syncAttribute;
	}
}
