library(RWeka)
library(rJava)
args <- commandArgs(trailingOnly = TRUE)
file_lr <- "./../../data/weka-data/data-a-z-LR_combined.arff"
data_lr <- read.arff(file_lr)
file_ud <- "./../../data/weka-data/data-a-z-UD_combined.arff"
data_ud <- read.arff(file_ud)

# load the saved models for LR and UD predictors
load(file="./adaboost.rf.lr.rda")
load(file="./adaboost.rf.ud.rda")

input_file <- "./../../data/weka-data/aks_1399412025.csv.arff"
if (length(args) >= 1) {
  input_file <- args[1]
}

data_input <- read.arff(input_file)
lr_labels <- predict(m_lr, newdata = data_input,
                     type = c("class", "probability"))
lr_labels <- ifelse(lr_labels == 1, "l", "r")

ud_labels <- predict(m_ud, newdata = data_input,
                     type = c("class", "probability"))
ud_labels <- ifelse(ud_labels == 1, "u", "d")

# print(lr_labels)
# print(ud_labels)

i <- 1
len <- length(lr_labels)
str_label <- ""
while ( i <= len ) {
  str_label <- paste(str_label, lr_labels[i], ud_labels[i], sep="")
  i <- i + 1
}

print(str_label)
output_label_filename <- paste(input_file, "pred", sep=".")
write(str_label, file = output_label_filename)
