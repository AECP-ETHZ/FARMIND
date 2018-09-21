********************************************************************************
$ontext

   Modelling the effects of a glyphosate ban on weed management in maize production
   --- Online Appendix 1 ---


   GAMS project: Herbicide
   GAMS file :  FIT_STRAT.GMS

   @purpose :  Calculate gross margin for reference scenarios that an be implemented in an ABM
   @author :  Böcker, T.
   @date :   28. March 2017
   @since :
   @refDoc :
   @seeAlso :
   @calledBy :



$offtext
********************************************************************************
*
*-----------------------------------------------------------------------------------
*
* (0) General program control
*
* "EstimateBetas ON" means that parameters of the production function are estimated.
* Turn EstimateGM and GlyposateBan OFF in this case. After estimating the parameters,
* you can turn EstimateBetas OFF, and first turn "EstiamteGM" ON. Now, a benchmark scenario
* under current, profit maximising situations is simulated. Afterwards, you can additionally
* turn GlyphosateBan ON, so that a counterfactual scenario without glyphosate is simulated.
*-----------------------------------------------------------------------------------
*
$offlisting

*Scenario analysis options:
$setglobal GlyphosateBan Off
*Sensitivity analysis:
*attainable yield: average, if OFF: attainable yield increases over time (maximum reached yield over time)
$setglobal YieldPlus10pc Off


*
*-----------------------------------------------------------------------------------
*
* (1) Set definitions
*
* In this part, sets and parameters for the parameter assessment are defined.
*-----------------------------------------------------------------------------------
*
set gn "municipalities in model" /
Ahaus, Beckum, Bruehl, Hoevelhof, Hopsten, Lippetal, Nottuln, Ostbevern,
Greven, Hamm, Hamminkeln,
Mechernich, Moenchengladbach, Salzkotten, Selm, Lotte, Holzwickede,
Juechen, Luedinghausen, Hilden, Borgholzhausen, Kirchlengern,
Marienmuenster, Sonsbeck, Straelen, Wuppertal, Suedlohn, Leverkusen,
Schwerte, Engelskirchen, Werl, Wesseling, Tecklenburg, Mettmann,
Bocholt, Schleiden, Ratingen, Meerbusch, Hellenthal, UebachPalenberg,
Bestwig, Lengerich, Radevormwald,
Siegen, Soest, Issum, Waltrop, CastropRauxel, Nettetal, BadSalzuflen, Rahden,
Steinfurt, Lemgo, Hoerstel, Nieheim, Steinheim,
Ruethen, BadWuennenberg, Vettweiss, Luenen, Bochum, Delbrueck,
Gangelt, Kerken, Horstmar, Barntrup, Ascheberg, Wenden, Drolshagen,
Geseke, Kevelaer, Hille, Heinsberg, Goch, Neuenrade, Loehne, Windeck,
Mettingen, Muenster, Alfter, Rheurdt, Rheine, Augustdorf, Stemwede,
Troisdorf, LangenfeldRhld, Extertal, Metelen, Wipperfuerth,
Schoeppingen, Uedem, FroendenbergRuhr, Coesfeld, Kalkar, Sassenberg,
Viersen, Buende, Oelde, SchiederSchwalenberg, Spenge, Wesel, WickedeRuhr,
Steinhagen, Bielefeld, Olpe, Werdohl, Kamen, Emsdetten, Brakel,
Roedinghausen, Xanten, Haan, HalleWestf, Gladbeck, Heimbach,
Beelen, Medebach, NachrodtWiblingwerde, HornBadMeinberg,
Warstein, Luegde, Luedenscheid, Schwalmtal,
Gummersbach, Ahlen, Senden, Werne, Iserlohn, Titz, Enger, Herford,
Recke, Finnentrop, Waldfeucht, RhedaWiedenbrueck, Brueggen, Erkelenz,
Weeze, Wilnsdorf, Datteln, Borken, Bergkamen, Oberhausen, Wettringen,
Attendorn, Herdecke, Bergheim, Much, Eitorf, Neuss, MendenSauerland, Wuelfrath,
Lippstadt, Lichtenau, Hallenberg, Gevelsberg,
Havixbeck, Petershagen, WertherWestf, Kleve, Verl,
HerzebrockClarholz, Ibbenbueren, Erkrath, Lennestadt,
OerErkenschwick, Huenxe, Lienen, Noervenich, Erndtebrueck,
Marsberg, Everswinkel, Euskirchen, BadLippspringe, Huertgenwald,
Plettenberg, Kalletal, GronauWestf, Herten, Doerentrup, Welver,
Nideggen, Geilenkirchen, Raesfeld, Versmold, Pulheim, Laer, Breckerfeld,
Westerkappeln, Velbert, SundernSauerland, Linnich, Detmold,
Rosendahl, Espelkamp, BedburgHau, Paderborn, Beverungen, SanktAugustin,
Niederzier, Elsdorf, Isselburg, EmmerichamRhein,
Wachtberg, BadMuenstereifel, Heiden, Wiehl,
Netphen, Huellhorst, Bedburg, Huerth, Hilchenbach, Inden, Schmallenberg,
Hueckelhoven, Ennigerloh, Zuelpich, Olfen, Ense, Nordwalde, Warendorf,
Sendenhorst, Witten, Willebadessen, Grevenbroich, Freudenberg,
Stadtlohn, Bueren, Duelmen, Rheinberg, Morsbach, BadSassendorf,
Geldern, Lage, Burbach, Guetersloh, Schwelm, Frechen, Unna,
PreussischOldendorf, Anroechte, BadOeynhausen, Saerbeck, Blankenheim,
Korschenbroich, Kreuzau, Bergneustadt, Kirchhundem, Halver, Arnsberg,
VoerdeNiederrhein, Langenberg, Alpen, Warburg, Niederkruechten,
Langerwehe, Recklinghausen, SchlossHolteStukenbrock, Kreuztal,
Ennepetal, Erwitte, Selfkant, BadBerleburg, Schermbeck, Luebbecke,
Sprockhoevel, Meinerzhagen, Hattingen, Olsberg, Kall, Blomberg,
BadLaasphe, NeunkirchenSeelscheid, Schalksmuehle, Dortmund,
Neunkirchen, Boenen, Rietberg, KampLintfort, Kranenburg, Willich,
Drensteinfurt, Minden, Moehnesee, Hemer, BadHonnef, Reichshof,
Juelich, Borchen, Hiddenhausen, Nuembrecht, MonheimamRhein,
NeukirchenVluyn, Rheinbach, HennefSieg, Swisttal, Lohmar,
Weilerswist, Borgentreich, PortaWestfalica, Merzenich, Brilon,
Kaarst, Marl, Dorsten, Oerlinghausen, Grefrath, Nettersheim, Rhede,
Altena, WetterRuhr, BadDriburg, Ruppichteroth, Siegburg, Hagen,
Herscheid, Altenbeken, Meckenheim, Reken, Leopoldshoehe, Wegberg,
Dueren, Ladbergen, Neuenkirchen, Lindlar, Kempen, Legden,
Rommerskirchen, Hueckeswagen, Wadersloh, Bornheim, Waldbroel, Telgte,
Dormagen, Nordkirchen, Niederkassel, Balve, Billerbeck, Altenberge,
Heiligenhaus, Ochtrup, Velen, Wassenberg, Harsewinkel, Erftstadt, Rees,
Heek, Hoexter, Vlotho, Aldenhoven, EsloheSauerland, Dahlem,
Gescher, Wachtendonk, Herne, HalternamSee, Toenisvorst, Moers,
Schlangen, Meschede, Dinslaken, Kerpen, Koenigswinter, Kierspe,
Marienheide, Winterberg, Aachen, Alsdorf, Baesweiler, Eschweiler,
Herzogenrath, Monschau, Roetgen, Simmerath, StolbergRhld, Vreden, Wuerselen
 /;
