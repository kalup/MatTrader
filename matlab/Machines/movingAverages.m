function [ r_volume, r_price, r_state ] = movingAverages( typeEvent, price, volume, timestamp, state, obj )
%movingAverages Summary of this function goes here
%   Detailed explanation goes here

    % init return values
    r_volume = 0;
    r_price = 0;
    r_state = [];

    % init with default values
    defaultArgs = {40, 80, 100, 10, [], 0, [], false, 0, 0};
    defaultArgs(1:length(state)) = state;

    [period1, period2, budget, checkCrossTime, prices, i, deltaMeans, cross, timesSinceCross, signOfCross] = defaultArgs{:};
    
    % fix periods
    period1 = abs(period1);
    period2 = abs(period2);
    
    if(period1 > period2)
        temp = period1;
        period1 = period2;
        period2 = temp;
    end
    
    % init prices and delta means
    if(isempty(prices))
        prices = zeros(period2,1);
        deltaMeans = zeros(2,1);
    end
    
    % if we receive a price then act
    if(strcmpi(typeEvent,'price') == true)
        i = i + 1;
        
        if(i < period1) % we are building first average
            prices(i) = price;
        elseif(i < period2) % we are building second average, but first is ready
            prices(i) = price;
            mean1 = mean(prices(i - period1 + 1:i));
            obj.plotter.plot('mean1',timestamp,mean1);
        else % means are ready
            if(i > period2)
                prices = circshift(prices, -1); % discard oldest data
            end
            prices(period2) = price;
            mean1 = mean(prices(period2 - period1 + 1:end));
            mean2 = mean(prices);
            deltaMean = mean1 - mean2;
            if(deltaMean*deltaMeans(1)/abs(mean1*mean2) < -eps^2 && deltaMeans(2)*deltaMeans(1)/abs(mean1*mean2) >= -eps^2) % check if we have a cross (check tolerance)
                cross = ~cross;
                timesSinceCross = 0;
                signOfCross = sign(deltaMean);
            end
            deltaMeans = circshift(deltaMeans, -1);
            deltaMeans(2) = deltaMean;
            obj.plotter.plot('mean1',timestamp,mean1);
            obj.plotter.plot('mean2',timestamp,mean2);
        end
        
        if(cross) % increment counter
            timesSinceCross = timesSinceCross + 1;
        end
        
        if(timesSinceCross == checkCrossTime)
            timesSinceCross = 0;
            cross = 0;
            r_volume = signOfCross*floor(budget/price); % calculte volume based on budget
            r_price = price;
        end
        
        % update output state
        r_state = {period1, period2, budget, checkCrossTime, prices, i, deltaMeans, cross, timesSinceCross, signOfCross};
    end

end

