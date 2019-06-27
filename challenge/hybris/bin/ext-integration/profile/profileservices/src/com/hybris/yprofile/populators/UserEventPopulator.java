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

import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.dto.Address;
import com.hybris.yprofile.dto.Consumer;
import com.hybris.yprofile.dto.PersonalDetails;
import com.hybris.yprofile.dto.User;
import com.hybris.yprofile.dto.UserBody;
import com.hybris.yprofile.dto.UserMasterData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserEventPopulator implements Populator<UserModel, User> {

    private static final String TYPE = "YaaS account";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    private Converter<UserModel, Consumer> profileConsumerConverter;
    private Converter<UserModel, List<Consumer>> profileIdentitiesConverter;
    private Converter<AddressModel, Address> profileAddressConverter;

    @Override
    public void populate(final UserModel userModel, User user) throws ConversionException {

        user.setDate(Utils.formatDate(new Date()));
        user.setBody(getUserBody(userModel));

    }

    protected UserBody getUserBody(final UserModel userModel){
        final UserBody userBody = new UserBody();

        userBody.setType(TYPE);
        userBody.setDate(Utils.formatDate(userModel.getCreationtime()));
        userBody.setIdentity(getProfileConsumerConverter().convert(userModel));
        userBody.setIdentities(getProfileIdentitiesConverter().convert(userModel));
        userBody.setMasterData(getUserMasterData(userModel));
        userBody.setPersonalDetails(getPersonalDetails(userModel));
        return userBody;
    }

    private PersonalDetails getPersonalDetails(final UserModel userModel) {
        final PersonalDetails personalDetails = new PersonalDetails();

        final Optional<Map<String, String>> displayName = parseName(userModel.getDisplayName());

        if (displayName.isPresent()) {
            personalDetails.setFirstName(displayName.get().get(FIRST_NAME));
            personalDetails.setLastName(displayName.get().get(LAST_NAME));
            personalDetails.setTitle(this.getTitleFromUserModel(userModel));
        }
        return personalDetails;
    }

    private String getTitleFromUserModel(final UserModel userModel) {
        if(userModel instanceof CustomerModel) {
            final CustomerModel customerModel = (CustomerModel) userModel;
            return customerModel.getTitle() == null ? StringUtils.EMPTY : customerModel.getTitle().getCode();
        }
        return "";
    }


    protected UserMasterData getUserMasterData(final UserModel userModel){

        final UserMasterData userMasterData = new UserMasterData();
        final List<Address> addresses = this.getConvertedAddresses(userModel);
        userMasterData.setAddresses(addresses);
        return userMasterData;
    }

    protected List<Address> getConvertedAddresses(UserModel userModel) {
        final List<Address> addresses = new ArrayList<Address>();
        userModel.getAddresses().forEach( addressModel ->  {
            addresses.add(convertAddress(Optional.ofNullable(addressModel)));
        });
        return addresses;
    }


    protected Address convertAddress(Optional<AddressModel> addressModel) {
        Address address = new Address();
        if (addressModel.isPresent()) {
            address = getProfileAddressConverter().convert(addressModel.get());
        }
        return address;
    }

    private Optional<Map<String, String>> parseName(final String fullName) {

        final int lastIndexOf = fullName.lastIndexOf(' ');
        if (lastIndexOf > 0) {
            final HashMap<String, String> displayName = new HashMap<>();
            displayName.put(FIRST_NAME, fullName.substring(0, lastIndexOf).trim());
            displayName.put(LAST_NAME , fullName.substring(lastIndexOf).trim());

            return Optional.of(displayName);
        }

        return Optional.empty();
    }

    public Converter<UserModel, Consumer> getProfileConsumerConverter() {
        return profileConsumerConverter;
    }

    @Required
    public void setProfileConsumerConverter(Converter<UserModel, Consumer> profileConsumerConverter) {
        this.profileConsumerConverter = profileConsumerConverter;
    }

    public Converter<UserModel, List<Consumer>> getProfileIdentitiesConverter() {
        return profileIdentitiesConverter;
    }

    @Required
    public void setProfileIdentitiesConverter(final Converter<UserModel, List<Consumer>> profileIdentitiesConverter) {
        this.profileIdentitiesConverter = profileIdentitiesConverter;
    }

    public Converter<AddressModel, Address> getProfileAddressConverter() {
        return profileAddressConverter;
    }

    @Required
    public void setProfileAddressConverter(Converter<AddressModel, Address> profileAddressConverter) {
        this.profileAddressConverter = profileAddressConverter;
    }
}