*$endif.smallmodel
 alias(gn,gn1);set gnCur(gn);
 Parameter gn_inAnalysis;
*number of geographical units that shall be calculated: max card(gn) = 377
gn_inAnalysis =377


 set plants "weeds" /
  "Alopecurus myo", "Digitaria isch",
  "Echinochloa cr", "Elymus repens", "Poa annua L.", "Setaria viridi",
  "Amaranthus ret", "Atriplex patul", "Brassica napus", "Capsella bursa",
  "Chenopodium al", "Cirsium arvens", "Convolvulus ar", "Equisetum arve",
  "Fallopia convo", "Fumaria offici", "Galium aparine", "Galinsoga parv",
  "Geranium pusil", "Lamium album L.", "Matricaria rec", "Mercurialis an",
  "Persicaria lap", "Persicaria mac", "Polygonum avic", "Rumex obtusifo",
  "Solanum nigrum", "Sonchus arvens", "Stellaria medi", "Thlaspi arvens",
  "Veronica arven", "Viola arvensis"
  /;

 set as "active substance" /
  NoHerb, AlsoMechanic, PureMechanic, Nothing,
  Herb, Terbuthylazin, Nicosulfuron, S-Metolachlor, Glyphosat, Mesotrione,
  Dimethenamid-P, Topramezone, Bromoxynil, Pethoxamid, Tembotrione,
  Dicamba, Prosulfuron, Tritosulfuron, Flufenacet, Rimsulfuron,
  Iodosulfuron, Foramsulfuron, Isodecylalkoholethoxylat, Pendimethalin,
  Sulcotrion, Thiencarbazone, Metosulam, Pyridate
  /;

 set s_HRAC "HRAC-categories of herbicide strategies" /
  HRAC_A,  HRAC_B,  HRAC_C1, HRAC_C2, HRAC_C3, HRAC_D, HRAC_E,  HRAC_F1,
  HRAC_F2, HRAC_F3, HRAC_G,  HRAC_H,  HRAC_I,  HRAC_J, HRAC_K1, HRAC_K2,
  HRAC_K3, HRAC_L,  HRAC_M,  HRAC_N,  HRAC_O,  HRAC_P, HRAC_Q,  HRAC_R,
  HRAC_S,  HRAC_T,  HRAC_U,  HRAC_V,  HRAC_W,  HRAC_X, HRAC_Y,  HRAC_Z
  /;

 Set s_soilVarieties "Soils and land cover"/
  Abbauflaechen, Gewaesser, Moore, Siedlung, ls, lu, sl, ss, tl, tu, ut
  /;

 Set
  s_soilTypes /Light, Medium, Heavy, Bog, Mined, Water, Built/
  s_soilArea /ShapeArea/;

 Sets
  betas / 0*3 /
  s_spre               "Pre-sowing weed control strategies" / spre1*spre6/
  s_spost              "Post-sowing weed control strategies" / spost1*spost55/
  s                    "Weed control strategies" / set.s_spre,set.s_spost /
  spre(s)              "pre-sowing weed control strategies strategies" / set.s_spre /
  spost(s)             "Post-sowing weed control strategies"  / set.s_spost /
  spread               "Factors for weed spread:  degree of presence and abundance" /s_abu/
  s_asban              "defining which strategies are not allowed anymore due to AS ban" /normal, GlyBan/
  s_dieselUseSoilDep   "Diesel Use per pre-sowing strategy. Increase/decrease for diesel in relation to soil type" /dieselUseSoilDep/
  s_dieselUseSpan      "multiplier for higher and lower diesel use/ha" /dieselLight,dieselMedium,dieselHeavy,directSowDisc/
  s_fert               "fertiliser types in model" /N,P,K,Ca/
  s_fertamount         "different fertiliser application amounts" /0,100,200,300,400,500,600,700,800/
  s_HerbRelCost        "Sow costs dependent on herb strategy"
                           /Sowcost,Sowwork,ApplCost,MechWeedCost,HerbC,Machcosts,MachCostsMech,Workhours,WorkhoursMech,WorkhoursHerb,MachCostsHerb,MachCostsFix,MachCostsVar,
                            MachCostsMechFix,MachCostsMechVar,MachCostsHerbFix,MachCostsHerbVar/
  s_MJkg               "MJ/kg necessary" /MJkgAS/
  s_NPK(s_fert)        "fertilisers to apply every year"    /N,P,K/
  s_process            "three different crop production processes being differentiated in T-distribution" /Nothing,Harrowing,Intense/
  s_stratChar          "Characteristics of different herbicide strategies"  /gASPerStrat/
  s_weatherStation     "data from 6 weather stations in NRW to estimate the time of weed emergence from 1999 to 2015"
                          /WeatherAachen,WeatherBadLippspringe,WeatherBrilon,WeatherDuesseldorf,WeatherGreven,WeatherKoelnBonn/
  s_workchar           "Characteristics of different works" /workhour,machinerycost/
  s_works              "different types of field works"  /workrating,workmanure,workliming/
  tt                   "time period, used for yield distribution" /t1999*t2011/
 ;


 Parameters
  p_v (gn,spre)                          "Pre-sow strategy's effectiveness against all weeds together"
  p_z(gn,spost)                          "Post-sow strategy's effectiveness against all weeds together"
  p_affYieldShare                        "Share of yield affected by post damage control"  /0.05/
  p_CRate                                "Rate at which I decreases as T increases" /0.017/
  p_drymatter                            "share of dry matter in silage maize: for p_yieldMgm(gn,tYR)" /0.348/
  p_timeofEmergence                      "Time of weed emergence in relation to crop emergence (T) -> DO NOT CHANGE" / 0  /
  p_TDistribution(gn,tt,s_process)       "Time of weed emergence in relation to crop emergence (T) based on German Weather Service data"
  p_totWeedPressure(gn)                  "total weed pressure per municipality"
  p_weedsInGn(gn,plants)                 "weed presence in different municipalities"
  p_yieldLossD0                          "Average yield loss when weed density approaches 0 (I)" /0.3/
  p_yieldLossDinfin                      "Average yield loss when weed density approaches infinity (A)" /63.8/
  p_yieldMgm(gn,tt)                      "Water-limited potential yield at municipality level, for gross margin calculation"
 ;


