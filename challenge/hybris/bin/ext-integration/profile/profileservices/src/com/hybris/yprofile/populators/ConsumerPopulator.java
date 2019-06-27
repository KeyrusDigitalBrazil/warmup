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
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;

public class ConsumerPopulator implements Populator<UserModel, Consumer> {

    public static final String TYPE = "email";

    @Override
    public void populate(UserModel userModel, Consumer consumer) {
        consumer.setType(TYPE);
        consumer.setRef(userModel.getUid());

        if (isGuestUser(userModel)) {
           consumer.setRef(((CustomerModel) userModel).getContactEmail());
        }
    }

    protected boolean isGuestUser(UserModel userModel) {
        return (userModel instanceof CustomerModel) && (CustomerType.GUEST.equals(((CustomerModel) userModel).getType()));
    }
}
