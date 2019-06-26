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
package de.hybris.platform.acceleratorservices.document.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.document.dao.DocumentPageDao;
import de.hybris.platform.acceleratorservices.model.cms2.pages.DocumentPageModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDocumentPageServiceTest
{
	@InjectMocks
	private DefaultDocumentPageService documentPageService;

	@Mock
	private DocumentPageDao documentPageDao;


	@Test
	public void testFindDocumentPageByFrontendTemplate()
	{
		//Given
		final DocumentPageModel documentPageModel = mock(DocumentPageModel.class);
		final CatalogVersionModel catalogVersionModel = mock(CatalogVersionModel.class);
		when(documentPageDao.findDocumentPageByTemplateName(Mockito.anyString(), anyCollectionOf(CatalogVersionModel.class))).thenReturn(
				documentPageModel);

		//When
		final DocumentPageModel result = documentPageService.findDocumentPageByTemplateName("testTemplate", Collections.singleton(catalogVersionModel));

		//Then
		assertEquals(documentPageModel, result);

	}

}
