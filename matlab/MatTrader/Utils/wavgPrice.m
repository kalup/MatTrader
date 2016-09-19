function [ avg ] = wavg( prices, volumes, period )
%AVG Summary of this function goes here
%   Detailed explanation goes here

    if(isempty(prices) || isempty(volumes))
        return
    end
    
    avg = zeros(length(prices),1);
    
    a = 0;
    cumul = 0;
    
    for i=1:period
        a = a + prices(i)*volumes(i);
        cumul = cumul+volumes(i);
        avg(i) = NaN;
    end
    avg(i) = a/cumul;
    for i=period+1:length(prices)
        a = a + prices(i)*volumes(i) - prices(i-period)*volumes(i-period);
        cumul = cumul + volumes(i) - volumes(i-period);
        avg(i) = a/cumul;
    end

end

