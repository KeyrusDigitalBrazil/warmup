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
package de.hybris.platform.commercefacades.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link EntryGroupData} serialization, notably parent/child and rootGroup references.
 */
@UnitTest
public class EntryGroupDataSerializationTest
{
	@Test
	public void testSerializeChildReferences() throws IOException
	{
		final EntryGroupData rootGroup = new EntryGroupData();
		rootGroup.setGroupNumber(1);

		final EntryGroupData childGroup1 = new EntryGroupData();
		childGroup1.setGroupNumber(2);
		childGroup1.setParent(rootGroup);

		final EntryGroupData childGroup2 = new EntryGroupData();
		childGroup2.setGroupNumber(3);
		childGroup2.setParent(rootGroup);
		rootGroup.setChildren(Arrays.asList(childGroup1, childGroup2));

		final ObjectMapper mapper = new ObjectMapper();
		final String groupJson = mapper.writeValueAsString(rootGroup);

		final EntryGroupData resultRootGroup = mapper.readValue(groupJson, EntryGroupData.class);
		assertEquals(2, resultRootGroup.getChildren().size());
		final EntryGroupData resultChildGroup1 = resultRootGroup.getChildren().get(0);
		final EntryGroupData resultChildGroup2 = resultRootGroup.getChildren().get(1);

		assertEquals(2, resultChildGroup1.getGroupNumber().intValue());
		assertEquals(resultRootGroup, resultChildGroup1.getParent());
		assertNull(resultChildGroup1.getChildren());
		assertEquals(3, resultChildGroup2.getGroupNumber().intValue());
		assertEquals(resultRootGroup, resultChildGroup2.getParent());
		assertNull(resultChildGroup2.getChildren());
	}

	@Test
	public void testSerializeRootGroupReference() throws IOException
	{
		final EntryGroupData rootGroup = new EntryGroupData();
		rootGroup.setGroupNumber(1);

		final EntryGroupData childGroup = new EntryGroupData();
		childGroup.setGroupNumber(2);
		childGroup.setParent(rootGroup);
		childGroup.setRootGroup(rootGroup);
		rootGroup.setChildren(Collections.singletonList(childGroup));

		final EntryGroupData leafGroup = new EntryGroupData();
		leafGroup.setGroupNumber(3);
		leafGroup.setParent(childGroup);
		leafGroup.setRootGroup(rootGroup);
		childGroup.setChildren(Collections.singletonList(leafGroup));

		final ObjectMapper mapper = new ObjectMapper();
		final String groupJson = mapper.writeValueAsString(rootGroup);

		final EntryGroupData resultRootGroup = mapper.readValue(groupJson, EntryGroupData.class);
		final EntryGroupData resultChildGroup = resultRootGroup.getChildren().get(0);
		final EntryGroupData resultLeafGroup = resultChildGroup.getChildren().get(0);

		assertEquals(resultRootGroup, resultChildGroup.getRootGroup());
		assertEquals(resultRootGroup, resultLeafGroup.getRootGroup());
	}
}
