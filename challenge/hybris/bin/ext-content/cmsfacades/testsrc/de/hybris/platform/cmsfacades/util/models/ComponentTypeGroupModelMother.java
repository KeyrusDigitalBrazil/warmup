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
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.cms2.model.ComponentTypeGroupModel;
import de.hybris.platform.cmsfacades.util.builder.ComponentTypeGroupModelBuilder;
import de.hybris.platform.cmsfacades.util.dao.ComponentTypeGroupDao;

import org.springframework.beans.factory.annotation.Required;


public class ComponentTypeGroupModelMother extends AbstractModelMother<ComponentTypeGroupModel>
{

	protected static final String CODE_PARAGRAPHS = "code-paragraphs";
	protected static final String CODE_LINKS = "code-links";
	protected static final String CODE_PARAGRAPHS_AND_BANNERS = "code-paragraphs-and-banners";
	protected static final String CODE_PARAGRAPHS_BANNERS_AND_LINKS = "code-paragraphs-banners-and-links";

	private ComponentTypeGroupDao componentTypeGroupDao;
	private CMSComponentTypeModelMother cmsComponentTypeModelMother;

	public ComponentTypeGroupModel Paragraphs()
	{
		return getOrSaveAndReturn( //
				() -> componentTypeGroupDao.getComponentTypeGroupByCode(CODE_PARAGRAPHS), //
				() -> ComponentTypeGroupModelBuilder.aModel() //
						.withCode(CODE_PARAGRAPHS) //
						.withCmsComponentTypes( //
								cmsComponentTypeModelMother.CMSParagraphComponent()) //
						.build());
	}

	public ComponentTypeGroupModel Links()
	{
		return getOrSaveAndReturn( //
				() -> componentTypeGroupDao.getComponentTypeGroupByCode(CODE_LINKS), //
				() -> ComponentTypeGroupModelBuilder.aModel() //
						.withCode(CODE_LINKS) //
						.withCmsComponentTypes( //
								cmsComponentTypeModelMother.LinkComponent()) //
						.build());
	}
	
	public ComponentTypeGroupModel ParagraphsAndBanners()
	{
		return getOrSaveAndReturn( //
				() -> componentTypeGroupDao.getComponentTypeGroupByCode(CODE_PARAGRAPHS_AND_BANNERS), //
				() -> ComponentTypeGroupModelBuilder.aModel() //
						.withCode(CODE_PARAGRAPHS_AND_BANNERS) //
						.withCmsComponentTypes( //
								cmsComponentTypeModelMother.CMSParagraphComponent(), //
								cmsComponentTypeModelMother.BannerComponent()) //
						.build());
	}

	public ComponentTypeGroupModel ParagraphsBannersAndLinks()
	{
		return getOrSaveAndReturn( //
				() -> componentTypeGroupDao.getComponentTypeGroupByCode(CODE_PARAGRAPHS_BANNERS_AND_LINKS), //
				() -> ComponentTypeGroupModelBuilder.aModel() //
						.withCode(CODE_PARAGRAPHS_BANNERS_AND_LINKS) //
						.withCmsComponentTypes( //
								cmsComponentTypeModelMother.CMSParagraphComponent(), //
								cmsComponentTypeModelMother.BannerComponent(), //
								cmsComponentTypeModelMother.LinkComponent())
						.build());
	}

	protected ComponentTypeGroupDao getComponentTypeGroupDao()
	{
		return componentTypeGroupDao;
	}

	@Required
	public void setComponentTypeGroupDao(final ComponentTypeGroupDao componentTypeGroupDao)
	{
		this.componentTypeGroupDao = componentTypeGroupDao;
	}

	protected CMSComponentTypeModelMother getCmsComponentTypeModelMother()
	{
		return cmsComponentTypeModelMother;
	}

	@Required
	public void setCmsComponentTypeModelMother(final CMSComponentTypeModelMother cmsComponentTypeModelMother)
	{
		this.cmsComponentTypeModelMother = cmsComponentTypeModelMother;
	}

}
