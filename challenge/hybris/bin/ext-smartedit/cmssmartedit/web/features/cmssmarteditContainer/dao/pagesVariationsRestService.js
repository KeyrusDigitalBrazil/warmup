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
angular.module('pagesVariationsRestServiceModule', [
        'resourceLocationsModule',
        'yLoDashModule'
    ])

    .service('pagesVariationsRestService', function(restServiceFactory, lodash, CONTEXT_SITE_ID, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION) {

        this._uri = '/cmswebservices/v1/sites/' + CONTEXT_SITE_ID +
            '/catalogs/' + CONTEXT_CATALOG +
            '/versions/' + CONTEXT_CATALOG_VERSION +
            '/pages/:pageId/variations';

        this._resource = restServiceFactory.get(this._uri);

        this.getVariationsForPrimaryPageId = function(pageId, params) {

            var extendedParams = lodash.assign({
                pageId: pageId
            }, params || {});

            return this._resource.get(extendedParams).then(function(response) {
                return response.uids;
            });
        }.bind(this);
    });
