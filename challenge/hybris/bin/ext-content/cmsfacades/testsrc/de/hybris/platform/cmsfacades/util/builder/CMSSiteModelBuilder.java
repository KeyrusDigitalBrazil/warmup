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
package de.hybris.platform.cmsfacades.util.builder;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Locale.ENGLISH;

public class CMSSiteModelBuilder {

    private final CMSSiteModel model;


    private CMSSiteModelBuilder(CMSSiteModel model) {
        this.model = model;
    }

    private CMSSiteModelBuilder() {
        this.model = new CMSSiteModel();
    }

    public CMSSiteModel getModel() {
        return model;
    }

    public static CMSSiteModelBuilder aModel()
    {
        return new CMSSiteModelBuilder();
    }

    public static CMSSiteModelBuilder fromModel(CMSSiteModel model)
    {
        return new CMSSiteModelBuilder(model);
    }

    public CMSSiteModelBuilder withEnglishName(String name) {
        return withName(name, ENGLISH);
    }

    public CMSSiteModelBuilder withName(String name, Locale locale) {
        getModel().setName(name, locale);
        return this;
    }

    public CMSSiteModelBuilder active() {
        return setActive(TRUE);
    }

    public CMSSiteModelBuilder notActive() {
        return setActive(FALSE);
    }

    protected CMSSiteModelBuilder setActive(Boolean isActive) {
        getModel().setActive(isActive);
        return this;
    }

    public CMSSiteModelBuilder from(Date activeFrom) {
        getModel().setActiveFrom(activeFrom);
        return this;
    }

    public CMSSiteModelBuilder until(Date activeUntil) {
        getModel().setActiveUntil(activeUntil);
        return this;
    }

    public CMSSiteModelBuilder withDefaultCatalog(CatalogModel defaultCatalog) {
        getModel().setDefaultCatalog(defaultCatalog);
        return this;
    }

    public CMSSiteModelBuilder inLanguage(LanguageModel defaultLanguage) {
        getModel().setDefaultLanguage(defaultLanguage);
        return this;
    }

    public CMSSiteModelBuilder withUid(String uid) {
        getModel().setUid(uid);
        return this;
    }

    public CMSSiteModelBuilder withRedirectUrl(String redirectUrl) {
        getModel().setRedirectURL(redirectUrl);
        return this;
    }

    public CMSSiteModelBuilder usingCatalogs(final List<ContentCatalogModel> catalogs) {
        getModel().setContentCatalogs(catalogs);
        return this;
    }

    public CMSSiteModel build()
    {
        return this.getModel();
    }

}
