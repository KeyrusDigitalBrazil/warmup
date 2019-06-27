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
(function() {

    /**
     * @ngdoc overview
     * @name dragAndDropServiceModule
     * @description
     * # The dragAndDropServiceModule
     *
     * The dragAndDropServiceModule provides a service that wraps over the HTML 5 drag and drop functionality.
     *
     */
    angular.module('dragAndDropServiceModule', ['yjqueryModule', 'yLoDashModule', '_dragAndDropScrollingModule', 'seConstantsModule', 'smarteditServicesModule'])

        /**
         * @ngdoc object
         * @name dragAndDropServiceModule.object:SEND_MOUSE_POSITION_THROTTLE_INTERVAL
         * @description
         * In cross-origin setup, this constant defines the throttle (ms.) to send the mouse position from the smarteditContainer to the inner frame.
         */
        .constant('SEND_MOUSE_POSITION_THROTTLE', 100)

        /**
         * @ngdoc service
         * @name dragAndDropServiceModule.service:dragAndDropService
         *
         * @description
         * The dragAndDropService wraps over the HTML 5 drag and drop functionality to provide a browser independent interface and very
         * basic functionality, like scrolling. The service is intended to be used by another service to provide a richer drag and drop
         * experience.
         */
        .service('dragAndDropService', function($injector, $timeout, isBlank, yjQuery, lodash, _dragAndDropScrollingService, OVERLAY_COMPONENT_CLASS, inViewElementObserver, systemEventService, SMARTEDIT_DRAG_AND_DROP_EVENTS, dragAndDropCrossOrigin) {
            // Constants
            var DRAGGABLE_ATTR = 'draggable';
            var DROPPABLE_ATTR = 'data-droppable';

            // Variables
            this.configurations = {};
            this._isDragAndDropExecuting = false;

            dragAndDropCrossOrigin.initialize();

            /**
             * @ngdoc method
             * @name dragAndDropServiceModule.service:dragAndDropService#register
             * @methodOf dragAndDropServiceModule.service:dragAndDropService
             *
             * @description
             * This method registers a new instance of the drag and drop service.
             * Note: Registering doesn't start the service. It just provides the configuration, which later must be applied with the apply method.
             *
             * @param {Object} configuration The drag and drop configuration
             * @param {String} configuration.id The identifier of the new drag and drop instance. This parameter must be unique, as it is used to track the instance in the service.
             * @param {String} configuration.sourceSelector This parameter specifies the yjQuery selector used to locate the draggable elements managed by the current instance of the drag and drop service.
             * @param {String} configuration.targetSelector This parameter specifies the yjQuery selector used to locate the droppable elements where draggable items of this instance can be dropped.
             * @param {Function} configuration.startCallback This callback is executed when draggable elements start dragging.
             * @param {Function} configuration.dragEnterCallback This callback is executed when the mouse enters a droppable area during a drag and drop operation.
             * @param {Function} configuration.dragOverCallback This callback is executed when the mouse hovers a droppable area during a drag and drop operation.
             * @param {Function} configuration.dropCallback This callback is executed when a draggable element is dropped over a droppable area during a drag and drop operation.
             * @param {Function} configuration.outCallback This callback is executed when the mouse leaves a droppable area during a drag and drop operation.
             * @param {Function} configuration.stopCallback This callback is executed when draggable elements stop dragging.
             * @param {Function} configuration.helper This function is called to specify a custom drag image. Note: IE does not support this functionality, so custom drag images are ignored for that browser.
             * @param {Boolean} configuration.enableScrolling This flag specifies whether to enable scrolling while dragging or not.
             *
             */
            this.register = function(configuration) {
                // Validate
                if (!configuration || !configuration.id) {
                    throw new Error('dragAndDropService - register(): Configuration needs an ID.');
                }

                this.configurations[configuration.id] = configuration;

                if (!isBlank(configuration.targetSelector)) {
                    inViewElementObserver.addSelector(configuration.targetSelector);
                }
            };

            /**
             * @ngdoc method
             * @name dragAndDropServiceModule.service:dragAndDropService#unregister
             * @methodOf dragAndDropServiceModule.service:dragAndDropService
             *
             * @description
             * This method removes the drag and drop instances specified by the provided IDs.
             *
             * @param {Array} configurationsIDList The array of drag and drop configuration IDs to remove.
             *
             */
            this.unregister = function(configurationsIDList) {
                configurationsIDList.forEach(function(configurationID) {
                    var configuration = this.configurations[configurationID];
                    if (configuration) {
                        this._deactivateConfiguration(configuration);
                        this._deactivateScrolling(configuration);
                        delete this.configurations[configurationID];
                    }
                }.bind(this));
            };

            /**
             * @ngdoc method
             * @name dragAndDropServiceModule.service:dragAndDropService#applyAll
             * @methodOf dragAndDropServiceModule.service:dragAndDropService
             *
             * @description
             * This method applies all drag and drop configurations registered.
             *
             */
            this.applyAll = function() {
                lodash.forEach(this.configurations, function(currentConfig) {
                    this.apply(currentConfig.id);
                }.bind(this));
            };

            /**
             * @ngdoc method
             * @name dragAndDropServiceModule.service:dragAndDropService#apply
             * @methodOf dragAndDropServiceModule.service:dragAndDropService
             *
             * @description
             * This method apply the configuration specified by the provided ID in the current page. After this method is executed drag and drop can be started by the user.
             *
             * @param {String} configurationID The identifier of the drag and drop configuration to apply in the current page.
             *
             */
            this.apply = function(configurationID) {
                var configuration = this.configurations[configurationID];
                if (configuration) {
                    this.update(configuration);
                    this._cacheDragImages(configuration);
                    this._initializeScrolling(configuration);
                }
            };

            /**
             * @ngdoc method
             * @name dragAndDropServiceModule.service:dragAndDropService#update
             * @methodOf dragAndDropServiceModule.service:dragAndDropService
             *
             * @description
             * This method updates the drag and drop instance specified by the provided ID in the current page. It is important to execute this method every time a draggable or droppable element
             * is added or removed from the page DOM.
             *
             * @param {String} configurationID The identifier of the drag and drop instance to update.
             *
             */
            this.update = function(configurationID) {
                var configuration = this.configurations[configurationID];
                if (configuration) {
                    this._deactivateConfiguration(configuration);
                    this._update(configuration);
                }
            };

            this._update = function(configuration) {
                var currentService = this;

                var sourceSelectors = lodash.isArray(configuration.sourceSelector) ? configuration.sourceSelector : [configuration.sourceSelector];

                sourceSelectors.forEach(function(sourceSelector) {
                    var draggableElements = this._getSelector(sourceSelector).filter(function() {
                        return !currentService._getSelector(this).attr(DRAGGABLE_ATTR);
                    });

                    draggableElements.attr(DRAGGABLE_ATTR, true);

                    draggableElements.on('dragstart', this._onDragStart.bind(this, configuration));
                    draggableElements.on('dragend', this._onDragEnd.bind(this, configuration));

                }.bind(this));

                var droppableElements = this._getSelector(configuration.targetSelector).filter(function() {
                    return !currentService._getSelector(this).attr(DROPPABLE_ATTR);
                });

                droppableElements.attr(DROPPABLE_ATTR, true); // Not needed by HTML5. It's to mark element as processed.

                droppableElements.on('dragenter', this._onDragEnter.bind(this, configuration));
                droppableElements.on('dragover', this._onDragOver.bind(this, configuration));
                droppableElements.on('drop', this._onDrop.bind(this, configuration));
                droppableElements.on('dragleave', this._onDragLeave.bind(this, configuration));
            };

            this._deactivateConfiguration = function(configuration) {
                var draggableElements = this._getSelector(configuration.sourceSelector);
                var droppableElements = this._getSelector(configuration.targetSelector);

                draggableElements.removeAttr(DRAGGABLE_ATTR);
                droppableElements.removeAttr(DROPPABLE_ATTR);

                draggableElements.off('dragstart');
                draggableElements.off('dragend');

                droppableElements.off('dragenter');
                droppableElements.off('dragover');
                droppableElements.off('dragleave');
                droppableElements.off('drop');
            };

            // Draggable Listeners
            this._onDragStart = function(configuration, yjQueryEvent) {

                // The native transferData object is modified outside the $timeout since it can only be modified 
                // inside the dragStart event handler (otherwise an exception is thrown by the browser).
                var evt = yjQueryEvent.originalEvent;
                this._setDragTransferData(configuration, evt);

                // Necessary because there's a bug in Chrome (and probably Safari) where dragEnd is triggered right after
                // dragStart whenever DOM is modified in the event handler. The problem can be circumvented by using $timeout.
                $timeout(function() {

                    var component = yjQuery(yjQueryEvent.target).closest("." + OVERLAY_COMPONENT_CLASS);

                    this._setDragAndDropExecutionStatus(true, component);

                    _dragAndDropScrollingService._enable();

                    if (configuration.startCallback) {
                        configuration.startCallback(evt);
                    }
                }.bind(this), 0);
            };

            this._onDragEnd = function(configuration, yjQueryEvent) {
                var evt = yjQueryEvent.originalEvent;

                _dragAndDropScrollingService._disable();

                if (this._isDragAndDropExecuting && configuration.stopCallback) {
                    configuration.stopCallback(evt);
                }

                this._setDragAndDropExecutionStatus(false);
            };

            // Droppable Listeners
            this._onDragEnter = function(configuration, yjQueryEvent) {
                var evt = yjQueryEvent.originalEvent;
                evt.preventDefault();

                if (this._isDragAndDropExecuting && configuration.dragEnterCallback) {
                    configuration.dragEnterCallback(evt);
                }
            };

            this._onDragOver = function(configuration, yjQueryEvent) {
                var evt = yjQueryEvent.originalEvent;
                evt.preventDefault();

                if (this._isDragAndDropExecuting && configuration.dragOverCallback) {
                    configuration.dragOverCallback(evt);
                }
            };

            this._onDrop = function(configuration, yjQueryEvent) {
                var evt = yjQueryEvent.originalEvent;
                evt.preventDefault(); // Necessary to receive the on drop event. Otherwise, other handlers are executed.
                evt.stopPropagation();

                if (evt.relatedTarget && evt.relatedTarget.nodeType === 3) {
                    return;
                }
                if (evt.target === evt.relatedTarget) {
                    return;
                }

                if (this._isDragAndDropExecuting && configuration.dropCallback) {
                    configuration.dropCallback(evt);
                }

                return false;
            };

            this._onDragLeave = function(configuration, yjQueryEvent) {
                var evt = yjQueryEvent.originalEvent;
                evt.preventDefault();

                if (this._isDragAndDropExecuting && configuration.outCallback) {
                    configuration.outCallback(evt);
                }
            };

            // Helper Functions
            this._cacheDragImages = function(configuration) {
                var helperImg = null;
                if (configuration.helper) {
                    helperImg = configuration.helper();
                }

                if (helperImg) {
                    if (typeof helperImg === 'string') {
                        configuration._cachedDragImage = new Image();
                        configuration._cachedDragImage.src = helperImg;
                    } else {
                        configuration._cachedDragImage = helperImg;
                    }
                }
            };

            this._setDragTransferData = function(configuration, evt) {
                /*
                    Note: Firefox recently added some restrictions to their drag and drop functionality; it only
                    allows starting drag and drop operations if there's data present in the dataTransfer object. 
                    Otherwise, the whole operation fails silently. Thus, some data needs to be added. 
                */
                evt.dataTransfer.setData('Text', configuration.id);

                if (configuration._cachedDragImage && evt.dataTransfer.setDragImage) {
                    evt.dataTransfer.setDragImage(configuration._cachedDragImage, 0, 0);
                }
            };

            this._getSelector = function(selector) {
                return yjQuery(selector);
            };

            /**
             * @ngdoc method
             * @name dragAndDropServiceModule.service:dragAndDropService#markDragStarted
             * @methodOf dragAndDropServiceModule.service:dragAndDropService
             *
             * @description
             * This method forces the page to prepare for a drag and drop operation. This method is necessary when the drag and drop operation is started somewhere else,
             * like on a different iFrame.
             *
             */
            this.markDragStarted = function() {
                this._setDragAndDropExecutionStatus(true);
                _dragAndDropScrollingService._enable();
            };

            // Method used to stop drag and drop from another frame.
            /**
             * @ngdoc method
             * @name dragAndDropServiceModule.service:dragAndDropService#markDragStarted
             * @methodOf dragAndDropServiceModule.service:dragAndDropService
             *
             * @description
             * This method forces the page to clean after a drag and drop operation. This method is necessary when the drag and drop operation is stopped somewhere else,
             * like on a different iFrame.
             *
             */
            this.markDragStopped = function() {
                this._setDragAndDropExecutionStatus(false);
                _dragAndDropScrollingService._disable();
            };

            this._setDragAndDropExecutionStatus = function(isExecuting, element) {
                this._isDragAndDropExecuting = isExecuting;
                systemEventService.publish(isExecuting ? SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START : SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END, element);
            };

            this._initializeScrolling = function(configuration) {
                if (configuration.enableScrolling && this._browserRequiresCustomScrolling()) {
                    _dragAndDropScrollingService._initialize();
                }
            };

            this._deactivateScrolling = function(configuration) {
                if (configuration.enableScrolling && this._browserRequiresCustomScrolling()) {
                    _dragAndDropScrollingService._deactivate();
                }
            };

            this._browserRequiresCustomScrolling = function() {
                // NOTE: It'd be better to identify if native scrolling while dragging is enabled in the browser, but
                // currently there's no way to know. Thus, browser fixing is necessary.

                return true;
            };
        });

})();
