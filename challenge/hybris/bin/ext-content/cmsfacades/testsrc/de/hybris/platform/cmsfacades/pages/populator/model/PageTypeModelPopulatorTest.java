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
package de.hybris.platform.cmsfacades.pages.populator.model;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cmsfacades.data.CatalogPageData;
import de.hybris.platform.cmsfacades.data.PageTypeData;
import de.hybris.platform.cmsfacades.pages.service.PageTypeMapping;
import de.hybris.platform.cmsfacades.pages.service.PageTypeMappingRegistry;
import de.hybris.platform.cmsfacades.pages.service.impl.DefaultPageTypeMapping;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PageTypeModelPopulatorTest
{
	private static final String TEST_TYPE = "testType";
	private static final String CATALOG_TYPE_DATA = "catalogPageData";

	@Mock
	private PageTypeMappingRegistry pageTypeMappingRegistry;
	@InjectMocks
	private PageTypeModelPopulator populator;

	private CMSPageTypeModel source;
	private PageTypeData target;
	private PageTypeMapping pageTypeMapping;

	@Before
	public void setUp()
	{
		source = new CMSPageTypeModel();
		target = new PageTypeData();

		pageTypeMapping = new DefaultPageTypeMapping();
		pageTypeMapping.setTypecode(TEST_TYPE);
		pageTypeMapping.setTypedata(CatalogPageData.class);

		when(pageTypeMappingRegistry.getPageTypeMapping(TEST_TYPE)).thenReturn(Optional.of(pageTypeMapping));
	}

	@Test
	public void shouldSetType()
	{
		source.setCode(TEST_TYPE);
		populator.populate(source, target);

		assertThat(target.getType(), is(CATALOG_TYPE_DATA));
	}

	@Test
	public void shouldNotSetType()
	{
		populator.populate(source, target);

		assertThat(target.getType(), nullValue());
	}

}