*
*-----------------------------------------------------------------------------------
*
* (2) Data input
* Here, data is read into the model from the provided CSV and GDX files. Some data
* was also typed directly into the model. (will be stored in GDX container after estimation for later re-load)
*-----------------------------------------------------------------------------------
*
 Parameter p_probWeed(plants,gn,*)     "Porbability to observe weed in each municipality"
  $$gdxin probWeed.gdx
  $$load p_probWeed=p_output
  $$gdxin

 table p_weedSpread(plants,spread)     "Average presence of weeds, degree of presence and av abundance"
  $$ondelim
  $$include p_weedSpread.csv
  $$offdelim

 table p_weedControl(s,plants)         "Effeciency of control strategy against weed"
  $$ondelim
  $$include p_weedControl.csv
  $$offdelim

 table p_asToS(s,as)                   "Is active substance in spray strategy, 0 or 1"
  $$ondelim
  $$include p_asToS.csv
  $$offdelim

 table p_HRAC(s,s_HRAC)                "HRAC categories that the different strategies consist of"
  $$ondelim
  $$include p_HRAC.csv
  $$offdelim

 table p_HerbRelCosts(s,s_HerbRelCost) "Costs that are related to a specific strategy, e.g. application costs"
  $$ondelim
  $$include p_HerbRelCosts.csv
  $$offdelim

 table p_HerbicideCost(spre,spost)     "Herbicide costs of the different pre- and post-sowing strategies"
  $$ondelim
  $$include p_HerbicideCost.csv
  $$offdelim

 table p_processes(spre,s_process)
  $$ondelim
  $$include p_processes.csv
  $$offdelim

 table p_weatherStation(gn,s_weatherStation)  "assigns municipalities gn to a certain weather station"
  $$ondelim
  $$include p_weatherstation.csv
  $$offdelim

 Parameter p_soil(gn,s_soilVarieties,s_soilarea) "soil type in different municipalites"   ;
  $$gdxin p_soil.gdx
  $$load p_soil
  $$gdxin

 table p_AllowedStratPrePost(gn,spost,spre)
  $$ondelim
  $$include p_AllowedStratPrePost.csv
  $$offdelim

 table p_YieldMdm(gn,tt)
  $$ondelim
  $$include p_YieldMdm.csv
  $$offdelim


