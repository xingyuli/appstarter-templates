$(function() {
    var $formModal = $('#formModal'),
        $form = $('#formTag'),
        table;

    function FormViewModel() {
        var self = this;

        var initialState = {
            id: null,
            name: null,
            minPoints: null,
            maxPoints: null,
            pointToRmbRatio: null
        };
        ko.mapping.fromJS(initialState, {}, self);

        self.reset = function(editorStatus) {
            ko.mapping.fromJS(initialState, self);
        };
        self.isEdit = ko.pureComputed(function() {
            return !!self.id();
        });
    }

    function emptyStrIfIsUndefined(value) {
        if (typeof value === 'undefined' || value === null) {
            return '';
        }
        return value;
    }

    var formViewModel = new FormViewModel();
    ko.applyBindings(formViewModel, $formModal[0]);

    table = $('#quizDataTable').DataTable({
        ajax: CON.API.ADMINUSERLEVEL_LIST,
        select: {
            style: 'single',
            blurable: true,
            info: false
        },
        responsive: true,
        dom: '<"row"<"col-sm-6"B>>rtip',
        buttons: [
            {
                text: '新增',
                action: function() {
                    $form.clearValidation();
                    formViewModel.reset();
                    $formModal.modal('show');
                }
            },
            {
                text: '编辑',
                action: function() {
                    var data = table.row({ selected: true }).data();
                    $form.clearValidation();
                    formViewModel.reset();
                    ko.mapping.fromJS(data, formViewModel);
                    $formModal.modal('show');
                },
                enabled: false
            },
            {
                text: '删除',
                action: function() {
                    var data = table.row({ selected: true }).data();
                    $.ajax({
                        url: CON.API.ADMINUSERLEVEL_DELETE.replace('{id}', data.id),
                        type: 'DELETE',
                        success: function(resp) {
                            if (CON.CODE.OK === resp.code) {
                                swalHelper.success("操作成功");
                                table.ajax.reload(null, false);
                            } else {
                                swalHelper.error(resp.message || '出错了...');
                            }
                        },
                        error: function() {
                            swalHelper.error('出错了...');
                        }
                    });
                },
                enabled: false
            }
        ],
        columns: [
            { title: '名称', data: 'name' },
            {
                title: '积分范围',
                data: '',
                render: function(data, type, row, meta) {
                    return emptyStrIfIsUndefined(row.minPoints) + ' ~ ' + emptyStrIfIsUndefined(row.maxPoints);
                }
            },
            { title: '积分提现比例', data: 'pointToRmbRatio' }
        ]
    });
    table.on('select deselect', function() {
        var selectedRow = table.row({ selected: true });
        table.button(1).enable(selectedRow.count() > 0);
        table.button(2).enable(selectedRow.count() > 0);
    });

    $form.validate({
        rules: {
            minPoints: 'digits',
            maxPoints: 'digits',
            pointToRmbRatio: 'number'
        },
        submitHandler: function(form) {
            var formData = new FormData(form);
            $.ajax({
                url: CON.API.ADMINUSERLEVEL_CREATE_OR_UPDATE,
                data: formData,
                type: 'POST',
                processData: false,
                contentType: false,
                success: function(resp) {
                    if (CON.CODE.OK === resp.code) {
                        $formModal.modal("hide");
                        table.ajax.reload(null, false);
                        swalHelper.success('保存成功！');
                    } else {
                        swalHelper.error('保存失败！');
                    }
                }
            });
        }
    });
});