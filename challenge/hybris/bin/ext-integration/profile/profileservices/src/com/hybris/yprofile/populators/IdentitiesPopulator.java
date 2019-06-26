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
package com.hybris.yprofile.populators;

import com.hybris.yprofile.dto.Consumer;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerEmailResolutionService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.mail.MailUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public class IdentitiesPopulator implements Populator<UserModel, List<Consumer>> {

    private static final Logger LOG = Logger.getLogger(IdentitiesPopulator.class);

    public static final String TYPE_EMAIL = "email";
    public static final String TYPE_UID = "UID";

    private String defaultEmail;

    private ConfigurationService configurationService;

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public void populate(final UserModel userModel, final List<Consumer> identities) {

        // use UID by default, if user is registered through accelerator this is usually the email,
        // if he is registered through gigya this is something more cryptic
        // if it is an guest user it is the email with a random prefix and separated by a pipe.
        // For the last case CustomerModel.getContactEmail strips the prefix

        final String uidString = getIdentityRef(userModel);
        identities.add(createConsumer(TYPE_UID, uidString));

        if (checkIfUidIsEmail(uidString)){
            identities.add(createConsumer(TYPE_EMAIL, uidString));
        } else {
            // in case of a gigya integration Address.email gets populated, take all emails we can get from all addresses
            if (userModel.getAddresses() != null) {
                userModel.getAddresses().stream()
                        .filter(addressModel -> StringUtils.isNotBlank(addressModel.getEmail()))
                        .forEach(addressModel -> identities.add(createConsumer(TYPE_EMAIL, addressModel.getEmail())));
            }
        }

    }

    protected String getIdentityRef(final UserModel userModel)
    {
        if (userModel instanceof CustomerModel) {
            final CustomerModel customerModel = (CustomerModel) userModel;
            // customerModel.getContactEmail() retrieves Email from UID
            // there is a special handling for guest users and a default value for the email address
            // see @DefaultCustomerEmailResolutionService for implementation details
            final String contactEmail = customerModel.getContactEmail();
            //  filter out default emails, as this is of no value
            if (!defaultEmail.equals(contactEmail)) {
                return contactEmail;
            }
        }
        // e.g. the gigya case
        return userModel.getUid();
    }                                       

    protected boolean checkIfUidIsEmail(final String uidToCheck) {
        try {
            MailUtils.validateEmailAddress(uidToCheck, "customer email");
            return true;
        } catch(EmailException e) {
            LOG.warn("Unexpected error occurred while validating the email address", e);
            return false;
        }
    }
    

    protected Consumer createConsumer(final String type, final String ref) {
        final Consumer consumer = new Consumer();
        consumer.setType(type);
        consumer.setRef(ref);
        return consumer;
    }

    protected void init() {
        this.defaultEmail = getConfigurationService().getConfiguration().getString(
                DefaultCustomerEmailResolutionService.DEFAULT_CUSTOMER_KEY,
                DefaultCustomerEmailResolutionService.DEFAULT_CUSTOMER_EMAIL);
    }
}
