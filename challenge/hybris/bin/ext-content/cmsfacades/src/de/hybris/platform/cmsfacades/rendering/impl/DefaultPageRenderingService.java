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
package de.hybris.platform.cmsfacades.rendering.impl;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.CMSDataFactory;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cmsfacades.common.service.RestrictionAwareService;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.dto.RenderingPageValidationDto;
import de.hybris.platform.cmsfacades.rendering.PageRenderingService;
import de.hybris.platform.cmsfacades.rendering.suppliers.page.RenderingPageModelSupplier;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;

import java.util.List;


/**
 * Default implementation for {@link PageRenderingService}.
 */
public class DefaultPageRenderingService implements PageRenderingService
{
	private Converter<AbstractPageModel, AbstractPageData> pageModelToDataRenderingConverter;
	private FacadeValidationService facadeValidationService;
	private Validator renderingPageValidator;
	private RestrictionAwareService restrictionAwareService;
	private CMSDataFactory cmsDataFactory;
	private List<RenderingPageModelSupplier> renderingPageModelSuppliers;


	@Override
	public AbstractPageData getPageRenderingData(final String pageTypeCode, final String pageLabelOrId, final String code)
			throws CMSItemNotFoundException
	{
		validateParameters(pageTypeCode, pageLabelOrId, code);
		final String pageQualifier = getPageQualifier(pageLabelOrId, code);
		final RestrictionData restrictionData = getRestrictionData(pageTypeCode, code);
		final AbstractPageModel pageModel = getPageModel(pageTypeCode, pageQualifier);
		return getPageData(pageModel, restrictionData);
	}

	/**
	 * Returns {@link AbstractPageData} based on {@link AbstractPageModel} and {@link RestrictionData}.
	 *
	 * @param pageModel       the {@link AbstractPageModel}.
	 * @param restrictionData the {@link RestrictionData}
	 * @return the {@link AbstractPageData}.
	 */
	protected AbstractPageData getPageData(final AbstractPageModel pageModel, final RestrictionData restrictionData)
	{
		return getRestrictionAwareService()
				.execute(restrictionData, () -> getPageModelToDataRenderingConverter().convert(pageModel));
	}

	/**
	 * Returns {@link RestrictionData} based on pageTypeCode and code. Never null.
	 *
	 * @param pageType the page type.
	 * @param code     the code. If the page type is ProductPage then the code should be a product code.
	 *                 If the page type is CategoryPage then the code should be a category code.
	 *                 If the page type is CatalogPage then the code should be a catalog page.
	 * @return the {@link RestrictionData}.
	 */
	protected RestrictionData getRestrictionData(final String pageType, final String code)
	{
		return getRenderingPageModelSuppliers()
				.stream()   //
				.filter(supplier -> supplier.getConstrainedBy().test(pageType))
				.findFirst()
				.flatMap(supplier -> supplier.getRestrictionData(code))
				.orElse(getCmsDataFactory().createRestrictionData());
	}

	/**
	 * Validates input parameters.
	 *
	 * @param pageTypeCode  the page type code.
	 * @param pageLabelOrId the page label or id.
	 * @param code          the code (product code, catalog log,
	 */
	protected void validateParameters(final String pageTypeCode, final String pageLabelOrId, final String code)
	{
		final RenderingPageValidationDto validationDto = new RenderingPageValidationDto();
		validationDto.setPageTypeCode(pageTypeCode);
		validationDto.setCode(code);
		validationDto.setPageLabelOrId(pageLabelOrId);

		getFacadeValidationService().validate(getRenderingPageValidator(), validationDto);
	}

	/**
	 * Returns the qualifier that is used to extract the page.
	 *
	 * @param pageLabelOrId the page label or id
	 * @param code          the code.
	 * @return the qualifier.
	 * @implSpec This implementation return {@code pageLabelOrId == null ? code : pageLabelOrId}.
	 */
	protected String getPageQualifier(final String pageLabelOrId, final String code)
	{
		return pageLabelOrId == null ? code : pageLabelOrId;
	}

	/**
	 * Returns the {@link AbstractPageModel}.
	 *
	 * @param pageType  the page type.
	 * @param qualifier the qualifier of the page. See {@code getPageQualifier()} for more information.
	 * @return the {@link AbstractPageModel}.
	 * @throws CMSItemNotFoundException if the page does not exist.
	 */
	protected AbstractPageModel getPageModel(final String pageType, final String qualifier) throws CMSItemNotFoundException
	{
		return getRenderingPageModelSuppliers()
				.stream()   //
				.filter(supplier -> supplier.getConstrainedBy().test(pageType))
				.findFirst()
				.flatMap(supplier -> supplier.getPageModel(qualifier))
				.orElseThrow(() -> new CMSItemNotFoundException(
						"No AbstractPageModel found for given page type: " + pageType + " and qualifier " + qualifier));

	}

	protected List<RenderingPageModelSupplier> getRenderingPageModelSuppliers()
	{
		return renderingPageModelSuppliers;
	}

	@Required
	public void setRenderingPageModelSuppliers(
			List<RenderingPageModelSupplier> renderingPageModelSuppliers)
	{
		this.renderingPageModelSuppliers = renderingPageModelSuppliers;
	}

	protected Converter<AbstractPageModel, AbstractPageData> getPageModelToDataRenderingConverter()
	{
		return pageModelToDataRenderingConverter;
	}

	@Required
	public void setPageModelToDataRenderingConverter(
			Converter<AbstractPageModel, AbstractPageData> pageModelToDataRenderingConverter)
	{
		this.pageModelToDataRenderingConverter = pageModelToDataRenderingConverter;
	}

	protected FacadeValidationService getFacadeValidationService()
	{
		return facadeValidationService;
	}

	@Required
	public void setFacadeValidationService(FacadeValidationService facadeValidationService)
	{
		this.facadeValidationService = facadeValidationService;
	}

	protected Validator getRenderingPageValidator()
	{
		return renderingPageValidator;
	}

	@Required
	public void setRenderingPageValidator(Validator renderingPageValidator)
	{
		this.renderingPageValidator = renderingPageValidator;
	}

	protected RestrictionAwareService getRestrictionAwareService()
	{
		return restrictionAwareService;
	}

	@Required
	public void setRestrictionAwareService(RestrictionAwareService restrictionAwareService)
	{
		this.restrictionAwareService = restrictionAwareService;
	}

	protected CMSDataFactory getCmsDataFactory()
	{
		return cmsDataFactory;
	}

	@Required
	public void setCmsDataFactory(CMSDataFactory cmsDataFactory)
	{
		this.cmsDataFactory = cmsDataFactory;
	}
}
