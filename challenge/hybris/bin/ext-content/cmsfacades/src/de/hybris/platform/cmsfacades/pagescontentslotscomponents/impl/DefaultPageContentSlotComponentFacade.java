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
package de.hybris.platform.cmsfacades.pagescontentslotscomponents.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminComponentService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.PageContentSlotComponentData;
import de.hybris.platform.cmsfacades.exception.ComponentNotFoundInSlotException;
import de.hybris.platform.cmsfacades.pagescontentslotscomponents.PageContentSlotComponentFacade;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.Validator;

import com.google.common.collect.Lists;


/**
 * Default implementation of {@link PageContentSlotComponentFacade}. This implementation will expect that a catalog
 * version has been placed into a local session.
 */
public class DefaultPageContentSlotComponentFacade implements PageContentSlotComponentFacade
{
	private CMSAdminPageService adminPageService;
	private CMSAdminContentSlotService adminContentSlotService;
	private CMSAdminComponentService adminComponentService;
	private FacadeValidationService facadeValidationService;
	private Validator createPageContentSlotComponentCompositeValidator;
	private Validator updatePageContentSlotComponentValidator;
	private Validator componentExistsInSlotValidator;
	private PlatformTransactionManager transactionManager;
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private CatalogVersionService catalogVersionService;
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;

	@Override
	public List<PageContentSlotComponentData> getPageContentSlotComponentsByPageId(final String pageId)
			throws CMSItemNotFoundException
	{
		AbstractPageModel page = null;
		try
		{
			page = getAdminPageService().getPageForIdFromActiveCatalogVersion(pageId);
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			throw new CMSItemNotFoundException("Cannot find page with uid \"" + pageId + "\".", e);
		}

		final Collection<ContentSlotData> contentSlots = getAdminContentSlotService().getContentSlotsForPage(page);
		return contentSlots.stream().flatMap(slot -> buildPageContentSlotComponentStream(pageId, slot))
				.collect(Collectors.toList());
	}

	/**
	 * Build a stream of <code>PageContentSlotComponentData<code>.
	 *
	 * @param pageId
	 *           - the page id
	 * @param contentSlot
	 *           - the content slot
	 * @return a new stream of DTOs
	 */
	protected Stream<PageContentSlotComponentData> buildPageContentSlotComponentStream(final String pageId,
			final ContentSlotData contentSlot)
	{
		final List<AbstractCMSComponentModel> components = getSessionSearchRestrictionsDisabler().execute(() -> Lists
				.newArrayList(getAdminComponentService().getDisplayedComponentsForContentSlot(contentSlot.getContentSlot())));

		return components.stream() //
				.map(component -> buildPageContentSlotComponent(pageId, contentSlot.getUid(), components.indexOf(component),
						component));
	}

	/**
	 * Build a single <code>PageContentSlotComponentData<code>.
	 *
	 * @param pageId
	 *           - the page id
	 * @param slotId
	 *           - the slot id
	 * @param position
	 *           - the position of the component in the slot
	 * @param component
	 *           - the CMS component model
	 * @return a new DTO
	 */
	protected PageContentSlotComponentData buildPageContentSlotComponent(final String pageId, final String slotId,
			final Integer position, final AbstractCMSComponentModel component)
	{
		final PageContentSlotComponentData pageContentSlotComponent = new PageContentSlotComponentData();
		pageContentSlotComponent.setPageId(pageId);
		pageContentSlotComponent.setSlotId(slotId);
		pageContentSlotComponent.setComponentId(component.getUid());
		getUniqueItemIdentifierService().getItemData(component).ifPresent(itemData -> {
			pageContentSlotComponent.setComponentUuid(itemData.getItemId());
		});
		pageContentSlotComponent.setPosition(position);
		return pageContentSlotComponent;
	}

