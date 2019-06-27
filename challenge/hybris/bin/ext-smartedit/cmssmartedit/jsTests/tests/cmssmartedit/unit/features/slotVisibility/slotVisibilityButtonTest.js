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
xdescribe('slotVisibilityButton', function() {

    var overlayEl;

    beforeEach(angular.mock.module('ui.bootstrap'));

    beforeEach(function() {
        overlayEl = angular.element('<div id="smarteditoverlay"></div>')[0];
        document.body.appendChild(overlayEl);
    });

    describe('when hidden component list is empty', function() {
        describe('template', function() {
            it('should not display the button', function() {
                // Arrange
                var element = buildComponentWithNoHiddenComponents();

                // Act

                // Assert
                expect(element.find('button[type=button]').length).toBe(0);
            });
        });

        describe('controller', function() {
            it('should set button visibility to false', function() {
                // Arrange
                var element = buildComponentWithNoHiddenComponents();
                var controller = element.isolateScope().ctrl;

                // Act

                // Assert
                expect(controller.buttonVisible).toBe(false);
            });
            it('should expect that the hidden component list is empty', function() {
                // Arrange
                var element = buildComponentWithNoHiddenComponents();
                var controller = element.isolateScope().ctrl;

                // Act

                // Assert
                expect(controller.hiddenComponents).toEqual([]);
            });
            it('should expect component list visibility status to not be opened', function() {
                // Arrange
                var element = buildComponentWithNoHiddenComponents();
                var controller = element.isolateScope().ctrl;

                // Act

                // Assert
                expect(controller.isComponentListOpened).toBe(false);
            });
        });
    });

    describe('when hidden component list is not empty', function() {
        describe('template', function() {
            it('should display the hidden components button', function() {
                // Arrange
                var element = buildComponentWithTwoHiddenComponents();

                // Act

                // Assert
                expect(element.find('button.slot-visibility-button-template__btn').length)
                    .toBe(1, 'Expected slot visibility hidden components button to be present');
            });

            it('should display dropdown menu if the button is clicked', function() {
                // Arrange
                var element = buildComponentWithTwoHiddenComponents();

                // Act
                element.find('button.slot-visibility-button-template__btn').click();

                // Assert
                expect(element.find('.btn-group.slot-visibility-button-template__btn-group.dropdown').length).toBe(1);
            });

            it('should properly flag hidden component with isExternal boolean', function() {
                // Arrange
                var element = buildComponentWithTwoHiddenComponents();
                var controller = element.isolateScope().ctrl;

                // Assert
                expect(controller.hiddenComponents[0].isExternal).toBe(false);
                expect(controller.hiddenComponents[1].isExternal).toBe(true);
            });
        });

        describe('controller', function() {
            it('should expect button to be visible', function() {
                // Arrange
                var element = buildComponentWithTwoHiddenComponents();
                var controller = element.isolateScope().ctrl;

                // Act
                element.find('button.slot-visibility-button-template__btn').click();

                // Assert
                expect(controller.buttonVisible).toBe(true);
            });

            it('should expect that the hidden component list is not empty', function() {
                // Arrange
                var element = buildComponentWithTwoHiddenComponents();
                var controller = element.isolateScope().ctrl;

                // Act
                element.find('button.slot-visibility-button-template__btn').click();

                // Assert
                expect(controller.hiddenComponents.length).toBe(2);
            });

            it('should expect component list visibility status to be opened', function() {
                // Arrange
                var element = buildComponentWithTwoHiddenComponents();
                var controller = element.isolateScope().ctrl;

                // Act
                element.find('button.slot-visibility-button-template__btn').click();

                // Assert
                expect(controller.isComponentListOpened).toBe(true);
            });
        });
    });

    afterEach(function() {
        document.body.removeChild(overlayEl);
    });

    function buildComponentWithNoHiddenComponents() {
        var template = '<slot-visibility-button ' +
            'data-slot-id="{{::smarteditComponentId}}" ' +
            'data-set-remain-open="setRemainOpen(button, remainOpen)">' +
            '</slot-visibility-button>';

        return AngularUnitTestHelper.prepareModule('slotVisibilityButtonModule')
            .mock('sharedDataService', 'get').and.returnResolvedPromise({
                pageContext: {
                    catalogVersionUuid: "MOCKED_CATALOG_VERSION_UUID"
                }
            })

            .mock('contextualMenuService', 'getContextualMenuByType').and.returnValue([])
            .mock('catalogService', 'getCatalogVersionByUuid').and.returnResolvedPromise({
                catalogId: 'catalogId',
                version: 'version'
            })
            .mock('catalogService', 'getContentCatalogsForSite').and.returnResolvedPromise([])
            .mock('catalogVersionPermissionService', 'hasWritePermission').and.returnResolvedPromise(true)
            .mock('gatewayProxy', 'initForService').and.returnValue()
            .mock('slotVisibilityService', 'getHiddenComponents').and.returnResolvedPromise([])
            .mock('editorModalService', 'openAndRerenderSlot').and.returnResolvedPromise()
            .mock('crossFrameEventService', 'subscribe').and.returnValue(function() {})
            .mockConstant('EVENT_OUTER_FRAME_CLICKED', 'EVENT_OUTER_FRAME_CLICKED')
            .component(template, {
                smarteditComponentId: 'abc-slot-id',
                setRemainOpen: jasmine.createSpy('setRemainOpen')
            }).element;
    }

    function buildComponentWithTwoHiddenComponents() {
        var template = '<slot-visibility-button ' +
            'data-slot-id="{{::smarteditComponentId}}" ' +
            'data-set-remain-open="setRemainOpen(button, remainOpen)">' +
            '</slot-visibility-button>';

        return AngularUnitTestHelper.prepareModule('slotVisibilityButtonModule')
            .mock('sharedDataService', 'get').and.returnResolvedPromise({
                pageContext: {
                    catalogVersionUuid: "MOCKED_CATALOG_VERSION_UUID"
                }
            })
            .mock('contextualMenuService', 'getContextualMenuByType').and.returnValue([])
            .mock('catalogService', 'getCatalogVersionByUuid').and.returnResolvedPromise({
                catalogId: 'catalogId',
                version: 'version'
            })
            .mock('catalogService', 'getContentCatalogsForSite').and.returnResolvedPromise([])
            .mock('catalogVersionPermissionService', 'hasWritePermission').and.returnResolvedPromise(true)
            .mock('gatewayProxy', 'initForService').and.returnValue()
            .mock('slotVisibilityService', 'getHiddenComponents').and.returnResolvedPromise([{
                catalogVersion: "MOCKED_CATALOG_VERSION_UUID",
                uid: '1',
                name: 'component1',
                typeCode: 'BannerComponent'
            }, {
                catalogVersion: "MOCKED_EXTERNAL_CATALOG_VERSION_UUID",
                uid: '2',
                name: 'component2',
                typeCode: 'SimpleBannerComponent'
            }])
            .mock('editorModalService', 'openand.rerenderSlot').and.returnResolvedPromise()
            .mock('crossFrameEventService', 'subscribe').and.returnValue(function() {})
            .mockConstant('EVENT_OUTER_FRAME_CLICKED', 'EVENT_OUTER_FRAME_CLICKED')
            .component(template, {
                smarteditComponentId: 'abc-slot-id',
                setRemainOpen: jasmine.createSpy('setRemainOpen')
            }).element;
    }

});
