/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.gigya.gigyafacades.login.impl;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyafacades.login.GigyaLoginFacade;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.constants.GigyaservicesConstants;
import de.hybris.platform.gigya.gigyaservices.data.GigyaJsOnLoginInfo;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;


/**
 * Default implementation of GigyaLoginFacade
 */
public class DefaultGigyaLoginFacade implements GigyaLoginFacade
{

	private static final Logger LOG = Logger.getLogger(DefaultGigyaLoginFacade.class);

	private GigyaLoginService gigyaLoginService;

	private UserService userService;

	private ModelService modelService;

	private CommonI18NService commonI18NService;

	private GigyaService gigyaService;

	private TaskService taskService;

	private String gigyaToHybrisTaskRunnerBean;

	private SessionService sessionService;

	private CustomerNameStrategy customerNameStrategy;

	private CustomerAccountService customerAccountService;

	@Override
	public boolean processGigyaLogin(final GigyaJsOnLoginInfo jsInfo, final GigyaConfigModel gigyaConfig)
	{
		try
		{
			if (gigyaLoginService.verifyGigyaCall(gigyaConfig, jsInfo.getUID(), jsInfo.getUIDSignature(),
					jsInfo.getSignatureTimestamp()))
			{
				final UserModel gigyaUser = gigyaLoginService.findCustomerByGigyaUid(jsInfo.getUID());
				if (gigyaUser != null)
				{
					updateUser(gigyaConfig, gigyaUser);
					return true;
				}
				else
				{
					// On creation all customer data from gigya is synched
					createNewCustomer(gigyaConfig, jsInfo.getUID());
					return true;
				}
			}
			return false;
		}
		catch (final DuplicateUidException e)
		{
			try
			{
				LOG.error("Error duplicate ID found for gigyaUid=" + jsInfo.getUID(), e);
				gigyaLoginService.notifyGigyaOfLogout(gigyaConfig, jsInfo.getUID());
			}
			catch (final GigyaApiException e1)
			{
				LOG.error(e1);
			}
			return false;
		}
		catch (final Exception e)
		{
			try
			{
				LOG.error("Error: " + e.getMessage(), e);
				gigyaLoginService.notifyGigyaOfLogout(gigyaConfig, jsInfo.getUID());
			}
			catch (final GigyaApiException e1)
			{
				LOG.error(e1);
			}
			return false;
		}
	}

	@Override
	public String getHybrisUidForGigyaUser(final String gigyaUid)
	{
		final UserModel gigyaUser = gigyaLoginService.findCustomerByGigyaUid(gigyaUid);
		return gigyaUser != null ? gigyaUser.getUid() : StringUtils.EMPTY;
	}

	@Override
	public UserModel createNewCustomer(final GigyaConfigModel gigyaConfig, final String uid) throws DuplicateUidException
	{
		final GigyaUserObject gigyaUserObject = gigyaLoginService.fetchGigyaInfo(gigyaConfig, uid);

		if (gigyaConfig == null)
		{
			return null;
		}

		if (StringUtils.isEmpty(gigyaUserObject.getEmail()))
		{
			throw new GigyaApiException("Gigya User does not have an email address");
		}

		if (gigyaLoginService.findCustomerByGigyaUid(uid) != null)
		{
			throw new DuplicateUidException("User with Gigya UID: " + uid + " already exists.");
		}

		final CustomerModel gigyaUser = modelService.create(CustomerModel.class);
		gigyaUser.setGyIsOriginGigya(true);
		gigyaUser.setName(customerNameStrategy.getName(gigyaUserObject.getFirstName(), gigyaUserObject.getLastName()));
		gigyaUser.setUid(gigyaUserObject.getEmail());
		gigyaUser.setOriginalUid(gigyaUserObject.getEmail());
		gigyaUser.setGyUID(uid);
		gigyaUser.setGyApiKey(gigyaConfig.getGigyaApiKey());
		gigyaUser.setSessionLanguage(commonI18NService.getCurrentLanguage());
		gigyaUser.setSessionCurrency(commonI18NService.getCurrentCurrency());
		gigyaUser.setCustomerID(UUID.randomUUID().toString());

		customerAccountService.register(gigyaUser, null);

		return gigyaUser;
	}

