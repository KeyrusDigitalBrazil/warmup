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
angular.module('perspectiveSelectorModule', [
        'yjqueryModule',
        'iframeClickDetectionServiceModule',
        'smarteditServicesModule',
        'yPopoverModule',
        'seConstantsModule'
    ])
    .constant('isE2eTestingActive', false)
    .controller('PerspectiveSelectorController', function($log, $translate, yjQuery, perspectiveService, iframeClickDetectionService, $scope, $document, systemEventService, EVENT_PERSPECTIVE_ADDED, EVENT_PERSPECTIVE_CHANGED, EVENT_PERSPECTIVE_REFRESHED, ALL_PERSPECTIVE, EVENTS, crossFrameEventService, isE2eTestingActive) {
        var perspectives = [];
        var displayedPerspectives = [];

        var unRegOverlayDisabledFn;
        var unRegPerspectiveAddedFn;
        var unRegPerspectiveChgFn;
        var unRegUserHasChanged;
        var unRegPerspectiveRefreshFn;

        this.activePerspective = null;
        this.isOpen = false;

        var closeDropdown = function() {
            this.isOpen = false;
        }.bind(this);

        var onPerspectiveAdded = function() {
            perspectiveService.getPerspectives().then(function(result) {
                perspectives = result;
                displayedPerspectives = this._filterPerspectives(perspectives);
            }.bind(this));
        }.bind(this);

        this.refreshPerspectives = function() {
            perspectiveService.getPerspectives().then(function(result) {
                perspectives = result;
                this._refreshActivePerspective();
                displayedPerspectives = this._filterPerspectives(perspectives);
            }.bind(this));
        };

        this.$onInit = function() {
            this.activePerspective = null;
            iframeClickDetectionService.registerCallback('perspectiveSelectorClose', closeDropdown);

            unRegOverlayDisabledFn = systemEventService.subscribe('OVERLAY_DISABLED', closeDropdown);
            unRegPerspectiveAddedFn = systemEventService.subscribe(EVENT_PERSPECTIVE_ADDED, onPerspectiveAdded);

            unRegPerspectiveChgFn = crossFrameEventService.subscribe(EVENT_PERSPECTIVE_CHANGED, this.refreshPerspectives.bind(this));
            unRegPerspectiveRefreshFn = crossFrameEventService.subscribe(EVENT_PERSPECTIVE_REFRESHED, this.refreshPerspectives.bind(this));
            unRegUserHasChanged = crossFrameEventService.subscribe(EVENTS.USER_HAS_CHANGED, onPerspectiveAdded);

            onPerspectiveAdded();

            $document.on('click', function(event) {
                if (yjQuery(event.target).parents('.ySEPerspectiveSelector').length <= 0 && this.isOpen) {
                    closeDropdown();
                    $scope.$apply();
                }
            }.bind(this));
        };

        this.$onDestroy = function() {
            unRegOverlayDisabledFn();
            unRegPerspectiveAddedFn();
            unRegPerspectiveChgFn();
            unRegPerspectiveRefreshFn();
            unRegUserHasChanged();
        };

        this.selectPerspective = function(choice) {
            try {
                perspectiveService.switchTo(choice);
                closeDropdown();
            } catch (e) {
                $log.error("selectPerspective() - Cannot select perspective.", e);
            }
        };

        this.getDisplayedPerspectives = function() {
            return displayedPerspectives;
        };

        this.getActivePerspectiveName = function() {
            return this.activePerspective ? this.activePerspective.nameI18nKey : '';
        };

        this.hasActivePerspective = function() {
            return this.activePerspective !== null;
        };

        this.isTooltipVisible = function() {
            return !!this.activePerspective && !!this.activePerspective.descriptionI18nKey;
        };

        this._filterPerspectives = function(perspectives) {
            return perspectives.filter(function(perspective) {
                var isActivePerspective = this.activePerspective && (perspective.key === this.activePerspective.key);
                var isAllPerspective = perspective.key === ALL_PERSPECTIVE;

                return !isActivePerspective && (!isAllPerspective || isE2eTestingActive);
            }.bind(this));
        };

        this.getTooltipTemplate = function() {
            return '<div>' + $translate.instant(this.activePerspective.descriptionI18nKey) + '</div>';
        };

        this._refreshActivePerspective = function() {
            this.activePerspective = perspectiveService.getActivePerspective();
        };
    })
    .component('perspectiveSelector', {
        templateUrl: 'perspectiveSelectorWidgetTemplate.html',
        controller: 'PerspectiveSelectorController'
    });
