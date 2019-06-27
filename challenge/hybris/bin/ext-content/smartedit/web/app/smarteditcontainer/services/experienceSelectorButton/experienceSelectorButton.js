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
angular.module('experienceSelectorButtonModule', ['smarteditServicesModule', 'l10nModule', 'seConstantsModule', 'seConstantsModule'])
    .controller('experienceSelectorButtonController', function(EVENTS, DATE_CONSTANTS, systemEventService, crossFrameEventService, sharedDataService, l10nFilter, dateFilter) {

        this.buildExperienceText = function() {
            if (!this.experience) {
                return '';
            }

            return l10nFilter(this.experience.catalogDescriptor.name) + ' - ' +
                this.experience.catalogDescriptor.catalogVersion + '  |  ' +
                this.experience.languageDescriptor.nativeName +
                (this.experience.time ? '  |  ' + dateFilter(this.experience.time, DATE_CONSTANTS.ANGULAR_FORMAT) : '') +
                this._returnProductCatalogVersionTextByUuids(this.experience.productCatalogVersions);
        };

        this._returnProductCatalogVersionTextByUuids = function() {
            return this.experience.productCatalogVersions.reduce(function(accumulator, productCatalogVersion) {
                accumulator += (" | " + l10nFilter(productCatalogVersion.catalogName) + ' (' + productCatalogVersion.catalogVersion + ')');
                return accumulator;
            }, "");
        };

        this.updateExperience = function() {
            return sharedDataService.get('experience').then(function(experience) {
                this.experience = experience;
            }.bind(this));
        };

        this.setPageFromParent = function(eventId, data) {
            var currentPageCatalogVersionUuid = data.pageContext.catalogVersionUuid;

            this.parentCatalogVersion = '<div>' + l10nFilter(data.pageContext.catalogName) + ' (' + data.pageContext.catalogVersion + ')' + '</div>';
            this.iscurrentPageFromParent = data.catalogDescriptor.catalogVersionUuid !== currentPageCatalogVersionUuid;
        };

        this.$onInit = function() {

            this.iscurrentPageFromParent = false;

            this.updateExperience();
            this.unregFn = systemEventService.subscribe(EVENTS.EXPERIENCE_UPDATE, this.updateExperience.bind(this));
            this.unRegNewPageContextEventFn = crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, this.setPageFromParent.bind(this));
        };

        this.$onDestroy = function() {
            this.unregFn();
            this.unRegNewPageContextEventFn();
        };

    })
    .component('experienceSelectorButton', {
        templateUrl: 'experienceSelectorButtonTemplate.html',
        transclude: true,
        controller: 'experienceSelectorButtonController'
    });
