function [ changed ] = dayChanged( dateTime1, dateTime2 )
%DAYCHANGED Summary of this function goes here
%   Detailed explanation goes here

    changed = dateTime1(8) ~= dateTime2(8);

end