*
*  --- load results from previous estimation step
*
   $$ifi not exist "ResultBetaEstimate.gdx" $$abort "ResultBetaEstimate.gdx is missing, copy file in folder!"



*
* ------------------------------------------------------------------------------------------------
*
*  (5) Simulate gross margin maximising weed management strategies
*      (remember to turn in the upper part of the model "EstimateGM" ON and "Estimatebetas" OFF)
*      For an analysis including glyphosate:
*                       1) Turn "GlyphosateBan" OFF in line 36
*                       2) define the crop price in line 624,
*                       3) define weed pressure with T in lines 657 and 658
*                       4) name output files correctly in lines 1037 and 1039
*                       5) run the model
*      For ana analysis without glyphosate:
*                       1) Turn "GlyphosateBan" ON in line 36
*                       2) define the crop price in line 624,
*                       3) define weed pressure with T in lines 657 and 658
*                       4) name output files correctly in lines 1037 and 1039
*                       5) refer to correct input files from benchmark sceanrio with glyphosate in line 947
*                       6) run the model
* -------------------------------------------------------------------------------------------------

*

* ---Redefine Variables and Parameters that are reloaded into the programme from GDX container
   Variable
    v_beta(betas)
    v_Dpost(gn,spost)
   ;

   Parameters
    p_affYieldShare
    p_HerbRelCosts(s,s_HerbRelCost)
    p_HerbicideCost(spre,spost)
    p_HRAC(s,s_HRAC)
    p_asToS(s,as)
    p_v(gn,spre)
    p_CRate
    p_processes(spre,s_process)
    p_soilType(gn,s_soilTypes,s_soilarea)
    p_TDistribution(gn,tt,s_process)
    p_weatherStation(gn,s_weatherStation)
    p_yieldLossD0
    p_yieldLossDinfin
  ;

* ---Reload Variables and Parameters from steps 1- 4 into programme
  execute_loadpoint "ResultBetaEstimate.gdx"
     v_beta.l,v_Dpost.l,p_HerbRelCosts,p_HerbicideCost,p_HRAC,p_asToS,p_v,
     p_yieldLossD0,p_CRate,p_yieldLossD0,p_yieldLossDinfin,
     p_soilType,p_affYieldShare,p_processes,
     p_TDistribution,p_weatherStation
  ;


* ---Define Parameters for part (5):
  Parameter
     p_attyield(gn)                              "attainable yield for GM calculations: water-limited potential yield + 5%"
     p_costsVar(gn,spre,spost)                   "total variable costs dependend on weed control strategy"
     p_costsFix(gn,spre,spost)                   "total fixed costs dependend on weed control strategy"
     p_costsInterest(gn,spre,spost)              "Interest claim"
