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
angular.module('externalComponentButtonModule', ['l10nModule', 'smarteditServicesModule'])
    .controller('externalComponentButtonController', function(l10nFilter, catalogService) {

        this.$onInit = function() {
            this.isReady = false;

            return catalogService.getCatalogVersionByUuid(this.catalogVersionUuid).then(function(catalogVersion) {
                this.catalogVersion = l10nFilter(catalogVersion.catalogName) + ' (' + catalogVersion.version + ')';
                this.isReady = true;
            }.bind(this));

        };

    })
    .component('externalComponentButton', {
        templateUrl: 'externalComponentButtonTemplate.html',
        controller: 'externalComponentButtonController',
        controllerAs: 'ctrl',
        bindings: {
            catalogVersionUuid: '<'
        }
    });
