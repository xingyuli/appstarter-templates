(function() {
    function parentIfInsideInputGroup(element) {
        var parent = $(element).parent();
        return parent.hasClass('input-group') ? parent : $(element);
    }

    // bootstrap datepicker
    $.extend(true, $.fn.datepicker.defaults, {
        language: 'zh-CN',
        autoclose: true
    });

    // jquery datatable
    $.extend(true, $.fn.dataTable.defaults, {
        rowId: 'id',
        processing: true,
        serverSide: true,
        ordering: false,
        searching: false,
        language: {
            url: contextRoot + "/assets/external/js/app/dataTables.chinese.lang.json"
        }
        // stateSave: true
        // ajax: {
        //     dataFilter: function(data) {
        //         data = JSON.parse(data);
        //         return JSON.stringify(data.result);
        //     }
        // }
    });

    // jquery validation
    (function() {
        $.extend(true, $.validator, {
            messages: {
                required: '此项必填',
                digits: '必须为整数',
                number: '必须为数字',
                minlength: $.validator.format('长度至少为{0}'),
                maxlength: $.validator.format('长度不得超过{0}')
            }
        });

        $.validator.setDefaults({

            // bootstrap input-group adapter
            errorPlacement: function(error, element) {
                parentIfInsideInputGroup(element).after(error);
            },
            highlight: function(element, errorClass, validClass) {
                parentIfInsideInputGroup(element).addClass(errorClass).removeClass(validClass);
            },
            unhighlight: function(element, errorClass, validClass) {
                parentIfInsideInputGroup(element).removeClass(errorClass).addClass(validClass);
            }

        });

        $.validator.addMethod('checkIdBadge', function(value, element) {
            return this.optional(element) || /^\d{16}[a-zA-Z0-9]{2}$/.test(value);
        }, '请输入正确的身份证号');
    })();

    // ajax
    $.ajaxSetup({
        beforeSend: function(req) {
            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            req.setRequestHeader(header, token);
        }
    });

    // clear jquery form validation errors
    $.fn.clearValidation = function() {
        var v = $(this).validate();
        $('[name]', this).each(function() {
            parentIfInsideInputGroup(this).removeClass('error');
        });
        v.resetForm();
    };
})();

var swalHelper = (function() {
    var defaults = {
        confirm: {
            title: '确认',
            text: '',
            type: 'warning',
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonColor: '#DD6B55',
            confirmButtonText: '是的',
            closeOnConfirm: false
        },
        input: {
            title: '请输入',
            text: '',
            type: 'input',
            animation: "slide-from-top",
            showCancelButton: true,
            cancelButtonText: '取消',
            confirmButtonText: '保存',
            closeOnConfirm: false
        },
        info: {
            title: '提示',
            text: '',
            type: 'info',
            timer: 2000,
            showConfirmButton: false
        },
        success: {
            title: '成功',
            text: '',
            type: 'success',
            confirmButtonText: '好'
        },
        error: {
            title: '错误',
            text: '',
            type: 'error',
            confirmButtonText: '好'
        }
    };

    return {
        /**
         * @param {string|object} setting
         * @param {function} fun
         */
        confirm: function(setting, fun) {
            var option;
            if (typeof setting === 'string') {
                option = $.extend({}, defaults.confirm, { text: setting })
            } else {
                option = $.extend({}, defaults.confirm, setting);
            }
            swal(option, fun);
        },

        /**
         * @param {object} setting With at least title, inputPlaceHolder, inputValue
         * @param {function} fun
         */
        input: function(setting, fun) {
            swal($.extend({}, defaults.input, setting), fun);
        },

        /**
         * @param {string} [title]
         * @param {string} [text]
         * @param {function} fn
         */
        success: function(title, text, fn) {
            if ($.isFunction(title)) {
                fn = title;
                title = undefined;
            }
            if ($.isFunction(text)) {
                fn = text;
                text = undefined;
            }
            swal($.extend({}, defaults.success, {title: title, text: text}), fn);
        },

        /**
         * @param {string} title
         * @param {string} [text='']
         * @param {number} [timer=2000]
         */
        info: function(title, text, timer) {
            swal($.extend({}, defaults.info, { title: title, text: text, timer: timer }));
        },

        /**
         * @param {string} title
         * @param {string} [text='']
         */
        error: function(title, text) {
            swal($.extend({}, defaults.error, { title: title, text: text }));
        }
    };
})();

