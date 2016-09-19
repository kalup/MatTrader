function [ r_volume, r_price, r_state ] = randomSample( typeEvent, price, ~, timestamp, state, obj ) %% AGGIUNGI prima dell'obj eventualmente l'evento
%RANDOMSAMPLE Summary of this function goes here
%   Detailed explanation goes here

    r_volume = 0;
    r_price = 0;


    % init with default values
    defaultArgs = {120, []};
    defaultArgs(1:length(state)) = state;

    [delay, lastTrade] = defaultArgs{:};
    
    if(strcmpi(typeEvent,'price') == true)
        if(ischar(timestamp) == 1)
            if(timeEslapsedInSeconds(lastTrade, timestamp) > poissrnd(delay))
                r_volume = mod(sum(timestamp),3)-1;
				r_price = 0; % price;
                if(r_volume ~= 0)
                    lastTrade = timestamp;
                end
            end
        end
    end
    
    r_state = {delay, lastTrade};
    
    function [serialtime] = getNumericTime(timestr)
        if(length(timestr) == 8)
            serialtime = datenum(timestr,'HH:MM:SS');
        elseif(length(timestr) == 16)
            serialtime = datenum(timestr,'yyyymmddHH:MM:SS');
        else
            serialtime = 0;
        end
    end

    function [seconds] = timeEslapsedInSeconds(timestr1, timestr2)
        seconds = abs(getNumericTime(timestr2) - getNumericTime(timestr1))*86400;
    end

end