p_cropPrice    "Price of silage maize Euro/st" /3.95/
     p_dieselprice                               "price of diesel - only for calculation of soil type-related costs of cultivation (source: KTBL 2016/17, p. 67)" /0.70/
     p_fertNneed(gn,spre,spost)                  "necessary N-fertiliser"
     p_fertPneed(gn,spre,spost)                  "necessary P-fertiliser"
     p_fertKneed(gn,spre,spost)                  "necessary K-fertiliser"
     p_fertneedtot(gn,spre,spost)                "total necessary fertiliser"
     p_fertapplcost(gn,spre,spost)               "fertiliser application cost depended on yield and amount of fertiliser"
     p_GLYUSER(gn)                               "glyphosate user yes/no, No Risk"

     p_gm1(gn,spre,spost)                        "Aux variable for ABM correction of Post1 strategies"
     p_gm(gn,spre,spost)                         "possibility matrix of gross margin for municipality gn for strategy combinations spre and spost"

     p_interest                                     "interest rate" /0.01/
     p_lime                                         "amount of lime per ha" /3000/
     p_manure                                       "amount of manure on field in m?" /25/
     p_manurePrice                                  "price of manure, assumption" /0/
     p_MinProfitShare                               "share of minimum wage" /1/
     p_NPKcont (s_NPK)                              "average NPK content in biogas slurry in kg/m?"
                           /N  5.1, P  2.3, K  5.5 /
     p_NPKprice (s_fert)                            "price for fertiliser"
                           /N  1.10,P  0.87,K  0.77,Ca 0.05 /
     p_NPKshare (s_NPK)                             "NPK in maize silage in kg/dt"
                           /N  0.43,P  0.18,K  0.51 /
     p_nutrNyield(gn,spre,spost)                 "N in achieved yield"
     p_nutrPyield(gn,spre,spost)                 "P in achieved yield"
     p_nutrKyield(gn,spre,spost)                 "K in achieved yield, No Risk"
     p_postSowDamage(gn,spost,s_process)         "post-sow damage with estimated parameters"
     p_seedcost                                     "price for seed (KTBL, 2016, p. 322)"/233.20/
     p_TotFert(s_fertAmount)                        "interim term to calculate fertiliser application costs"
     p_wage (s_workchar)                            "wage in Euro/ha"
                        /workhour     17.5
                         machinerycost 1/
     p_weightshare(s_NPK)                           "NPK-weight-shares in the three different fertilisers"
                         /N  0.27
                          P  0.18
                          K  0.40 /
     p_yieldFunc(gn,spre,spost)                  "yield function for GM-estimation -> p_timeofemergence can be changed"
     p_revenue(gn,spre,spost)                    "yield * output price"
     p_cost1(gn,spre,spost)                      "Additional cost for post1 strategies to make ABM reasonable"
   ;


   table p_AllowedStrat(spre,s_ASban)
                     normal             GlyBan
     spre1*spre4       1                   1
     spre5             1                   0
     spre6             1                   0

   table p_fertappl(s_fertamount, s_workchar)
                       workhour            machinerycost
*                      h/ha                Euro/ha (fix+var)
       0               0.00                0.00
     100               0.12                2.06
     200               0.13                2.65
     300               0.16                3.22
     400               0.19                4.79
     500               0.22                4.38
     600               0.24                5.96
     700               0.26                5.55
     800               0.29                6.14


   table p_workparam (s_works, s_workchar)
                       workhour            machinerycost
*                      h/ha                Euro/ha (fix+var)
     workrating        0.04                 0.21
     workmanure        0.89                55.13
     workliming        0.19                12.47
   ;
* --- workmanure: 20m? poly-tanker with 18m drag-hoses, 25m?/ha: KTBL 2016/17, p. 175
* --- worksowing: 8 rows, 6 m width: KTBL 2016/17, p. 185

   table p_DieselUse(spre,s_dieselUseSoilDep)
                  dieselUseSoilDep
     spre1           0
     spre2           7.50
     spre3          15.00
     spre4          33.05
     spre5           0
     spre6           7.50
   ;


   table p_dieselUseSpan(spre,s_dieselUseSpan)
* --- Assumption 20% higher/lower use for one run of chisel ploughing and 30% higher/lower use for moulboard ploughing.
*     0.277 is the weighted factor in relation to the diesel use (chisel plough + mouldboard plough)
*     directSowDisc is for calculating a discount for direct sowing on light soils. E.g. on the light soils in north NRW,
*     strip-till practices are very popular due to the lower costs and better practicability on those soils
                dieselLight    dieselMedium      dieselHeavy       directSowDisc
     spre1           0                0                0                -0.2
     spre2          -0.2              0                0.2              -0.2
     spre3          -0.2              0                0.2               0
     spre4          -0.277            0                0.277             0
     spre5           0                0                0                -0.2
     spre6          -0.2              0                0.2              -0.2
   ;

*
* ---Calculation of attainable yield:
*
p_attyield(gn) =  p_yieldMdm(gn,'t2007')/p_drymatter * 10 * (1 + p_affYieldShare);


*
* ---Use betas and create production function (possibly with changed p_timeofEmergence2)
*
 p_postSowDamage(gn,spost,s_process) =   {1 -  p_yieldLossD0 * v_Dpost.l(gn,spost)
/ [100 * ( exp(p_CRate * p_TDistribution(gn,'t2007',s_process) )
                                                     + (p_yieldLossD0 * v_Dpost.l(gn,spost) ) / p_yieldLossDinfin ) ] };


  p_yieldFunc(gn,spre,spost)  = [1 - EXP( -  sqr ( v_beta.l('0') + v_beta.l('1') * p_v (gn,spre) ) ) ]
                                    *  p_attYield(gn)
                                    *  sum[ s_process, p_postSowDamage(gn,spost,s_process) * p_processes(spre,s_process) ] ;



