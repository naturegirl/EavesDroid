library(mclust)
library(scatterplot3d)

path <- "~/Documents/School/Interacting_With_Data/cos424project/signal-plotting/data/nm.features.csv"

signals <- read.csv(path, header=TRUE, sep=",")

colum <- ncol(signals)
row <- nrow(signals)

#print(signals)

result<-Mclust(signals[,1:5], G=2)
print(summary(result))
print(result)
plot(result, what="classification")
par(new=TRUE)
mtext("",side=2, line=0)
cluster_mapping <- cbind(signals, result$classification)
print(cluster_mapping)



