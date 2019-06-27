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
describe('Editor Tabset Directive ', function() {

    var tabsetService, genericEditor;

    beforeEach(function() {
        window.addModulesIfNotDeclared(['experienceInterceptorModule', 'tabsetModule']);

        angular.mock.module('editorTabsetModule', function($provide) {
            genericEditor = jasmine.createSpy('genericEditor');

            $provide.value('genericEditor', genericEditor);
        });

        inject(function(editorTabsetService) {
            tabsetService = editorTabsetService;
        });
    });

    function getTabById(tabsList, tabId) {
        return tabsList.filter(function(tab) {
            if (tab) {
                return tab.id === tabId;
            }
            return false;
        })[0];
    }


    it('registerTab will register a tab for a group of content types', function() {
        // Arrange
        var tabId = 'tab1';
        var tabTitle = 'some title';
        var tabTemplateURL = 'some URL';
        spyOn(tabsetService, '_validateTab').and.callFake(function() {});

        // Act
        tabsetService.registerTab(tabId, tabTitle, tabTemplateURL);

        // Assert
        var tabsList = tabsetService.tabsList;
        var expectedTabs = ['tab1'];
        expect(tabsetService._validateTab).toHaveBeenCalledWith(tabId, tabTitle, tabTemplateURL);
        expectedTabs.forEach(function(tabId) {
            var tab = getTabById(tabsList, tabId);
            expect(tab).not.toBeUndefined();
            expect(tab.id).not.toBeUndefined();
            expect(tab.title).not.toBeUndefined();
            expect(tab.templateUrl).not.toBeUndefined();
            expect(tab.hasErrors).toBe(false);
        });

        var registeredTab = getTabById(tabsList, tabId);
        expect(registeredTab.id).toBe(tabId);
        expect(registeredTab.title).toBe(tabTitle);
        expect(registeredTab.templateUrl).toBe(tabTemplateURL);
    });

    it('registerTab will throw an exception if validation unsuccessful', function() {
        // Arrange
        var tabId = 'tab1';
        var tabTitle = 'some title';
        var tabTemplateURL = 'some URL';
        spyOn(tabsetService, '_validateTab').and.throwError("Some error message");

        // Act/Assert
        expect(function() {
            tabsetService.registerTab(tabId, tabTitle, tabTemplateURL);
        }).toThrowError("Some error message");
    });

    it('_validateTab will throw an exception for invalid ID', function() {
        // Arrange/Act/Assert
        expect(function() {
            tabsetService._validateTab(null, "some Title", "some URL");
        }).toThrow(new Error("editorTabsetService.registerTab.invalidTabID"));
    });

    it('_validateTab will throw an exception for missing tab title', function() {
        // Arrange/Act/Assert
        expect(function() {
            tabsetService._validateTab("Some ID", null, "some URL");
        }).toThrow(new Error("editorTabsetService.registerTab.missingTabTitle"));
    });

    it('_validateTab will throw an exception for missing template Url', function() {
        // Arrange/Act/Assert
        expect(function() {
            tabsetService._validateTab("Some ID", "some Title", null);
        }).toThrow(new Error("editorTabsetService.registerTab.missingTemplateUrl"));
    });

    it('removeTab will remove a tab for a group of content types', function() {
        // Arrange
        var tab = {
            id: 'tab1',
            title: 'Tab1 Title',
            templateUrl: 'test1'
        };

        tabsetService.tabsList = [tab];

        expect(tabsetService.tabsList[0]).toBeDefined();
        // Act
        tabsetService.deleteTab(tab.id);

        // Assert
        expect(tabsetService.tabsList[0]).toBeUndefined();
    });
});
