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
package de.hybris.platform.commerceservices.consent.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.util.Objects.nonNull;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.consent.dao.ConsentDao;
import de.hybris.platform.commerceservices.consent.dao.ConsentTemplateDao;
import de.hybris.platform.commerceservices.consent.exceptions.CommerceConsentGivenException;
import de.hybris.platform.commerceservices.consent.exceptions.CommerceConsentWithdrawnException;
import de.hybris.platform.commerceservices.event.AbstractConsentEvent;
import de.hybris.platform.commerceservices.event.ConsentGivenEvent;
import de.hybris.platform.commerceservices.event.ConsentWithdrawnEvent;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.AccessDeniedException;


/**
 * Default implementation of {@link CommerceConsentService}
 */
public class DefaultCommerceConsentService implements CommerceConsentService
{
	private ModelService modelService;

	private TimeService timeService;

	private ConsentDao consentDao;

	private ConsentTemplateDao consentTemplateDao;

	private EventService eventService;

	private UserService userService;

	@Override
	public List<ConsentTemplateModel> getConsentTemplates(final BaseSiteModel baseSite)
	{
		validateParameterNotNullStandardMessage("baseSite", baseSite);

		return getConsentTemplateDao().findConsentTemplatesBySite(baseSite);
	}

	@Override
	public ConsentTemplateModel getConsentTemplate(final String consentTemplateId, final Integer consentTemplateVersion,
			final BaseSiteModel baseSite)
	{
		validateParameterNotNullStandardMessage("consentTemplateId", consentTemplateId);
		validateParameterNotNullStandardMessage("consentTemplateVersion", consentTemplateVersion);
		validateParameterNotNullStandardMessage("baseSite", baseSite);

		return getConsentTemplateDao().findConsentTemplateByIdAndVersionAndSite(consentTemplateId, consentTemplateVersion,
				baseSite);
	}

	@Override
	public ConsentTemplateModel getLatestConsentTemplate(final String consentTemplateId, final BaseSiteModel baseSite)
	{
		validateParameterNotNullStandardMessage("consentTemplateId", consentTemplateId);
		validateParameterNotNullStandardMessage("baseSite", baseSite);

		return getConsentTemplateDao().findLatestConsentTemplateByIdAndSite(consentTemplateId, baseSite);
	}

	@Override
	public ConsentModel getConsent(final String consentCode)
	{
		validateParameterNotNullStandardMessage("consentCode", consentCode);

		final Map<String, Object> queryParams = Collections.singletonMap(ConsentModel.CODE, consentCode);
		return getConsentDao().find(queryParams).stream().findFirst()
				.orElseThrow(() -> new ModelNotFoundException(String.format("Consent not found for code [%s]", consentCode)));
	}

	@Override
	public ConsentModel getActiveConsent(final CustomerModel customer, final ConsentTemplateModel consentTemplate)
	{
		validateParameterNotNullStandardMessage("customer", customer);
		validateParameterNotNullStandardMessage("consentTemplate", consentTemplate);

		return getConsentDao().findConsentByCustomerAndConsentTemplate(customer, consentTemplate);
	}

	@Override
	public boolean hasEffectivelyActiveConsent(final CustomerModel customer, final ConsentTemplateModel consentTemplate)
	{
		validateParameterNotNullStandardMessage("customer", customer);
		validateParameterNotNullStandardMessage("consentTemplate", consentTemplate);

		final ConsentModel consent = getActiveConsent(customer, consentTemplate);
		return consent != null ? consent.isActive() : false;
	}

	@Override
	public void giveConsent(final CustomerModel customer, final ConsentTemplateModel consentTemplate)
	{
		validateParameterNotNullStandardMessage("customer", customer);
		validateParameterNotNullStandardMessage("consentTemplate", consentTemplate);

		ConsentModel consent = getActiveConsent(customer, consentTemplate);
		if (consent != null && isConsentGiven(consent) && !isConsentWithdrawn(consent))
		{
			throw new CommerceConsentGivenException(
					String.format("User with uid : [%s] has already given consent for ConsentTemplate with id : [%s], version : [%s] ",
							customer.getUid(), consentTemplate.getId(), consentTemplate.getVersion()));
		}

		if (consent == null || isConsentWithdrawn(consent))
		{
			consent = createConsentModel(customer, consentTemplate);
		}

		consent.setConsentGivenDate(getTimeService().getCurrentTime());
		getModelService().save(consent);
		getEventService().publishEvent(initializeEvent(new ConsentGivenEvent(), consent));
	}

	@Override
	public void withdrawConsent(final ConsentModel consent)
	{
		validateParameterNotNullStandardMessage("consent", consent);

		final UserModel currentUser = getUserService().getCurrentUser();
		final String consentCode = consent.getCode();

		if (currentUser != null && consent.getCustomer() != null)
		{
			if (!currentUser.getUid().equals(consent.getCustomer().getUid()))
			{
				throw new AccessDeniedException(
						String.format("Consent with code [%s] is not associated to the current user with uid [%s]", consentCode,
								currentUser.getUid()));
			}
		}
		else
		{
			throw new CommerceConsentWithdrawnException("Either current customer or consent customer have invalid uid");
		}
		if (isConsentWithdrawn(consent))
		{
			throw new CommerceConsentWithdrawnException(String.format(
					"Current user with uid [%s] has already withdrawn consent with code [%s]", currentUser.getUid(), consentCode));
		}

		consent.setConsentWithdrawnDate(getTimeService().getCurrentTime());
		getModelService().save(consent);
		getEventService().publishEvent(initializeEvent(new ConsentWithdrawnEvent(), consent));
	}

	protected boolean isConsentWithdrawn(final ConsentModel consent)
	{
		return nonNull(consent.getConsentWithdrawnDate());
	}

	protected boolean isConsentGiven(final ConsentModel consent)
	{
		return nonNull(consent.getConsentGivenDate());
	}

	protected ConsentModel createConsentModel(final CustomerModel customer, final ConsentTemplateModel consentTemplate)
	{
		final ConsentModel consent = modelService.create(ConsentModel._TYPECODE);
		consent.setConsentTemplate(consentTemplate);
		consent.setCustomer(customer);
		return consent;
	}

	protected AbstractConsentEvent initializeEvent(final AbstractConsentEvent event, final ConsentModel consent)
	{
		event.setConsent(consent);
		return event;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected ConsentDao getConsentDao()
	{
		return consentDao;
	}

	@Required
	public void setConsentDao(final ConsentDao consentDao)
	{
		this.consentDao = consentDao;
	}

	protected ConsentTemplateDao getConsentTemplateDao()
	{
		return consentTemplateDao;
	}

	@Required
	public void setConsentTemplateDao(final ConsentTemplateDao consentTemplateDao)
	{
		this.consentTemplateDao = consentTemplateDao;
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}