	@Override
	public boolean processGigyaProfileUpdate(final GigyaJsOnLoginInfo jsInfo, final GigyaConfigModel gigyaConfig)
	{
		if (gigyaLoginService.verifyGigyaCall(gigyaConfig, jsInfo.getUID(), jsInfo.getUIDSignature(),
				jsInfo.getSignatureTimestamp()))
		{
			final UserModel gigyaUser = gigyaLoginService.findCustomerByGigyaUid(jsInfo.getUID());
			if (gigyaUser != null)
			{
				try
				{
					updateUser(gigyaConfig, gigyaUser);
					return true;
				}
				catch (final GSKeyNotFoundException | GigyaApiException e)
				{
					LOG.error("Error updating user information for user with gigyaUid=" + jsInfo.getUID(), e);
				}
			}
			else
			{
				LOG.error("Error, no user with gigya UID found." + jsInfo.getUID());
				return false;
			}
		}
		return false;
	}


	@Override
	public void updateUser(final GigyaConfigModel gigyaConfig, final UserModel user) throws GSKeyNotFoundException
	{
		// Update mandatory info i.e. UID, name and then update based on mappings
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

		if (user instanceof CustomerModel)
		{
			final CustomerModel gigyaUser = (CustomerModel) user;
			final Map<String, Object> params = new LinkedHashMap<>();
			params.put("UID", gigyaUser.getGyUID());
			params.put("include", "loginIDs,emails,profile,data");

			final GSObject gigyaParams = convertMapToGsObject(mapper, params);

			final GSResponse gsResponse = gigyaService.callRawGigyaApiWithConfigAndObject("accounts.getAccountInfo", gigyaParams,
					gigyaConfig, GigyaservicesConstants.MAX_RETRIES, GigyaservicesConstants.TRY_NUM);

			if (gsResponse.hasData() && gsResponse.getData().get("profile") != null)
			{
				final GSObject profile = (GSObject) gsResponse.getData().get("profile");
				final String emailId = profile.getString("email");

				gigyaUser.setName(getCustomerNameStrategy().getName(profile.getString("firstName"), profile.getString("lastName")));

				// Checks if email ID is updated in the gigya profile, if yes that needs to be updated in commerce as well
				if (!StringUtils.equals(gigyaUser.getUid(), emailId))
				{
					gigyaUser.setUid(emailId);
					getSessionService().setAttribute("emailUpdated", true);
				}
			}
			gigyaUser.setGyIsOriginGigya(true);

			getModelService().save(gigyaUser);

			// Task to synchronize data between gigya and commerce when gigya user logs in
			final TaskModel task = modelService.create(TaskModel.class);
			task.setRunnerBean(gigyaToHybrisTaskRunnerBean);
			task.setExecutionDate(new Date());
			task.setContextItem(gigyaUser);

			taskService.scheduleTask(task);
		}
	}

	private GSObject convertMapToGsObject(final ObjectMapper mapper, final Map<String, Object> parms)
	{
		GSObject gigyaParams = new GSObject();
		try
		{
			gigyaParams = new GSObject(mapper.writeValueAsString(parms));
		}
		catch (final Exception e)
		{
			final String msg = "Error creating gigya request parmeters";
			LOG.error(msg, e);
			throw new GigyaApiException(msg);
		}
		return gigyaParams;
	}

	public GigyaLoginService getGigyaLoginService()
	{
		return gigyaLoginService;
	}

	@Required
	public void setGigyaLoginService(final GigyaLoginService gigyaLoginService)
	{
		this.gigyaLoginService = gigyaLoginService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	@Required
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public GigyaService getGigyaService()
	{
		return gigyaService;
	}

	@Required
	public void setGigyaService(final GigyaService gigyaService)
	{
		this.gigyaService = gigyaService;
	}

	@Required
	public TaskService getTaskService()
	{
		return taskService;
	}

	@Required
	public void setTaskService(final TaskService taskService)
	{
		this.taskService = taskService;
	}

	public String getGigyaToHybrisTaskRunnerBean()
	{
		return gigyaToHybrisTaskRunnerBean;
	}

	@Required
	public void setGigyaToHybrisTaskRunnerBean(final String gigyaToHybrisTaskRunnerBean)
	{
		this.gigyaToHybrisTaskRunnerBean = gigyaToHybrisTaskRunnerBean;
	}

	public SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public CustomerAccountService getCustomerAccountService() {
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(CustomerAccountService customerAccountService) {
		this.customerAccountService = customerAccountService;
	}

}
