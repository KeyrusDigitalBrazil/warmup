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
 * @ngdoc overview
 * @name selectPageTypeModule
 * @description
 * #selectPageTypeModule
 *
 * The selectPageTypeModule module contains the {@link selectPageTypeModule.directive:selectPageType selectPageType} component
 *
 */
angular.module('selectPageTemplateModule', ['pageTemplateServiceModule', 'l10nModule'])

    /**
     * @ngdoc directive
     * @name selectPageTemplateModule.directive:selectPageTemplate
     * @scope
     * @restrict E
     * @element select-page-template
     *
     * @description
     * Displays a list of all CMS page templates in the system, and allows the user to select one, triggering the on-template-selected callback.
     *
     * @param {Function} onTemplateSelected [Required] A callback function that is called when a template is selected from the list.
     * The function is called with a single argument, an object representing the selected page template.
     */
    .component('selectPageTemplate', {
        controller: 'selectPageTemplateController',
        templateUrl: 'selectPageTemplateTemplate.html',
        bindings: {
            uriContext: '<',
            pageTypeCode: '<',
            onTemplateSelected: '<'
        }
    })

    .controller('selectPageTemplateController', ['pageTemplateService', '$rootScope', function(pageTemplateService) {

        var self = this;
        var cache = {};

        this.templateSelected = function templateSelected(pageTemplate) {
            this.selectedTemplate = pageTemplate;
            this.onTemplateSelected(pageTemplate);
        };

        this.isSelected = function isSelected(pageTemplate) {
            return pageTemplate === this.selectedTemplate;
        };

        this.clearSearch = function clearSearch() {
            this.searchString = "";
        };

        this.$onChanges = function onChanges(changesObj) {
            if (changesObj.pageTypeCode.currentValue) {
                this._onInputUpdated();
            }
        };

        this._onInputUpdated = function _onInputUpdated() {
            this.clearSearch();
            this.selectedTemplate = null;
            if (cache[self.pageTypeCode]) {
                self.pageTemplates = cache[self.pageTypeCode];
            } else {
                self.pageTemplates = [];

                pageTemplateService.getPageTemplatesForType(self.uriContext, self.pageTypeCode).then(function(pageTemplates) {
                    cache[self.pageTypeCode] = pageTemplates.templates;
                    self.pageTemplates = cache[self.pageTypeCode];
                });
            }
        };
    }])

    .filter('templateNameFilter', function() {
        return function(templates, criteria) {
            var filterResult = [];
            if (!criteria) {
                return templates;
            }

            criteria = criteria.toLowerCase();
            var criteriaList = criteria.split(" ");

            (templates || []).forEach(function(template) {
                var match = true;
                var term = template.name.toLowerCase();

                criteriaList.forEach(function(item) {
                    if (term.indexOf(item) === -1) {
                        match = false;
                        return false;
                    }
                });

                if (match && filterResult.indexOf(template) === -1) {
                    filterResult.push(template);
                }
            });
            return filterResult;
        };
    });