	@Transactional
	@Override
	public PageContentSlotComponentData addComponentToContentSlot(final PageContentSlotComponentData pageContentSlotComponent)
			throws CMSItemNotFoundException
	{
		getFacadeValidationService().validate(getCreatePageContentSlotComponentCompositeValidator(),
				buildPageContentSlotComponentValidationDto(pageContentSlotComponent.getSlotId(), pageContentSlotComponent));

		final Collection<CatalogVersionModel> catalogVersions = getCatalogVersionService().getSessionCatalogVersions();
		final AbstractCMSComponentModel component = getAdminComponentService()
				.getCMSComponentForIdAndCatalogVersions(pageContentSlotComponent.getComponentId(), catalogVersions);
		try
		{
			final ContentSlotModel contentSlot = getAdminContentSlotService()
					.getContentSlotForIdAndCatalogVersions(pageContentSlotComponent.getSlotId(), catalogVersions);

			return new TransactionTemplate(getTransactionManager()).execute(status -> {
				getAdminContentSlotService().addCMSComponentToContentSlot(component, contentSlot,
						pageContentSlotComponent.getPosition());
				return pageContentSlotComponent;
			});
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			// should not arrive here because this was already checked in the validator
			throw new CMSItemNotFoundException("Content slot with uid \"" + pageContentSlotComponent.getSlotId() + "\" not found.",
					e);
		}
	}

	@Override
	public void removeComponentFromContentSlot(final String slotId, final String componentId) throws CMSItemNotFoundException
	{

		final AbstractCMSComponentModel component = fetchComponent(componentId);
		final ContentSlotModel contentSlot = fetchContentSlot(slotId);

		if (!contentSlot.getCmsComponents().contains(component))
		{
			throw new ComponentNotFoundInSlotException("Cannot remove component from slot. Content slot with Uid '" + slotId
					+ "' does not contain component item with Uid '" + componentId + "'.");
		}

		new TransactionTemplate(getTransactionManager()).execute(new TransactionCallbackWithoutResult()
		{
			@Override
			protected void doInTransactionWithoutResult(final TransactionStatus status)
			{
				getAdminComponentService().removeCMSComponentFromContentSlot(component, contentSlot);
			}
		});
	}

	@Override
	public PageContentSlotComponentData moveComponent(final String pageUid, final String componentUid, final String originSlotUid,
			final PageContentSlotComponentData pageContentSlotComponentData) throws CMSItemNotFoundException
	{
		getFacadeValidationService().validate(getUpdatePageContentSlotComponentValidator(), pageContentSlotComponentData);
		final ContentSlotModel originSlot = fetchContentSlot(originSlotUid);
		final AbstractCMSComponentModel component = fetchComponent(componentUid);

		final String destSlotUid = pageContentSlotComponentData.getSlotId();

		if (originSlotUid.equals(destSlotUid)) //it is a reorder
		{
			new TransactionTemplate(getTransactionManager()).execute(new TransactionCallbackWithoutResult()
			{
				@Override
				protected void doInTransactionWithoutResult(final TransactionStatus status)
				{
					getAdminContentSlotService().updatePositionCMSComponentInContentSlot(component, originSlot,
							pageContentSlotComponentData.getPosition());
				}
			});
		}

		else //it is a move
		{
			final ContentSlotModel destSlot = fetchContentSlot(destSlotUid);

			getFacadeValidationService().validate(getComponentExistsInSlotValidator(), pageContentSlotComponentData);

			new TransactionTemplate(getTransactionManager()).execute(new TransactionCallbackWithoutResult()
			{

				@Override
				protected void doInTransactionWithoutResult(final TransactionStatus status)
				{
					getAdminContentSlotService().addCMSComponentToContentSlot(component, destSlot,
							pageContentSlotComponentData.getPosition());
					getAdminComponentService().removeCMSComponentFromContentSlot(component, originSlot);
				}

			});
		}

		return pageContentSlotComponentData;
	}

