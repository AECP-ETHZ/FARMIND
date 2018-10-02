:: This command copies a gams results file so we can fake the resuls to allow program testing. 
copy ".\data\Grossmargin_P4,00.csv" .\projdir

copy ".\data\data_FARMIND.gms" .\projdir\DataModelIn
copy ".\data\data_FARMINDLandData.gms" .\projdir\DataModelIn

:: cd projdir
:: gams Fit_StratABM_Cal