*
* --- Calculate fertliiser and ohter costs for each strategy based on expected yields
*
  p_nutrNyield(gn,spre,spost) = p_yieldFunc(gn,spre,spost) * p_NPKshare('N');
  p_nutrPyield(gn,spre,spost) = p_yieldFunc(gn,spre,spost) * p_NPKshare('P');
  p_nutrKyield(gn,spre,spost) = p_yieldFunc(gn,spre,spost) * p_NPKshare('K');

  p_fertNneed(gn,spre,spost)  = p_nutrNyield(gn,spre,spost) - p_manure * p_NPKcont ('N');
  p_fertPneed(gn,spre,spost)  = p_nutrPyield(gn,spre,spost) - p_manure * p_NPKcont ('P');
  p_fertKneed(gn,spre,spost)  = p_nutrKyield(gn,spre,spost) - p_manure * p_NPKcont ('K');

  p_fertneedtot(gn,spre,spost) = p_fertNneed(gn,spre,spost)/p_weightshare('N')
                                  + p_fertPneed(gn,spre,spost)/p_weightshare('P')
                                  + p_fertKneed(gn,spre,spost)/p_weightshare('K');

*
* --- Interim term for total fertiliser demand
*
  p_TotFert(s_fertAmount) = (s_FertAmount.pos-1) * 100;
  p_TotFert(s_fertAmount) $ (s_FertAmount.pos eq card(s_FertAmount)) = inf;

*
* --- Calculate fertiliser application costs (no fertiliser costs included)
*
;
  p_fertapplcost(gn,spre,spost)
        = sum(  s_fertAmount $ (      (p_fertneedtot(gn,spre,spost) ge p_TotFert(s_fertAmount-1))
                                  and (p_fertneedtot(gn,spre,spost) <  p_TotFert(s_fertAmount))),
                p_fertappl(s_fertAmount-1,'workhour') * p_wage('workhour') + p_fertappl(s_fertAmount-1,'machinerycost')
             );
  ;



*
* --- Calculate variable costs for each weed control strategy
*
  p_costsVar(gn,spre,spost)
       =  {p_HerbRelCosts (spre, 'sowcost')
             + Sum [ s_soilarea, (p_dieselUseSpan(spre,'directSowDisc') * p_HerbRelCosts(spre,'sowCost')) $ (  p_soilType(gn,'Light',s_soilarea) >= p_soilType(gn,'Medium',s_soilarea)
                                                                                                           and p_soilType(gn,'Light',s_soilarea) >= p_soilType(gn,'Heavy',s_soilarea) )]
             + p_HerbRelCosts (spre, 'sowWork') * p_wage ('workhour') }
* ---Light soils get a discount dor sirect sowing. E.g. on the light soils in north NRW, strip-till practices are very popular due the lower costs and better practicability on those soils
      +  [(p_workparam('workrating','machinerycost') + p_workparam('workrating','workhour')*p_wage ('workhour'))]/5
* ---Rating is only done every 5th year
      +  p_manure * p_manurePrice
      +  (p_workparam('workmanure','machinerycost') + p_workparam('workmanure','workhour') * p_wage ('workhour'))
      +  p_seedcost
      +  p_fertapplcost (gn,spre,spost)
      +  p_fertNneed (gn,spre,spost) * p_NPKprice ('N')
      +  p_fertPneed (gn,spre,spost) * p_NPKprice ('P')
      +  p_fertKneed (gn,spre,spost) * p_NPKprice ('K')
      +  [p_lime  * p_NPKprice ('Ca')]/3
      +  [(p_workparam('workliming','machinerycost') + p_workparam('workliming','workhour')*p_wage ('workhour'))]/3
* ---Liming is done every 3rd year
* ---Machinery costs for weed control:
      +  Sum {s_soilarea, ( [p_HerbRelCosts (spre,'MachCostsMechVar') ] $ (  p_soilType(gn,'Medium',s_soilarea) >= p_soilType(gn,'Light',s_soilarea)  and p_soilType(gn,'Medium',s_soilarea) >= p_soilType(gn,'Heavy',s_soilarea))
      +  [p_HerbRelCosts (spre,'MachCostsMechVar') + p_DieselUse(spre,'dieselUseSoilDep') * p_dieselUseSpan(spre,'dieselLight') * p_dieselprice ] $ (  p_soilType(gn,'Light',s_soilarea)  >= p_soilType(gn,'Medium',s_soilarea) and p_soilType(gn,'Light',s_soilarea)  >= p_soilType(gn,'Heavy',s_soilarea))
      +  [p_HerbRelCosts (spre,'MachCostsMechVar') + p_DieselUse(spre,'dieselUseSoilDep') * p_dieselUseSpan(spre,'dieselHeavy') * p_dieselprice ] $ (  p_soilType(gn,'Heavy',s_soilarea)  >= p_soilType(gn,'Medium',s_soilarea) and p_soilType(gn,'Heavy',s_soilarea)  >= p_soilType(gn,'Light',s_soilarea))
             ) }
      +  p_wage ('workhour') * p_HerbRelCosts (spre,'workhoursMech')

      +  p_HerbicideCost(spre,spost)
      +  p_HerbRelCosts(spre,'MachCostsHerbVar')  + p_wage ('workhour') * p_HerbRelCosts(spre,'workhoursHerb')
      +  p_HerbRelCosts(spost,'MachCostsHerbVar') + p_wage ('workhour') * p_HerbRelCosts(spost,'workhours')
