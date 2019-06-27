ACC.punchout = {

    _autoload: [
        "blockInspectLogoLink",
        "punchoutNavigation"
    ],


    blockInspectLogoLink: function(){
        $(".inspect-logo a").on("click touchend", function(e){
            e.preventDefault();
        });
    },

    punchoutNavigation: function(){
        if($('.punchout-header').length > 0){
            $('.js-userAccount-Links').remove();
        }
    }
};