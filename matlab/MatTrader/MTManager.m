classdef MTManager < handle
    %MTManager main class for handling calls to MatTrader
    
    properties (SetAccess = private, GetAccess = private)
        manager
    end
    properties
        killClientsOnClear = false
    end
    
    methods
        function obj = MTManager(provider)
            import com.mattrader.matlab.*
            import com.mattrader.common.IProvider
            obj.manager = MTClientManager.Manager;
            obj.manager.setProvider(provider);
        end
        
        function client = getClient(obj, name)
            if nargin < 2
                error('Error: Expected a name for the client.');
            end
            import com.mattrader.matlab.*
            client = obj.manager.getClient(name);
        end
        
        function delete(obj, kill)
            import com.mattrader.matlab.*
            if exist('kill','var') && ~isempty(kill)
                if kill
                    obj.manager.stopAll();
                else
                    return
                end
            end
            if obj.killClientsOnClear
                obj.manager.stopAll();
            end
        end
        
        function set.killClientsOnClear(obj, flag)
            if ~islogical(flag)
                error('Error: killClientsOnClear accept only boolean values (true/false).');
            end
            obj.killClientsOnClear = flag;
        end
    end
    
    methods (Static)
        function help(objName)
            import com.mattrader.matlab.*
            disp(Help.doc(objName));
        end
    end
    
end