* ---Harvest costs dependent on yield
*       ---Fix cost (incl. depreciation,interest,insurance)  + Var. cost (incl. operating materials,utilities,reparis) + Cost for wage
*       ---Costs for harvest:
      +  [(86.89 + 0.0009 * p_yieldFunc(gn,spre,spost))   + (31.838 + 0.01948 * p_yieldFunc(gn,spre,spost))      + ((0.4325 + 0.00003 * p_yieldFunc(gn,spre,spost)) * p_wage('workhour'))]
*       ---Costs for transport:
      +  [(4.8075 + 0.07933 * p_yieldFunc(gn,spre,spost)) + (8.36 + 0.0594 * p_yieldFunc(gn,spre,spost))         + ((0.7425 + 0.00308 * p_yieldFunc(gn,spre,spost)) * p_wage('workhour'))]
*       ---Costs for compaction:
      +  [(0.005 + 0.02885 * p_yieldFunc(gn,spre,spost))  + (0.005 + 0.01985 * p_yieldFunc(gn,spre,spost))       + ((0.2225 + 0.00218 * p_yieldFunc(gn,spre,spost)) * p_wage('workhour'))]
  ;

*
* --- Matrix of gross margins for each strategy
*
  p_revenue(gn,spre,spost) = p_yieldFunc(gn,spre,spost) * p_cropPrice;
  p_costsInterest(gn,spre,spost) = p_interest * 6/12 * [p_costsVar(gn,spre,spost) + p_HerbRelCosts (spre,'machcostsFix') + p_HerbRelCosts (spost,'machcostsFix')];
  p_gm1(gn,spre,spost) = p_revenue(gn,spre,spost) - p_costsVar(gn,spre,spost)
                                                 - p_HerbRelCosts (spre,'machcostsFix')
                                                 - p_HerbRelCosts (spost,'machcostsFix')
                                                 - p_costsInterest(gn,spre,spost);


  p_cost1(gn,spre,'spost1') = 500;
  p_gm(gn,spre,spost) = p_gm1(gn,spre,spost) - p_cost1(gn,spre,spost);



* ---------------------------------------------------------------------------
*
*   Define and apply simulation model
*
*
* --------------------------------------------------------------------------

  Equations
     e_gm(gn)              "gross margin calculation for municipality gn for strategy cominations spre and spost"
     e_gmobj                  "total gross margin in model"
     e_IPre(gn)            "sum of pre-sowing strategies has to be unity for each gn"
     e_IPost(gn)           "sum of post-sowing strategies has to be unity for each gn"
     e_NicoRestr(gn)       "restriction for nicosulfuron-containing plant protection products"
     e_HRAC(gn,s_HRAC)     "restriction that HRAC categories have to be changed"

  ;

  Variables
     v_gm(gn)              "gross margin for municipality gn for strategy cominations spre and spost"
     v_gmobj                  "total gross margin for all gn"
     v_shareSpray(s,gn)    "variable to chose a strategy"
  ;

*
* --- Bounds and starting values for v_shareSpray
*
  v_shareSpray.lo(s,gn) = 0;
  v_shareSpray.up(s,gn) = 1;
$ontext
  v_shareSpray.l('spre1',gn,'t1999') = 1;
  v_shareSpray.l('spost1',gn,'t1999') = 1;
  v_shareSpray.l('spre1',gn,'t2000') = 1;
  v_shareSpray.l('spost1',gn,'t2000') = 1;
  v_shareSpray.l('spre1',gn,'t2001') = 1;
  v_shareSpray.l('spost1',gn,'t2001') = 1;
  v_shareSpray.l('spre1',gn,'t2002') = 1;
  v_shareSpray.l('spost1',gn,'t2002') = 1;
  v_shareSpray.l('spre1',gn,'t2003') = 1;
  v_shareSpray.l('spost1',gn,'t2003') = 1;
  v_shareSpray.l('spre1',gn,'t2004') = 1;
  v_shareSpray.l('spost1',gn,'t2004') = 1;
  v_shareSpray.l('spre1',gn,'t2005') = 1;
  v_shareSpray.l('spost1',gn,'t2005') = 1;
  v_shareSpray.l('spre1',gn,'t2006') = 1;
  v_shareSpray.l('spost1',gn,'t2006') = 1;
  v_shareSpray.l('spre1',gn,'t2007') = 1;
  v_shareSpray.l('spost1',gn,'t2007') = 1;
  v_shareSpray.l('spre1',gn,'t2008') = 1;
  v_shareSpray.l('spost1',gn,'t2008') = 1;

  v_shareSpray.l('spre1',gn) = 1;
  v_shareSpray.l('spost1',gn) = 1;

  v_shareSpray.l('spre1',gn,'t2010') = 1;
  v_shareSpray.l('spost1',gn,'t2010') = 1;
  v_shareSpray.l('spre1',gn,'t2011') = 1;
  v_shareSpray.l('spost1',gn,'t2011') = 1;
$offtext


