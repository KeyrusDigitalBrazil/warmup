angular.module('personalizationsmarteditPreviewServiceModule', [
        'experienceServiceModule'
    ])
    .factory('personalizationsmarteditPreviewService', function(experienceService) {
        var previewService = {};

        previewService.removePersonalizationDataFromPreview = function() {
            return previewService.updatePreviewTicketWithVariations([]);
        };

        previewService.updatePreviewTicketWithVariations = function(variations) {
            return experienceService.getCurrentExperience().then(function(experience) {
                if (!experience) {
                    return;
                }
                experience.variations = variations;
                return experienceService.setCurrentExperience(experience).then(function() {
                    return experienceService.updateExperience();
                });
            });
        };

        return previewService;
    });