FormData.prototype.appendIfNotNull = function(name, value) {
    if (value) {
        this.append(name, value);
    }
};

String.prototype.capitalize = function() {
    if (this.length === 0) {
        return '';
    } else if (this.length === 1) {
        return this.toUpperCase();
    } else {
        return this[0].toUpperCase() + this.substr(1);
    }
};

var imageHelper = (function() {
    function clacImgZoomParam(maxWidth, maxHeight, width, height) {
        var param = {top: 0, left: 0, width: width, height: height};
        if (width > maxWidth || height > maxHeight) {
            rateWidth = width / maxWidth;
            rateHeight = height / maxHeight;

            if (rateWidth > rateHeight) {
                param.width = maxWidth;
                param.height = Math.round(height / rateWidth);
            } else {
                param.width = Math.round(width / rateHeight);
                param.height = maxHeight;
            }
        }

        param.left = Math.round((maxWidth - param.width) / 2);
        param.top = Math.round((maxHeight - param.height) / 2);
        return param;
    }

    return {
        /**
         * 图片上传预览（IE使用了滤镜）
         * @param file
         * @param {string} containerId='preview'
         * @param {string} imageId='imgHead'
         * @param {string} inputId='fileInput'
         * @param {boolean} alreadyFile=false
         * @returns {boolean}
         */
        preview: function(file, containerId, imageId, inputId, alreadyFile) {
            containerId = containerId || 'preview';
            imageId = imageId || 'imgHead';
            inputId = inputId || 'fileInput';
            alreadyFile = !!alreadyFile;

            // var originalSrc = document.getElementById(imageId).src;
            var f = document.getElementById(inputId).value;
            if (!/\.(gif|jpg|jpeg|png|GIF|JPG|PNG)$/.test(f)) {
                swalHelper.info("上传的图片类型不正确，应为gif，jpg，jpeg，png中的一种！");
                return false;
            }
            var MAX_WIDTH = 260;
            var MAX_HEIGHT = 180;
            var div = document.getElementById(containerId);

            var selectedFiles = alreadyFile ? file : file.files;
            if (selectedFiles && selectedFiles[0]) {
                div.innerHTML = '<img id="' + imageId + '">';
                var img = document.getElementById(imageId);
                img.width = 100;
                img.height = 100;
                // img.onload = function () {
                // var rect = clacImgZoomParam(MAX_WIDTH, MAX_HEIGHT, img.offsetWidth, img.offsetHeight);
                // img.width = 100;
                // img.height = 100;
                // };
                var reader = new FileReader();
                reader.onload = function (evt) {
                    img.src = evt.target.result;
                    // var imgHeight = img.naturalWidth - img.naturalHeight;
                    // if (imgHeight > 20 || imgHeight < (-20)) {
                    //     alert("图片长宽尺寸比例必须为1:1");
                    //     // file.outerHTML=file.outerHTML;
                    //     img.src = originalSrc;
                    //     return false;
                    // }
                };
                reader.readAsDataURL(selectedFiles[0]);
            }
            else //兼容IE
            {
                var sFilter = 'filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale,src="';
                file.select();
                var src = document.selection.createRange().text;
                div.innerHTML = '<img id="' + imageId + '">';
                img = document.getElementById(imageId);
                img.filters.item('DXImageTransform.Microsoft.AlphaImageLoader').src = src;
                var rect = clacImgZoomParam(MAX_WIDTH, MAX_HEIGHT, img.offsetWidth, img.offsetHeight);
                var status = ('rect:' + rect.top + ',' + rect.left + ',' + rect.width + ',' + rect.height);
                div.innerHTML = "<div id=divhead style='width:" + rect.width + "px;height:" + rect.height + "px;" + sFilter + src + "\"'></div>";
            }
        }
    };
})();