*
* --- Gross margin calculation, restrictions and objective function:
*
  e_gm(gnCur(gn)) ..
    v_gm(gn) =E= Sum [(spre,spost), p_gm(gn,spre,spost)
                                                              * v_shareSpray(spre,gn)
                                                              * v_shareSpray(spost,gn)
                                                              * p_AllowedStratPrePost(gn,spost,spre)

$iftheni.GlyphosateBan %GlyphosateBan%==ON
                                                              * p_AllowedStrat(spre,"GlyBan")
$endif.GlyphosateBan
                      ];
*
* --- Restriction that shares of pre resp. post strategies add up to unity
*
  e_IPre(gnCur(gn)) ..
    sum(spre, v_shareSpray(spre,gn) )  =E= 1;

  e_IPost(gnCur(gn)) ..
    sum(spost, v_shareSpray(spost,gn)) =E= 1;



*
* --- Objective variable: Max Gross margin summed over all municipalities
*
  e_gmobj ..
    v_gmobj =E= Sum[ (gnCur(gn)), v_gm(gn) ]
  ;

  Option NLP=conopt;
  Option limcol=0;
  Option limrow=0;

  Model M_GM/
      e_gm,
      e_IPre,
      e_IPost,
      e_gmobj
  /;

  option NLP=conopt;
  option limcol = 0;
  option limrow = 0;

  option solveLink=5;
  M_GM.reslim    = 200000;
  M_GM.iterlim   = 15000;
  M_GM.solprint  = 2;
*
* --- solve the model
*
*
* --- solve the model
*
  loop(gn1 $ (ord(gn1) le gn_inAnalysis),
      option kill=gnCur;gnCur(gn1)=yes;
      Solve M_GM using NLP maximizing v_gmobj;
      );

*
* --- load / calculate results for benchmark (i.e. glyphospate allowed)
*

   p_GLYUSER(gn) = 1$(v_sharespray.l('spre5',gn) > 0 or v_sharespray.l('spre6',gn) > 0) ;


* ----------------------------------------------------------------------------------------
*
*  (6) Post model processing
*
*  Model output, which shall be displayed is defined and calculated.
* ----------------------------------------------------------------------------------------
  Parameters
     p_appSharePre(as)                  "Application share of AS as calculated by model for pre-sowing strategies"
     p_appSharePost(as)                 "Application share of AS as calculated by model for post-sowing strategies"
     p_appSharePreGN(gn,as)                "Application share of AS as calculated by model for pre-sowing strategies"
     p_appSharePostGN(gn,as)               "Application share of AS as calculated by model for post-sowing strategies"
     p_appSharePostGLY(as)              "only of glyphosate using m"
     p_GMAverage(gn)                       "Average gross margin over the period tt"
     p_cost1
   ;

* --- Share of active substances and cultivations strategies for pre-sowing strategies

  p_appSharePre(as) = sum( (spre,gn), v_shareSpray.l(spre,gn) * p_asToS(spre,as)) / card(gn);

* --- Share of active substances and cultivations strategies for post-sowing strategies

  p_appSharePost(as) = sum( (spost,gn), v_shareSpray.l(spost,gn) * p_asToS(spost,as)) / card(gn) ;



* --- Share of active substances and cultivations strategies for pre-sowing strategies

  p_appSharePreGN(gn,as) = sum( (spre), v_shareSpray.l(spre,gn) * p_asToS(spre,as)) / card(tt);

* --- Share of active substances and cultivations strategies for post-sowing strategies

  p_appSharePostGN(gn,as) = sum( (spost), v_shareSpray.l(spost,gn) * p_asToS(spost,as)) / card(tt) ;


*
* --- unload post-model procssing results into GDX container and show in listing
*
  Display
          v_sharespray.l,
          p_appSharePre,
          p_appSharePost,
          p_appSharePreGN,
          p_appSharePostGN,
          v_gm.l,
          p_gm,
          p_cost1

 ;

*
*  --- load results from previous estimation step
*
   $$ifi not exist "resultBetaEstimate.gdx" $$abort "resultBetaEstimate.gdx is missing, copy file in folder"


*
* --- unload post-model procssing results into GDX container and CSV file and show in listing
*
Parameter p_1(gn);




Parameter p_GMAverage(gn) "Average gross margin for each geographical unit";
 loop(gn1 $ (ord(gn1) le gn_inAnalysis),
      option kill=gnCur;gnCur(gn1)=yes;
      p_GMAverage(gnCur(gn)) = v_gm.l(gn);
      );
Display p_GMAverage;

  FILE fh /Grossmargin_P4,00.csv/;
  fh.ap=0;
*  fh.pw=10000;
*  fh.nw=20;
*  fh.nd=5;
  PUT fh @1#1 "Municipality,"
         "GMAverage,"
         "Pre-Sow-Herbicide,"
         "Post-Sow-Herbicide"  /;
  loop(gn1 $ (ord(gn1) le gn_inAnalysis),
      option kill=gnCur;gnCur(gn1)=yes;
     Loop ((gnCur(gn),spre,spost)$(v_sharespray.l(spre,gn) and v_sharespray.l(spost,gn)),
         Put gn.tl","
             p_GMAverage(gn)","
             spre.tl ","
             spost.tl
         /;
           )
      );
  PUTCLOSE fh;

*** end ***

