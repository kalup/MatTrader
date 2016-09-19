# MatTrader
An  automated  trading  and  data  analysis  framework  for Matlab  and  Java

## License
BSD-new: https://github.com/kalup/MatTrader/blob/master/LICENSE

## Insall

Download or clone this repo into a dedicated folder (ie. C:\Users\username\MatTrader or for *nix system /user/username/MatTrader/). Check the existance of two folders: "java" and "matlab".

Within Matlab add this folder to the path:

  File -> Set Path -> Add Folder...

then select MatTrader folder

   (ie. C:\Users\username\MatTrader)

click 'Save' and then 'Close'.

### Matlab >= R2012b

Write the following into Matlab prompt

  > cd(prefdir)

Create or edit a file 'javaclasspath.txt' and add as last row the path containing java files

   (ie. C:\Users\username\MatTrader\java\bin)

Save and close this file. Now restart Matlab.

### Matlab < R2012b

Write the following into Matlab prompt

  > cd([matlabroot,'/toolbox/local'])

edit file 'classpath.txt' and add as last row the path containing java files

(ie. C:\Users\username\MatTrader\java\bin)

Save and close this file. Now restart Matlab.

## Sample Test

(This sample is available as an .m file under "matlab" folder)

```matlab
%% This file provide some simple commands to test the interface and get in touch
%% with some of the functionality of the framework

manager = MTManager;
client = manager.getClient('test');
fca = client.getTicker('fca');
fca.dailyMax % daily Max

tbt = fca.getTBT(1); %TickByTick
isReady = 0
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

eurusd = client.getTicker('lx.eurusd')

book = eurusd.book;
book.bid
book.ask

eurusd.buy(5)
eurusd.sell(5)

[gain, plotter] = HistoricalEval( fca, 20, 1, @seek, @follow, @customPlot);
```