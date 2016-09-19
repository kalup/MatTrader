function [ avg ] = avg( prices, period )
%AVG Summary of this function goes here
%   Detailed explanation goes here

    if(isempty(prices))
        return
    end
    
    avg = zeros(length(prices),1);
    
    a = 0;
    
    for i=1:period
        a = a + prices(i)/period;
        avg(i) = NaN;
    end
    avg(i) = a;
    for i=period+1:length(prices)
        a = a + prices(i)/period - prices(i-period)/period;
        avg(i) = a;
    end

end

