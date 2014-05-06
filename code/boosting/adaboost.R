library(RWeka)
file <- "./../../data/weka-data/triads-lr-ud.arff"
data <- read.arff(file)
# DecisionStumps
# m <- AdaBoostM1(class ~ ., data = data,
#                  control = Weka_control(W = "DecisionStump"))

# J48
# m <- AdaBoostM1(class ~ ., data = data,
#                 control = Weka_control(W = list(J48,
#                                                 M=30)))

# RandomForest
RF = make_Weka_classifier("weka/classifiers/trees/RandomForest")
m <- AdaBoostM1(class ~ ., data = data,
                control = Weka_control(W = list(RF,
                                                I=100, # number of trees
                                                K=3))) # number of features
summary(m)
e <- evaluate_Weka_classifier(m,
                              class = TRUE,
                              complexity = TRUE,
                              seed = 1,
                              numFolds = 10)
summary(e)
e$details

# === Summary ===
#   
#   Correctly Classified Instances        1422              100      %
# Incorrectly Classified Instances         0                0      %
# Kappa statistic                          1     
# Mean absolute error                      0.0343
# Root mean squared error                  0.068 
# Relative absolute error                 17.4102 %
# Root relative squared error             21.6422 %
# Coverage of cases (0.95 level)         100      %
# Mean rel. region size (0.95 level)      31.333  %
# Total Number of Instances             1422     
# 
# === Confusion Matrix ===
#   
#   a   b   c   d   e   f   g   h   i   <-- classified as
# 167   0   0   0   0   0   0   0   0 |   a = 1
# 0 160   0   0   0   0   0   0   0 |   b = 2
# 0   0 166   0   0   0   0   0   0 |   c = 3
# 0   0   0 165   0   0   0   0   0 |   d = 4
# 0   0   0   0 161   0   0   0   0 |   e = 5
# 0   0   0   0   0 163   0   0   0 |   f = 6
# 0   0   0   0   0   0 169   0   0 |   g = 7
# 0   0   0   0   0   0   0 161   0 |   h = 8
# 0   0   0   0   0   0   0   0 110 |   i = 9
# 
# > summary(e)
# Length Class  Mode     
# string             1     -none- character
# details            8     -none- numeric  
# detailsComplexity  4     -none- numeric  
# detailsClass      54     -none- numeric  
# confusionMatrix   81     -none- numeric  
#
# > e$details
# pctCorrect             pctIncorrect          pctUnclassified 
# 87.69338959              12.30661041               0.00000000 
# kappa        meanAbsoluteError     rootMeanSquaredError 
# 0.86132944               0.09631505               0.18343206 
# relativeAbsoluteError rootRelativeSquaredError 
# 48.83106820              58.41061642 