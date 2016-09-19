classdef MachineHist < handle
    %AUTOMACHINE Gestisce una strategia e ne applica delle strategie RT
    %   Accetta due parametri obbligatori ed una serie di parametri
    %   opzionali:
    %    - ticker: stringa o oggetto Ticker, il titolo sul quale vorremmo
    %       operare
    %    - algorithm: una funzione che implementa la strategia di trading.
    %       Questa funzione dovrà accettare 6 parametri:
    %        - typeEvent: 'price' o 'order'
    %        - price: valido solo nel caso 'price'. Il prezzo di mercato
    %        - volume: valido solo nel caso 'price'. Il volume dell'ultimo
    %           scambio
    %        - timestamp: valido solo nel caso 'price' (per ora). Il tempo
    %           dell'ultimo scambio
    %        - event: l'evento scatenante PriceEventManager o
    %           TradeEventManager
    %        - machine: questo oggetto, ossia la macchina che ha passato
    %           l'aggiornamento. Utile per recuperare lo stato
    %           (machine.state) o i dati di portafoglio
    %           (machine.qtyPortfolio)
    %       Inoltre questa funzione dovrà restituire 3 parametri
    %        - volume: volume di acquisto, se diverso da zero si procederà
    %           ad un acquisto o ad una vendita
    %        - price: opzionale, il prezzo di vendita
    %        - state: opzionale, il nuovo stato del sistema
    %    - i parametri opzionali invece sono una serie di parametri che
    %       vengono salvati nella machine.state e che sono specifici della
    %       strategia implementata nella funzione "algorithm". Da
    %       immaginarsi come un sistema di feedback.
    %
    %   Per fermare il trading chiamare il metodo "machine.stop"
    %
    %   TODO: a parte i vari TODO nel codice, da farsi la gestione del
    %   grafico
    
    properties (SetAccess = private)
        ticker % TOTHINK move to Get private
        
        algorithm % TODO move to Get private
        state % TODO move to Get private
        
        timeTradeStart
        priceAvgPortfolio
        qtyPortfolio
        qtyDirecta
        qtyNegotiation
        
        plotter
    end
    properties (SetAccess = private, GetAccess = private)
        dm
        client
        
		candle
		candleEventHandler
        
		pricesC
		volumesC
		datetimesC
        
		iterIndex
        
        priceNegotiation
        
        gain
    end
    
    methods
        function obj = MachineHist(ticker, algorithm, days, granularity, varargin)
            
            if(nargin < 2)
                error('Not enough input arguments.');
            end
            
            if(nargin == 2)
                days = 1;
                granularity = 1;
            elseif(nargin == 3)
                granularity = 1;
            end
            
            if(ischar(ticker))
                obj.dm = MTManager;
                obj.dm.killClientsOnClear = true;
                obj.client = obj.dm.getClient(['MachineHist_',datestr(now,'yymmddHHMMSS')]);
                obj.ticker = obj.client.getTicker(ticker);
            
            disp('ISIN');
            disp(obj.client.isReady);
            elseif(isa(ticker,'com.mattrader.matlab.Ticker') == 0)
                error('Expected a ticker');
            else
                obj.ticker = ticker;
            end
            
            obj.plotter = MachinePlot;
	
			obj.iterIndex = 1;
            obj.gain = 0;
            
            obj.algorithm = algorithm;
            obj.state = varargin;
			obj.candle = ticker.getCANDLE(days,granularity);
            obj.candleEventHandler = EventHandler(obj.candle.onReadyEventManager);
            obj.candleEventHandler.setCallback(@(h,e) obj.init(h,e));
			
            disp('constr end');
        end
        
        function stop(obj)
            obj.candleEventHandler.setNullCallback;
            % obj.ticker.revokeAll; % da fare meglio, così è parecchio brutale
            % TODO clear client
        end
    end
    
    methods (Access = private)
        function init(obj, ~, ~)
            disp('init');
            
            obj.pricesC = obj.candle.getOffPrices;
            obj.volumesC = obj.candle.getVolumes;
            obj.datetimesC = obj.candle.getDateTimeSeries;
            obj.datetimesC = obj.datetimesC(:,end-7:end);
			obj.timeTradeStart = obj.datetimesC(1,:);
            
            obj.priceAvgPortfolio = 0;
            obj.qtyPortfolio = 0;
            obj.qtyDirecta = 0;
            obj.qtyNegotiation = 0;
            
            obj.priceNegotiation = 0;
            
            % plot the first point for gain at 0
            obj.plotter.plotGain(obj.datetimesC(1,:),0);
            
            while(obj.iterIndex <= obj.candle.size)
				obj.priceDispatch();
				obj.iterIndex = obj.iterIndex + 1;
            end
            disp('End');
            disp(obj.gain + obj.qtyPortfolio * obj.pricesC(obj.iterIndex - 1));
            disp(obj.qtyPortfolio);
            % plot final gain
            obj.plotter.plotGain(obj.datetimesC(end,:),obj.gain + obj.qtyPortfolio * obj.pricesC(obj.iterIndex - 1));
        end
        
        function delete(obj)
            obj.stop();
            clear obj.plotter;
        end
        
		% data la natura generica di ALGORITHM, non posso passare l'oggetto ORDEREVENT o
		% l'oggetto PRICEEVENT, in quanto per lo storico non posso dare queste informazioni
        
        function priceDispatch(obj)
            obj.plotter.plotPrice(obj.datetimesC(obj.iterIndex,:),obj.pricesC(obj.iterIndex));
			obj.checkOrder(obj.pricesC(obj.iterIndex));
            [volume, price, state] = obj.algorithm('price', obj.pricesC(obj.iterIndex), obj.volumesC(obj.iterIndex), obj.datetimesC(obj.iterIndex,:), obj.state, obj);
            obj.makeAnAction(volume, price, state);
        end
        
        function makeAnAction(obj, volume, price, state)
            if(isempty(state) == 0)
                obj.state = state;
            end
			
            if(volume == 0)
				return
            end
            
			obj.priceNegotiation = 0;
			obj.qtyNegotiation = 0;
			
			if(price == 0 || sign(volume)*price >= sign(volume)*obj.pricesC(obj.iterIndex))
				obj.evaluateOrder(volume);
			else
				obj.queueOrder(price, volume);
			end
        end
        
		function evaluateOrder(obj, volume)
			tot = obj.qtyPortfolio * obj.priceAvgPortfolio + volume * obj.pricesC(obj.iterIndex);
			obj.qtyPortfolio = obj.qtyPortfolio + volume;
			
            if(obj.qtyPortfolio == 0)
				obj.priceAvgPortfolio = 0;
			else
				obj.priceAvgPortfolio = tot/obj.qtyPortfolio;
            end
            obj.plotter.plotOrder(obj.datetimesC(obj.iterIndex,:),obj.pricesC(obj.iterIndex));
            obj.gain = obj.gain - volume * obj.pricesC(obj.iterIndex);
            obj.plotter.plotGain(obj.datetimesC(obj.iterIndex,:),obj.gain + obj.qtyPortfolio * obj.pricesC(obj.iterIndex)); % usare in qualche maniera questa differenza per il calcolo effettivo del gain
		end
		
		function queueOrder(obj, price, volume)
			obj.priceNegotiation = price;
			obj.qtyNegotiation = volume;
		end
		
		function checkOrder(obj, price)
			if(obj.qtyNegotiation ~= 0 && sign(obj.qtyNegotiation)*obj.priceNegotiation >= sign(obj.qtyNegotiation)*price)
				obj.evaluateOrder(obj.qtyNegotiation);
				[volume, price, state] = obj.algorithm('order', price, obj.qtyNegotiation, obj.datetimesC(obj.iterIndex,:), obj.state, obj);
                obj.makeAnAction(volume, price, state);
			end
		end
        
    end
    
end

