data <- read.delim("./signals/t_1397260381.data.csv",
                   header = FALSE, sep = ",", quote = "",
                   dec = ".", fill = TRUE, comment.char = "")

plot(data[,1], data[,2], xlab="time(ms)",
     ylab="g-force", main="", type="l")