classdef MachinePlot < handle
    %MACHINEPLOT Summary of this class goes here
    %   Detailed explanation goes here
    
    properties (SetAccess = private, GetAccess = private)
        f
        plotH
        plotId
        ax
        firstTimestamp
    end
    
    methods
        function obj = MachinePlot()
            obj.f = figure;
            ax = zeros(2,1);
            ax(2) = subplot(3,1,3);
            ax(1) = subplot(3,1,[1,2]);
            obj.ax = ax;
            obj.plotH{1} = plot(ax(1),NaN,NaN,'Color',[.7,.7,1]);
            obj.plotH{2} = plot(ax(2),NaN,NaN,'Color',[1,.5,.5]);
            hold(ax(1),'on')
            obj.plotH{3} = plot(ax(1),NaN,NaN,'x','Color',[1,.3,.3]);
            hold(ax(1),'off')
            obj.plotId = {'price','gain','orders'};
            obj.firstTimestamp = 0;
            % sincronizzo gli assi così da ottenere lo stesso zoom
            % non funziona quando i plot evolvono dinamicamente
                 linkaxes(ax,'x');
                 set(ax(1),'XLimMode','auto');
                 set(ax(2),'XLimMode','auto');
        end
        
        function plot(obj, id, x, y, varargin)
            x = obj.getNumericTime(x);
            index = find(strcmp(id, obj.plotId));
            if(isempty(index))
                index = length(obj.plotId) + 1;
                obj.plotId{index} = id;
                color = rand(3,1);
                while(norm(color) < 1.2)
                    color = sqrt(color);
                end
                
                ax = obj.ax;
                hold(ax(1),'on')
                obj.plotH{index} = plot(ax(1),NaN,NaN,varargin{:},'Color',color);
                hold(ax(1),'off')
            end
            
            obj.updateGraph(obj.plotH{index}, x, y);
        end
        
        function plotPrice(obj, x, y)
            x = obj.getNumericTime(x);
            obj.updateGraph(obj.plotH{1}, x, y);
        end
        
        function plotGain(obj, x, y)
            x = obj.getNumericTime(x);
            obj.updateGraph(obj.plotH{2}, x, y);
        end
        
        function plotOrder(obj, x, y)
            x = obj.getNumericTime(x);
            obj.updateGraph(obj.plotH{3}, x, y);
        end
    end
    
    methods (Access = private, Static = true)

        function updateGraph( h_graph, x, y)
            if  isnan(get(h_graph,'XData'))
                set(h_graph, 'XData', x, 'YData', y);
            else
                set(h_graph, 'XData',[get(h_graph,'XData'), x], 'YData', [get(h_graph,'YData'), y]);
            end
        end
        
    end
    
    methods (Access = private)
        
        function [serialtime] = getNumericTime(obj, timestr)
            if(length(timestr) == 8)
                serialtime = datenum(timestr,'HH:MM:SS');
            elseif(length(timestr) == 16)
                serialtime = datenum(timestr,'yyyymmddHH:MM:SS');
            else
                serialtime = 0;
            end
            if(serialtime ~= 0 && obj.firstTimestamp == 0)
                obj.firstTimestamp = serialtime;
            end
        end
        
        function delete(obj)
            for i=length(obj.plotH):-1:1
                delete(obj.plotH{i})
            end
            delete(obj.f);
        end
    end
    
end

