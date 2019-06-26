/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.gigya.gigyaservices.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSyncDirection;
import de.hybris.platform.gigya.gigyaservices.enums.GyAttributeType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaFieldMappingModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaUserPopulatorTest {

	@InjectMocks
	private final DefaultGigyaUserPopulator populator = new DefaultGigyaUserPopulator();

	@Mock
	private GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao;

	@Mock
	private ModelService modelService;

	@Mock
	private CustomerNameStrategy customerNameStrategy;

	@Mock
	private CustomerModel gigyaUser;

	@Test
	public void testPopulateWithMultipleMappingsOfDifferentTypes() throws GSKeyNotFoundException {
		final GigyaFieldMappingModel fieldMapping = Mockito.mock(GigyaFieldMappingModel.class);
		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.singletonList(fieldMapping));
		Mockito.when(fieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.H2G);
		Mockito.when(fieldMapping.getHybrisType()).thenReturn(GyAttributeType.STRING);
		Mockito.when(fieldMapping.getGigyaAttributeName()).thenReturn("profile.description");
		Mockito.when(fieldMapping.getHybrisAttributeName()).thenReturn("description");

		Mockito.when(gigyaUser.getUid()).thenReturn("uid");
		Mockito.when(gigyaUser.getGyUID()).thenReturn("gigya-uid");
		Mockito.when(gigyaUser.getName()).thenReturn("user name");
		final String[] names = new String[2];
		names[0] = "user";
		names[1] = "name";
		Mockito.when(customerNameStrategy.splitName("user name")).thenReturn(names);

		Mockito.when(modelService.getAttributeValue(gigyaUser, "description")).thenReturn("description");

		final GSObject gsObject = new GSObject();

		populator.populate(gigyaUser, gsObject);

		Assert.assertEquals("gigya-uid", gsObject.getString("UID"));
		Assert.assertTrue(gsObject.containsKey("profile"));
		final GSObject profileObject = (GSObject) gsObject.get("profile");
		Assert.assertEquals("user", profileObject.getString("firstName"));
		Assert.assertEquals("name", profileObject.getString("lastName"));
		Assert.assertEquals("uid", profileObject.getString("email"));
		Assert.assertEquals("description", profileObject.getString("description"));
	}

}
