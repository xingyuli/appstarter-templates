var ColumnRenderer = (function() {

    function shortTime(time) {
        return moment(time).format('MM-DD HH:mm');
    }
    function fullTime(time) {
        return moment(time).format('YYYY-MM-DD HH:mm:ss');
    }

    function getGenderLabel(value) {
        switch (value) {
            case 'MALE':   return '男';
            case 'FEMALE': return '女';
            default: return value;
        }
    }

    var scope = {};

    scope.Common = {

        renderShortTime: function(data) {
            return shortTime(data);
        },

        renderFullTime: function(data) {
            if (!data) {
                return null;
            }
            return fullTime(data);
        },

        renderUser: function(data) {
            var areaName = data.areaName ? ' ' + data.areaName : '';
            return '<div class="user-render">' +
                     '<div class="uname">' + data.nickname + '</div>' +
                     '<div class="udetail">' +
                      getGenderLabel(data.gender) + ' ' + data.mobile + areaName +
                     '</div>' +
                   '</div>';
        },

        /**
         *
         * @param {object} data object with properties named by titleProp, createAtProp and updateAtProp
         * @param {object} [config={'titleProp':'title','createAtProp':'createAt','updateAtProp':'updateAt'}]
         */
        renderTitle: function(data, config) {
            config = config || {};

            var title = data[config.titleProp || 'title'],
                createAt = data[config.createAtProp || 'createAt'],
                updateAt = data[config.updateAtProp || 'updateAt'];
            return '<span>' + title + '</span>' +
                '<div class="row-timestamp">' +
                    '<span data-toggle="tooltip" title="创建时间: ' + fullTime(createAt) + '">' + shortTime(createAt) + '</span> |' +
                    '<span data-toggle="tooltip" title="修改时间: ' + fullTime(updateAt) + '">' + shortTime(updateAt) + '</span>' +
                '</div>';
        },

        /**
         *
         * @param {object} data object with properties named by titleProp, createAtProp, updateAtProp and imagesProp
         * @param {object} [config={'titleProp':'title','createAtProp':'createAt','updateAtProp':'updateAt','imagesProp':'images'}]
         */
        renderTitleWithImages: function(data, config) {
            config = config || {};

            var images = data[config.imagesProp || 'images'];
            var content = scope.Common.renderTitle(data, config) + '<br/>';

            if (!images.length) {
                content += '<span style="color: #ccc;">暂无图片</span>';

            } else {
                var carouselId = 'images-carousel-' + data.id;

                content += '<div id="' + carouselId + '" class="carousel slide" data-ride="carousel">';

                    content += '<ol class="carousel-indicators">';
                    images.forEach(function(it, index) {
                        var cssClass = index == 0 ? ' class="active"' : '';
                        content += '<li data-target="#' + carouselId + '" data-slide-to="' + index + '" ' + cssClass + '></li>';
                    });
                    content += '</ol>';

                    content += '<div class="carousel-inner">';
                    images.forEach(function(it, index) {
                        var itemCssClass = index == 0 ? ' class="item active"' : 'class="item"';
                        content += '<div ' + itemCssClass + '>' +
                                       '<img src="' + it.url + '" style="width: 100%;" />' +
                                   '</div>'
                    });
                    content += '</div>';

                content += '<a class="left carousel-control" href="#' + carouselId +'" data-slide="prev">' +
                               '<span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>' +
                           '</a>' +
                           '<a class="right carousel-control" href="#' + carouselId + '" data-slide="next">' +
                               '<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>' +
                           '</a>';

                content += '</div>';
            }

            return content;
        }
    };

    return scope;
})();