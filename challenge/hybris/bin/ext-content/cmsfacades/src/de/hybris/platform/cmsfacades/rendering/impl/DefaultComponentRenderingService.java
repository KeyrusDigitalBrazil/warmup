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
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.data.CMSDataFactory;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cmsfacades.common.service.RestrictionAwareService;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.dto.RenderingComponentValidationDto;
import de.hybris.platform.cmsfacades.rendering.ComponentRenderingService;
import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityService;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;


/**
 * Default implementation of {@link ComponentRenderingService}.
 */
public class DefaultComponentRenderingService implements ComponentRenderingService
{
	private CMSDataFactory cmsDataFactory;
	private CMSComponentService cmsComponentService;
	private RestrictionAwareService restrictionAwareService;
	private Converter<AbstractCMSComponentModel, AbstractCMSComponentData> componentRenderingConverter;
	private FacadeValidationService facadeValidationService;
	private Validator renderingComponentValidator;
	private RenderingVisibilityService renderingVisibilityService;

	@Override
	public AbstractCMSComponentData getComponentById(final String componentId, final String categoryCode, final String productCode,
			final String catalogCode) throws CMSItemNotFoundException
	{
		validateParameters(categoryCode, productCode, catalogCode);
		final AbstractCMSComponentModel componentModel = getCmsComponentService().getAbstractCMSComponent(componentId);
		final RestrictionData restrictionData = getCmsDataFactory().createRestrictionData(categoryCode, productCode, catalogCode);
		return getComponentData(componentModel, restrictionData)
				.orElseThrow(() -> new CMSItemNotFoundException("Can not find component with uid \"" + componentId + "\"."));
	}

	@Override
	public SearchPageData<AbstractCMSComponentData> getComponentsByIds(final Collection<String> componentIds,
			final String categoryCode, final String productCode, final String catalogCode, final SearchPageData searchPageData)
	{
		validateParameters(categoryCode, productCode, catalogCode);
		final SearchPageData<AbstractCMSComponentModel> componentsSearchData = //
				getCmsComponentService().getAbstractCMSComponents(componentIds, searchPageData);
		final RestrictionData restrictionData = getCmsDataFactory().createRestrictionData(categoryCode, productCode, catalogCode);
		return getComponentsData(componentsSearchData, restrictionData);
	}

	protected void validateParameters(final String categoryCode, final String productCode, final String catalogCode)
	{
		final RenderingComponentValidationDto validationDto = new RenderingComponentValidationDto();
		validationDto.setCatalogCode(catalogCode);
		validationDto.setCategoryCode(categoryCode);
		validationDto.setProductCode(productCode);

		getFacadeValidationService().validate(getRenderingComponentValidator(), validationDto);
	}

	/**
	 * Returns the list of found {@link AbstractCMSComponentData} in {@link SearchPageData} based on
	 * {@link RestrictionData} object. If nothing is found then the empty list is returned.
	 *
	 * @param componentsSearchData
	 * @param restrictionData
	 * @return the list of found {@link AbstractCMSComponentData} in {@link SearchPageData}.
	 */
	protected SearchPageData<AbstractCMSComponentData> getComponentsData(
			final SearchPageData<AbstractCMSComponentModel> componentsSearchData, final RestrictionData restrictionData)
	{
		final SearchPageData<AbstractCMSComponentData> result = new SearchPageData();

		final List<AbstractCMSComponentData> componentDataList = componentsSearchData.getResults().stream()
				.map(componentModel -> getComponentData(componentModel, restrictionData)).filter(Optional::isPresent)
				.map(Optional::get).collect(toList());

		result.setResults(componentDataList);
		result.setPagination(componentsSearchData.getPagination());
		result.setSorts(componentsSearchData.getSorts());
		return result;
	}

	/**
	 * Returns the {@link AbstractCMSComponentData} dto based on {@link AbstractCMSComponentModel} and
	 * {@link RestrictionData}. When the component is not visible based on the restriction, an empty Optional is
	 * returned.
	 *
	 * @param componentModel  the {@link AbstractCMSComponentModel} object.
	 * @param restrictionData the {@link RestrictionData} object determining whether the component is visible or not.
	 * @return the {@link Optional} of {@link AbstractCMSComponentData} object; return {@code Optional#empty()} when the
	 * component is not visible
	 */
	protected Optional<AbstractCMSComponentData> getComponentData(final AbstractCMSComponentModel componentModel,
			final RestrictionData restrictionData)
	{
		final Supplier<Optional<AbstractCMSComponentData>> convertComponentSupplier = //
				() -> getRenderingVisibilityService().isVisible(componentModel)
						? Optional.of(getComponentRenderingConverter().convert(componentModel))
						: Optional.empty();

		return getRestrictionAwareService().execute(restrictionData, convertComponentSupplier);
	}

	protected CMSDataFactory getCmsDataFactory()
	{
		return cmsDataFactory;
	}

	@Required
	public void setCmsDataFactory(final CMSDataFactory cmsDataFactory)
	{
		this.cmsDataFactory = cmsDataFactory;
	}

	protected CMSComponentService getCmsComponentService()
	{
		return cmsComponentService;
	}

	@Required
	public void setCmsComponentService(final CMSComponentService cmsComponentService)
	{
		this.cmsComponentService = cmsComponentService;
	}

	protected Converter<AbstractCMSComponentModel, AbstractCMSComponentData> getComponentRenderingConverter()
	{
		return componentRenderingConverter;
	}

	@Required
	public void setComponentRenderingConverter(
			final Converter<AbstractCMSComponentModel, AbstractCMSComponentData> componentRenderingConverter)
	{
		this.componentRenderingConverter = componentRenderingConverter;
	}

	protected RestrictionAwareService getRestrictionAwareService()
	{
		return restrictionAwareService;
	}

	@Required
	public void setRestrictionAwareService(final RestrictionAwareService restrictionAwareService)
	{
		this.restrictionAwareService = restrictionAwareService;
	}

	protected FacadeValidationService getFacadeValidationService()
	{
		return facadeValidationService;
	}

	@Required
	public void setFacadeValidationService(final FacadeValidationService facadeValidationService)
	{
		this.facadeValidationService = facadeValidationService;
	}

	protected Validator getRenderingComponentValidator()
	{
		return renderingComponentValidator;
	}

	@Required
	public void setRenderingComponentValidator(final Validator renderingComponentValidator)
	{
		this.renderingComponentValidator = renderingComponentValidator;
	}

	protected RenderingVisibilityService getRenderingVisibilityService()
	{
		return renderingVisibilityService;
	}

	@Required
	public void setRenderingVisibilityService(
			RenderingVisibilityService renderingVisibilityService)
	{
		this.renderingVisibilityService = renderingVisibilityService;
	}
}
