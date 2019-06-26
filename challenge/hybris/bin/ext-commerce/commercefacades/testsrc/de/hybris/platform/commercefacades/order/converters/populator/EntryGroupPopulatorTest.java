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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Unit tests for {@link EntryGroupPopulator}
 */
public class EntryGroupPopulatorTest
{
	private static final String ROOT_GROUP_LABEL = "RootGroupLabel";
	private static final GroupType GROUP_TYPE = GroupType.STANDALONE;
	private static final Integer ROOT_GROUP_NUMBER = 0;
	private static final String ROOT_GROUP_EXTERNAL_REFERENCE_ID = "id";

	private EntryGroupPopulator entryGroupPopulator;
	private AbstractPopulatingConverter<EntryGroup, EntryGroupData> entryGroupConverter;
	private EntryGroup rootGroup;

	@Before
	public void init()
	{
		entryGroupPopulator = new EntryGroupPopulator();
		rootGroup = new EntryGroup();
		entryGroupConverter = new AbstractPopulatingConverter<>();
		entryGroupConverter.setPopulators(Collections.singletonList(entryGroupPopulator));
		entryGroupPopulator.setEntryGroupConverter(entryGroupConverter);

		rootGroup.setLabel(ROOT_GROUP_LABEL);
		rootGroup.setGroupNumber(ROOT_GROUP_NUMBER);
		rootGroup.setExternalReferenceId(ROOT_GROUP_EXTERNAL_REFERENCE_ID);
		rootGroup.setGroupType(GROUP_TYPE);
	}

	@Test
	public void testPopulateNoChild()
	{
		final EntryGroupData rootGroupCopy = new EntryGroupData();
		rootGroup.setChildren(null);

		entryGroupPopulator.populate(rootGroup, rootGroupCopy);

		assertThat(rootGroupCopy.getLabel()).isEqualTo(ROOT_GROUP_LABEL);
		assertThat(rootGroupCopy.getGroupNumber()).isEqualTo(ROOT_GROUP_NUMBER);
		assertThat(rootGroupCopy.getExternalReferenceId()).isEqualTo(ROOT_GROUP_EXTERNAL_REFERENCE_ID);
		assertThat(rootGroupCopy.getGroupType()).isEqualTo(GROUP_TYPE);
		assertThat(rootGroupCopy.getParent()).isNull();
		assertThat(rootGroupCopy.getOrderEntries()).isNull();
		assertThat(rootGroupCopy.getChildren()).isEmpty();
	}

	@Test
	public void testPopulateWithChildren()
	{
		final EntryGroupData rootGroupData = new EntryGroupData();
		final EntryGroup childGroup = new EntryGroup();
		childGroup.setLabel("test");
		childGroup.setExternalReferenceId("child");
		childGroup.setPriority(45);
		childGroup.setGroupType(GroupType.STANDALONE);
		childGroup.setErroneous(Boolean.TRUE);
		childGroup.setGroupNumber(109);
		rootGroup.setChildren(Collections.singletonList(childGroup));

		entryGroupPopulator.populate(rootGroup, rootGroupData);

		assertThat(rootGroupData.getChildren()).isNotEmpty();
		assertThat(rootGroupData.getChildren().size()).isEqualTo(1);
		final EntryGroupData childData = rootGroupData.getChildren().get(0);
		assertThat(childData.getChildren()).isEmpty();
		assertThat(childData.getGroupNumber()).isEqualTo(childGroup.getGroupNumber());
		assertThat(childData.getErroneous()).isEqualTo(childData.getErroneous());
		assertThat(childData.getParent()).isEqualTo(rootGroupData);
		assertThat(childData.getPriority()).isEqualTo(childGroup.getPriority());
		assertThat(childData.getExternalReferenceId()).isEqualTo(childGroup.getExternalReferenceId());
		assertThat(childData.getLabel()).isEqualTo(childGroup.getLabel());
		assertThat(childData.getGroupType()).isEqualTo(childGroup.getGroupType());
	}
}