	protected AbstractCMSComponentModel fetchComponent(final String componentId) throws CMSItemNotFoundException
	{
		try
		{
			return getAdminComponentService().getCMSComponentForIdAndCatalogVersions(componentId,
					getCatalogVersionService().getSessionCatalogVersions());
		}
		catch (AmbiguousIdentifierException | UnknownIdentifierException e)
		{
			throw new CMSItemNotFoundException("Component Item with Uid '" + componentId + "' not found.", e);
		}
	}

	protected ContentSlotModel fetchContentSlot(final String slotId) throws CMSItemNotFoundException
	{
		try
		{
			return getAdminContentSlotService().getContentSlotForIdAndCatalogVersions(slotId,
					getCatalogVersionService().getSessionCatalogVersions());
		}
		catch (AmbiguousIdentifierException | UnknownIdentifierException e)
		{
			throw new CMSItemNotFoundException("Content slot with Uid '" + slotId + "' not found.", e);
		}
	}

	/**
	 * Build a new DTO for validating adding existing component to content slot.
	 *
	 * @param slotId
	 * @param pageContentSlotComponentData
	 * @return the new DTO
	 */
	protected PageContentSlotComponentData buildPageContentSlotComponentValidationDto(final String slotId,
			final PageContentSlotComponentData pageContentSlotComponentData)
	{
		final PageContentSlotComponentData validationDto = new PageContentSlotComponentData();
		validationDto.setComponentId(pageContentSlotComponentData.getComponentId());
		validationDto.setSlotId(slotId);
		validationDto.setPosition(pageContentSlotComponentData.getPosition());
		validationDto.setPageId(pageContentSlotComponentData.getPageId());
		return validationDto;
	}

	protected CMSAdminPageService getAdminPageService()
	{
		return adminPageService;
	}

	@Required
	public void setAdminPageService(final CMSAdminPageService adminPageService)
	{
		this.adminPageService = adminPageService;
	}

	protected CMSAdminContentSlotService getAdminContentSlotService()
	{
		return adminContentSlotService;
	}

	@Required
	public void setAdminContentSlotService(final CMSAdminContentSlotService adminContentSlotService)
	{
		this.adminContentSlotService = adminContentSlotService;
	}

	protected CMSAdminComponentService getAdminComponentService()
	{
		return adminComponentService;
	}

	@Required
	public void setAdminComponentService(final CMSAdminComponentService adminComponentService)
	{
		this.adminComponentService = adminComponentService;
	}

	protected Validator getCreatePageContentSlotComponentCompositeValidator()
	{
		return createPageContentSlotComponentCompositeValidator;
	}

	@Required
	public void setCreatePageContentSlotComponentCompositeValidator(
			final Validator createPageContentSlotComponentCompositeValidator)
	{
		this.createPageContentSlotComponentCompositeValidator = createPageContentSlotComponentCompositeValidator;
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

	protected Validator getUpdatePageContentSlotComponentValidator()
	{
		return updatePageContentSlotComponentValidator;
	}

	@Required
	public void setUpdatePageContentSlotComponentValidator(final Validator updatePageContentSlotComponentValidator)
	{
		this.updatePageContentSlotComponentValidator = updatePageContentSlotComponentValidator;
	}

	public PlatformTransactionManager getTransactionManager()
	{
		return transactionManager;
	}

	@Required
	public void setTransactionManager(final PlatformTransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
	}

	public Validator getComponentExistsInSlotValidator()
	{
		return componentExistsInSlotValidator;
	}

	@Required
	public void setComponentExistsInSlotValidator(final Validator componentExistsInSlotValidator)
	{
		this.componentExistsInSlotValidator = componentExistsInSlotValidator;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected SessionSearchRestrictionsDisabler getSessionSearchRestrictionsDisabler()
	{
		return sessionSearchRestrictionsDisabler;
	}

	@Required
	public void setSessionSearchRestrictionsDisabler(final SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler)
	{
		this.sessionSearchRestrictionsDisabler = sessionSearchRestrictionsDisabler;
	}
}
