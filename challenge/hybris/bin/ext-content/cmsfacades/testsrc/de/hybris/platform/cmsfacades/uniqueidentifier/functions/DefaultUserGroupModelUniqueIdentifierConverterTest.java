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
package de.hybris.platform.cmsfacades.uniqueidentifier.functions;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.daos.UserGroupDao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.security.access.method.P;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultUserGroupModelUniqueIdentifierConverterTest
{

	@InjectMocks
	private DefaultUserGroupModelUniqueIdentifierConverter converter;

	@Mock
	private UserGroupDao userGroupDao;
	
	@Mock
	private ObjectFactory<ItemData> itemDataDataFactory;

	private String fakeItemId = "item-id";
	
	@Mock
	private UserGroupModel userGroupModel;

	@Before
	public void setup()
	{
		when(itemDataDataFactory.getObject()).thenReturn(new ItemData());
		when(userGroupModel.getUid()).thenReturn(fakeItemId);
		when(userGroupDao.findUserGroupByUid(fakeItemId)).thenReturn(userGroupModel);
	}
	
	@Test
	public void itemTypeReturnsCorrectType()
	{
		assertThat(String.format("DefaultUserGroupModelUniqueIdentifierConverterTest itemType " +
						"should return %s typecode", UserGroupModel._TYPECODE),
				converter.getItemType(), is(UserGroupModel._TYPECODE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenItemModelIsNull()
	{
		//prepare
		UserGroupModel itemModel = null;
		//execute
		converter.convert(itemModel);
	}

	@Test
	public void shouldPassConversionModelToData()
	{
		final ItemData convert = converter.convert(userGroupModel);
		assertThat(convert.getItemId(), is(fakeItemId));
	}

	@Test
	public void shouldPassConversionDataToModel()
	{

		final ItemData itemData = new ItemData();
		itemData.setItemId(fakeItemId);
		
		final UserGroupModel convert = converter.convert(itemData);
		assertThat(convert.getUid(), is(fakeItemId));
	}
}
