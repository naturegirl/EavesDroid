library(mclust)
library(e1071)
library(rpart)
library(scatterplot3d)

#path <- "~/Documents/School/Interacting_With_Data/cos424project/signal-plotting/data/nm.features.csv"
path <- "~/Documents/School/Interacting_With_Data/cos424project/data/test.csv"

signals <- read.csv(path, header=TRUE, sep=",")
#unlabeled signals
features <- signals[,1:7]
labels <- signals[,8]
#print(signals)

result<-Mclust(features, G=8)
print(summary(result))
print(result)
#plot(result, what="classification")
#par(new=TRUE)
#mtext("",side=2, line=0)
cluster_mapping <- cbind(labels, result$classification)
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

clusterLabeling <- array(0, dim=c(1,26))
for(c in 1:col){
  clusterLabeling[1,c] = which.max(clusterLetter[,c])
}

print(clusterLabeling)

data(Glass, package="mlbench")
index <- 1:nrow(Glass)
testindex <- sample(index, trunc(length(index)/3))
testset <- Glass[testindex,]
trainset <- Glass[-testindex,]

svm.model <- svm( Type ~ ., data = trainset, cost = 100, gamma = 1)
svm.pred <- predict(svm.model, testset[,-10])
table(pred = svm.pred, true = testset[,10])
##split data into train and test set
index <- 1:nrow(signals)
testindex <- sample(index, trunc(length(index)/3))
testset <- signals[testindex,]
trainset <- signals[-testindex,]

#svm
svm.model <- svm( label ~ ., data = trainset, cost = 100, gamma = 1)
svm.pred <- predict(svm.model, testset[,-8])

## rpart
#rpart.model <- rpart(label ~ ., data = trainset)
#rpart.pred <- predict(rpart.model, testset[,-8], type = "class")

## compute svm confusion matrix
table(pred = svm.pred, true = testset[,8])




