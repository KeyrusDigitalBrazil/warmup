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
package de.hybris.platform.cms2.version.converter.customattribute.data;

import static de.hybris.platform.core.PK.fromLong;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ContentSlotForPageRelationDataTest
{

	private final String position = "1";
	private final PK pk = fromLong(Long.valueOf(123));

	@InjectMocks
	private ContentSlotForPageRelationData contentSlotForPageRelationData;

	@Test
	public void test()
	{

		// WHEN
		contentSlotForPageRelationData = new ContentSlotForPageRelationData();
		contentSlotForPageRelationData.setPosition(position);
		contentSlotForPageRelationData.setPk(pk);

		// THEN
		assertThat(contentSlotForPageRelationData.toData(), is(position + "__::__" + pk.toString()));
	}

}
