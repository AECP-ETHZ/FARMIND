ifelse(Sys.getenv('USERNAME')=='xiongh', root<-'D:', root<-'~')
setwd(paste(root,'/polybox/Agricultural_ABM/data/test',sep=''))
rm(list = ls())

###------------------ Generate parameter combinations ------------------###
lbl <- paste("grid_", format(Sys.time(), "%Y%m%d_%H%M"), sep = "")
parameters <- expand.grid(pars <- list(
  memory = c(5),
  edu_mu = c(8, 10, 12),            # mean of education levels
  #edu_sd = edu_mu * sd_share,
  beta_mu = c(0.5, 1, 1.5),         # mean of coefficients of learning in aggregate
  #beta_sd = beta_mu * sd_share, 
  sbeta_mu = c(0.5, 1),             # mean of coefficients of social learning
  #sbeta_sd = sbeta_mu * sd_share,
  
  aspir_mu = c(0.5, 0.7, 0.9),     # mean of aspiration levels
  #aspir_sd = aspir_mu * sd_share,
  tolincome_mu = c(0.5, 0.7, 0.9), # mean of tolerance of dissimilarity in income change
  #tolincome_sd = tolincome_mu * sd_share,
  tolactivi_mu = c(0.5, 0.7, 0.9), # mean of tolerance of dissimilarity in activity
  #tolactivi_sd = tolactivi_mu * sd_share,
  
  lambda      = c(0.7, 0.8, 0.9),
  alpha_plus  = c(0.6, 0.7, 0.8),
  #alpha_minus = c(0.6, 0.7, 0.8),
  phi_plus    = c(0.7, 0.8, 0.9),
  #phi_minus   = c(0.7, 0.8, 0.9),
  
  income_mu = c(400,500,600),
  #income_sd <- income_mu * sd_share,
  fyear_mu = c(3),
  #fyear_sd <- fyear_mu * sd_share,
  pref_mu = c(0.4,0.6,0.8)
  #pref_sd <- pref_mu * sd_share0
))
parameters$name = sprintf("%s_%05d", lbl, seq(1:nrow(parameters)))
#write.csv(parameters, sprintf("parameters_%s.csv", lbl))

###------------------ Produce input files ------------------###
nFarm <- 50
region_names <- read.csv('region_names-Herbicide.csv', header = FALSE)
farm_names <- region_names[1:nFarm,]
nAct <- 72
act_names <- paste('strat', formatC(1:nAct, width=nchar(nAct), flag='0'), sep='')
actLimit <- 3
sd_share = 0.2
source('prepareInputs-func.R')
for(i in 1:20)#nrow(parameters))
{
  prepare_inputs(
    i,
    nFarm,
    nAct,
    actLimit,
    farm_names,
    act_names,
    latitude <- 0,
    longitude <- 0,
    year <- sample(1940:1985, 1),
    
    memory   <- parameters$memory[i],
    edu_mu   <- parameters$edu_mu[i],
    edu_sd   <- edu_mu * sd_share,
    beta_mu  <- parameters$beta_mu[i],
    beta_sd  <- beta_mu * sd_share,
    sbeta_mu <- parameters$sbeta_mu[i],
    sbeta_sd <- sbeta_mu * sd_share,
    
    aspir_mu <- parameters$aspir_mu[i],
    aspir_sd <- aspir_mu * sd_share,
    tolincome_mu <- parameters$tolincome_mu[i],
    tolincome_sd <- tolincome_mu * sd_share,
    tolactivi_mu <- parameters$tolactivi_mu[i],
    tolactivi_sd <- tolactivi_mu * sd_share,
    
    lambda <- parameters$lambda[i],
    alpha_plus  <- parameters$alpha_plus[i],
    alpha_minus <- alpha_plus,
    phi_plus  <- parameters$phi_plus[i],
    phi_minus <- phi_plus,
    
    fyear_mu <- parameters$fyear_mu[i],
    fyear_sd <- fyear_mu * sd_share,
    pref_mu <- parameters$pref_mu[i],
    pref_sd <- pref_mu * sd_share,
    income_mu <- parameters$income_mu[i],
    income_sd <- income_mu * sd_share
  )
}
