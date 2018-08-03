:: This command calls a java jar file
:: optional commands are 
:: -uncertainty :: if set to 0, then ABM will not use the dissimilarity calculations during simulations
:: -individual_learning :: if set to 0, then the ABM will not use individual learning for activities during simulation
:: -social_learning :: if set to 0, then the ABM will not use social learning for activities during simulation
:: -activity_preference :: if set to 0, then the ABM will not use activity preference during simulation

java -jar ABM.jar -year 5 -modelName WEEDCONTROL -uncertainty 1
pause