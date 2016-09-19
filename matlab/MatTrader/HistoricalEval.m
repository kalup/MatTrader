function [gainGetter, plotter, c] = HistoricalEval( ticker, days, granularity, seek, follow, customPlot, varargin )
%HISTORICALEVAL This function is a wrapper to simplify management of
%Historical data events.

    if(ischar(ticker))
        ticker = client.getTicker('ticker');
    elseif(isa(ticker,'com.mattrader.matlab.Ticker') == 0)
        disp('Expected a ticker');
        return
    end
    
    gain = 0;
    
    buyTime = [];
    buyPrice = [];
    
    sellTime = [];
    sellPrice = [];
    
    gains = [];
    
    dayChanges = [];
    maxP = 0;
    minP = 0;
    
    gainGetter = @getGain;
    
    c = ticker.getCANDLE(days, granularity);
    
    onReady = EventHandler(c.onReadyEventManager);
    
    onReady.setCallback(@whenReady);
    
    plotter = @plotFunction;
    
    disp('In attesa dei dati storici');
    
    function whenReady(~, ~)
        % c = event.series; % we could have used this, but since we are in
        % the same scope we can refer to the external c directly due to
        % avoid memory usage

        disp('Dati storici ricevuti');
        tic;
        
        % analisi per i grafici
        dt = c.getDateTimeSeries;
        p = c.getOffPrices;
        maxP = max(p);
        minP = min(p);
        for i=2:length(dt)
            if(dt(i-1,(8)) ~= dt(i,8))
                dayChanges(end+1) = i;
            end
        end
        % analisi dati
        finalTime = c.size;
        startTime = 1;
        operating = 0;
        while(startTime < finalTime) % finché c'è ancora tempo
            if(operating == 0) % se non c'è nessuna operazione in corso fai un seek
                [ opType, crossTime, crossPrice ] = seek( c, startTime, dayChanges, varargin{:} );
                if(isempty(opType) == 0)
                    operating = 1;
                    buyTime(end+1) = crossTime;
                    buyPrice(end+1) = crossPrice;
                end
                startTime = crossTime + 1;
            end
            if(operating == 1)
                [ opGain, exitTime, exitPrice ] = follow( c, startTime, crossPrice, opType, dayChanges, varargin{:} );
                if(isempty(opGain) == 0)
                    gain = (1+gain)*(1+opGain) - 1;
                    sellTime(end+1) = exitTime;
                    sellPrice(end+1) = exitPrice;
                    gains(end+1) = opGain;
                else
                    disp('Un errore è avvenuto, candela nulla o rggiunta la fine della giornata in maniera non prevista')
                    sellTime(end+1) = exitTime;
                    sellPrice(end+1) = exitPrice;
                    gains(end+1) = NaN;
                end
                startTime = exitTime + 1;
                operating = 0;
            end
        end
        disp('Analisi completata, guadagno totale:');
        disp(gain);
        toc
        plotFunction();
    end

    function [mGain] = getGain()
        mGain = gain;
    end

    % not generic
    function plotFunction()
        if(c.isReady == 0)
            return
        end
        hold off
        plot(NaN,NaN);
        hold on
        for i=1:length(dayChanges)
            plot([dayChanges(i), dayChanges(i)],[minP,maxP],'--','Color',[.8 .8 .8])
        end
        for i=1:length(buyTime)
            x = min(buyTime(i),sellTime(i));
            y = min(buyPrice(i),sellPrice(i));
            xl = abs(buyTime(i)-sellTime(i));
            yl = abs(buyPrice(i)-sellPrice(i));
            if(yl == 0)
                yl = .00001;
            end
            r = rectangle('Position',[x,y,xl,yl]);
            if(gains(i) > 0)
                set(r,'FaceColor',[.7,1,.7]);
                set(r,'EdgeColor',[.7,1,.7]);
            else
                set(r,'FaceColor',[1,.8,.7]);
                set(r,'EdgeColor',[1,.8,.7]);
            end
        end
        customPlot( c, varargin{:} );
        str = sprintf('Gain: %.3f%%',gain()*100);
        title(str);
        hold off
    end

end

