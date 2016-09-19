function [ sigma, accuracy ] = volatility( ticker, period )
%VOLATILITY Summary of this function goes here
%   Detailed explanation goes here

    tbt = ticker.getTBT(period);
    while(tbt.isReady == false)
        pause(0.3);
    end
    prices = tbt.getPrices();
    % evaluate std
    sigma = std(prices);
    % evaluate mean
    avg = mean(prices);
    % normalize data
    prices = (prices - avg)/sigma;
    % calc frequencies
    [freq,x] = hist(prices,11);
    % normalize freq
    freq = freq/sum(freq);
    % evaluate normal distribution over x
    gaussf = zeros(1,11);
    d = (max(x) - min(x))/11;
    x_i = min(x);
    for i=1:11
        gaussf(i) = normcdf(x_i+d)-normcdf(x_i);
        x_i = x_i + d;
    end
    sum(gaussf)
    % calculate Bhattacharyya distance
    accuracy = -log(sum(sqrt(gaussf.*freq)))/6.58887e-02;

end

