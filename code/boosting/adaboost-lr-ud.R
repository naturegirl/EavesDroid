library(RWeka)
library(rJava)
file_lr <- "./../../data/weka-data/all-lr-40.arff"
data_lr <- read.arff(file_lr)
file_ud <- "./../../data/weka-data/all-ud-40.arff"
data_ud <- read.arff(file_ud)

# RandomForest
RF = make_Weka_classifier("weka/classifiers/trees/RandomForest")
m_lr <- AdaBoostM1(class ~ ., data = data_lr,
                control = Weka_control(W = list(RF,
                                                I=100, # number of trees
                                                K=3))) # number of features
m_ud <- AdaBoostM1(class ~ ., data = data_ud,
                   control = Weka_control(W = list(RF,
                                                   I=100, # number of trees
                                                   K=3))) # number of features

# cache and save the 2 models
.jcache(m_lr$classifier)
save(m_lr, file="./adaboost.rf.lr.rda")

.jcache(m_ud$classifier)
save(m_ud, file="./adaboost.rf.ud.rda")

summary(m_lr)
summary(m_ud)

e_lr <- evaluate_Weka_classifier(m_lr,
                              class = TRUE,
                              complexity = TRUE,
                              seed = 1,
                              numFolds = 10)
summary(e_lr)
e_lr$details

e_ud <- evaluate_Weka_classifier(m_ud,
                                 class = TRUE,
                                 complexity = TRUE,
                                 seed = 1,
                                 numFolds = 10)
summary(e_ud)
e_ud$details
