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
package de.hybris.platform.integration.cis.avs.services.impl;

import java.util.Collections;
import java.util.List;
import com.hybris.cis.client.avs.models.AvsResult;
import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisDecision;
import com.hybris.cis.service.CisClientAvsService;
import de.hybris.platform.commerceservices.address.AddressErrorCode;
import de.hybris.platform.commerceservices.address.AddressFieldType;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.address.AddressVerificationService;
import de.hybris.platform.commerceservices.address.data.AddressFieldErrorData;
import de.hybris.platform.commerceservices.address.data.AddressVerificationResultData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.cis.avs.strategies.CheckVerificationRequiredStrategy;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;



/**
 * Implementation of {@link AddressVerificationService} using CIS webservices.
 */
public class DefaultCisAddressVerificationService implements
		AddressVerificationService<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>>
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCisAddressVerificationService.class);
	private String defaultClientRef;
	private String tenantId;
	private CisClientAvsService cisClientAvsService;
	private CartService cartService;
	private Populator<AddressModel, AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>>> cisAvsAddressMatchingPopulator;
	private CheckVerificationRequiredStrategy applyVerificationStrategy;
	private BaseStoreService baseStoreService;
	private Converter<AddressModel, CisAddress> cisAvsAddressConverter;
	private Converter<AvsResult, AddressVerificationResultData> cisAvsResultAddressVerificationResultDataConverter;
	private CommonI18NService commonI18NService;

	@Override
	public AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> verifyAddress(
			final AddressModel addressModel)
	{
		//Only apply the verification strategy on certain addresses
		if (!getApplyVerificationStrategy().isVerificationRequired(addressModel))
		{
			final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> verifiedResult = createVerificationResult();
			verifiedResult.setDecision(AddressVerificationDecision.ACCEPT);
			verifiedResult.setSuggestedAddresses(Collections.EMPTY_LIST);
			return verifiedResult;
		}

		final CisAddress cisAddress = getCisAvsAddressConverter().convert(addressModel);

		final AvsResult avsResult = getCisClientAvsService().verifyAddress(getClientRef(), getTenantId(), cisAddress);

		if (avsResult.getDecision().equals(CisDecision.ERROR))
		{
			final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> verifiedResult = createVerificationResult();
			verifiedResult.setDecision(AddressVerificationDecision.UNKNOWN);
			return verifiedResult;
		}

		removeVerifiedAddressFromSuggestedAddresses(cisAddress, avsResult.getSuggestedAddresses());

		final AddressVerificationResultData verificationResultData = getCisAvsResultAddressVerificationResultDataConverter()
				.convert(avsResult);

		//set first name, last name on the suggested addresses
		if (CollectionUtils.isNotEmpty(verificationResultData.getSuggestedAddresses()))
		{
			getCisAvsAddressMatchingPopulator().populate(addressModel, verificationResultData);
		}

		return verificationResultData;
	}

	/**
	 * This method will remove suggested address from list when it is identical with address we sent for verification but
	 * only when it is the only address on the list
	 */
	protected void removeVerifiedAddressFromSuggestedAddresses(final CisAddress veryfiedAddress,
			final List<CisAddress> suggestedAddressesList)
	{
		if (suggestedAddressesList.size() == 1 && areAddressesEqual(veryfiedAddress, suggestedAddressesList.get(0)))
		{
			if (LOG.isInfoEnabled())
			{
				LOG.info(String.format("Removing suggestedAddress %s because it matches the entered address exactly %s",
						ReflectionToStringBuilder.toString(suggestedAddressesList.get(0)),
						ReflectionToStringBuilder.toString(veryfiedAddress)));
			}
			suggestedAddressesList.clear();
		}
	}

	protected boolean areAddressesEqual(final CisAddress veryfiedAddress, final CisAddress suggestedAddress)
	{
		if (!equalsIgnoreNullAndEmpty(veryfiedAddress.getCountry(), suggestedAddress.getCountry()))
		{
			return false;
		}
		if (!equalsIgnoreNullAndEmpty(veryfiedAddress.getZipCode(), suggestedAddress.getZipCode()))
		{
			return false;
		}
		if (!equalsIgnoreNullAndEmpty(veryfiedAddress.getCity(), suggestedAddress.getCity()))
		{
			return false;
		}
		if (!equalsIgnoreNullAndEmpty(veryfiedAddress.getAddressLine1(), suggestedAddress.getAddressLine1()))
		{
			return false;
		}
		if (!equalsIgnoreNullAndEmpty(veryfiedAddress.getAddressLine2(), suggestedAddress.getAddressLine2()))
		{
			return false;
		}
		if (!equalsIgnoreNullAndEmpty(veryfiedAddress.getState(), suggestedAddress.getState()))
		{
			final RegionModel regionModel = getRegionForCountryIsoCode(veryfiedAddress);
			return (regionModel != null) ? regionModel.equals(getRegionForCountryIsoCode(suggestedAddress)) : false;
		}

		return true;
	}

	protected boolean equalsIgnoreNullAndEmpty(final String str1, final String str2)
	{
		if (StringUtils.isBlank(str1) && StringUtils.isBlank(str2))
		{
			return true;
		}
		else
		{
			return StringUtils.equals(str1, str2);
		}
	}

	@Override
	public boolean isCustomerAllowedToIgnoreSuggestions()
	{
		final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		return baseStore != null && baseStore.isCustomerAllowedToIgnoreSuggestions();
	}

	protected AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> createVerificationResult()
	{
		return new AddressVerificationResultData<>();
	}

	protected String getClientRef()
	{
		if (getCartService().hasSessionCart())
		{
			return getCartService().getSessionCart().getGuid();
		}
		return getDefaultClientRef();
	}

	protected RegionModel getRegionForCountryIsoCode(final CisAddress address)
	{
		if (address.getState() != null)
		{
			return getCommonI18NService().getRegion(getCommonI18NService().getCountry(address.getCountry()), address.getState());
		}
		return null;
	}

	protected CisClientAvsService getCisClientAvsService()
	{
		return cisClientAvsService;
	}

	@Required
	public void setCisClientAvsService(final CisClientAvsService cisClientAvsService)
	{
		this.cisClientAvsService = cisClientAvsService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected CheckVerificationRequiredStrategy getApplyVerificationStrategy()
	{
		return applyVerificationStrategy;
	}

	@Required
	public void setApplyVerificationStrategy(final CheckVerificationRequiredStrategy applyVerificationStrategy)
	{
		this.applyVerificationStrategy = applyVerificationStrategy;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected Converter<AddressModel, CisAddress> getCisAvsAddressConverter()
	{
		return cisAvsAddressConverter;
	}

	@Required
	public void setCisAvsAddressConverter(final Converter<AddressModel, CisAddress> cisAvsAddressConverter)
	{
		this.cisAvsAddressConverter = cisAvsAddressConverter;
	}

	protected Converter<AvsResult, AddressVerificationResultData> getCisAvsResultAddressVerificationResultDataConverter()
	{
		return cisAvsResultAddressVerificationResultDataConverter;
	}

	@Required
	public void setCisAvsResultAddressVerificationResultDataConverter(
			final Converter<AvsResult, AddressVerificationResultData> cisAvsResultAddressVerificationResultDataConverter)
	{
		this.cisAvsResultAddressVerificationResultDataConverter = cisAvsResultAddressVerificationResultDataConverter;
	}

	protected Populator<AddressModel, AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>>> getCisAvsAddressMatchingPopulator()
	{
		return cisAvsAddressMatchingPopulator;
	}

	@Required
	public void setCisAvsAddressMatchingPopulator(
			final Populator<AddressModel, AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>>> cisAvsAddressMatchingPopulator)
	{
		this.cisAvsAddressMatchingPopulator = cisAvsAddressMatchingPopulator;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected String getTenantId()
	{
		return tenantId;
	}

	@Required
	public void setTenantId(String tenantId)
	{
		this.tenantId = tenantId;
	}

	protected String getDefaultClientRef()
	{
		return defaultClientRef;
	}

	@Required
	public void setDefaultClientRef(String defaultClientRef)
	{
		this.defaultClientRef = defaultClientRef;
	}
}
