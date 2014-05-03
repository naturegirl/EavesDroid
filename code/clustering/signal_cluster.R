library(mclust)
library(e1071)
library(rpart)
library(scatterplot3d)

#path <- "~/Documents/School/Interacting_With_Data/cos424project/signal-plotting/data/nm.features.csv"
path <- "~/Documents/School/Interacting_With_Data/cos424project/data/features/data-a-z_combined.csv"
#path <- "~/Documents/School/Interacting_With_Data/cos424project/data/features/lr-all-combined.csv"
#path <- "~/Documents/School/Interacting_With_Data/cos424project/data/LR_output/lr-labelled-training-data.csv"

signals <- read.csv(path, header=TRUE, sep=",", stringsAsFactors = FALSE)
signals <- data.matrix(signals)
cols <- ncol(signals)
range01 <- function(x){(x-min(x))/(max(x)-min(x))}
signals <- cbind(range01(signals[,-cols]), label=signals[,cols])


#unlabeled signals
features <- signals[,1:cols-1]
label <- signals[,cols]
result<-Mclust(features, G=26)
#print(summary(result))
#plot(result, what="classification")
cluster_mapping <- cbind(label, result$classification)
print(cluster_mapping)
#print(result$classification)
row <- nrow(cluster_mapping)
clusterLetter <- array(0, dim=c(26,26))
col <- ncol(clusterLetter)
for(r in 1:row){
  clusterLetter[cluster_mapping[r,1], cluster_mapping[r,2]] = 
    clusterLetter[cluster_mapping[r,1], cluster_mapping[r,2]] + 1

}
print(clusterLetter)

clusterLabeling <- array(0, dim=c(1, 26))
for(c in 1:col){
  clusterLabeling[1,c] = which.max(clusterLetter[, c])
}

print(clusterLabeling)

##split data into train and test set
index <- 1:nrow(signals)
testindex <- sample(index, trunc(length(index)/3))
testset <- signals[testindex,]
trainset <- signals[-testindex,]

#svm
svm.model <- svm( as.factor(label) ~ . , data = trainset, cost = 100, gamma = 1)
svm.pred <- predict(svm.model, testset[,-cols])

## compute svm confusion matrix
table(pred = svm.pred, true = testset[,cols])



