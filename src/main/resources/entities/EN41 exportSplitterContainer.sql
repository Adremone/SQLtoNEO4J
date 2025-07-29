SELECT 
    'SplitterContainer' as Kind,
    name as localName,
    name as globalName,
    name as displayName,
    description
FROM Splitter_container FETCH FIRST 1000 ROWS ONLY;
