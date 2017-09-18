(function(components) {
    var Status = {
        VIEW: 'view',
        EDIT: 'edit'
    };

    var NO_SELECTION = {};

    function SuggestViewModel(options) {
        var self = this;
        self._options = $.extend({
            // mandatory
            dataUrl: null,
            name: null,
            renderSelection: null,
            renderOriginal: null,
            renderOriginalId: null,

            // optional
            queryKey: 'keyword',
            idKey: 'id',
            required: true,
            placeholder: '请填写',
            onSelect: null,
            onRemove: null // must clean the data that is used by renderOriginal and renderOriginId, if present
        }, options);

        if (!self._options.dataUrl) {
            throw new Error('option dataUrl is missing');
        }
        if (!self._options.name) {
            throw new Error('option name is missing');
        }
        if (!self._options.renderSelection) {
            throw new Error('option renderSelection is missing');
        }
        if (!self._options.renderOriginal) {
            throw new Error('option renderOriginal is missing');
        }
        if (!self._options.renderOriginalId) {
            throw new Error('option renderOriginalId is missing');
        }

        var _timer;

        function _hasValue() {
            return self.selection() && self.selection() !== NO_SELECTION || self._options.renderOriginalId()
        }

        // states
        self.status = ko.observable(Status.VIEW);
        self.selection = ko.observable();
        self._query = ko.observable();
        self._candidates = ko.observableArray();
        self._candidatesTooMany = ko.observable(false);
        self._candidatesNoResult = ko.observable(false);

        // behaviours
        self.reset = function(toStatus) {
            self.status(toStatus);
            self.selection(null);
            self._query('');
            self._candidates([]);
            self._candidatesTooMany(false);
            self._candidatesNoResult(false);
        };
        self._isViewMode = ko.pureComputed(function() {
            return self.status() === Status.VIEW;
        });
        self._isEditMode = ko.pureComputed(function() {
            return self.status() === Status.EDIT;
        });
        self._enableClear = ko.pureComputed(function() {
            return self._options.onRemove && _hasValue();
        });
        self._enableCancel = ko.pureComputed(function() {
            return _hasValue();
        });
        self._clearHandler = function() {
            // must be called at first, so that _idToDisplay and _selectionToDisplay work correctly
            self._options.onRemove();

            self.reset(Status.EDIT);

            // trigger dom re-rendering via marker object
            self.selection(NO_SELECTION);
        };
        self._editHandler = function() {
            self.status(Status.EDIT);
        };
        self._searchHandler = function() {
            var query = self._query().trim();
            if (query) {
                var params = {};
                params[self._options.queryKey] = query;
                $.getJSON(self._options.dataUrl, params, function(resp) {
                    self._candidates(resp.result.rows);
                    self._candidatesTooMany(resp.result.total > 10);
                    self._candidatesNoResult(resp.result.total === 0);
                });
            }
        };
        self._cancelHandler = function() {
            self.status(Status.VIEW);
            self._query('');
            self._candidates([]);
            self._candidatesTooMany(false);
            self._candidatesNoResult(false);
        };
        self._notifyQueryChange = function(data, event) {
            if (_timer) {
                clearTimeout(_timer);
            }
            _timer = setTimeout(function() {
                var query = $(event.currentTarget).val();
                if (!query) {
                    self._candidates([]);
                    self._candidatesTooMany(false);
                    self._candidatesNoResult(false);
                }
            }, 500);
        };
        self._choose = function(data, event) {
            var target = $(event.currentTarget);

            self.status(Status.VIEW);
            self.selection(data);
            self._query('');
            self._candidates([]);
            self._candidatesTooMany(false);
            self._candidatesNoResult(false);

            if (self._options.onSelect) {
                self._options.onSelect(data, target);
            }
        };
        self._idToDisplay = ko.pureComputed(function() {
            var selection = self.selection();
            if (selection && selection !== NO_SELECTION) {
                return selection[self._options.idKey];
            }
            return self._options.renderOriginalId();
        });
        self._selectionToDisplay = ko.pureComputed(function() {
            var selection = self.selection();
            if (selection && selection !== NO_SELECTION) {
                return self._options.renderSelection(selection);
            }
            return self._options.renderOriginal();
        });
    }

    components.SuggestViewModel = SuggestViewModel;
    components.SuggestViewModel.Status = Status;

})(window.KOComponents = window.KOComponents || {});