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
package com.hybris.yprofile.consent.cookie;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * @deprecated Consent Reference Cookie is generated automatically by Profile Tag
 */
@Deprecated
public class ConsentReferenceCookieGenerator extends EnhancedCookieGenerator
{

    private BaseSiteService baseSiteService;

    @Override
    public String getCookieName()
    {
        final StringBuilder cookieName = new StringBuilder();
        cookieName.append(getSiteId());
        cookieName.append("-consentReference");

        return StringUtils.deleteWhitespace(cookieName.toString());
    }

    protected String getSiteId(){
        return getCurrentBaseSiteModel().isPresent() ? getCurrentBaseSiteModel().get().getUid() : StringUtils.EMPTY;
    }

    protected Optional<BaseSiteModel> getCurrentBaseSiteModel() {
        return ofNullable(getBaseSiteService().getCurrentBaseSite());
    }


    protected BaseSiteService getBaseSiteService()
    {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(final BaseSiteService baseSiteService)
    {
        this.baseSiteService = baseSiteService;
    }

}
