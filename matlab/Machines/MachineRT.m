classdef MachineRT < handle
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
    end
    properties (SetAccess = private, GetAccess = private)
        dm
        client
        priceEventHandler
        stockEventHandler
        stock
    end
    
    methods
        function obj = MachineRT(ticker, algorithm, varargin)
            
            if(nargin < 2)
                error('Not enough input arguments.');
            end
            
            if(ischar(ticker))
                obj.dm = MTManager;
                obj.dm.killClientsOnClear = true;
                obj.client = obj.dm.getClient(['MachineRT_',datestr(now,'yymmddHHMMSS')]);
                obj.ticker = obj.client.getTicker(ticker);
            
      disp('ISIN');
            
            elseif(isa(ticker,'com.mattrader.matlab.Ticker') == 0)
                error('Expected a ticker');
            else
                obj.ticker = ticker;
            end
            
            obj.algorithm = algorithm;
            obj.state = varargin;
            
            obj.stock = obj.ticker.getStock();
            
            obj.priceEventHandler = EventHandler(obj.ticker.onPriceEventManager);
            
            obj.stockEventHandler = EventHandler(obj.ticker.onStockUpdateEventManager);
            obj.stockEventHandler.setCallback(@(h,e) obj.init());
            
            obj.stock.update;
            disp('constr end');
        end
        
        function stop(obj)
            obj.priceEventHandler.setNullCallback;
            obj.stockEventHandler.setNullCallback;
            obj.ticker.revokeAll; % da fare meglio, così è parecchio brutale
            % TODO clear client
        end
    end
    
    methods (Access = private)
        function init(obj)
            disp('init');
            obj.timeTradeStart = obj.stock.timestamp;
            obj.priceAvgPortfolio = obj.stock.price;
            obj.qtyPortfolio = obj.stock.qtyPortfolio;
            obj.qtyDirecta = obj.stock.qtyDirecta;
            obj.qtyNegotiation = obj.stock.qtyNegotiation;
            
            obj.stockEventHandler.setCallback(@(h,e) obj.onStockEvent(h,e));
            
            obj.priceEventHandler.setCallback(@(h,e) obj.onPriceEvent(h,e)); %onPriceEvent(obj,h,e)
        end
        
        function delete(obj)
            obj.stop();
            clear obj.plotter
        end
        
        function onPriceEvent(obj, ~, priceEvent)
            [volume, price, state] = obj.algorithm('price', priceEvent.price, priceEvent.volume, priceEvent.timestamp, obj.state, obj);
            obj.makeAnAction(volume, price, state);
        end
        
        function makeAnAction(obj, volume, price, state) % varargin)

            %defaultArgs = {0, 0, []};
            %defaultArgs(1:nargin - 1) = varargin;
            
            %[volume, price, state] = defaultArgs{:};
            
            if(isempty(state) == 0)
                obj.state = state;
            end
            % TODO se l'intenzione di ordine si incrocia con un ordine
            % pendente, modificare l'ordine pendente, per adesso appena
            % viene immesso un ordine si ANNULLANO tutti gli ordini
            % presenti!!!
            if(volume ~= 0) % TODO REMOVE
                obj.ticker.revokeAll;
            end
            if(volume > 0)
          disp('volume');
          disp(volume);
                if(price > 0)
                    obj.registerOrder(obj.ticker.buy(price, volume));
                else
                    obj.registerOrder(obj.ticker.buy(volume));
                end
            elseif(volume < 0)
          disp('volume');
          disp(volume);
                if(price > 0)
                    obj.registerOrder(obj.ticker.sell(price, -volume));
                else
                    obj.registerOrder(obj.ticker.sell(-volume));
                end
            end
        end
        
        function registerOrder(obj, order)
            orderEventManager = EventHandler(order.onTradeEventManager);
            orderEventManager.setCallback(@(h,e) obj.dispatchOrder(h,e,orderEventManager));
        end
        
        function dispatchOrder(obj, ~, orderEvent, orderEventManager)
            order = orderEvent.order;
            obj.stock.update;
            if(order.getStatus == order.ERROR) % TODO gestire meglio l'errore notificandolo
                orderEventManager.setNullCallback;
            elseif(order.getStatus == order.CONFIRMATION_NEEDED)
                obj.ticker.confirm(order); % TODO gestire le conferme tramite un parametro
            elseif(order.getStatus == order.EXECUTED)
                orderEventManager.setNullCallback;
                [volume, price, state] = obj.algorithm('order', order.getPrice, order.getQuantity, order.getTime, obj.state, obj); % TODO MANCA IL TIMESTAMP!!!! Manca nell'oggetto evento!!!!
                obj.makeAnAction(volume, price, state);
            elseif(order.getStatus == order.CANCELLED)
                orderEventManager.setNullCallback;
            end
        end
        
        function onStockEvent(obj, ~, ~)
            
            obj.priceAvgPortfolio = obj.stock.price;
            obj.qtyPortfolio = obj.stock.qtyPortfolio;
            obj.qtyDirecta = obj.stock.qtyDirecta;
            obj.qtyNegotiation = obj.stock.qtyNegotiation;
            
        end
    end
    
end

