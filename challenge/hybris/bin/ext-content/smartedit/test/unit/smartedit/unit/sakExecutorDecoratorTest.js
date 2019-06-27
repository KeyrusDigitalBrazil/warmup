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
describe(
    'Unit integration test of sakExecutorDecorator directive',
    function() {
        var decorators, decoratorService, componentHandlerService, systemEventService, sakExecutorService, parent, browserService;
        var $q, $rootScope, $compile, parentScope, directiveScope, element, smarteditComponentType, smarteditComponentId, smarteditProperties;

        beforeEach(angular.mock.module('ui.bootstrap'));
        beforeEach(angular.mock.module('coretemplates'));

        beforeEach(angular.mock.module('smarteditServicesModule', function($provide) {
            decorators = ['decorator1', 'decorator2'];
            decoratorService = jasmine.createSpyObj('decoratorService', ['getDecoratorsForComponent']);
            $provide.value('decoratorService', decoratorService);

            componentHandlerService = jasmine.createSpyObj('componentHandlerService', ['getOriginalComponent', 'getParent']);
            parent = jasmine.createSpyObj('parent', ['attr']);
            var realElement = {};
            componentHandlerService.getOriginalComponent.and.returnValue(realElement);
            componentHandlerService.getParent.and.returnValue(parent);
            $provide.value('componentHandlerService', componentHandlerService);

            browserService = jasmine.createSpyObj('browserService', ['isIE']);
            browserService.isIE.and.returnValue(true);
            $provide.value('browserService', browserService);
        }));

        // Store references to $rootScope and $compile so they are available to all tests in this describe block
        beforeEach(inject(function(_$rootScope_, _sakExecutorService_, _$compile_, _$q_, _systemEventService_) {
            $compile = _$compile_;
            sakExecutorService = _sakExecutorService_;
            spyOn(sakExecutorService, 'registerScope').and.callThrough();
            smarteditComponentType = "ContentSlot";
            smarteditComponentId = "theId";
            smarteditProperties = {
                "smarteditComponentId": smarteditComponentId,
                "smarteditComponentType": smarteditComponentType
            };
            $rootScope = _$rootScope_;
            $q = _$q_;
            systemEventService = _systemEventService_;
            parentScope = $rootScope.$new();
            parentScope.active = false;
            directiveScope = parentScope.$new();
        }));

        function compileDirective() {
            element = angular.element("<smartedit-element class=\"smartEditComponentX\" data-smartedit-component-id=\"" + smarteditComponentId + "\" data-smartedit-component-type=\"" + smarteditComponentType + "\">initialContent</smartedit-element>");
            $compile(element)(directiveScope);
            // fire all the watches, so the scope expressions will be evaluated
            $rootScope.$digest();
            expect(element.scope()).toBe(directiveScope);
            expect(sakExecutorService.registerScope).toHaveBeenCalled();
        }

        it('sakExecutor stacks decorators in this order : decorator2, decorator1', function() {
            var deferred = $q.defer();
            deferred.resolve(decorators);
            decoratorService.getDecoratorsForComponent.and.returnValue(deferred.promise);
            compileDirective();
            expect(element.find('div.decorator2').length).toBe(1);
            expect(element.find('div.decorator2 > div.decorator1').length).toBe(1);

        });

        it("GIVEN IE, sakExecutor will remove decorators when drag and drop starts", function() {
            var deferred = $q.defer();
            deferred.resolve(decorators);
            decoratorService.getDecoratorsForComponent.and.returnValue(deferred.promise);
            compileDirective();

            systemEventService.publish("EVENT_DRAG_DROP_START");

            $rootScope.$digest();
            expect(element.find('.decorator2').length).toBe(0);
            expect(element.find('.decorator1').length).toBe(0);

        });

        it("GIVEN IE, sakExecutor will reapply decorators when drag and drop stops", function() {
            var deferred = $q.defer();
            deferred.resolve(decorators);
            decoratorService.getDecoratorsForComponent.and.returnValue(deferred.promise);
            compileDirective();

            systemEventService.publish("EVENT_DRAG_DROP_START");
            $rootScope.$digest();
            systemEventService.publish("EVENT_DRAG_DROP_END");
            $rootScope.$digest();

            expect(element.find('> div > div.decorator2').length).toBe(1);
            expect(element.find('> div > div.decorator2 > div.decorator1').length).toBe(1);

        });

        it('sakExecutor will process all decorators', function() {
            var deferred = $q.defer();
            deferred.resolve(decorators);
            decoratorService.getDecoratorsForComponent.and.returnValue(deferred.promise);
            compileDirective();
        });

        it('sakExecutor will process all decorators and will add smarteditProperties map to each scope', function() {
            var deferred = $q.defer();
            deferred.resolve(decorators);
            decoratorService.getDecoratorsForComponent.and.returnValue(deferred.promise);
            compileDirective();
            expect(element.find('[data-component-attributes]').length).toBe(2);
        });

    });
