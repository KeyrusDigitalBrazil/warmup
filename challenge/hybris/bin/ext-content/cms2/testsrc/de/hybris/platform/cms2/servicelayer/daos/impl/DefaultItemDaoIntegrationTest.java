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
package de.hybris.platform.cms2.servicelayer.daos.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.servicelayer.daos.ItemDao;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultItemDaoIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private ItemDao itemDao;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;

	private MediaModel mediaModel;
	private ContentPageModel contentPageModel;
	private CatalogVersionModel catalogVersion;

	private final String CODE_QUALIFIER = "code";
	private final String CATALOG_VERSION_QUALIFIER = "catalogVersion";
	private final String INTERNAL_URL_QUALIFIER = "internalURL";
	private final String UID_QUALIFIER = "uid";
	private final String LABEL_QUALIFIER = "label";


	private final String MEDIA_MODEL_CODE = "mediaModelCode";
	private final String MEDIA_INTERNAL_URL = "internalURL";
	private final String CONTENT_PAGE_UID = "contentPageUid";
	private final String CONTENT_PAGE_LABEL = "Content Page Label";

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");

		mediaModel = new MediaModel();
		mediaModel.setCode(MEDIA_MODEL_CODE);
		mediaModel.setCatalogVersion(catalogVersion);
		mediaModel.setInternalURL(MEDIA_INTERNAL_URL);
		modelService.save(mediaModel);

		final PageTemplateModel pageTemplateModel = new PageTemplateModel();
		pageTemplateModel.setUid("pageTemplate");
		pageTemplateModel.setCatalogVersion(catalogVersion);

		contentPageModel = new ContentPageModel();
		contentPageModel.setUid(CONTENT_PAGE_UID);
		contentPageModel.setCatalogVersion(catalogVersion);
		contentPageModel.setLabel(CONTENT_PAGE_LABEL);
		contentPageModel.setMasterTemplate(pageTemplateModel);
		modelService.save(contentPageModel);
	}

	@Test
	public void shouldRetrieveItemModelByUniqueAttributeValuesOnly()
	{
		// GIVEN
		final Map<String, Object> attributeValues = new HashMap<>();
		//unique attributes
		attributeValues.put(CODE_QUALIFIER, MEDIA_MODEL_CODE);
		attributeValues.put(CATALOG_VERSION_QUALIFIER, catalogVersion.getPk().getLongValueAsString());
		//non unique attribute
		attributeValues.put(INTERNAL_URL_QUALIFIER, MEDIA_INTERNAL_URL);

		// WHEN
		final Optional<MediaModel> optionalItemModel = itemDao
				.getItemByUniqueAttributesValues(MediaModel._TYPECODE, attributeValues);

		// THEN
		assertTrue(optionalItemModel.isPresent());
		assertThat(optionalItemModel.get().getCode(), is(MEDIA_MODEL_CODE));
		assertThat(optionalItemModel.get().getInternalURL(), is(MEDIA_INTERNAL_URL));
	}

	@Test
	public void shouldRetrieveCMSItemModelByUniqueAttributeValuesOnly()
	{
		// GIVEN
		final Map<String, Object> attributeValues = new HashMap<>();
		//unique attributes
		attributeValues.put(UID_QUALIFIER, CONTENT_PAGE_UID);
		attributeValues.put(CATALOG_VERSION_QUALIFIER, catalogVersion);
		//non unique attribute
		attributeValues.put(LABEL_QUALIFIER, CONTENT_PAGE_LABEL);

		// WHEN
		final Optional<ContentPageModel> optionalItemModel = itemDao
				.getItemByUniqueAttributesValues(ContentPageModel._TYPECODE, attributeValues);

		// THEN
		assertTrue(optionalItemModel.isPresent());
		assertThat(optionalItemModel.get().getUid(), is(CONTENT_PAGE_UID));
		assertThat(optionalItemModel.get().getLabel(), is(CONTENT_PAGE_LABEL));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailRetrievingItemIfUniqueAttributeIsNotProvided()
	{
		// GIVEN
		final Map<String, Object> attributeValues = new HashMap<>();
		//unique attributes
		attributeValues.put(CODE_QUALIFIER, MEDIA_MODEL_CODE);
		//non unique attribute
		attributeValues.put(INTERNAL_URL_QUALIFIER, MEDIA_INTERNAL_URL);

		// WHEN
		itemDao.getItemByUniqueAttributesValues(MediaModel._TYPECODE, attributeValues);
	}

	@Test
	public void shouldReturnEmptyIfNoItemModelWithProvidedUniqueAttributeValues()
	{
		// GIVEN
		final Map<String, Object> attributeValues = new HashMap<>();
		//unique attributes
		attributeValues.put(CODE_QUALIFIER, "wrongMediaCode");
		attributeValues.put(CATALOG_VERSION_QUALIFIER, catalogVersion);

		// WHEN
		final Optional<ItemModel> optionalItemModel = itemDao
				.getItemByUniqueAttributesValues(MediaModel._TYPECODE, attributeValues);

		// THEN
		assertFalse(optionalItemModel.isPresent());
	}
}
