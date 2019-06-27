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
/**
 *
 */
package de.hybris.platform.personalizationcms.strategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.personalizationservices.service.CxCatalogService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class LeavesOrClosestAncestorsCatalogsLookupStrategyTest
{
	private LeavesOrClosestAncestorsCatalogLookupStrategy strategy;

	@Mock
	private CxCatalogService cxCatalogService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private CatalogModel c1;

	@Mock
	private ContentCatalogModel cc1, cc2, cc3a, cc3b;

	@Mock
	private CatalogVersionModel cv1, ccv1, ccv2, ccv3a, ccv3b;

	private List<CatalogVersionModel> allCvs;
	private List<CatalogVersionModel> leafCvs;
	private List<CatalogVersionModel> intermediateCvs;
	private List<CatalogVersionModel> topContentCvs;
	private List<CatalogVersionModel> nonContentCv;


	@Before
	public void setupTest()
	{
		MockitoAnnotations.initMocks(this);
		strategy = new LeavesOrClosestAncestorsCatalogLookupStrategy();
		strategy.setCatalogVersionService(catalogVersionService);
		strategy.setCxCatalogService(cxCatalogService);

		//setup catalog versions
		BDDMockito.given(c1.getCatalogVersions()).willReturn(new HashSet<>(Arrays.asList(cv1)));
		BDDMockito.given(cc1.getCatalogVersions()).willReturn(new HashSet<>(Arrays.asList(ccv1)));
		BDDMockito.given(cc2.getCatalogVersions()).willReturn(new HashSet<>(Arrays.asList(ccv2)));
		BDDMockito.given(cc3a.getCatalogVersions()).willReturn(new HashSet<>(Arrays.asList(ccv3a)));
		BDDMockito.given(cc3b.getCatalogVersions()).willReturn(new HashSet<>(Arrays.asList(ccv3b)));
		BDDMockito.given(cv1.getCatalog()).willReturn(c1);
		BDDMockito.given(ccv1.getCatalog()).willReturn(cc1);
		BDDMockito.given(ccv2.getCatalog()).willReturn(cc2);
		BDDMockito.given(ccv3a.getCatalog()).willReturn(cc3a);
		BDDMockito.given(ccv3b.getCatalog()).willReturn(cc3b);


		//setup hierarchy
		BDDMockito.given(cc1.getSubCatalogs()).willReturn(new HashSet<>(Arrays.asList(cc2)));
		BDDMockito.given(cc2.getSubCatalogs()).willReturn(new HashSet<>(Arrays.asList(cc3a, cc3b)));
		BDDMockito.given(cc3a.getSuperCatalog()).willReturn(cc2);
		BDDMockito.given(cc3b.getSuperCatalog()).willReturn(cc2);
		BDDMockito.given(cc2.getSuperCatalog()).willReturn(cc1);

		allCvs = Arrays.asList(cv1, ccv1, ccv2, ccv3a, ccv3b);
		leafCvs = Arrays.asList(cv1, ccv3a, ccv3b);
		intermediateCvs = Arrays.asList(ccv2);
		topContentCvs = Arrays.asList(ccv1);
		nonContentCv = Arrays.asList(cv1);
	}

	@Test
	public void shouldReturnLeavesCatalogWhenRecursionIs0()
	{
		assertCatalogsVersions(0, nonContentCv, nonContentCv, nonContentCv);
		assertCatalogsVersions(0, allCvs, nonContentCv, nonContentCv);
		assertCatalogsVersions(0, allCvs, leafCvs, leafCvs);
		assertCatalogsVersions(0, allCvs, allCvs, leafCvs);
		assertCatalogsVersions(0, allCvs, intermediateCvs, Collections.emptyList());
		assertCatalogsVersions(0, allCvs, topContentCvs, Collections.emptyList());
	}

	@Test
	public void shouldReturnClosestParentsWhenRecursionIs10()
	{
		assertCatalogsVersions(10, nonContentCv, nonContentCv, nonContentCv);
		assertCatalogsVersions(10, allCvs, nonContentCv, nonContentCv);
		assertCatalogsVersions(10, allCvs, leafCvs, leafCvs);
		assertCatalogsVersions(10, allCvs, allCvs, leafCvs);
		assertCatalogsVersions(10, allCvs, intermediateCvs, intermediateCvs);
		assertCatalogsVersions(10, allCvs, topContentCvs, topContentCvs);
	}

	/**
	 * This should not happen, but just making sure we dont get a stack overflow in this case.
	 */
	@Test
	public void shouldHandleBrokenHierarchy()
	{
		//setup broken hierarchy
		BDDMockito.given(cc1.getSubCatalogs()).willReturn(new HashSet<>(Arrays.asList(cc2)));
		BDDMockito.given(cc2.getSubCatalogs()).willReturn(new HashSet<>(Arrays.asList(cc1)));
		BDDMockito.given(cc2.getSuperCatalog()).willReturn(cc1);
		BDDMockito.given(cc1.getSuperCatalog()).willReturn(cc2);

		assertCatalogsVersions(0, Arrays.asList(ccv1, ccv2), Collections.emptyList(), Collections.emptyList());
		assertCatalogsVersions(0, Arrays.asList(ccv1, ccv2), Arrays.asList(ccv1, ccv2), Collections.emptyList());
		assertCatalogsVersions(10, Arrays.asList(ccv1, ccv2), Collections.emptyList(), Collections.emptyList());
		assertCatalogsVersions(10, Arrays.asList(ccv1, ccv2), Arrays.asList(ccv1, ccv2), Collections.emptyList());
	}


	protected void assertCatalogsVersions(final int maxRecursions, final List<CatalogVersionModel> cvsInSession,
			final List<CatalogVersionModel> cvsWithPersonalization, final List<CatalogVersionModel> assertionResults)
	{
		//by default no personalization in catalog versions
		BDDMockito.given(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(cv1))).willReturn(Boolean.FALSE);
		BDDMockito.given(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(ccv1))).willReturn(Boolean.FALSE);
		BDDMockito.given(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(ccv2))).willReturn(Boolean.FALSE);
		BDDMockito.given(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(ccv3a))).willReturn(Boolean.FALSE);
		BDDMockito.given(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(ccv3b))).willReturn(Boolean.FALSE);


		strategy.setMaxRecursions(maxRecursions);
		BDDMockito.given(catalogVersionService.getSessionCatalogVersions()).willReturn(cvsInSession);
		cvsWithPersonalization.forEach(
				cv -> BDDMockito.given(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(cv))).willReturn(Boolean.TRUE));

		final List<CatalogVersionModel> results = strategy.getCatalogVersionsForCalculation();
		Assert.assertEquals(assertionResults.size(), results.size());
		for (int i = 0; i < assertionResults.size(); i++)
		{
			Assert.assertEquals(assertionResults.get(i), results.get(i));
		}

	}
}
