function csvTable( filename, table )
%CSVTABLE Output a table into a .csv file
%   Write out a .csv file from a table of historical data obtained from a
%   call to HistoricalTable.table()

    f = fopen(filename,'w');
    
    len = length(table);
    
    if len == 0
        fclose(f);
        return
    end

    sout = '';
    
    for i=1:len
        sout = strcat( ...
            sout, ...
            '\n', ...
            strjoin({
                char(table(i,1)), ...
                char(table(i,2)), ...
                char(table(i,3)), ...
                char(table(i,4)), ...
                char(table(i,5)), ...
                char(table(i,6))
            }, ','));
    end
    
    fprintf(f,sout);
    
    fclose(f);

end

