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
package de.hybris.platform.cmsfacades.media.namedquery;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.SortDirection;
import de.hybris.platform.cms2.exceptions.InvalidNamedQueryException;
import de.hybris.platform.cms2.namedquery.NamedQuery;
import de.hybris.platform.cms2.namedquery.Sort;
import de.hybris.platform.cms2.namedquery.service.NamedQueryService;
import de.hybris.platform.cmsfacades.util.BaseIntegrationTest;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;



@IntegrationTest
public class MediaNamedQueryIntegrationTest extends BaseIntegrationTest
{
	private static final String CATALOG_VERSION = MediaModel.CATALOGVERSION;
	private static final String QUERY_NAME = "namedQueryMediaSearchByCodeCatalogVersion";
	private static final String CODE = MediaModel.CODE;

	private static final String CODE_MEDIA = "media-code";
	private static final String CODE_SEARCH = "%" + CODE_MEDIA + "%";

	@Resource
	private NamedQueryService flexibleSearchNamedQueryService;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private ModelService modelService;

	private CatalogVersionModel catalogVersion;
	private String code;
	private NamedQuery namedQuery;

	@Before
	public void setUp()
	{
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		code = CODE_SEARCH;

		namedQuery = new NamedQuery();
		namedQuery.setQueryName(QUERY_NAME);
		namedQuery.setCurrentPage(0);
		namedQuery.setPageSize(10);

		final Map<String, Object> parameters = new HashMap<>();
		parameters.put(CATALOG_VERSION, catalogVersion);
		parameters.put(CODE, code);
		namedQuery.setParameters(parameters);
	}

	@Test
	public void shouldGetMediaByNamedQuery_AssertWhereClause() throws InvalidNamedQueryException
	{
		createMediaModels(CODE_MEDIA, 5);
		namedQuery.setSort(Arrays.asList(new Sort().withParameter(CODE).withDirection(SortDirection.ASC)));
		final List<MediaModel> mediaList = flexibleSearchNamedQueryService.search(namedQuery);

		assertEquals(5, mediaList.size());
		assertEquals(CODE_MEDIA + "0", mediaList.get(0).getCode());
	}

	@Test
	public void shouldGetMediaByNamedQuery_AssertOrdering() throws InvalidNamedQueryException
	{
		namedQuery.setSort(Arrays.asList(new Sort().withParameter(CODE).withDirection(SortDirection.DESC)));

		createMediaModels(CODE_MEDIA, 9);
		final List<MediaModel> mediaList = flexibleSearchNamedQueryService.search(namedQuery);

		for (int i = 0; i < 8; i++)
		{
			assertEquals(CODE_MEDIA + (8 - i), mediaList.get(i).getCode());
		}
	}

	protected void createMediaModels(final String code, final int quantity)
	{
		for (int i = 0; i < quantity; i++)
		{
			final MediaModel media = modelService.create(MediaModel.class);
			media.setCode(code + i);
			media.setCatalogVersion(catalogVersion);
			modelService.save(media);
		}
	}
}
