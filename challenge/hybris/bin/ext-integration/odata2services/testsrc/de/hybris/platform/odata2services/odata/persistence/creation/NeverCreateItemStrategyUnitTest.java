/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2services.odata.persistence.creation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.persistence.InvalidDataException;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NeverCreateItemStrategyUnitTest
{
	@InjectMocks
	private NeverCreateItemStrategy strategy;

	@Test
	public void testThatExceptionIsThrown() throws EdmException
	{
		final StorageRequest storageRequest = mock(StorageRequest.class);
		when(storageRequest.getEntitySet()).thenReturn(mock(EdmEntitySet.class));
		final EdmEntityType entityType = mock(EdmEntityType.class);
		when(entityType.getName()).thenReturn("AType");
		when(storageRequest.getEntityType()).thenReturn(entityType);
		when(storageRequest.getODataEntry()).thenReturn(mock(ODataEntry.class));

		assertThatThrownBy(()-> strategy.createItem(storageRequest))
				.isInstanceOf(InvalidDataException.class)
				.hasFieldOrPropertyWithValue("errorCode", "missing_nav_property");
	}
}
