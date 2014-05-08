data <- read.delim("./a_1398794177.gforce.csv",
                   header = FALSE, sep = ",", quote = "",
                   dec = ".", fill = TRUE, comment.char = "")

plot(as.vector(data$V1), as.vector(data$V2), xlab="time (us)", ylab="g-force", main="", type="l")