classdef EventHandler < handle
    %EVENTHANDLER this is a wrapper to manage events in a native Matlab way
    
    properties (SetAccess = private, GetAccess = private)
        em;
    end
    
    methods
        function obj = EventHandler(eventManager)
            import com.mattrader.matlab.*
            import com.mattrader.common.*
            obj.em = handle(eventManager,'CallbackProperties');
        end
        
        function setCallback(obj, callback, varargin)
            import com.mattrader.matlab.*
            if nargin ~= 2 && nargin ~= 3
                error('Wrong number of parameters\n\tie. setCallback(@myFunction)');
            end
            if nargin == 2
                set(obj.em,'OnEventCallback',@(h,e)callback(h,MTEvent.toMatlabEvent(e)));
            else
                % abbiamo un input, che dovrà essere in formato cell array;
                % ci saranno 3 stringhe speciali che delimiteranno i tipi
                % di input inseriti: 'static', 'dynamic', 'evaluated'.
                % 'static' saranno i dati che non variano o quelli che
                % (beati loro) vengono passati by reference; 'dynamic' sono
                % quei dati che vengono modificati da altri processi e per
                % i quali non ho un handler che li circonda, pertanto ne
                % creo al volo un getter e via! (sono molto pericolosi per
                % via del evalin(), a causa di questo e del pessimo sistema
                % di scoping (Workspace) di Matlab non vi è la certezza che
                % il dato a cui si fa riferimento esista, ossia che la get
                % creata al volo faccia riferimento all'environment giusto;
                % piccola nota aggiuntiva, questi parametri devono essere
                % passati per stringa, una stringa che ha il nome della
                % variabile in oggetto [**[**[ considerare la funzione
                % inputname ]**]**], così non bisogna passare i nomi delle
                % variabili (come farlo funzionare con un cell-array?);
                % 'evaluated' è per i dati che verranno valutati nel
                % momento in cui la callbackFunction sta per essere
                % chiamata.
                input = varargin{1};
                newInput = {};
                if(~isempty(input))
                    index = 1;
                    i = 1;
                    toEval = [];
                    statusConsidered = 'static';
                    while i < length(index)
                        % ovviamente non posso permettere che venga passata
                        % una stringa he contenga una delle 3 keyword come
                        % parametro vero, quindi l'utente dovrà evitare di
                        % passare ste 3 stringhe (sarebbe stato meglio
                        % usare dei cell array per i parametri :/
                        if isequal(lower(input{i}),'static')
                            statusConsidered = 'static';
                        elseif isequal(lower(input{i}),'dynamic')
                            statusConsidered = 'dynamic';
                        elseif isequal(lower(input{i}),'evaluated')
                            statusConsidered = 'evaluated';
                            newInput{index} = {};
                            toEval(end + 1) = index;
                            index = index + 1;
                            indexEvaluated = 1;
                        else
                            if strcmp(statusConsidered,'static')
                                newInput{index} = input{i};
                                index = index + 1;
                            elseif strcmp(statusConsidered,'dynamic')
                                newInput{index} = evalin('caller',['@()',input{i}]);
                                index = index + 1;
                            elseif strcmp(statusConsidered,'evaluated')
                                realIndex = index - indexEvaluated;
                                if isa(input{i},'function_handle')
                                    tempStr = func2str(input{i});
                                    % newInput{realIndex}{indexEvaluated} = ['eval(''',tempStr(4,end),''')'];
                                    newInput{realIndex}{indexEvaluated} = tempStr(4,end);
                                elseif strcmp(input{i}(1,3),'@()')
                                    % newInput{realIndex}{indexEvaluated} = ['eval(''',input{i}(4,end),''')'];
                                    newInput{realIndex}{indexEvaluated} = input{i}(4,end);
                                else
                                    % newInput{realIndex}{indexEvaluated} = ['eval(''',input{i},''')'];
                                    if ~isa(input{i},'char')
                                        error('''evaluated'' parameters must be function_handlers or strings');
                                    end
                                    newInput{realIndex}{indexEvaluated} = input{i};
                                end
                                index = index + 1;
                                indexEvaluated = indexEvaluated + 1;
                            end
                        end
                    end
                end
                set(obj.em,'OnEventCallback',@(h,e)callback(h,MTEvent.toMatlabEvent(e),organizeInput(newInput, toEval)));
            end
        end
        
        function setNullCallback(obj)
            set(obj.em,'OnEventCallback',[]);
        end
    end
    
    methods (Static)
        
        function input = organizeInput(input, toEval)
            for iEval = 1:length(toEval)
                i = toEval(iEval);
                i = i{1};
                array = input{i};
                for j = length(array) : -1 : 1
                    input{i + j - 1} = eval(array{j});
                end
            end
        end
        
    end
    
end

