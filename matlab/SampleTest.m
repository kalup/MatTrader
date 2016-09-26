%% This file provide some simple commands to test the interface and get in touch
% with some of the functionality of the framework
% You need to build a class implementing IProvider that let you connect to your
% provider (for development we used an implementation that made references to
% Directa APIs, which cannot be shared for APIs policies).

manager = MTManager(provider);
client = manager.getClient('test');
fca = client.getTicker('fca');
fca.dailyMax % daily Max

tbt = fca.getTBT(1); %TickByTick
isReady = 0;
while isReady == 0
	pause(5);
	isReady = tbt.isReady
	% we expect that it return 1
end

tbt.getTickerCode
tbt.size

p = tbt.getPrices;
plot(p);

p100 = avgPrice(tbt.getPrices,100);
plot(p100);

eurusd = client.getTicker('lx.eurusd');

book = eurusd.book;
book.bid
book.ask

eurusd.buy(5)
eurusd.sell(5)

[gain, plotter] = HistoricalEval( fca, 20, 1, @seek, @follow, @customPlot);
