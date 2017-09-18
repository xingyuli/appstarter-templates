var ViewModel = (function() {
    var ImageStatus = {
        // existed image
        NORMAL: 'normal',
        REMOVED: 'removed',

        // new image
        CHOOSING: 'choosing',
        CHOSE: 'chose'
    };

    function ImageViewModel(data) {
        var self = this;
        ko.mapping.fromJS(data, {}, self);

        var timeBasedId = new Date().getTime();

        self.inputId = 'choose-image-' + timeBasedId;
        self.previewContainerId = 'preview-image-container-' + timeBasedId;
        self.previewImageId = 'preview-image-' + timeBasedId;
        self.inputFile = ko.observable();

        if (self.id()) {
            self.status = ko.observable(ImageStatus.NORMAL);
        } else {
            self.status = ko.observable(ImageStatus.CHOOSING);
        }

        self.chooseImage = function() {
            $('#' + self.inputId).click();
        };
        self.imageChosen = function(files) {
            if (files && files.length) {
                self.inputFile(files[0]);
                self.status(ImageStatus.CHOSE);
                imageHelper.preview(files, self.previewContainerId, self.previewImageId, self.inputId, true);
            }
        };
    }

    var scope = {};

    scope.Image = ImageViewModel;
    scope.Image.Status= ImageStatus;
    scope.Image.Support = function(self, observableImagesProp) {
        function getObservableImages() {
            return self[observableImagesProp || 'images'];
        }
        self.allowMoreImage = ko.pureComputed(function() {
            return getObservableImages()().filter(function(it) { return it.status() === ImageStatus.CHOOSING; }).length == 0;
        });
        self.moreImage = function() {
            var imageVM = new ImageViewModel({ id: null });
            getObservableImages().push(imageVM);
            imageVM.chooseImage();
        };
        self.removeChosenImage = function(data) {
            getObservableImages().remove(data);
        };
    };

    return scope;
})();