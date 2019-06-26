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
/**
 * @name cmsLinkToSelectModule
 * @description
 * # The cmsLinkToSelectModule
 *
 * The cmsLinkToSelectModule contains the CMSLinkTo component which is a wrapper on top of the seDropDown component.
 * When one of the dropdown option is selected, the `CMS_LINK_TO_RELOAD_STRUCTURE_EVENT_ID` event is sent through the systemEvent Service.
 * That event is listened in the Generic Tab component to dynamically reload the Generic Editor structure (the Generic Editor listens for structure and structureApi changes)
 *
 */
angular.module('cmsLinkToSelectModule', ['smarteditServicesModule', 'seDropdownModule', 'resourceLocationsModule', 'cmsLinkComponentContentPageDropdownPopulatorModule'])
    .constant('CMS_LINK_TO_SELECT_OPTIONS', [{
        id: 'content',
        structureApiMode: 'CONTENT'
    }, {
        id: 'product',
        structureApiMode: 'PRODUCT',
        hasCatalog: true
    }, {
        id: 'category',
        structureApiMode: 'CATEGORY',
        hasCatalog: true
    }, {
        id: 'external',
        structureApiMode: 'EXTERNAL'
    }])
    /**
     * Custom event id that is listened in Generic Tab to dynamically reload the Generic Editor structure
     */
    .constant('CMS_LINK_TO_RELOAD_STRUCTURE_EVENT_ID', 'cms-link-to-reload-structure')
    .controller('CmsLinkToSelectController', function(CMS_LINK_TO_SELECT_OPTIONS, CMS_LINK_TO_RELOAD_STRUCTURE_EVENT_ID, LINKED_DROPDOWN, systemEventService) {
        this.$onInit = function() {
            var linkTo = _getLinkToValue(this.model);
            if (linkTo !== null) {
                this.model.linkTo = linkTo;
            }
            this.unregisterDropdownListener = systemEventService.subscribe(this.id + LINKED_DROPDOWN, _onLinkToSelectValueChanged.bind(this));
        };

        this.$onDestroy = function() {
            this.unregisterDropdownListener();
        };

        function _onLinkToSelectValueChanged(eventId, handle) {

            if (this.qualifier !== handle.qualifier) {
                return;
            }

            if (!handle.optionObject) {
                return;
            }
            var optionValue = handle.optionObject.id;
            var selectedOption = CMS_LINK_TO_SELECT_OPTIONS.find(function(selectOption) {
                return selectOption.id === optionValue;
            });

            if (!selectedOption) {
                throw new Error('Error selected option not supported');
            }

            // Prevent cycling infinitely because the Generic Editor append the component each time the structure change
            if (optionValue === this.model.currentSelectedOptionValue) {
                return;
            }
            this.model.currentSelectedOptionValue = optionValue;
            this.model.external = optionValue !== 'external';

            _cleanModel.call(this, selectedOption);

            systemEventService.publishAsync(CMS_LINK_TO_RELOAD_STRUCTURE_EVENT_ID, {
                content: this.model,
                structureApiMode: selectedOption.structureApiMode,
                editorId: this.id
            });
        }

        function _getLinkToValue(model) {
            if (model.url) {
                return 'external';
            } else if (model.product) {
                return 'product';
            } else if (model.contentPage) {
                return 'content';
            } else if (model.category) {
                return 'category';
            }
            return null;
        }

        function _cleanModel(selectedOption) {
            if (!this.model) {
                return;
            }

            if (selectedOption.id !== 'category') {
                delete this.model.category;
            }

            if (selectedOption.id !== 'product') {
                delete this.model.product;
            }

            if (!selectedOption.hasCatalog) {
                delete this.model.productCatalog;
            }

            if (selectedOption.id !== 'content') {
                delete this.model.contentPage;
            }

            if (selectedOption.id !== 'external') {
                delete this.model.url;
            }
        }
    })
    /**
     * @name cmsLinkToSelectModule.directive:cmsLinkToSelect
     * @scope
     * @restrict E
     * @element cms-link-to-select
     * 
     * @description
     * Component wrapper on top of seDropdown component that upon selection of an option, will trigger an event with a new structureApi or structure
     * The `CMSLinkToSelect` custom component is registered in cmssmarteditContainerAppModule and contains the following option values:
     * - content (link to a content page)
     * - product (link to a product page)
     * - category (link to a specific category page)
     * - external (external link)
     * 
     * @param {<Object} field The component field
     * @param {<String} id The component id
     * @param {<Object} model The component model
     * @param {<String} qualifier The qualifier within the structure attribute
     */
    .component('cmsLinkToSelect', {
        transclude: true,
        replace: false,
        templateUrl: 'cmsLinkToSelectTemplate.html',
        controller: 'CmsLinkToSelectController',
        controllerAs: 'ctrl',
        bindings: {
            field: '<',
            id: '<',
            model: '<',
            qualifier: '<'
        }
    });
