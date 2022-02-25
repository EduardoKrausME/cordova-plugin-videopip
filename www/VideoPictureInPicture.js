var exec = require ( 'cordova/exec' );

var VideoPictureInPicture = {

    enterPip : function ( width, height, success, error ) {
        exec ( success, error, "VideoPictureInPicture", "enterPip", [ width, height ] );
    },

    initializePip : function ( success, error ) {
        exec ( success, error, "VideoPictureInPicture", "initializePip", [] );
    },

    isPip : function ( success, error ) {
        exec ( success, error, "VideoPictureInPicture", "isPip", [] );
    },

    onChanged : function ( success, error ) {
        exec ( success, error, "VideoPictureInPicture", "onChanged", [] );
    },

    isSupported : function ( success, error ) {
        exec ( success, error, "VideoPictureInPicture", "isSupported", [] );
    }
};

module.exports = VideoPictureInPicture;