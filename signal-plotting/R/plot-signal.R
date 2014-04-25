data <- read.delim("./stephen/wh_1397932403.data.csv",
                   header = FALSE, sep = ",", quote = "",
                   dec = ".", fill = TRUE, comment.char = "")

plot(data[,1], data[,2], xlab="time (us)",
     ylab="g-force", main="", type="l",  ylim=c(-1,.75),
     xlim=c(1e6, 3e6)
     )