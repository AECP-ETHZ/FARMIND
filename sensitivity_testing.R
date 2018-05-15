

sensitivity_step = c(1)

for(i in sensitivity_step) {
  
  ## Generate Data Files based on which sensitivity step you want to test
  # generate_parameters()
  # generate_social_networks()
  # generate_farming_years()
  # etc
  
  ## Start Java program
  shell.exec("run_java_ABM.bat")
}
