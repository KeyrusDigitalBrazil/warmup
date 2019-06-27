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
package de.hybris.platform.cmsfacades.types.service.impl;

import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.CATEGORY;
import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.CATEGORYCODE;
import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.CATEGORYPOS;
import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.CONTENTPAGE;
import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.CONTENTPAGELABELORID;
import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.CONTENTPAGEPOS;
import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.EXTERNAL;
import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.NAVIGATIONNODES;
import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.PRODUCT;
import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.PRODUCTCODE;
import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.PRODUCTPOS;
import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.TARGET;
import static de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel.URL;
import static de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel.COMPONENTS;
import static de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel.DESCRIPTION;
import static de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel.INVERSERESTRICTIONS;
import static de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel.PAGES;
import static de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel.ACTIVEFROM;
import static de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel.ACTIVEUNTIL;
import static de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel.CATEGORYCODES;
import static de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel.PRODUCTCODES;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.data.StructureTypeMode;
import de.hybris.platform.cmsfacades.types.ComponentTypeFacade;
import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmsfacades.util.BaseIntegrationTest;

import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultStructureTypeModeServiceIntegrationTest extends BaseIntegrationTest
{
	@Resource
	private ComponentTypeFacade componentTypeFacade;

	@Before
	public void setUp()
	{
		setCurrentUserCmsManager();
	}

	@Test
	public void testCMSLinkComponentByModeDEFAULT_shouldNotReturnExcludedAttributes() throws ComponentTypeNotFoundException
	{
		final ComponentTypeData componentTypeData = componentTypeFacade
				.getComponentTypeByCodeAndMode(CMSLinkComponentModel._TYPECODE, StructureTypeMode.DEFAULT.name());

		assertThat(
				componentTypeData.getAttributes().stream().map(ComponentTypeAttributeData::getQualifier).collect(Collectors.toList()),
				not(hasItems(CONTENTPAGE, PRODUCT, TARGET, CATEGORY, URL, EXTERNAL, CONTENTPAGELABELORID, PRODUCTCODE, CATEGORYCODE,
						NAVIGATIONNODES, CONTENTPAGEPOS, PRODUCTPOS, CATEGORYPOS)));
	}

	@Test
	public void testCMSTimeRestrictionByModeDEFAULT_shouldNotReturnExcludedAttributesFromAbstractRestriction()
			throws ComponentTypeNotFoundException
	{
		final ComponentTypeData componentTypeData = componentTypeFacade
				.getComponentTypeByCodeAndMode(CMSTimeRestrictionModel._TYPECODE, StructureTypeMode.DEFAULT.name());

		assertThat(
				componentTypeData.getAttributes().stream().map(ComponentTypeAttributeData::getQualifier).collect(Collectors.toList()),
				not(hasItems(DESCRIPTION, PAGES, COMPONENTS, INVERSERESTRICTIONS)));

		assertThat(
				componentTypeData.getAttributes().stream().map(ComponentTypeAttributeData::getQualifier).collect(Collectors.toList()),
				hasItems(ACTIVEFROM, ACTIVEUNTIL));
	}

	@Test
	public void testProductCarouselComponentModelByModeDEFAULT_shouldNotReturnExcludedAttributes()
			throws ComponentTypeNotFoundException
	{
		final ComponentTypeData componentTypeData = componentTypeFacade
				.getComponentTypeByCodeAndMode(ProductCarouselComponentModel._TYPECODE, StructureTypeMode.DEFAULT.name());

		assertThat(
				componentTypeData.getAttributes().stream().map(ComponentTypeAttributeData::getQualifier).collect(Collectors.toList()),
				not(hasItems(PRODUCTCODES, CATEGORYCODES, CATEGORYCODE)));
	}

	@Test
	public void testCMSTimeRestrictionByModeDEFAULTWithAttributePermissions_nameShouldNotBeEditableForCmsEditor()
			throws ComponentTypeNotFoundException
	{
		// GIVEN
		setCurrentUserCmsEditor();

		// WHEN
		final ComponentTypeData componentTypeData = componentTypeFacade
				.getComponentTypeByCodeAndMode(CMSTimeRestrictionModel._TYPECODE, StructureTypeMode.DEFAULT.name());

		// THEN
		final ComponentTypeAttributeData nameAttributeData = componentTypeData.getAttributes().stream()
				.filter(attributeData -> attributeData.getQualifier().equals(CMSItemModel.NAME)).findFirst().orElse(null);

		assertFalse(nameAttributeData.isEditable());
	}

}
