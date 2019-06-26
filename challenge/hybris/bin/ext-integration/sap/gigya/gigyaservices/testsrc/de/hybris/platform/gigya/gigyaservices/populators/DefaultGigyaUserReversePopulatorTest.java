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
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSyncDirection;
import de.hybris.platform.gigya.gigyaservices.enums.GyAttributeType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaFieldMappingModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.InvalidClassException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaUserReversePopulatorTest {

	@InjectMocks
	private final DefaultGigyaUserReversePopulator populator = new DefaultGigyaUserReversePopulator();

	@Mock
	private GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao;

	@Mock
	private ModelService modelService;

	@Mock
	private GSResponse gsResponse;

	@Mock
	private CustomerModel gigyaUser;

	@Mock
	private GSObject gsObject;

	@Test
	public void testPopulateWhenResponseHasNoData() {
		Mockito.when(gsResponse.hasData()).thenReturn(false);

		populator.populate(gsResponse, gigyaUser);

		Mockito.verifyZeroInteractions(gigyaUser);
	}

	@Test
	public void testPopulateWhenResponseHasData() throws GSKeyNotFoundException {
		Mockito.when(gsResponse.hasData()).thenReturn(true);

		final GigyaFieldMappingModel fieldMapping = Mockito.mock(GigyaFieldMappingModel.class);
		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.singletonList(fieldMapping));
		Mockito.when(fieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.G2H);
		Mockito.when(fieldMapping.getHybrisType()).thenReturn(GyAttributeType.STRING);
		Mockito.when(fieldMapping.getGigyaAttributeName()).thenReturn("profile.description");
		Mockito.when(fieldMapping.getHybrisAttributeName()).thenReturn("description");

		Mockito.when(gsResponse.getData()).thenReturn(gsObject);
		Mockito.when(gsObject.containsKey("profile")).thenReturn(true);
		Mockito.when(gsObject.getObject("profile")).thenReturn(gsObject);
		Mockito.when(gsObject.getString("description")).thenReturn("some-desc");
		Mockito.when(gsObject.containsKey("description")).thenReturn(true);

		populator.populate(gsResponse, gigyaUser);

		Mockito.verify(modelService).setAttributeValue(gigyaUser, "description", "some-desc");
	}

	@Test
	public void testPopulateWhenResponseHasDataWithMultipleMappings()
			throws GSKeyNotFoundException, InvalidClassException, NullPointerException {
		Mockito.when(gsResponse.hasData()).thenReturn(true);

		final GigyaFieldMappingModel fieldMapping1 = Mockito.mock(GigyaFieldMappingModel.class);
		Mockito.when(fieldMapping1.getSyncDirection()).thenReturn(GigyaSyncDirection.G2H);
		Mockito.when(fieldMapping1.getHybrisType()).thenReturn(GyAttributeType.STRING);
		Mockito.when(fieldMapping1.getGigyaAttributeName()).thenReturn("profile.description");
		Mockito.when(fieldMapping1.getHybrisAttributeName()).thenReturn("description");

		final GigyaFieldMappingModel fieldMapping2 = Mockito.mock(GigyaFieldMappingModel.class);

		Mockito.when(fieldMapping2.getSyncDirection()).thenReturn(GigyaSyncDirection.G2H);
		Mockito.when(fieldMapping2.getHybrisType()).thenReturn(GyAttributeType.BOOLEAN);
		Mockito.when(fieldMapping2.getGigyaAttributeName()).thenReturn("isVerified");
		Mockito.when(fieldMapping2.getHybrisAttributeName()).thenReturn("isVerified");

		Mockito.when(gigyaFieldMappingGenericDao.find()).thenReturn(Arrays.asList(fieldMapping1, fieldMapping2));

		Mockito.when(gsResponse.getData()).thenReturn(gsObject);
		Mockito.when(gsObject.containsKey("profile")).thenReturn(true);
		Mockito.when(gsObject.getObject("profile")).thenReturn(gsObject);
		Mockito.when(gsObject.getString("description")).thenReturn("some-desc");
		Mockito.when(gsObject.containsKey("description")).thenReturn(true);
		Mockito.when(gsObject.containsKey("isVerified")).thenReturn(true);
		Mockito.when(gsObject.getBool("isVerified")).thenReturn(true);

		populator.populate(gsResponse, gigyaUser);

		Mockito.verify(modelService).setAttributeValue(gigyaUser, "description", "some-desc");
		Mockito.verify(modelService).setAttributeValue(gigyaUser, "isVerified", true);
	}

}
