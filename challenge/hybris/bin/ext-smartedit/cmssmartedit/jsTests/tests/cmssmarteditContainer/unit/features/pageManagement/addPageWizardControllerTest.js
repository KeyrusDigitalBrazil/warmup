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
/* jshint unused:false, undef:false */
describe('addPageWizardController', function() {

    var addPageWizardController;
    var mocks;
    var $q;

    var MOCK_PAGE_TYPES = [{
        code: 'contentPageType',
        name: {
            "en": 'Content Page - en',
            "fr": 'Content Page - fr'
        },
        description: {
            "en": 'Description for content page - en',
            "fr": 'Description for content page - fr'
        }
    }, {
        code: 'productPageType',
        name: {
            "en": 'Product Page - en',
            "fr": 'Product Page - fr'
        },
        description: {
            "en": 'Description for product page - en',
            "fr": 'Description for product page - fr'
        }
    }];

    beforeEach(function() {

        var restrictionsStepHandler = {
            onDisplayConditionResult: function() {},

            isStepValid: function() {
                return true;
            },

            getStepId: function() {
                return 'restrictionsStepId';
            }
        };

        var harness = AngularUnitTestHelper.prepareModule('addPageServiceModule')
            .mock('languageService', 'getDefaultToolingLanguages')
            .mock('languageService', 'getResolveLocale').and.returnResolvedPromise("TE_LANGUAGE")
            .mock('pageTemplateService', 'getPageTemplatesForType')
            .mock('createPageService', 'createPage').and.returnResolvedPromise('page created')
            .mock('addPageWizardService', 'getPageTypeFields').and.returnResolvedPromise(true)
            .mock('experienceService', 'updateExperiencePageId')
            .mock('pageTypeService', 'getPageTypes').and.returnResolvedPromise(MOCK_PAGE_TYPES)
            .mock('pageService', 'getPrimaryPagesForPageType').and.returnResolvedPromise([])
            .mock('restrictionsStepHandlerFactory', 'createRestrictionsStepHandler').and.returnValue(restrictionsStepHandler)
            .mock('wizardManager')
            .controller('addPageWizardController', {
                $routeParams: {
                    siteId: 'someSiteID',
                    catalogId: 'someCatalogID',
                    catalogVersion: 'someCatalogVersion'
                }
            });

        addPageWizardController = harness.controller;
        mocks = harness.mocks;
        $q = harness.injected.$q;
    });


    //describe('init', function() {
    //    it('WHEN controller is created THEN it is initialized with the right data', function() {
    //        expect(addPageWizardController.model.pageTypes).toEqual(MOCK_PAGE_TYPES);
    //        expect(addPageWizardController.model.selectedType).toBe(null);
    //        expect(addPageWizardController.model.toolingLanguage).toBe('TE_LANGUAGE');
    //    });
    //});
    //
    //describe('isFormValid', function() {
    //    it('should validate page type form as valid if a type is selected', function() {
    //        addPageWizardController.model.selectedType = {
    //            isSelected: true
    //        };
    //        expect(addPageWizardController.isFormValid('pageType')).toBe(true);
    //    });
    //
    //    it('should validate page type form as invalid if no type is selected', function() {
    //        addPageWizardController.model.selectedType = {
    //            isSelected: false
    //        };
    //        expect(addPageWizardController.isFormValid('pageType')).toBe(false);
    //    });
    //
    //    it('should validate page template form as valid if a template is selected', function() {
    //        addPageWizardController.model.selectedTemplate = {
    //            isSelected: true
    //        };
    //        expect(addPageWizardController.isFormValid('pageTemplate')).toBe(true);
    //    });
    //
    //    it('should validate page template form as invalid if no template is selected', function() {
    //        addPageWizardController.model.selectedTemplate = {
    //            isSelected: false
    //        };
    //        expect(addPageWizardController.isFormValid('pageTemplate')).toBe(false);
    //    });
    //
    //    it('should validate page info form as valid if the form is dirty', function() {
    //        addPageWizardController.model.editor = {
    //            isDirty: function() {
    //                return true;
    //            }
    //        };
    //        expect(addPageWizardController.isFormValid('pageInfo')).toBe(true);
    //    });
    //
    //    it('should validate any nonexistent form as invalid', function() {
    //        expect(addPageWizardController.isFormValid('something else')).toBe(false);
    //    });
    //});
    //
    //describe('onNext', function() {
    //    beforeEach(function() {
    //        spyOn(addPageWizardController, 'getPageTemplates').and.returnValue($q.when(true));
    //    });
    //
    //    it('should return true for pageType when a template is selected', function() {
    //        addPageWizardController.model.selectedType = {
    //            code: 'somePageTypeCode',
    //            isSelected: true
    //        };
    //
    //        expect(addPageWizardController.onNext('pageType')).toBeResolvedWithData(true);
    //    });
    //
    //    it('should return true for pageDisplayCondition when a type and template are selected', function() {
    //        addPageWizardController.model.selectedType = {
    //            code: 'somePageTypeCode',
    //            isSelected: true
    //        };
    //        addPageWizardController.model.selectedTemplate = {
    //            isSelected: true
    //        };
    //        addPageWizardController.model.page = {};
    //
    //        expect(addPageWizardController.onNext('pageDisplayCondition')).toBeResolvedWithData(true);
    //    });
    //
    //    it('should return false for an invalid page', function() {
    //        expect(addPageWizardController.onNext('some invalid page')).toBeResolvedWithData(false);
    //    });
    //});
    //
    //describe('createPage', function() {
    //    beforeEach(function() {
    //        addPageWizardController.model.editor = {
    //            component: {}
    //        };
    //        addPageWizardController.model.selectedType = {
    //            type: 'somePageType',
    //            code: 'somePageTypeCode'
    //        };
    //        addPageWizardController.model.selectedTemplate = {
    //            uid: 'someTemplateUID'
    //        };
    //        addPageWizardController.displayCondition = {
    //            isPrimary: false
    //        };
    //    });
    //
    //    it('should delegate to createPageService', function() {
    //        addPageWizardController.createPage();
    //        expect(mocks.createPageService.createPage).toHaveBeenCalled();
    //    });
    //});
    //
    //describe('selectTemplate', function() {
    //    var oldTemplate;
    //
    //    beforeEach(function() {
    //        oldTemplate = {
    //            value: "Old Template",
    //            isSelected: true
    //        };
    //        addPageWizardController.model.selectedTemplate = oldTemplate;
    //    });
    //
    //    it('should store the newly selected template', function() {
    //        addPageWizardController.selectTemplate({
    //            value: "New Template",
    //            isSelected: false
    //        });
    //
    //        expect(addPageWizardController.model.selectedTemplate).toEqual({
    //            value: "New Template",
    //            isSelected: true
    //        });
    //    });
    //
    //    it('should de-select the previously selected template', function() {
    //        addPageWizardController.selectTemplate({
    //            value: "New Template",
    //            isSelected: false
    //        });
    //
    //        expect(oldTemplate.isSelected).toBe(false);
    //    });
    //});

});
