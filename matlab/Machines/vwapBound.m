function [ r_volume, r_price, r_state ] = vwapBound( typeEvent, price, volume, timestamp, state, obj )
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here

    % init return values
    r_volume = 0;
    r_price = 0;
    r_state = [];

    % init with default values
    defaultArgs = {100, 40, 0, 0, 0, 0, false, false, 0, 0, 0, 0};
    defaultArgs(1:length(state)) = state;

    [budget, checkCrossTime, N, D, S, i, cross1, cross2, tCross1, tCross2, cooldown1, cooldown2] = defaultArgs{:};
    
    % set valid variables
    budget = abs(budget);
    checkCrossTime = abs(checkCrossTime);
    
    % local variables
    nearVWAP = false;
    fallback1 = false;
    fallback2 = false;
    
    % if we receive a price then act
    if(strcmpi(typeEvent,'price') == true)
        % build vwap
        i = i + 1;
        N = N + volume*price;
        D = D + volume;
        vwap = N/D;
        S = S + (price - vwap)^2;
        sigma = sqrt(S/i);
        
        % check resistances
        % we wait a little warm up time
        if(i > checkCrossTime * 2)

            % if price has broken first resistance
            if(price > vwap+sigma || price < vwap-sigma)
                if(~cross1) % we just crossed
                    cross1 = true;
                    tCross1 = 0;
                end
                tCross1 = tCross1 + 1;
                % if price has broken second resistance
                if(price > vwap+2*sigma || price < vwap-2*sigma)
                    if(~cross2) % we just crossed
                        cross2 = true;
                        tCross2 = 0;
                    end
                    tCross2 = tCross2 + 1;
                    % eventually reset the cooldown
                    cooldown2 = 0;
                else
                    if(cross2) % we cross back
                        cooldown2 = cooldown2 + 1; % cooling off
                        tCross2 = tCross2 - 1;
                        if(cooldown2 >= floor(checkCrossTime / 4)) % enough time under the second resistance
                            cross2 = false;
                            if(tCross2 >= 0)
                                fallback2 = true;
                            end
                            tCross2 = 0;
                            cooldown2 = 0;
                        end
                    end
                end
                % eventually reset the cooldown
                cooldown1 = 0;
            else % if we are within first resistences
                if(cross2) % we do a double jump down
                    cross2 = false;
                    tCross2 = 0;
                    % fallback2 = true;
                    cooldown2 = 0;
                end
                if(cross1) % if we fall back
                    cooldown1 = cooldown1 + 1; % cooling off
                    tCross1 = tCross1 - 1;
                    if(cooldown1 >= floor(checkCrossTime / 2)) % enough time under the second resistance
                        cross1 = false;
                        if(tCross1 >= 0)
                            fallback1 = true;
                        end
                        tCross1 = 0;
                        cooldown1 = 0;
                    end
                end
                % if we are near vwap clear also cross1 and then sell
                if(price > vwap-sigma/5 && price < vwap+sigma/5)
                    cross1 = false;
                    tCross1 = 0;
                    nearVWAP = true;
                    cooldown1 = 0;
                end
            end

            % check if buy
            % if we break the 2nd barrier and enough time has passed
            if(cross2 && tCross2 == floor(checkCrossTime / 2) && (price > vwap+2*sigma || price < vwap-2*sigma))
                r_volume = sign(price - vwap) * floor(budget / price) - obj.qtyPortfolio;
            elseif(fallback2)
                % just get ready for a strong inversion
                r_volume = - sign(price - vwap) * floor(budget / price) - obj.qtyPortfolio;
            elseif(~(cross2 && tCross2 >= floor(checkCrossTime / 2)) && cross1 && tCross1 == checkCrossTime && (price > vwap+sigma || price < vwap-sigma))
                r_volume = sign(price - vwap) * floor(budget / price / 2) - obj.qtyPortfolio;
            elseif(fallback1)
                % just get ready for an inversion
                r_volume = - sign(price - vwap) * floor(budget / price / 2) - obj.qtyPortfolio;
            end
            if(nearVWAP)
                % sell all
                r_volume = - obj.qtyPortfolio;
            end
            if(r_volume ~= 0)
                r_price = price * (1 + sign(r_volume)*0.1); % offer a bit more to avoid dead orders
            end
        end
        
        % update output state
        r_state = {budget, checkCrossTime, N, D, S, i, cross1, cross2, tCross1, tCross2, cooldown1, cooldown2};
        
        obj.plotter.plot('vwap',timestamp,vwap);
        obj.plotter.plot('vwap+',timestamp,vwap+sigma);
        obj.plotter.plot('vwap++',timestamp,vwap+2*sigma);
        obj.plotter.plot('vwap-',timestamp,vwap-sigma);
        obj.plotter.plot('vwap--',timestamp,vwap-2*+sigma);
    end
    
end

