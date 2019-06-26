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
package de.hybris.platform.cmsfacades.pagesrestrictions.impl;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminRestrictionService;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.PageRestrictionData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.factory.ErrorFactory;
import de.hybris.platform.cmsfacades.pagesrestrictions.PageRestrictionFacade;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Default implementation of <code>PageRestrictionFacade</code>. This uses {@link CMSAdminPageService} and
 * {@link CMSAdminRestrictionService} to retrieve pages and restrictions respectively.
 */
public class DefaultPageRestrictionFacade implements PageRestrictionFacade
{
	private CMSAdminPageService adminPageService;
	private CMSAdminRestrictionService adminRestrictionService;
	private FacadeValidationService facadeValidationService;
	private Validator updatePageRestrictionValidator;
	private PlatformTransactionManager transactionManager;
	private ErrorFactory validatorErrorFactory;

	@Override
	public List<PageRestrictionData> getRestrictionsByPage(final String pageId) throws CMSItemNotFoundException
	{
		try
		{
			final AbstractPageModel page = getAdminPageService().getPageForIdFromActiveCatalogVersion(pageId);
			return getAllRestrictionsByPage(page);
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			throw new CMSItemNotFoundException("Cannot find page with uid \"" + pageId + "\".", e);
		}
	}

	@Override
	public List<PageRestrictionData> getAllPagesRestrictions()
	{
		return getAdminPageService().getAllPages().stream().flatMap(page -> getAllRestrictionsByPage(page).stream())
				.collect(Collectors.toList());
	}

	protected List<PageRestrictionData> getAllRestrictionsByPage(final AbstractPageModel page)
	{
		return getAdminRestrictionService().getRestrictionsForPage(page).stream() //
				.map(restriction -> buildPageRestrictionData(page, restriction)) //
				.collect(Collectors.toList());
	}

	@Override
	public void updateRestrictionRelationsByPage(final String pageId, final List<PageRestrictionData> pageRestrictionsListData)
			throws CMSItemNotFoundException
	{
		final AbstractPageModel page;
		// Validate pageId
		try
		{
			page = getAdminPageService().getPageForIdFromActiveCatalogVersion(pageId);
		}
		catch (final AmbiguousIdentifierException | UnknownIdentifierException ex)
		{
			throw new CMSItemNotFoundException("Cannot find page with uid \"" + pageId + "\".", ex);
		}

		validatePageForUpdate(page, pageRestrictionsListData);

		new TransactionTemplate(getTransactionManager()).execute(new TransactionCallbackWithoutResult()
		{
			@Override
			protected void doInTransactionWithoutResult(final TransactionStatus status)
			{
				//delete all existing restrictions
				getAdminRestrictionService().getRestrictionsForPage(page)
						.forEach(res -> getAdminRestrictionService().deleteRelation(res, page));

				//add the new ones
				pageRestrictionsListData.stream().map(pageRestriction -> {
					Optional<AbstractRestrictionModel> optional;
					try
					{
						optional = Optional.ofNullable(getAdminRestrictionService().getRestriction(pageRestriction.getRestrictionId()));
					}
					catch (final CMSItemNotFoundException e)
					{
						optional = Optional.empty();
					}
					return optional;
				}).forEach(res -> getAdminRestrictionService().createRelation(page, res.get()));
			}
		});
	}

	/**
	 * Validate that page defaultPage attribute and list of restrictions to be applied to the page.
	 *
	 * @param page
	 *           - the page which defaultPage flag should be validate
	 * @param pageRestrictionsListData
	 *           - the restrictions to be applied to the page
	 */
	protected void validatePageForUpdate(final AbstractPageModel page, final List<PageRestrictionData> pageRestrictionsListData)
	{
		// Validate that variation page has at least one restriction
		if (!page.getDefaultPage() && pageRestrictionsListData.isEmpty())
		{
			final Errors errors = getValidatorErrorFactory().createInstance(page);
			errors.rejectValue(AbstractPageModel.RESTRICTIONS, CmsfacadesConstants.FIELD_MIN_VIOLATED);
			throw new ValidationException(errors);
		}

		// Validate the relations and make sure that the query param pageId matches the relations
		pageRestrictionsListData.stream().forEach(pageRestrictionsData -> {
			getFacadeValidationService().validate(getUpdatePageRestrictionValidator(), pageRestrictionsData);
			if (!pageRestrictionsData.getPageId().equals(page.getUid()))
			{
				throw new AmbiguousIdentifierException("The page id '" + pageRestrictionsData.getPageId()
						+ "' in page-restriction relation cannot be different than the id of the page '" + page.getUid() + "'.");
			}
		});
	}


	/**
	 * Build a new page restriction dto to hold a single pageId - restrictionId pair.
	 *
	 * @param page
	 *           - the page
	 * @param restriction
	 *           - the restriction
	 * @return a page restriction dto
	 */
	protected PageRestrictionData buildPageRestrictionData(final AbstractPageModel page,
			final AbstractRestrictionModel restriction)
	{
		final PageRestrictionData dto = new PageRestrictionData();
		dto.setPageId(page.getUid());
		dto.setRestrictionId(restriction.getUid());
		return dto;
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

	protected CMSAdminPageService getAdminPageService()
	{
		return adminPageService;
	}

	@Required
	public void setAdminPageService(final CMSAdminPageService adminPageService)
	{
		this.adminPageService = adminPageService;
	}

	protected CMSAdminRestrictionService getAdminRestrictionService()
	{
		return adminRestrictionService;
	}

	@Required
	public void setAdminRestrictionService(final CMSAdminRestrictionService adminRestrictionService)
	{
		this.adminRestrictionService = adminRestrictionService;
	}

	protected Validator getUpdatePageRestrictionValidator()
	{
		return updatePageRestrictionValidator;
	}

	@Required
	public void setUpdatePageRestrictionValidator(final Validator updatePageRestrictionValidator)
	{
		this.updatePageRestrictionValidator = updatePageRestrictionValidator;
	}

	protected PlatformTransactionManager getTransactionManager()
	{
		return transactionManager;
	}

	@Required
	public void setTransactionManager(final PlatformTransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
	}

	protected ErrorFactory getValidatorErrorFactory()
	{
		return validatorErrorFactory;
	}

	@Required
	public void setValidatorErrorFactory(final ErrorFactory validatorErrorFactory)
	{
		this.validatorErrorFactory = validatorErrorFactory;
	}
}
