prepare_inputs <- function(i, nFarm, nAct, actLimit, farm_names, act_names, 
                           latitude, longitude, year, memory, 
                           edu_mu, edu_sd, beta_mu, beta_sd, sbeta_mu, sbeta_sd, 
                           aspir_mu, aspir_sd, tolincome_mu, tolincome_sd, tolactivi_mu, tolactivi_sd, 
                           lambda, alpha_plus, alpha_minus, phi_plus, phi_minus, 
                           fyear_mu, fyear_sd, pref_mu, pref_sd, income_mu, income_sd)
{
  ###------------------ Generate farm level parameters ------------------###
  library(msm)
  farm_chars_list <- rep(0, 15)
  act_list <- rep(NA, actLimit)
  income_list <- rep(0, memory)
  pref_list <- round(rtnorm(nAct, pref_mu, pref_sd, lower=1, upper=5))
  fyears_list<- round(rtnorm(nAct, fyear_mu, fyear_sd, lower=0, upper=memory))
  for(k in 1:nFarm)
  {
    ### Generate farm level characteristics
    edu <- round(rtnorm(1, edu_mu, edu_sd, lower=5, upper=18))
    beta <- round(rtnorm(1, beta_mu, beta_sd, lower=0.1, upper=1))
    sbeta <- round(rtnorm(1, sbeta_mu, sbeta_sd, lower=0.1, upper=1))
    aspir <- round(rtnorm(1, aspir_mu, aspir_sd, lower=0.1, upper=1))
    tolincome <- round(rtnorm(1, tolincome_mu, tolincome_sd, lower=0.1, upper=1), 2)
    tolactivi <- round(rtnorm(1, tolactivi_mu, tolactivi_sd, lower=0.1, upper=1), 2)
    farm_chars <- c(latitude, longitude, year, edu, memory, beta, sbeta,
                    aspir, tolincome, tolactivi, 
                    lambda, alpha_plus, alpha_minus, phi_plus, phi_minus)
    farm_chars_list <- rbind(farm_chars_list, farm_chars)
    
    ### Generate initial activities
    activities <- rep(NA, actLimit)
    activities[1] <- as.character(paste('strat', sample(nAct, 1), sep=''))
    act_list <- rbind(act_list, activities)
    
    ### Generate initial incomes
    income <- round(rtnorm(memory, income_mu, income_sd, lower=200, upper=1000))*100
    income_list <- rbind(income_list, income)
    
    ### Generate activity preference
    preference <- round(rtnorm(nAct, pref_mu, pref_sd, lower=1, upper=5))
    pref_list <- rbind(pref_list, preference)
    
    ### Generate performing years
    perform_years <- round(rtnorm(nAct, fyear_mu, fyear_sd, lower=0, upper=memory))
    fyears_list <- rbind(fyears_list, perform_years)
    
  }
  
  ### Write out farme parameters
  farm_chars_list <- farm_chars_list[-1,]
  #farm_chars_list <- farm_chars_list[,]
  rownames(farm_chars_list) <- farm_names
  colnames(farm_chars_list) <- c('latitude','longitude','year','education','memory', 'beta', 'beta_s',
                                 'aspiration_coef', 'tolerance_income', 'tolerance_activity',
                                 'lambda',	'alpha_plus',	'alpha_minus', 'phi_plus',	'phi_minus')
  #write.csv(farm_chars_list, sprintf("farm_parameters_%d.csv", i))
  write.csv(farm_chars_list, sprintf("farm_parameters.csv"))
  
  ### Write out initial activities
  act_list <- act_list[-1,]
  rownames(act_list) <- farm_names
  colnames(act_list) <- c('strat1','strat2','strat3')
  #write.csv(act_list, sprintf("initial_activities_%d.csv", i))
  write.csv(act_list, sprintf("initial_activities.csv"))
  ### Write out initial incomes
  income_list <- income_list[-1,]
  rownames(income_list) <- farm_names
  colnames(income_list) <- c('income_t-1','income_t-2','income_t-3','income_t-4','income_t-5')
  #write.csv(income_list, sprintf("initial_incomes_%d.csv", i))
  write.csv(income_list, sprintf("initial_incomes.csv"))
  
  ### Write out activity perference
  pref_list <- pref_list[-1,]
  colnames(pref_list) <- act_names
  rownames(pref_list) <- farm_names
  #write.csv(pref_list, sprintf("activity_preference_%d.csv", i))
  write.csv(pref_list, sprintf("activity_preference.csv"))
  
  ### Write out performing years
  fyears_list <- fyears_list[-1,]
  colnames(fyears_list) <- act_names
  rownames(fyears_list) <- farm_names
  #write.csv(fyears_list, sprintf("performing_years_%d.csv", i))
  write.csv(fyears_list, sprintf("performing_years.csv"))
  
  ###----------------------- Generate social networks -----------------------###
  library(igraph)
  
  g <- erdos.renyi.game(nFarm, 0.5)
  adjmat <- as.matrix(as_adjacency_matrix(g))
  rownames(adjmat) <- farm_names
  colnames(adjmat) <- farm_names
  write.csv(adjmat, sprintf("social_networks_%d.csv", i))
  stre <- strength(g)
  betw <- betweenness(g)
  farm_net_char <- as.matrix(cbind(stre, betw))
  rownames(farm_net_char) <- farm_names
  colnames(farm_net_char) <- c('degree/strength', 'betweenness_centrality')
  write.csv(farm_net_char, sprintf("farm_network_characteristics_%d.csv", i))
}


