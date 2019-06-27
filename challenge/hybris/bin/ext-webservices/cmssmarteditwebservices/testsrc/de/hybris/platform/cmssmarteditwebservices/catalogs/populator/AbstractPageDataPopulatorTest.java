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
package de.hybris.platform.cmssmarteditwebservices.catalogs.populator;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractPageDataPopulatorTest
{
	private static final String NAME = "FAQ Page";
	private static final String UID = "uid_faq";
	private static final String UUID = "uuid_faq";

	@InjectMocks
	private AbstractPageDataPopulator populator;

	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private AbstractPageData target;

	private AbstractPageModel source;
	private ItemData itemData;

	@Before
	public void setUp()
	{
		source = new AbstractPageModel();
		source.setUid(UID);
		source.setName(NAME);
		source.setCatalogVersion(catalogVersion);
	}

	@Test
	public void shouldPopulatePageData()
	{
		itemData = new ItemData();
		itemData.setItemId(UUID);
		when(uniqueItemIdentifierService.getItemData(catalogVersion)).thenReturn(Optional.of(itemData));

		populator.populate(source, target);

		verify(target).setUid(UID);
		verify(target).setName(NAME);
		verify(target).setCatalogVersionUuid(UUID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldFailCatalogVersionUUIDNotFound()
	{
		when(uniqueItemIdentifierService.getItemData(catalogVersion)).thenReturn(Optional.empty());

		populator.populate(source, target);
	}

}
